package org.nova.html.bootstrap.ext.input;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import org.nova.html.bootstrap.Select;
import org.nova.html.tags.option;


public class SelectZoneId extends Select
{
    final private ZoneId[] zoneIds;
    public SelectZoneId(ZoneId[] zoneIds,ZoneId value)
    {
        this.zoneIds=zoneIds;
        set(value);
    }
    public SelectZoneId(ZoneId[] zoneIds)
    {
        this(zoneIds,null);
    }
    public SelectZoneId set(ZoneId value)
    {
        clearInners();
        option prompt=returnAddInner(new option()).selected().value("").addInner("Select");
        if (this.isRequired())
        {
            prompt.disabled();
        }
        Instant now=Instant.now();
        for (ZoneId item:this.zoneIds)
        {
            option option=returnAddInner(new option()).value(item.getId());
            String offset=item.getRules().getOffset(now).getId();
            if ("Z".equals(offset))
            {
                offset="+00:00";
            }
            option.addInner("UTC"+offset+" "+item+" ("+item.getDisplayName(TextStyle.FULL, Locale.US)+")");
            option.selected(value!=null&&value.getId().equals(item.getId()));
        } 
        return this;
    }
    
}
