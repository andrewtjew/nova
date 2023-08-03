package org.nova.html.remote;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.script;
import org.nova.tracing.Trace;
//
// Each RemoteElement must have an id.
// All state for rendering must be set, render() can only use internal data.
// render() should never be called other than in this class.

public abstract class RemoteElement extends Element
{
    final private String id;
    
    protected RemoteElement(String id)
    {
        this.id=id;
    }
    public String id()
    {
        return this.id;
    }
    
    abstract protected TagElement<?> render() throws Throwable;
    
    private void getScripts(TagElement<?> parent,RemoteResponse response)
    {
        if (parent==null)
        {
            return;
        }
        for (Element element:parent.getInners())
        {
            if (element instanceof NodeElement<?>)
            {
                getScripts((TagElement<?>)element,response);
            }
            else if (element instanceof script)
            {
                response.script(((script) element).getScript());
            }
        }
    }
    
    public void compose(Composer composer) throws Throwable
    {
        TagElement<?> element=render();
        if (element!=null)
        {
            element.id(this.id);
            element.compose(composer);
        }
    }

    public RemoteResponse composeRemoteResponse() throws Throwable
    {
        RemoteResponse response=new RemoteResponse();
        composeRemoteResponse(response);
        return response;
    }
    public RemoteResponse composeRemoteResponse(RemoteResponse response) throws Throwable
    {
        TagElement<?> element=render();
        if (element!=null)
        {
            element.id(this.id);
            
            response.outerHtml(this.id, element);
            getScripts(element,response);
        }
        return response;
    }
    
}
