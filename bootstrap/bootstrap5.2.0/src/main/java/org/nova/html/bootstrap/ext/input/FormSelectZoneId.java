package org.nova.html.bootstrap.ext.input;

import java.time.ZoneId;

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputSwitch;
import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.InputElement;
import org.nova.html.ext.Content;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.div;
import org.nova.localization.CountryCode;


public class FormSelectZoneId extends FormInput<SelectZoneId>
{
    public FormSelectZoneId(Integer columns, String labelText,String name,ZoneId[] zoneIds,ZoneId value,boolean required)
    {
        super(null, columns, labelText, new SelectZoneId(zoneIds,value), null);
        input().name(name).required(required);
    }
    public FormSelectZoneId(Integer columns, String labelText,String name,ZoneId[] zoneIds,ZoneId value)
    {
        this(columns, labelText, name,zoneIds,value,false);
    }
    public FormSelectZoneId(String labelText,String name,ZoneId[] zoneIds,ZoneId value,boolean required)
    {
        this(null,labelText,name,zoneIds,value,required);
    }
    public FormSelectZoneId(String labelText,String name,ZoneId[] zoneIds,ZoneId value)
    {
        this(labelText, name,zoneIds,value,false);
    }
    public FormSelectZoneId(String labelText,String name,ZoneId[] zoneIds)
    {
        this(labelText,name,zoneIds,null);
    }
    
}
