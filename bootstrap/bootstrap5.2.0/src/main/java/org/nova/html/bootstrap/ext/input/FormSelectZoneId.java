package org.nova.html.bootstrap.ext.input;

import java.time.ZoneId;


public class FormSelectZoneId extends FormInput<SelectZoneId>
{
    public FormSelectZoneId(FormCol col, String labelText,String name,ZoneId[] zoneIds,ZoneId value,boolean required)
    {
        super(col, labelText, new SelectZoneId(zoneIds,value), null);
        input().name(name).required(required);
    }
    public FormSelectZoneId(FormCol col, String labelText,String name,ZoneId[] zoneIds,ZoneId value)
    {
        this(col, labelText, name,zoneIds,value,false);
    }
//    public FormSelectZoneId(String labelText,String name,ZoneId[] zoneIds,ZoneId value,boolean required)
//    {
//        this(null,labelText,name,zoneIds,value,required);
//    }
//    public FormSelectZoneId(String labelText,String name,ZoneId[] zoneIds,ZoneId value)
//    {
//        this(labelText, name,zoneIds,value,false);
//    }
    public FormSelectZoneId(FormCol col,String labelText,String name,ZoneId[] zoneIds)
    {
        this(col,labelText,name,zoneIds,null);
    }
    
}
