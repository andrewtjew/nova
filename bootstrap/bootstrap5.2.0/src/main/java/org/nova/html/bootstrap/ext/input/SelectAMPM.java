package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Select;
import org.nova.html.bootstrap.localization.AMPM;
import org.nova.html.tags.option;

public class SelectAMPM extends Select
{
    public SelectAMPM(AMPM value)
    {
        set(value);
    }
    public SelectAMPM()
    {
        this(null);
    }
    public SelectAMPM set(AMPM value)
    {
        clearInners();
        for (AMPM item:AMPM.values())
        {
            option option=returnAddInner(new option()).value(item.name());
            option.addInner(item.name());
            option.selected(item==value);
        }    
        return this;
    }
}
