package org.nova.html.bootstrap.remote;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.Styling;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.elements.Element;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.elements.TagElement;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.JsObject;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.remote.Remote;
import org.nova.html.remote.RemoteElement;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.html.remote.RemoteStateElement;
import org.nova.html.tags.div;
import org.nova.html.tags.script;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.FilterChain;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.tracing.Trace;

import org.nova.html.enums.enctype;

@ContentWriters(RemoteResponseWriter.class)
public class RemoteItem extends RemoteElement<RemoteItem> implements Styling<RemoteItem>
{
    public RemoteItem() throws Throwable
    {
        super("div");
    }
    
    @Override
    public TagElement<?> getElement()
    {
        return this;
    }
    
    public RemoteItem load(String href,Long interval,Long timeout,boolean showSpinner,String waitingMessage) throws Throwable
    {
        if ((showSpinner||waitingMessage!=null))
        {
            Item item=new Item();
            item.d(Display.flex).justify_content(Justify.center);
            if (showSpinner)
            {
                item.returnAddInner(new Spinner(SpinnerType.border,BreakPoint.md));
            }
            if (waitingMessage!=null)
            {
                Item messageItem=item.returnAddInner(new Item()).addInner(waitingMessage);
                if (showSpinner)
                {
                    messageItem.me(1);
                }
            }
            if (showSpinner||(waitingMessage!=null))
            {
                this.addInner(item);
            }
        }
        return (RemoteItem) super.load(href, interval, timeout);
    }    
    public RemoteItem load(String href) throws Throwable
    {
        return load(href,null,null,true,null);
    }    

    
}