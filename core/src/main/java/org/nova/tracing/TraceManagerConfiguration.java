/*******************************************************************************
 * Copyright (C) 2016-2019 Kat Fung Tjew
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
package org.nova.tracing;

public class TraceManagerConfiguration
{
	public int maximumActives=2000;
    public int lastTraceBufferSize=2000;
    public int watchListLastTraceBufferSize=2000;
	public boolean enableLastTraceWatching=false;
    public int lastExceptionBufferSize=2000;
    public int lastSecondaryExceptionBufferSize=1000;
	public boolean logTraces=true;
	public boolean logExceptionTraces=true;
    public boolean captureCreateStackTrace=false;
    public boolean captureCloseStackTrace=false;
	public int logSlowTraceDurationMs=-1; //disabled
	
	
	public TraceManagerConfiguration()
	{
	}

	/*
	static public TraceManagerConfiguration NormalApplicationConfiguration()
	{
		TraceManagerConfiguration configuration=new TraceManagerConfiguration();
		configuration.maximumActives=10000;
		configuration.enableLastTraceWatching=false;
		configuration.lastExceptionBufferSize=100;
        configuration.lastTraceBufferSize=100;
        configuration.watchListLastTraceBufferSize=100;
		configuration.logExceptionTraces=true;
		configuration.logTraces=false;
        configuration.captureCreateStackTrace=true;
        configuration.captureCloseStackTrace=false;
        configuration.logSlowTraceDurationMs=-1000;
		return configuration;
	}
	*/
}
