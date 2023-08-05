package org.nova.html.remote;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.StringComposer;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.script;
import org.nova.tracing.Trace;
//

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
    
    private void getScripts(NodeElement<?> parent,RemoteResponse response) throws Throwable
    {
        if (parent==null)
        {
            return;
        }
        for (Element element:parent.getInners())
        {
            if (element instanceof NodeElement<?>)
            {
                getScripts((NodeElement<?>)element,response);
            }
            else if (element instanceof script)
            {
                script script=(script)element;
                StringComposer composer=new StringComposer();
                for (Element inner:script.getInners())
                {
                    inner.compose(composer);
                }
                String scriptText=composer.getStringBuilder().toString();
                response.script(scriptText);
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
