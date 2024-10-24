package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputNumber;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.elements.Composer;
import org.nova.localization.CurrencyCode;

public class InputCurrencyAmount extends InputNumber
{
    final private CurrencyCode currencyCode;
    public InputCurrencyAmount(CurrencyCode currencyCode)
    {
        this.currencyCode=currencyCode;
        double step=1.0/Math.pow(10,currencyCode.getValue().digits);
        form_control().step(step);
    }
    
    boolean outer=false;
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (outer==false)
        {
            outer=true;
            Item group=new Item().input_group().d(Display.flex);
            group.returnAddInner(new Span()).input_group_text().addInner(this.currencyCode.getValue().symbol);
            group.returnAddInner(this);
            composer.compose(group);
        }
        else
        {
            outer=false;
            super.compose(composer);
        }
    }    
}
