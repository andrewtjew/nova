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

import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.Log;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.Test;

public class Annotations
{
	Path path=null;
	ContentWriters contentWriters = null;
	ContentReaders contentReaders = null;
	ContentEncoders contentEncoders=null;
	ContentDecoders contentDecoders=null;
	Test test=null;
	Filters filters = null;
	Log log=null;
	
    Annotations()
    {
        
    }
	Annotations(Annotations that)
	{
	    this.path=that.path;
	    this.contentWriters = that.contentWriters;
	    this.contentReaders = that.contentReaders;
	    this.contentEncoders=that.contentEncoders;
	    this.contentDecoders=that.contentDecoders;
	    this.filters = that.filters;
	    this.log=that.log;
	    this.test=that.test;
	}

}
