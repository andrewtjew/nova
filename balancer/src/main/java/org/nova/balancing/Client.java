package org.nova.balancing;

import org.nova.metrics.RateMeter;

public class Client 
{
    public final RateMeter accessRateMeter;
    private final String endPoint;
    public final long created;
    
    public double load;
    public double cores;
    public long lastUpdated;
    public long lastUsed;
    public double available;
    public long capacity;
    
    public Client(String endPoint)
    {
        this.endPoint=endPoint;
        this.accessRateMeter=new RateMeter();
        this.created=System.currentTimeMillis();
    }
    
    public synchronized void update(double load,double cores)
    {
        this.load=load;
        this.cores=cores;
        this.lastUpdated=System.currentTimeMillis();
        this.available=cores*(1.0-load);
    }
    
    public String use()
    {
        this.lastUsed=System.currentTimeMillis();
        return this.endPoint;
    }
    public String getEndPoint()
    {
        return this.endPoint;
    }
    
}
