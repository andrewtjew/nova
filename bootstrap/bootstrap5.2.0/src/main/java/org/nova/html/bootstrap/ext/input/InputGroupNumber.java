package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputNumber;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Span;
import org.nova.html.elements.Composer;

public class InputGroupNumber extends InputNumber
{
    private String prefix;
    private String postfix;
    
    public InputGroupNumber()
    {
        form_control();
    }
    
    public InputGroupNumber prefix(String prefix)
    {
        this.prefix=prefix;
        return this;
    }
    public InputGroupNumber postfix(String postfix)
    {
        this.postfix=postfix;
        return this;
    }
    
    boolean outer=false;
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (outer==false)
        {
            outer=true;
            Item group=new Item().input_group();
            if (this.prefix!=null)
            {
                group.returnAddInner(new Span()).input_group_text().addInner(this.prefix);
            }
            group.returnAddInner(this);
            if (this.postfix!=null)
            {
                group.returnAddInner(new Span()).input_group_text().addInner(this.postfix);
            }
            
            composer.compose(group);
        }
        else
        {
            outer=false;
            super.compose(composer);
        }
    }    
}
