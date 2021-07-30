package org.nova.html.bootstrap;

import org.nova.html.bootstrap.classes.DeviceClass;

public class Spinner extends StyleComponent<Spinner>
{
    public Spinner(String tag,SpinnerType type,DeviceClass deviceClass)
    {
        super(tag,null);
        addClass("spinner",type,deviceClass);
        attr("role","status");
    }
    public Spinner(DeviceClass deviceClass,SpinnerType type)
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
