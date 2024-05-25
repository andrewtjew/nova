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

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.http.server.Response;
import org.nova.http.server.Context;
import org.nova.tracing.Trace;
import org.nova.html.ext.Redirect;;

public class RedirectAbnormalSessionRequestHandling implements AbnormalSessionRequestHandling
{

    final private String noSessionRedirect;
    final private String accessDeniedRedirect;
    final private String noLockRedirect;
    
    public RedirectAbnormalSessionRequestHandling(String noSessionRedirect,String accessDeniedRedirect,String noLockRedirect)
    {
        this.noSessionRedirect=noSessionRedirect;
        this.accessDeniedRedirect=accessDeniedRedirect;
        this.noLockRedirect=noLockRedirect;
    }

    public RedirectAbnormalSessionRequestHandling(String redirect)
    {
        this(redirect,redirect,redirect);
    }

    public RedirectAbnormalSessionRequestHandling()
    {
        this("/");
    }
    
    @Override
    public Response<?> handleNoSessionRequest(Trace parent,SessionFilter sessionFilter,Context context)
    {
        HttpServletResponse response=context.getHttpServletResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED_401);
        return new Response<Redirect>(new Redirect(this.noSessionRedirect));
    }

    @Override
    public Response<?> handleAccessDeniedRequest(Trace parent,SessionFilter sessionFilter,Session session, Context context)
    {
        HttpServletResponse response=context.getHttpServletResponse();
        response.setStatus(HttpStatus.FORBIDDEN_403);
        return new Response<Redirect>(new Redirect(this.accessDeniedRedirect));
    }

    @Override
    public Response<?> handleNoLockRequest(Trace parent,SessionFilter sessionFilter,Session session, Context context)
    {
        HttpServletResponse response=context.getHttpServletResponse();
        response.setStatus(HttpStatus.CONFLICT_409);
        return new Response<Redirect>(new Redirect(this.noLockRedirect));
    }

    @Override
    public String[] getMediaTypes()
    {
        return new String[]{"*/*"};
    }

}
