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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nova.collections.RingBuffer;
import org.nova.metrics.LongValueMeter;
import org.nova.metrics.LongValueSample;

import java.util.TreeMap;

public class RequestHandler
{
	final private Object object;
	final private Method method;
	final private Filter[] filters;
	final private ParameterInfo[] parameterInfos;
	final private String path;
	final private Map<String,ContentReader<?>> contentReaders;
	final private Map<String,ContentWriter<?>> contentWriters;
	final private Map<String,ContentEncoder> contentEncoders;
	final private Map<String,ContentDecoder> contentDecoders;
	final private String key;
	final private String httpMethod;
	final private boolean public_;
    final private boolean log;
    final private boolean logRequestHeaders;
    final private boolean logRequestParameters;
    final private boolean logRequestContent;
    final private boolean logResponseHeaders;
    final private boolean logResponseContent;
    final private boolean logLastRequestsInMemory;
	
	
	final private HashMap<Integer,LongValueMeter> meters;
	final private LongValueMeter requestUncompressedContentSizeMeter;
	final private LongValueMeter responseUncompressedContentSizeMeter;
	final private LongValueMeter requestCompressedContentSizeMeter;
	final private LongValueMeter responseCompressedContentSizeMeter;
    final private RingBuffer<RequestLogEntry> lastRequestsLogEntries;
    final private int cookieStatesLength;
    
	RequestHandler(Object object,Method method,String httpMethod,String path,Filter[] filters,ParameterInfo[] parameterInfos,	Map<String,ContentDecoder> contentDecoders,Map<String,ContentEncoder> contentEncoders,Map<String,ContentReader<?>> contentReaders,Map<String,ContentWriter<?>> contentWriters,boolean log,boolean logRequestHeaders,boolean logRequestParameters,boolean logRequestContent,boolean logResponseHeaders,boolean logResponseContent,boolean logLastRequestsInMemory,boolean public_,int bufferSize,int cookieStatesLength)
	{
		this.object=object;
		this.method=method;
		this.filters=filters;
		this.parameterInfos=parameterInfos;
		this.path=path;
		this.contentReaders=contentReaders;
		this.contentWriters=contentWriters;
		this.contentEncoders=contentEncoders;
		this.contentDecoders=contentDecoders;
		this.httpMethod=httpMethod;
		this.key=httpMethod+" "+path;
		this.public_=public_;
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
        this.cookieStatesLength=cookieStatesLength;
	}

	int getCookieStatesLength()
	{
	    return this.cookieStatesLength;
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

	public Filter[] getFilters()
	{
		return filters;
	}

	public ParameterInfo[] getParameterInfos()
	{
		return parameterInfos;
	}

	public String getKey()
	{
		return key;
	}

	
	public String getPath()
	{
		return path;
	}

	public Map<String, ContentReader<?>> getContentReaders()
	{
		return contentReaders;
	}

	public Map<String, ContentWriter<?>> getContentWriters()
	{
		return contentWriters;
	}

	public Map<String, ContentEncoder> getContentEncoders()
	{
		return contentEncoders;
	}

	public Map<String, ContentDecoder> getContentDecoders()
	{
		return contentDecoders;
	}

	public boolean isPublic()
	{
		return this.public_;
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
        
	
}
