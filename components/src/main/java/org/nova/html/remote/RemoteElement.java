package org.nova.html.remote;


import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.tags.script;
import org.nova.tracing.Trace;

//Use this to populate dynamic content by calling back to server. The element itself is mostly a place holder.
public abstract class RemoteElement<ELEMENT extends GlobalEventTagElement<ELEMENT>> extends GlobalEventTagElement<ELEMENT>
{
    public RemoteElement(String tag,String id) throws Throwable
    {
        super(tag);
        if (id==null)
        {
            id();
        }
        else
        {
            id(id);
        }
    }
    public RemoteElement(String tag) throws Throwable
    {
        this(tag,null);
    }     
    public RemoteElement<ELEMENT> load(String href,Long interval,Long timeout) throws Throwable
    {
        if (interval!=null)
        {
            returnAddInner(new script()).addInner(new LiteralHtml(Remote.js_getRemote(id(),href)));
            returnAddInner(new script()).addInner(HtmlUtils.js_setInterval(interval, "nova.remote.getRemote",id(),href));
        }
        else if (timeout!=null)
        {
            returnAddInner(new script()).addInner(HtmlUtils.js_setTimeout(timeout, "nova.remote.getRemote",id(),href));
        }
        return this;
    }    
    public RemoteElement<ELEMENT> loadStatic(String href,Long interval,Long timeout) throws Throwable
    {
        if (interval!=null)
        {
            returnAddInner(new script()).addInner(new LiteralHtml(Remote.js_getStatic(href)));
            returnAddInner(new script()).addInner(HtmlUtils.js_setInterval(interval, "nova.remote.getStatic",href));
        }
        else if (timeout!=null)
        {
            returnAddInner(new script()).addInner(HtmlUtils.js_setTimeout(timeout, "nova.remote.getStatic",href));
        }
        else
        {
            returnAddInner(new script()).addInner(new LiteralHtml(Remote.js_getStatic(href)));
        }
        return this;
    }    

    public RemoteResponse render(Trace parent,RemoteResponse response) throws Throwable
    {
        if (response!=null)
        {
            response.outerHtml(this);
        }
        return response;
    }
}
