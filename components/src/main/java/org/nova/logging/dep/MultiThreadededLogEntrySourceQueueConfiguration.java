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
package org.nova.logging.dep;

import org.nova.flow.SourceQueueConfiguration;

public class MultiThreadededLogEntrySourceQueueConfiguration extends SourceQueueConfiguration
{
    public long writerStallWaitMs=100;
    
    //For best performance set writerMaxQueueSize=2*writerThreads*writerSegmentSize, otherwise we will stall
    //writerStallSizeThreshold = writerMaxQueueSize- writerSegmentSize; 
    
    public int writerStallSizeThreshold=0;  //set to zero to compute writerStallSizeThreshold = writerMaxQueueSize- writerSegmentSize;
    public int writerMaxQueueSize      =0;  //set to zero to compute writerMaxQueueSize=2*writerThreads*writerSegmentSize
    public int writerSegmentSize      = 100000;
    public int writerBufferInitialCapacity=65536;
    public int writerThreads=4;

    public int entriesPerFile=20000;
	
	public MultiThreadededLogEntrySourceQueueConfiguration(int entriesPerFile)
	{
		this.entriesPerFile=entriesPerFile;
		this.rollOverWaitMs=200; //Since the log entries are buffered, we need to set rollover to small value to prevent data loss. 
	}
	public MultiThreadededLogEntrySourceQueueConfiguration()
	{
	}
}
