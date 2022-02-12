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

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.nova.concurrent.Lock;
import org.nova.http.Header;
import org.nova.http.server.Context;
import org.nova.http.server.Filter;
import org.nova.http.server.FilterChain;
import org.nova.http.server.Response;
import org.nova.tracing.Trace;


public class HeaderFilter extends Filter
{
    final private Header[] headers;
    
    public HeaderFilter(Header...headers)
    {
        this.headers=headers;
    }

    @Override
    public Response<?> executeNext(Trace parent, Context context) throws Throwable
    {
        HttpServletResponse response=context.getHttpServletResponse();
        for (Header header:this.headers)
        {
            response.addHeader(header.getName(), header.getValue());
        }
        return context.next(parent);
    }

}
