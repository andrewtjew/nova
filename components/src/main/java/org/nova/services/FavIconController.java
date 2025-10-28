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
package org.nova.services;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Path;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

import jakarta.servlet.http.HttpServletResponse;

@ContentEncoders({DeflaterContentEncoder.class,GzipContentEncoder.class})
public class FavIconController
{
    final private byte[] icoImage;
    final private byte[] pngImage;
    final private String cacheControl;
    public FavIconController(String icoFileName,String pngFileName,String cacheControl) throws Exception
    {
        if (icoFileName!=null)
        {
            this.icoImage=FileUtils.readFile(icoFileName);
        }
        else
        {
            this.icoImage=null;
        }
        
        if (pngFileName!=null)
        {
            this.pngImage=FileUtils.readFile(pngFileName);
        }
        else
        {
            this.pngImage=null;
        }
       
        this.cacheControl=cacheControl;
    }
    public FavIconController(String icoFileName,String pngFileName) throws Exception
    {
        this(icoFileName,pngFileName,"max-age=604800");
    }
	
	@GET
	@Path("/favicon.ico")
	public void ico(Trace parent,Context context) throws Throwable
	{
	    if (this.icoImage!=null)
	    {
    	    context.capture();
            HttpServletResponse response=context.getHttpServletResponse();
            response.setHeader("Cache-Control", this.cacheControl);
            response.getOutputStream().write(this.icoImage);
            response.setContentType("image/x-icon");
	    }
	    else
	    {
            context.getHttpServletResponse().setStatus(HttpStatus.NOT_FOUND_404);
	    }
	}

	@GET
    @Path("/favicon.png")
    public void png(Trace parent,Context context) throws Throwable
    {
        if (this.pngImage!=null)
        {
            context.capture();
            HttpServletResponse response=context.getHttpServletResponse();
            response.setHeader("Cache-Control", this.cacheControl);
            response.getOutputStream().write(this.pngImage);
            response.setContentType("image/png");
        }
        else
        {
            context.getHttpServletResponse().setStatus(HttpStatus.NOT_FOUND_404);
        }
    }

	@GET
    @Path("/apple-touch-icon.png")
    public void apple(Trace parent,Context context) throws Throwable
    {
        if (this.pngImage!=null)
        {
            context.capture();
            HttpServletResponse response=context.getHttpServletResponse();
            response.setHeader("Cache-Control", this.cacheControl);
            response.getOutputStream().write(this.pngImage);
            response.setContentType("image/png");
        }
        else
        {
            context.getHttpServletResponse().setStatus(HttpStatus.NOT_FOUND_404);
        }
    }
}
