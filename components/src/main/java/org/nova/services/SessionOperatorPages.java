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

import org.nova.core.NameObject;
import org.nova.frameworks.OperatorPage;
import org.nova.frameworks.ServerApplication;
import org.nova.frameworks.ServerOperatorPages;
import org.nova.frameworks.ServerOperatorPages.OperatorTable;
import org.nova.html.elements.Element;
import org.nova.html.operator.MoreButton;
import org.nova.html.operator.NameValueList;
import org.nova.html.operator.TableRow;
import org.nova.html.tags.button_button;
import org.nova.html.tags.button_submit;
import org.nova.html.tags.form_post;
import org.nova.html.tags.hr;
import org.nova.html.tags.p;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.Redirect;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.JSONPatchContentReader;
import org.nova.http.server.Response;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.metrics.RateSample;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

@ContentDecoders(GzipContentDecoder.class)
@ContentEncoders(GzipContentEncoder.class)
@ContentReaders({JSONContentReader.class,JSONPatchContentReader.class})
@ContentWriters({JSONContentWriter.class,HtmlElementWriter.class})
public class SessionOperatorPages<SESSION extends Session>
{
    final private SessionManager<SESSION> sessionManager;
    final private ServerApplication serverApplication;
    public SessionOperatorPages(SessionManager<SESSION> sessionManager,ServerApplication serverApplication)
    {
        this.sessionManager=sessionManager;
        this.serverApplication=serverApplication;
    }
    
    public void bind() throws Throwable
    {
        this.serverApplication.getMenuBar().add("/operator/sessions","Sessions","View All");
        this.serverApplication.getOperatorServer().registerHandlers(this);
    }
    
    @GET
    @Path("/operator/sessions")
    public Element getAll(Trace parent) throws Exception, Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("All Sessions");
        page.content().addInner(new hr());
        OperatorTable table=page.content().returnAddInner(new ServerOperatorPages.OperatorTable(page.head()));
        table.setHeader("Token","User","Created","Last Accessed","Active","Idle","Accessed","Rate","");
        
        for (Session session:this.sessionManager.getSessionSnapshot())
        {
            long duration=System.currentTimeMillis()-session.getCreated();
            long idle=System.currentTimeMillis()-session.getLastAccess();
            TableRow row=new TableRow();
            RateSample sample=session.getAccessRateMeter().sample();
            row.add(session.getToken(),session.getUser(),Utils.millisToLocalDateTimeString(session.getCreated()),Utils.millisToLocalDateTimeString(session.getLastAccess()),Utils.millisToDurationString(duration),Utils.millisToDurationString(idle)
                    ,sample.getSamples(),String.format("%.2f",sample.getRate()
                            ));
            /*
            row.addRemoveAndDetailButtons("window.location='"+new PathAndQueryBuilder("/operator/sessions/remove").addQuery("token", session.getToken()).toString()+"';"
                    ,new PathAndQueryBuilder("/operator/session").addQuery("token",session.getToken()).toString()
                    );
            */
            row.add(new MoreButton(page.head(),new PathAndQuery("/operator/session").addQuery("token",session.getToken()).toString()));
           table.addRow(row);
        }
        return page;
    }   

    @GET
    @Path("/operator/sessions/remove")
    public Element delete(Trace parent,Context context,@QueryParam("token") String token) throws Exception, Throwable
    {
        this.sessionManager.removeSession(parent, token);
        return new Redirect("/operator/sessions");
    }   

    @GET
    @Path("/operator/session")
    public Element getSession(Trace parent,@QueryParam("token") String token) throws Exception, Throwable
    {
        OperatorPage page=this.serverApplication.buildOperatorPage("Session Info");
        SESSION session= this.sessionManager.getSessionByToken(token);
        if (session==null)
        {
            page.content().addInner("Sesson not found");
            return page;
        }
        NameValueList list=page.content().returnAddInner(new NameValueList());
        for (NameObject item:session.getDisplayItems())
        {
            list.add(item.getName(),item.getValue());
        }
        page.content().returnAddInner(new p());
        form_post form=page.content().returnAddInner(new form_post()).action("/operator/session/delete");
        form.returnAddInner(new InputHidden("token",token));
        button_submit button=form.returnAddInner(new button_submit()).addInner("Delete");
        return page;
    }   

    @POST
    @Path("/operator/session/delete")
    public void deleteSession(Trace parent,@QueryParam("token") String token,Context context) throws Exception, Throwable
    {
        this.sessionManager.removeSession(parent, token);
        context.seeOther("/operator/sessions");
    }   

}
