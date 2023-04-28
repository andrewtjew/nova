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
import org.nova.metrics.LongValueMeter;
import org.nova.metrics.LongValueSample;

import java.util.TreeMap;

public class RequestHandler
{
	final private Object object;
	final private Method method;
	final private Filter[] bottomFilters;
	final private Filter[] topFilters;
	final private ParameterInfo[] parameterInfos;
	final private String path;
	final private Map<String,ContentReader> contentReaders;
	final private Map<String,ContentWriter> contentWriters;
	final private ContentEncoder[] contentEncoders;
	final private Map<String,ContentDecoder> contentDecoders;
	final private String key;
	final private String httpMethod;
    final private boolean log;
    final private boolean logRequestHeaders;
    final private boolean logRequestParameters;
    final private boolean logRequestContent;
    final private boolean logResponseHeaders;
    final private boolean logResponseContent;
    final private boolean logLastRequestsInMemory;
	final int cookieParamCount;
	
	final private HashMap<Integer,LongValueMeter> meters;
	final private LongValueMeter requestUncompressedContentSizeMeter;
	final private LongValueMeter responseUncompressedContentSizeMeter;
	final private LongValueMeter requestCompressedContentSizeMeter;
	final private LongValueMeter responseCompressedContentSizeMeter;
    final private RingBuffer<RequestLogEntry> lastRequestsLogEntries;
//    final private Attributes attributes;
    final private HashSet<String> attributes;
    final private boolean stateParam;
    final private boolean test;
    
	RequestHandler(Object object,Method method,String httpMethod,String path,Filter[] bottomFilters,Filter[] topFilters,ParameterInfo[] parameterInfos,	Map<String,ContentDecoder> contentDecoders,ContentEncoder[] contentEncoders,Map<String,ContentReader> contentReaders,Map<String,ContentWriter> contentWriters,boolean log,boolean logRequestHeaders,boolean logRequestParameters,boolean logRequestContent,boolean logResponseHeaders,boolean logResponseContent,boolean logLastRequestsInMemory,int bufferSize,int cookieParamCount,ClassAnnotations annotations)
	{
	    boolean stateParam=false;
	    for (ParameterInfo info:parameterInfos)
	    {
	        if (info.getSource()==ParameterSource.STATE)
	        {
	            stateParam=true;
	        }
	    }
	    this.stateParam=stateParam;
	    
		this.cookieParamCount=cookieParamCount;
		this.object=object;
		this.method=method;
		this.bottomFilters=bottomFilters;
		this.topFilters=topFilters;
		this.parameterInfos=parameterInfos;
		this.path=path;
		this.contentReaders=contentReaders;
		this.contentWriters=contentWriters;
		this.contentEncoders=contentEncoders;
		this.contentDecoders=contentDecoders;
		this.httpMethod=httpMethod;
		this.key=httpMethod+" "+path;
		this.meters=new HashMap<>();
		this.requestUncompressedContentSizeMeter=new LongValueMeter();
		this.responseUncompressedContentSizeMeter=new LongValueMeter();
		this.requestCompressedContentSizeMeter=new LongValueMeter();
		this.responseCompressedContentSizeMeter=new LongValueMeter();
		
		this.log=log;
		this.logRequestHeaders=logRequestHeaders;
		this.logRequestParameters=logRequestParameters;
		this.logRequestContent=logRequestContent;
        this.logResponseHeaders=logResponseHeaders;
        this.logResponseContent=logResponseContent;
        this.logLastRequestsInMemory=logLastRequestsInMemory;
        this.lastRequestsLogEntries=new RingBuffer<>(new RequestLogEntry[bufferSize]);
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
   }
	
	public Object getObject()
	{
		return object;
	}

	public Method getMethod()
	{
		return method;
	}
	
	public String getHttpMethod()
	{
		return this.httpMethod;
	}
	public Filter[] getBottomFilters()
	{
		return this.bottomFilters;
	}
	public Filter[] getTopFilters()
	{
		return this.topFilters;
	}

	public ParameterInfo[] getParameterInfos()
	{
		return parameterInfos;
	}

	public String getKey()
	{
		return key;
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

	public Map<String, ContentReader> getContentReaders()
	{
		return contentReaders;
	}

	public Map<String, ContentWriter> getContentWriters()
	{
		return contentWriters;
	}

	public ContentEncoder[] getContentEncoders()
	{
		return contentEncoders;
	}

	public Map<String, ContentDecoder> getContentDecoders()
	{
		return contentDecoders;
	}

	public void update(int statusCode,long duration,long requestUncompressedContentSize,long responseUncompressedContentSize,long requestCompressedContentSize,long responseCompressedContentSize)
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
		this.requestUncompressedContentSizeMeter.update(requestUncompressedContentSize);
		this.responseUncompressedContentSizeMeter.update(responseUncompressedContentSize);
		this.requestCompressedContentSizeMeter.update(requestCompressedContentSize);
		this.responseCompressedContentSizeMeter.update(responseCompressedContentSize);
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

	public LongValueMeter getRequestUncompressedContentSizeMeter()
	{
		return requestUncompressedContentSizeMeter;
	}

	public LongValueMeter getResponseUncompressedContentSizeMeter()
	{
		return responseUncompressedContentSizeMeter;
	}

	public LongValueMeter getRequestCompressedContentSizeMeter()
	{
		return requestCompressedContentSizeMeter;
	}

	public LongValueMeter getResponseCompressedContentSizeMeter()
	{
		return responseCompressedContentSizeMeter;
	}

    public boolean isLog()
    {
        return log;
    }

    public boolean isLogRequestHeaders()
    {
        return logRequestHeaders;
    }

    public boolean isLogRequestContent()
    {
        return logRequestContent;
    }
    public boolean isLogRequestParameters()
    {
        return logRequestParameters;
    }

    public boolean isLogResponseHeaders()
    {
        return logResponseHeaders;
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
    public boolean hasStateParam()
    {
        return this.stateParam;
    }
	public boolean isTest()
	{
	    return this.test;
	}
}
