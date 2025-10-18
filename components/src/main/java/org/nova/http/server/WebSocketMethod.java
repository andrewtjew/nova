/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.http.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nova.collections.RingBuffer;
import org.nova.http.server.RequestMethodMap.FragmentIndexMap;
import org.nova.metrics.LongValueMeter;
import org.nova.metrics.LongValueSample;
import org.nova.services.ForbiddenRoles;
import org.nova.services.RequiredRoles;

import java.util.TreeMap;

public class WebSocketMethod
{
	final private Method method;
	final private ParameterInfo[] parameterInfos;
	final private String path;
    final private boolean log;
    final private boolean logRequestParameters;
    final private boolean logRequestContent;
    final private boolean logResponseContent;
    final private boolean logLastRequestsInMemory;
    final private RequiredRoles requiredRoles;
    final private ForbiddenRoles forbiddenRoles;
	
	final private HashMap<Integer,LongValueMeter> meters;
    final private RingBuffer<RequestLogEntry> lastRequestsLogEntries;
    final private HashSet<String> attributes;
    final private Class<?> stateType;
    final private boolean test;
    final private HashSet<String> hiddenParameters;
    final private long runtimeKey;
    
    final private FragmentIndexMap fragmentIndexMap;
    final private boolean isSecurityVerificationRequired;
    WebSocketMethod(Method method,String path,ParameterInfo[] parameterInfos,boolean log,boolean logRequestParameters,boolean logRequestContent,boolean logResponseContent,boolean logLastRequestsInMemory,int bufferSize,WebSocketHandlerClassAnnotations annotations,HashSet<String> hiddenParameters)
	{
        this.runtimeKey=HttpServer.RUNTIME_KEY_GENERATOR.getAndIncrement();
        this.fragmentIndexMap=new FragmentIndexMap();
	    
        Class<?> stateType=null;
        int queryParameterInfos=0;
	    for (ParameterInfo info:parameterInfos)
	    {
	        switch(info.getSource())
	        {
	            case ParameterSource.STATE:
	            stateType=info.getType();
	            break;
	            
                case ParameterSource.QUERY:
                queryParameterInfos++;
                break;

                default:
                break;
            }
	    }
	    
	    
	    
	    this.stateType=stateType;
		this.method=method;
		this.parameterInfos=parameterInfos;
		this.path=path;
		this.meters=new HashMap<>();
		this.hiddenParameters=hiddenParameters;
		
		this.log=log;
		this.logRequestParameters=logRequestParameters;
		this.logRequestContent=logRequestContent;
        this.logResponseContent=logResponseContent;
        this.logLastRequestsInMemory=logLastRequestsInMemory;
        this.lastRequestsLogEntries=new RingBuffer<>(new RequestLogEntry[bufferSize]);

        this.requiredRoles=annotations.requiredRoles;
        this.forbiddenRoles=annotations.forbiddenRoles;
        if (annotations.attributes!=null)
        {
            this.attributes=new HashSet<String>();
            for (String value:annotations.attributes.value())
            {
                this.attributes.add(value);
            }
        }
        else
        {
            this.attributes=null;
        }
        this.test=annotations.test!=null;
        this.isSecurityVerificationRequired=(queryParameterInfos>0)&&(((this.requiredRoles!=null)&&this.requiredRoles.value().length>0))||((this.forbiddenRoles!=null)&&(this.forbiddenRoles.value().length>0));        
   }

    public boolean isSecurityVerificationRequired()
    {
        return this.isSecurityVerificationRequired;
    }
    
	public FragmentIndexMap getFragmentIndexMap()
	{
	    return this.fragmentIndexMap;
	}
	
	public long getRunTimeKey()
	{
	    //It is not expected that this key will roll over as runtime RequestHandlers are not expected to be dynamic.
	    return this.runtimeKey;
	}
	public Method getMethod()
	{
		return method;
	}
	
	public RequiredRoles getRequiredRoles()
	{
	    return this.requiredRoles;
	}
	public ForbiddenRoles getForbiddenRoles()
	{
	    return this.forbiddenRoles;
	}
	
	public HashSet<String> getHiddenParameters()
	{
	    return this.hiddenParameters;
	}

	public ParameterInfo[] getParameterInfos()
	{
		return this.parameterInfos;
	}

	public boolean containsAttribute(String name)
	{
	    if (this.attributes==null)
	    {
	        return false;
	    }
	    return this.attributes.contains(name);
	}
	public Set<String> getAttributes()
	{
	    return this.attributes;
	}
	
	
	public String getPath()
	{
		return path;
	}

	public void update(int statusCode,long duration)
	{
		synchronized (this)
		{
			LongValueMeter meter=this.meters.get(statusCode);
			if (meter==null)
			{
				meter=new LongValueMeter();
				this.meters.put(statusCode, meter);
			}
			meter.update(duration);
		}
	}

    public Map<Integer,LongValueSample> sampleStatusMeters()
    {
        synchronized (this)
        {
            TreeMap<Integer,LongValueSample> results =new TreeMap<>();
            for (Entry<Integer, LongValueMeter> entry:this.meters.entrySet())
            {
                results.put(entry.getKey(), entry.getValue().sample());
            }
            return results;
        }       
    }

    public Map<Integer,LongValueMeter> getStatusMeters()
    {
        synchronized (this)
        {
            TreeMap<Integer,LongValueMeter> results =new TreeMap<>();
            for (Entry<Integer, LongValueMeter> entry:this.meters.entrySet())
            {
                results.put(entry.getKey(), entry.getValue());
            }
            return results;
        }       
    }

    public boolean isLog()
    {
        return log;
    }

    public boolean isLogRequestContent()
    {
        return logRequestContent;
    }
    public boolean isLogRequestParameters()
    {
        return logRequestParameters;
    }

    public boolean isLogResponseContent()
    {
        return logResponseContent;
    }

    public boolean isLogLastRequestsInMemory()
    {
        return logLastRequestsInMemory;
    }
    public void log(RequestLogEntry entry)
    {
        synchronized(this.lastRequestsLogEntries)
        {
            this.lastRequestsLogEntries.add(entry);
        }
    }
    public RequestLogEntry[] getLastRequestLogEntries()
    {
        synchronized (this.lastRequestsLogEntries)
        {
            List<RequestLogEntry> list=this.lastRequestsLogEntries.getSnapshot();
            return list.toArray(new RequestLogEntry[list.size()]);
        }
    }
    public Class<?> getStateType()
    {
        return this.stateType;
    }
	public boolean isTest()
	{
	    return this.test;
	}
}
