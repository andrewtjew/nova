package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.properties.Size;
import org.nova.html.properties.Unit;

public class ApplyButton extends SpinnerButton 
{
    static class SpinnerLabel extends Item
    {
        public SpinnerLabel(String label)
        {
            addInner(new Icon("check2").me(1));
            addInner(label);
        }
    }

    public ApplyButton(String label)
    {
        super(new SpinnerLabel(label), SpinnerType.border,new Size(label.length()+1,Unit.em));
        color(StyleColor.primary);
    }
    public ApplyButton() 
    {
        this("Apply");
    }
    
}
