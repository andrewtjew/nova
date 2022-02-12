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

import java.util.HashMap;
import java.util.Random;

import org.nova.tracing.Trace;
import org.nova.tracing.TraceWaiter;
import org.nova.utils.MathUtils;

public class LatencyFilter extends Filter
{
    final private HashMap<String,RequestHandlerLatencyTracker> trackers;
    private boolean enabled;
    private LatencyDescriptor overrideLatencyDescriptor;
    final private Random random;
    
    public LatencyFilter()
    {
        this.trackers=new HashMap<>();
        this.random=new Random();
    }
    
    public synchronized void setEnabled(boolean enabled)
    {
        this.enabled=enabled; 
    }
    
    public synchronized boolean isEnabled()
    {
        return this.enabled;
    }
    
    public synchronized void setOverrideLatencyDescriptor(LatencyDescriptor overrideLatencyDescriptor)
    {
        this.overrideLatencyDescriptor=overrideLatencyDescriptor;
    }
    
    public synchronized LatencyDescriptor getOverrideLatencyDescriptor()
    {
        return this.overrideLatencyDescriptor;
    }
    
//    public synchronized void setLatency(String key,LatencyDescriptor latencyDescriptor)
//    {
//        RequestHandlerLatencyTracker tracker=this.trackers.get(key);
//        if (tracker!=null)
//        {
//            tracker.setLatencyDescriptor(latencyDescriptor);
//        }
//    }
    
    public RequestHandlerLatencyTracker getRequestHandlerLatencyTracker(String key)
    {
        return this.trackers.get(key);
    }
    
    @Override
    public Response<?> executeNext(Trace trace, Context context) throws Throwable
    {
        long beforeLatencyMs=0;
        long afterLatencyMs=0;
        synchronized(this)
        {
            if (this.enabled)
            {
                RequestHandlerLatencyTracker tracker=this.trackers.get(context.getRequestHandler().getKey());
                if (tracker!=null)
                {
                    LatencyDescriptor descriptor=(this.overrideLatencyDescriptor!=null)&&(this.overrideLatencyDescriptor.isEnabled())?this.overrideLatencyDescriptor:tracker.getLatencyDescriptor();
                    if ((descriptor!=null)&&(descriptor.isEnabled()))
                    {
                        beforeLatencyMs=descriptor.getBeforeExecuteMinimumMs();
                        long varianceMs=descriptor.getBeforeExecuteMaximumMs()-descriptor.getBeforeExecuteMinimumMs();
                        if (varianceMs>0)
                        {
                            beforeLatencyMs+=MathUtils.mod(this.random.nextLong(), varianceMs);
                        }
                        afterLatencyMs=descriptor.getAfterExecuteMinimumMs();
                        varianceMs=descriptor.getAfterExecuteMaximumMs()-descriptor.getAfterExecuteMinimumMs();
                        if (varianceMs>0)
                        {
                            afterLatencyMs+=MathUtils.mod(this.random.nextLong(), varianceMs);
                        }
                        tracker.update(beforeLatencyMs, afterLatencyMs);
                    }
                }
            }
        }
        if (beforeLatencyMs>0)
        {
            try (TraceWaiter waiter=new TraceWaiter(trace))
            {
                Thread.sleep(beforeLatencyMs);
            }
        }
        Response<?> response=context.next(trace);
        if (afterLatencyMs>0)
        {
            try (TraceWaiter waiter=new TraceWaiter(trace))
            {
                Thread.sleep(afterLatencyMs);
            }
        }
        return response;
    }

    @Override
    public void onRegister(RequestHandler requestHandler)
    {
        this.trackers.put(requestHandler.getKey(), new RequestHandlerLatencyTracker(requestHandler));
    }
    
    synchronized public RequestHandlerLatencyTracker[] getSnapshot()
    {
        return this.trackers.values().toArray(new RequestHandlerLatencyTracker[this.trackers.size()]);
    }
    
}
