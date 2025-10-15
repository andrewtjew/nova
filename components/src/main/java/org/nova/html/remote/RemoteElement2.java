package org.nova.html.remote;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.StringComposer;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.script;

public abstract class RemoteElement2 extends Element
{
    final private String id;
    private TagElement<?> element;
    
    protected RemoteElement2(String id)
    {
        this.id=id!=null?id:"_"+this.hashCode();
        
    }
    protected RemoteElement2()
    {
        this(null);
    }
    public String id()
    {
        return this.id;
    }
    
    abstract protected TagElement<?> render() throws Throwable;
    
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
            else if (element instanceof RemoteElement2)
            {
                RemoteElement2 remoteElement=(RemoteElement2)element;
                addScripts(remoteElement.build(),response);
            }
        }
    }

    public TagElement<?> build() throws Throwable
    {
        
        if (this.element!=null)
        {
            return this.element;
        }
        this.element=render();
        if (element!=null)
        {
            this.element.id(this.id);
        }
        return this.element;
    }
    
    public void compose(Composer composer) throws Throwable
    {
        Element element=build();
        if (element!=null)
        {
            element.compose(composer);
        }
    }
    public RemoteResponse respond() throws Throwable
    {
        RemoteResponse response=new RemoteResponse();
        respond(response);
        return response;
    }
    public RemoteResponse respond(RemoteResponse response) throws Throwable
    {
        this.element=render();
        if (element!=null)
        {
            element.id(this.id);
            
            response.outerHtml(this.id, element);
            addScripts(element,response);
        }
        return response;
    }
    
}
