package org.nova.html.bootstrap.ext.input;

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

public class FormSelectValue extends FormInput<SelectValue>
{
    public FormSelectValue(FormCol col, String labelText,String name,String unit,int start,int end,int increment,String none,Integer value,boolean required)
    {
        super(col, labelText, new SelectValue(unit,start,end,increment,none,value),null);
        input().name(name).required(required);
    }
    public FormSelectValue(FormCol col, String labelText,String name,String unit,int start,int end,int increment,String none,Integer value)
    {
        this(col,labelText,name,unit,start,end,increment,none,value,false);
    }
    public FormSelectValue(FormCol col, String labelText,String name,String unit,int start,int end,int increment,String none)
    {
        this(col,labelText,name,unit,start,end,increment,none,null);
    }
    
    
}
