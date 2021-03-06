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
package org.nova.metrics;

import java.util.HashMap;

public class StackTraceNode
{
    final StackTraceElement stackTraceElement;
    private HashMap<String,StackTraceNode> childNodes;
    private long activeNs; 
    private long waitNs;
    private int count;
    
    StackTraceNode(StackTraceElement stackTraceElement)
    {
        this.stackTraceElement=stackTraceElement;
    }
    StackTraceNode(StackTraceElement stackTraceElement,long totalActiveNs,long totalWaitNs,int count)
    {
        this.stackTraceElement=stackTraceElement;
        this.activeNs=totalActiveNs;
        this.waitNs=totalWaitNs;
        this.count=count;
                
    }
    void addChildNode(StackTraceNode node)
    {
        if (childNodes==null)
        {
            childNodes=new HashMap<>();
        }
        StackTraceElement element=node.getStackTraceElement();
        String key=getKey(element);
        this.childNodes.put(key, node);
    }
    void update(long durationNs,StackTraceElement[] stackTrace)
    {
        update(durationNs,stackTrace,stackTrace.length);
    }
    private String getKey(StackTraceElement element)
    {
        return element.getClassName()+'.'+element.getMethodName()+"."+element.getLineNumber();
    }
    public boolean isThreadWaiting(StackTraceElement element)
    {
        if ("java.lang.Object".equals(element.getClassName()))
        {
            return "wait".equals(element.getMethodName());
        }
        if ("java.lang.Thread".equals(element.getClassName()))
        {
            return "sleep".equals(element.getMethodName());
        }
        return false;
    }
    
    boolean update(long durationNs,StackTraceElement[] stackTrace,int index)
    {
        this.count++;
        if (index<=1)
        {
            StackTraceElement element=stackTrace[index];
            boolean wait=isThreadWaiting(element);
            if (wait)
            {
                this.waitNs+=durationNs;
                return true;
            }
            this.activeNs+=durationNs;
            if (index==0)
            {
                return false;
            }
        }
        StackTraceElement childElement=stackTrace[index-1];
        String childKey=getKey(childElement);
        StackTraceNode childNode;
        if (this.childNodes==null)
        {
            this.childNodes=new HashMap<>();
            childNode=new StackTraceNode(childElement);
            this.childNodes.put(childKey, childNode);
        }
        else
        {
            childNode=this.childNodes.get(childKey);
            if (childNode==null)
            {
                childNode=new StackTraceNode(childElement);
                this.childNodes.put(childKey, childNode);
            }            
        }
        boolean wait=childNode.update(durationNs,stackTrace,index-1);
        if (wait)
        {
            this.waitNs+=durationNs;
        }
        else
        {
            this.activeNs+=durationNs;
        }
        return wait;
            
    }
    public long getActiveNs()
    {
        return this.activeNs;
    }
    public int getCount()
    {
        return this.count;
    }
    public long getWaitNs()
    {
        return this.waitNs;
    }
    public StackTraceElement getStackTraceElement()
    {
        return this.stackTraceElement;
    }
    public StackTraceNode[] getChildNodes()
    {
        if (this.childNodes==null)
        {
            return null;
        }
        return this.childNodes.values().toArray(new StackTraceNode[this.childNodes.size()]);
    }
}
