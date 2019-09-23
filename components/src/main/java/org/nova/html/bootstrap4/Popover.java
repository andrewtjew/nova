package org.nova.html.bootstrap4;

import org.nova.html.bootstrap4.classes.Placement;
import org.nova.html.bootstrap4.classes.Trigger;
import org.nova.html.elements.Element;
import org.nova.html.elements.InnerElement;
import org.nova.html.elements.StringComposer;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.script;
import org.nova.json.ObjectMapper;
import org.nova.utils.Utils;

public class Popover
{
    private String title; 
    private String content;
    private String triggers;
    private boolean html=false;
    private Placement placement;
    private String container;
    private String delay;
    
    static class Delay
    {
        public int show;
        public int hide;
    }
    
    public Popover()
    {
    }
    
    public Popover title(String title)
    {
        this.title=title;
        return this;
    }

    public Popover content(String content)
    {
        this.content=content;
        this.html=false;
        return this;
    }

    public Popover content(Element element) throws Throwable
    {
        StringComposer composer=new StringComposer();
        element.compose(composer);
        this.content=composer.getStringBuilder().toString();
        this.html=true;
        return this;
    }
    
    public Popover trigger(Trigger...triggers) throws Exception
    {
        if (triggers.length>1)
        {
            for (Trigger trigger:triggers)
            {
                if (trigger==Trigger.manual)
                {
                    throw new Exception("manual cannot be combined.");
                }
            }
        }
        this.triggers=Utils.combine(triggers, " ");
        return this;
    }
    
    public Popover placement(Placement placement)
    {
        this.placement=placement;
        return this;
    }
    
    public Popover delay(int show,int hide)
    {
        Delay delay=new Delay();
        delay.show=show;
        delay.hide=hide;
        try
        {
            this.delay=ObjectMapper.writeObjectToString(delay);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
        return this;
    }
    
    public void attachTo(TagElement<?> element)
    {
        element.attr("data-toggle","popover");
        if (this.title!=null)
        {
            element.attr("title",this.title);
        }
        if (this.content!=null)
        {
            element.attr("data-content",this.content,'\'');
        }
        if (this.html)
        {
            element.attr("data-html",true);
        }
        if (this.placement!=null)
        {
            element.attr("data-placement",this.placement);
        }
        if (this.delay!=null)
        {
            element.attr("data-delay",this.delay);
        }
        if (this.triggers!=null)
        {
            element.attr("data-trigger",this.triggers);
        }
    }
    public static script readyScript()
    {
        return new script().addInner("$(document).ready(function(){$('[data-toggle=\"popover\"]').popover();});");
    }
    
}