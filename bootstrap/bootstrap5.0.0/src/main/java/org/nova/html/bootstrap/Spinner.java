package org.nova.html.bootstrap;

import org.nova.html.bootstrap.classes.BreakPoint;

public class Spinner extends StyleComponent<Spinner>
{
    public Spinner(String tag,SpinnerType type,BreakPoint breakPoint)
    {
        super(tag,null);
        addClass("spinner",type);
        if (breakPoint!=null)
        {
            addClass("spinner",type,breakPoint);
        }
        attr("role","status");
    }
    public Spinner(SpinnerType type,BreakPoint breakPoint)
    {
        this("div",type,breakPoint);
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
