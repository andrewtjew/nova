package org.nova.html.remote;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.StringComposer;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.script;

public abstract class RemoteElement extends Element
{
    final private String id;
    private TagElement<?> element;
    protected RemoteElement(String id)
    {
        this.id=id;
    }
    public String id()
    {
        return this.id;
    }
    
    abstract protected TagElement<?> refresh() throws Throwable;
    
    private void addScripts(NodeElement<?> parent,RemoteResponse response) throws Throwable
    {
        if (parent==null)
        {
            return;
        }
        for (Element element:parent.getInners())
        {
            if (element instanceof script)
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
            else if (element instanceof NodeElement<?>)
            {
                addScripts((NodeElement<?>)element,response);
            }
            else if (element instanceof RemoteElement)
            {
                RemoteElement remoteElement=(RemoteElement)element;
                addScripts(remoteElement.element,response);
            }
        }
    }
    protected Element setup() throws Throwable
    {
        this.element=refresh();
        if (element!=null)
        {
            this.element.id(this.id);
        }
        return this.element;
    }
    
    public void compose(Composer composer) throws Throwable
    {
        Element element=setup();
        if (element!=null)
        {
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
        this.element=refresh();
        if (element!=null)
        {
            element.id(this.id);
            
            response.outerHtml(this.id, element);
            addScripts(element,response);
        }
        return response;
    }
    
}
