package org.nova.html.bootstrap.ext;

import org.nova.html.attributes.Size;
import org.nova.html.attributes.unit;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.StyleColor;

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
        super(new SpinnerLabel(label), SpinnerType.border,new Size(label.length()+1,unit.em));
        color(StyleColor.primary);
    }
    public ApplyButton() 
    {
        this("Apply");
    }
    
}
