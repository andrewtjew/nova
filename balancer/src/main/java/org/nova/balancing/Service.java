package org.nova.balancing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.nova.concurrent.TimeBase;
import org.nova.concurrent.TimerRunnable;
import org.nova.concurrent.TimerTask;
import org.nova.configuration.Configuration;
import org.nova.frameworks.CoreEnvironment;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerApplicationRunner;
import org.nova.http.server.HttpServer;
import org.nova.http.server.HttpTransport;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.services.SessionServerApplication;
import org.nova.tracing.Trace;

public class Service extends ServerApplication
{
    
    public static void main(String[] args) throws Throwable
    {
        new ServerApplicationRunner().run(args,(coreEnvironment,operatorServer)->{return new Service(coreEnvironment,operatorServer);});
    }


    public Service(CoreEnvironment coreEnvironment,HttpTransport transport) throws Throwable
    {
        super("Balancer", coreEnvironment,transport);
        
    }
    
    public void onStart(Trace parent) throws Throwable
    {
		this.clients=new HashMap<String, Client>();
		Configuration configuration=getConfiguration();
        Handler controller=new Handler(this);
        this.getPublicServer().registerFrontServletHandlers(controller);
        this.getPrivateServer().registerHandlers(controller);
        
		this.overloadThreshold=configuration.getDoubleValue("Application.overloadThreshold",0.8);
		this.unusableThreshold=configuration.getDoubleValue("Application.unusableThreshold",0.05);
		this.rebuildInterval=configuration.getLongValue("Application.rebuildInterval",1000);
		this.clientTimeout=configuration.getLongValue("Application.clientTimeout",10*1000);
		
		this.lastRebuild=System.currentTimeMillis();
		
		this.getTimerScheduler().schedule("clientTimeout",TimeBase.FREE,this.clientTimeout/2,this.clientTimeout/2,new TimerRunnable() {
			
			@Override
			public void run(Trace parent, TimerTask event) throws Throwable 
			{
				removeStaleClients();
			}
		});
    }
    public void onStop()
    {
    }

    private HashMap<String,Client> clients;
	private double overloadThreshold;
	private double unusableThreshold;
	private long rebuildInterval;
	private long lastRebuild;
	private long clientTimeout;
	
	private List<Client> availableList=new ArrayList<Client>();
	private List<Client> overloadedList=new ArrayList<Client>();
	private List<Client> unusableList=new ArrayList<Client>();
	
	static class Pointer
	{
		int index=0;
		int used=0;
	}
	
	private Pointer availablePointer;
	private Pointer overloadedPointer;
    
	public void update(String endPoint,double load,double cores)
	{
		boolean rebuild=false;
		long now=System.currentTimeMillis();
		
		synchronized(this.clients)
		{
			if (cores<=0)
			{
				this.clients.remove(endPoint);
				rebuild=true;
			}
			else
			{
				Client client=this.clients.get(endPoint);
				if (client==null)
				{
					client=new Client(endPoint);
					this.clients.put(endPoint, client);
					rebuild=true;
				}
				else
				{
					long interval=now-this.lastRebuild;
					if (interval>this.rebuildInterval)
					{
						rebuild=true;
					}
				}
				client.update(load, cores);
			}
		}
		if (rebuild)
		{
		    rebuild();
		}
	}	

	public void removeStaleClients()
    {
        long now=System.currentTimeMillis();
        boolean rebuild=false;
        ArrayList<Client> removeList=new ArrayList<Client>();
        synchronized(this.clients)
        {
            for (Client client:this.clients.values())
            {
                if (now-client.lastUpdated>this.clientTimeout)
                {
                    removeList.add(client);
                }
            }
            if (removeList.size()>0)
            {
                for (Client client:removeList)
                {
                    this.clients.remove(client.getEndPoint());
                }
                rebuild=true;
            }
        }
        if (rebuild)
        {
            rebuild();
        }
    }   
    public void rebuild()
    {
        long now=System.currentTimeMillis();
        ArrayList<Client> availableList=new ArrayList<Client>();
        ArrayList<Client> overloadedList=new ArrayList<Client>();
        ArrayList<Client> unusableList=new ArrayList<Client>();
        synchronized(this.clients)
        {
            for (Client client:this.clients.values())
            {
                if (client.available<=this.unusableThreshold)
                {
                    unusableList.add(client);
                }
                else if (client.load>this.overloadThreshold)
                {
                    overloadedList.add(client);
                }
                else
                {
                    availableList.add(client);
                }
            }
            this.lastRebuild=now;
        }
        build(availableList);
        build(overloadedList);
        synchronized(this)
        {
            this.availableList=availableList;
            this.overloadedList=overloadedList;
            this.unusableList=unusableList;
            this.availablePointer=new Pointer();
            this.overloadedPointer=new Pointer();
        }
    }   
	
	public synchronized String getEndPoint()
	{
		if (this.availableList.size()>0)
		{
			return getEndPoint(this.availableList,this.availablePointer);
		}
		if (this.overloadedList.size()>0)
		{
			return getEndPoint(this.overloadedList,this.overloadedPointer);
		}
		return null;
	}
	
	String getEndPoint(List<Client> list,Pointer pointer)
	{
		Client client=list.get(pointer.index);
		pointer.used++;
		if (pointer.used>=client.capacity)
		{
			pointer.used=0;
			pointer.index--;
			if (pointer.index<0)
			{
				pointer.index=list.size()-1;
			}
		}
		return client.use();
	}
	
	void build(List<Client> list)
	{
		if (list.size()==0)
		{
			return;
		}
		list.sort(new Comparator<Client>() 
		{
			@Override
			public int compare(Client client1, Client client2) 
			{
				return Double.compare(client1.available, client2.available);
			}
		});
		
		double minimum=list.get(0).available;
		for (Client client:list)
		{
			client.capacity=Math.round(client.available/minimum);
		}
	}
	
    
}
