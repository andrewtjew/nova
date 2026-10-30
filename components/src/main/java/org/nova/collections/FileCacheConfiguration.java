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
package org.nova.collections;

public class FileCacheConfiguration
{
    public String sharedDirectory;
    public String localDirectory;
    public long maxAgeMs;
    public long maxSize;
    public int capacity;
    public long freeMemoryCapacity;

    public FileCacheConfiguration(String sharedDirectory,String localDirectory,int capacity,long maxAgeMs,long maxSize,long  freeMemoryCapacity)
    {
        this.sharedDirectory=sharedDirectory;
        this.localDirectory=localDirectory;
        this.maxAgeMs=maxAgeMs;
        this.maxSize=maxSize;
        this.capacity=capacity;
        this.freeMemoryCapacity=freeMemoryCapacity;
    }   
    
    public FileCacheConfiguration(String sharedDirectory,String localDirectory,int capacity)
    {
        this(sharedDirectory,localDirectory,capacity,Long.MAX_VALUE,Long.MAX_VALUE,1_000_000_000);
    }
    public FileCacheConfiguration()
    {
        this(null,null,1000);
    }
    
}
