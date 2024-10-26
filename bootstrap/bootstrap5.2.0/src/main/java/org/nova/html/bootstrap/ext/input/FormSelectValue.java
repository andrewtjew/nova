package org.nova.html.bootstrap.ext.input;

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
