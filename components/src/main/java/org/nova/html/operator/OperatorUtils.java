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
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.ext.Head;
import org.nova.html.tags.p;
import org.nova.html.tags.textarea;
import org.nova.tracing.Trace;
import org.nova.utils.Utils;

public class OperatorUtils
{
    private static TitleText formatNsToMs(long durationNs)
    {
        return new TitleText(Utils.millisToNiceDurationString(durationNs/1000000),String.format("%.3f",durationNs/1.0e6));
    }
    
    public static void writeTraceRowHeading(TableHeader header,boolean excludeWaiting)
    {
        String small="width:8em;";
        String medium="width:12em;";
        
            
//      row.addInner(new td().style("width:5em;").addInner(new TitleText("Trace Number","#")));
        header.add(new th_title("Number","Sequential Trace Number").style(small));
//        header.add("Category");
        header.add(new th_title("Created","When the thread was created").style(medium));
        header.add(new th_title("Active (ms)","Amount of time execution is active (milliseconds)").style(small));
        
        header.add(new th_title("Wait (ms)","Amount of time waiting (milliseconds)").style(small));
        header.add(new th_title("Duration (ms)","Active and wait time (milliseconds)").style(small));
        header.add(new th_title("Status","Execution status").style(small));
        header.add(new th_title("Parent","Parent Trace Number").style(small));
        header.add(new th_title("Root","Root Trace Number").style(small));
        header.add(new TitleText("Thread id and name shown as id:name","Thread"));
//        row.add(new TitleText("Thread details","Details"));
    }
    public static void writeTraceRow(TableRow row,Trace trace,boolean excludeWaiting)
    {
        row.add(trace.getNumber());
        row.add(Utils.millisToLocalDateTime(trace.getCreatedMs()));
//                    .add(new TitleText(trace.getDetails(),60))
        row.add(formatNsToMs(trace.getActiveNs()));
        row.add(formatNsToMs(trace.getWaitNs()));
        row.add(formatNsToMs(trace.getDurationNs()));
        if (trace.isClosed())
        {
            if (trace.getThrowable()==null)
            {
                row.add("Closed");
            }
            else
            {
                row.add("Exception");
            }
        }
        else
        {
            if (trace.isWaiting())
            {
                row.add("Waiting");
            }
            else
            {
                row.add("Running");
            }
        }
        if (trace.getParent()!=null)
        {
            row.add(trace.getParent().getNumber());
        }
        else
        {
            row.add("-");
        }
        if (trace.getRoot()!=null)
        {
            row.add(trace.getRoot().getNumber());
        }
        else
        {
            row.add("-");
        }
        row.add(trace.getThread().getId()+":"+trace.getThread().getName());
    }

    static private Element formatStackTrace(String heading,StackTraceElement[] stackTrace)
    {
        Accordion accordion=new Accordion(false, heading);
        accordion.content().addInner(new textarea().style("width:100%;border:0;").readonly().rows(stackTrace.length+1).addInner(Utils.toString(stackTrace)));
        return accordion;
    }
    static private Element formatThrowable(String heading,Throwable throwable) throws Exception
    {
        Accordion accordion=new Accordion(true, heading);
        String text=Utils.getStrackTraceAsString(throwable);
        int occurs=Utils.occurs(text,"\n");
        accordion.content().addInner(new textarea().style("width:100%;border:0;").readonly().rows(occurs+1).addInner(text));
        return accordion;
    }

    public static void writeTrace(Head head,NodeElement<?> content,Trace trace,boolean includeStackTraces) throws Exception
    {
        Panel3 panel=content.returnAddInner(new Panel3(head,trace.getCategory()));
        Table table=panel.content().returnAddInner(new Table(head));
        table.table().style("width:100%;");
        TableHeader header=new TableHeader();
        writeTraceRowHeading(header,false);
        table.setHeader(header);
    
        TableRow row=new TableRow();
        writeTraceRow(row,trace,false);
        table.addRow(row);

        NameValueList list=null;
        String details = trace.getDetails();
        if (details != null)
        {
            if (list==null)
            {
                list=new NameValueList();
            }
            list.add("Details",details);
        }

        String fromLink = trace.getFromLink();
        if (fromLink != null)
        {
            if (list==null)
            {
                list=new NameValueList();
            }
            list.add("FromLink",fromLink);
        }

        String toLink = trace.getToLink();
        if (toLink != null)
        {
            if (list==null)
            {
                list=new NameValueList();
            }
            list.add("ToLink",toLink);
        }
        if (list!=null)
        {
            Panel4 listPanel=content.returnAddInner(new Panel4(head,"Additional Trace Info"));
            listPanel.content().addInner(list);
        }

        if (trace.getThrowable() != null)
        {
            panel.content().addInner(formatThrowable("Exception Stack Trace" ,trace.getThrowable()));
            panel.content().addInner(new p());
        }
        if (includeStackTraces)
        {
            if (trace.isClosed()==false)
            {
                StackTraceElement[] currentStackTrace = trace.getThread().getStackTrace();
                if (currentStackTrace != null)
                {
                    panel.content().addInner(formatStackTrace("Current Stack Trace",currentStackTrace ));
                }
            }
            StackTraceElement[] createStackTrace = trace.getCreateStackTrace();
            if (createStackTrace != null)
            {
                panel.content().addInner(formatStackTrace("Create Stack Trace",createStackTrace));
            }
            StackTraceElement[] closeStackTrace = trace.getCloseStackTrace();
            if (closeStackTrace != null)
            {
                panel.content().addInner(formatStackTrace("Close Stack Trace",closeStackTrace));
            }
        }
    }

}
