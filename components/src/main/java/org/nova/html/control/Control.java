package org.nova.html.control;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.TagElement;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.script;
import org.nova.tracing.Trace;

public abstract class Control 
{
    final private String id;
    private Deque<Control> childControls;
    private Control parent;
    
    protected Control(String id,Control parent)
    {
        this.parent=parent;
        this.id=id;
    }
    protected Control(String id)
    {
        this(id,null);
    }
    protected Control(Control parent)
    {
        this();
        this.parent=parent;
    }
    protected Control()
    {
        this.id="_"+hashCode();
    }
    
    private Deque<Control> getChildControls()
    {
        if (this.childControls==null)
        {
            this.childControls=new LinkedList<Control>();
        }
        return this.childControls;
    }
    
    public void addFirstChild(Control control)
    {
        this.getChildControls().addFirst(control);
    }
    public void addLastChild(Control control)
    {
        this.getChildControls().addLast(control);
    }
    public void removeChild(Control control)
    {
        this.getChildControls().remove(control);
    }
    public void removeFirstChild()
    {
        this.getChildControls().removeFirst();
    }
    public void removeLastChild()
    {
        this.getChildControls().removeFirst();
    }
    public Control getParent()
    {
        return this.parent;
    }
    
    public Control getRootControl()
    {
        Control root=this;
        while (root.getParent()!=null)
        {
            root=root.getParent();
        }
        return root;
    }
    
    public RemoteResponse composeRemoteResponse(Trace parent,RenderContext context) throws Throwable
    {
        RemoteResponse response=new RemoteResponse();
        composeRemoteResponse(parent,context,response);
        return response;
    }
    
    public boolean composeRemoteResponse(Trace parent,RenderContext context,RemoteResponse response) throws Throwable
    {
        if (this.childControls!=null)
        {
            for (Control childControl:this.childControls)
            {
                childControl.composeRemoteResponse(parent, context, response);
            }
        }
        RenderResult renderResult=this.render(parent,context);
        if (renderResult!=null)
        {
            renderResult.tagElement.id(this.id);
            response.outerHtml(this.id, renderResult.tagElement);
            if (renderResult.script!=null)
            {
                response.script(renderResult.script);
            }
        }
        return true;
    }
    
    public String id()
    {
        return this.id;
    }


    public TagElement<?> compose(Trace parent,RenderContext context) throws Throwable
    {
        RenderResult renderResult=this.render(parent,context);
        renderResult.tagElement.id(this.id);
        if(renderResult.script!=null)
        {
            renderResult.tagElement.returnAddInner(new script()).addInner(renderResult.script);
        }
        return renderResult.tagElement;
    }
    
    public abstract RenderResult render(Trace parent,RenderContext context) throws Throwable;
}
