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
package org.nova.html.operator;

import org.nova.html.deprecated.th_title;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.ext.Head;
import org.nova.html.tags.p;
import org.nova.html.tags.textarea;
import org.nova.http.server.RequestLogEntry;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

public class HttpRequestWidget extends Element
{
    final private Panel2 panel;
    public HttpRequestWidget(Head head,RequestLogEntry entry)
    {
        String title;
        if (entry.getQueryString()!=null)
        {
            title=entry.getRequest()+"?"+entry.getQueryString();
        }
        else
        {
            title=entry.getRequest();
        }
        this.panel=new Panel2(head, title);
//        panel.style("width:100%;");
        Table table=this.panel.content().returnAddInner(new Table(head));
        table.table().style("width:100%;");
        TableHeader header=new TableHeader();
        String tiny="width:3em;";
        String small="width:8em;";
        String medium="width:12em;";
        String large="width:16em;";
        header.add(new th_title("Status","Http Status Code").style(tiny));
        header.add(new th_title("Remote","Remote end point").style(medium));
        header.add(new th_title("Created","When the thread was created").style(medium));
        header.add(new th_title("Active (ms)","Amount of time execution is active (ms)").style(small));
        header.add(new th_title("Wait (ms)","Amount of time waiting (ms)").style(small));
        header.add(new th_title("Duration (ms)","Active and wait time (ms)").style(small));
        header.add(new th_title("Thread","Thread id and name shown as id:name").style(small));
//        header.add(new TitleText("Thread id and name shown as id:name","Thread"));
        table.setHeader(header);


        Trace trace=entry.getTrace();
        
        TableRow row=new TableRow();
        row.add(entry.getStatusCode());
        row.add(entry.getRemoteEndPoint());
        row.add(Utils.millisToLocalDateTime(trace.getCreatedMs()));
        row.add(formatNsToMs(trace.getActiveNs()));
        row.add(formatNsToMs(trace.getWaitNs()));
        row.add(formatNsToMs(trace.getDurationNs()));
        row.add(trace.getThread().getId()+":"+trace.getThread().getName());
        table.addRow(row);

        String fromLink = trace.getFromLink();
        if (fromLink != null)
        {
            NameValueList list=new NameValueList();
            list.add("FromLink",fromLink);
        }
        
        writeHeaders(head,"Request Headers",panel.content(),entry.getRequestHeaders());
        writeHeaders(head,"Request Parameters",panel.content(),entry.getRequestParameters());
        writeContent(head,"Request Content",panel.content(),entry.getRequestContentText(),null);
        writeHeaders(head,"Response Headers",panel.content(),entry.getResponseHeaders());
        writeContent(head,"Response Content",panel.content(),entry.getResponseContentText(),entry.getContentType());
        
    
        if (trace.getThrowable() != null)
        {
            panel.content().addInner(formatThrowable(head, "Exception: "+trace.getThrowable().getMessage() ,trace.getThrowable(),false));
            panel.content().addInner(new p());
        }
    }
    
    
    
    private static TitleText formatNsToMs(long durationNs)
    {
        return new TitleText(Utils.millisToNiceDurationString(durationNs/1000000),String.format("%.3f",durationNs/1.0e6));
    }
    
    private void writeContent(Head head,String heading,NodeElement<?> content,String text,String contentType)
    {
        if (text==null)
        {
            return;
        }
        Accordion textAccodion=content.returnAddInner(new Accordion(head,false,heading+", length: "+text.length()));
        int rows=Utils.occurs(text, "\n")+3;
        if (rows>20)
        {
            rows=20;
        }
//        if ((contentType!=null)&&(contentType.toLowerCase().contains("/html")))
//        {
//            text=HtmlUtils.toHtmlText(text);
//        }
        textAccodion.content().addInner(new textarea().readonly().style("width:100%;resize:vertical;").addInner(text).rows(rows));
    }

    private void writeHeaders(Head head,String heading,NodeElement<?> content,String headers)
    {
        if (headers==null)
        {
            return;
        }
//        String[] array=Utils.split(headers, '\r');
        String[] array=headers.split("\r\n");
        NameValueList list=new NameValueList();
        int lines=0;
        for (String item:array)
        {
            int index=item.indexOf(':');
            if (index<0)
            {
                list.add(item+"*","");
                continue;
            }
            String name=item.substring(0, index);
            String value=item.substring(index+1);
            list.add(name,value);
            lines++;
        }
        if (lines==0)
        {
            return;
        }
        Accordion accordion=content.returnAddInner(new Accordion(head,false, heading+", length: "+(array.length-1)));
        accordion.content().addInner(list);
        
    }    

    static private Element formatStackTrace(Head head,String heading,StackTraceElement[] stackTrace,boolean open)
    {
        Accordion accordion=new Accordion(head, false, heading);
        accordion.content().addInner(new textarea().style("width:100%;border:0;resize:vertical;").readonly().rows(stackTrace.length+1).addInner(Utils.toString(stackTrace)));
        return accordion;
    }
    static private Element formatThrowable(Head head,String heading,Throwable throwable,boolean open)
    {
        Accordion accordion=new Accordion(head, open, heading);
        String text=Utils.getStrackTraceAsString(throwable);
        int occurs=Utils.occurs(text,"\n");
        accordion.content().addInner(new textarea().style("width:100%;border:0;resize:vertical;").readonly().rows(occurs+1).addInner(text));
        return accordion;
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.compose(this.panel);
    }
    
    

}
