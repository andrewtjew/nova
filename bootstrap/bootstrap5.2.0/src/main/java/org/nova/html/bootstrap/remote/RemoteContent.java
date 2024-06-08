package org.nova.html.bootstrap.remote;


import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.elements.Composer;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.remote.Remote;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.div;
import org.nova.html.tags.script;

//Use this to populate content by calling back to server.
public class RemoteContent<ELEMENT extends GlobalEventTagElement<ELEMENT>> extends GlobalEventTagElement<ELEMENT>
{
    public RemoteContent(String tag,String id) throws Throwable
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
    public RemoteContent(String tag) throws Throwable
    {
        this(tag,null);
    }        
    public RemoteContent<ELEMENT> load(String href,Long interval,Long timeout) throws Throwable
    {
        if (interval!=null)
        {
            returnAddInner(new script()).addInner(HtmlUtils.js_setInterval(timeout, "nova.remote.getRemote",href));
        }
        else if (timeout!=null)
        {
            returnAddInner(new script()).addInner(HtmlUtils.js_setTimeout(timeout, "nova.remote.getRemote",href));
        }
        else
        {
            returnAddInner(new script()).addInner(new LiteralHtml(Remote.js_getRemote(id(),href)));
        }
        return this;
    }    
    
    public void addScript(RemoteResponse response,String js_script)
    {
        if (response!=null)
        {
            response.script(js_script);
        }
        else
        {
            returnAddInner(new script()).addInner(new LiteralHtml(js_script));
        }
    }
    public RemoteResponse render(RemoteResponse response) throws Throwable
    {
        if (response!=null)
        {
            response.outerHtml(this);
        }
        return response;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        render(null);
        super.compose(composer);
    }
}
