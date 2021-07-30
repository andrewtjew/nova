package org.nova.html.bootstrap;

import org.nova.html.bootstrap.classes.BreakPoint;

public class Spinner extends StyleComponent<Spinner>
{
    public Spinner(String tag,SpinnerType type,BreakPoint deviceClass)
    {
        super(tag,null);
        addClass("spinner",type,deviceClass);
        attr("role","status");
    }
    public Spinner(BreakPoint deviceClass,SpinnerType type)
    {
        this("div",type,deviceClass);
    }
    public Spinner(SpinnerType type)
    {
        this("div",type,null);
    }
    public Spinner()
    {
        this("div",SpinnerType.border,null);
    }
    
}
