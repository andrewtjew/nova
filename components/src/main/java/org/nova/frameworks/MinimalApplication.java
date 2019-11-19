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
package org.nova.frameworks;

import org.nova.logging.LogUtils;
import org.nova.logging.Logger;
import org.nova.logging.SourceQueueLogger;
import org.nova.metrics.SourceEventBoard;
import org.nova.tracing.TraceManager;

public class MinimalApplication
{
	final protected TraceManager traceManager;
	final private Logger logger;
	final private SourceEventBoard statusBoard;
	
	public MinimalApplication() throws Throwable
	{
		this.logger=LogUtils.createConsoleLogger();
		this.traceManager=new TraceManager(this.logger);
		this.statusBoard=new SourceEventBoard();
	}
    public MinimalApplication(Logger logger) throws Throwable
    {
        this.logger=logger;
        this.traceManager=new TraceManager(this.logger);
        this.statusBoard=new SourceEventBoard();
    }
	
	public SourceEventBoard getStatusBoard()
	{
	    return this.statusBoard;
	}
	public TraceManager getTraceManager()
	{
		return traceManager;
	}

	public Logger getLogger()
	{
		return this.logger;
	}

}
