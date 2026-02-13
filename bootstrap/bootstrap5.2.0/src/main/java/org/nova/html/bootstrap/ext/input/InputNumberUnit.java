package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputNumber;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.elements.Composer;

public class InputNumberUnit extends InputNumber
{
    final private String unit;
    public InputNumberUnit(String unit)
    {
        this.unit=unit;
        form_control();
    }
    
    boolean outer=false;
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (outer==false)
        {
            outer=true;
            Item group=new Item().input_group().d(Display.flex);
            group.returnAddInner(this);
            group.returnAddInner(new Span()).input_group_text().addInner(unit);
            composer.compose(group);
        }
        else
        {
            outer=false;
            super.compose(composer);
        }
    }    
}
