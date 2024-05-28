package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputNumber;
import org.nova.html.bootstrap.InputPassword;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.ext.Content;
import org.nova.html.ext.HtmlUtils;
import org.nova.localization.CurrencyCode;

public class InputNumberGroup extends InputNumber
{
    private String prefix;
    private String postfix;
    
    public InputNumberGroup()
    {
        form_control();
    }
    
    public InputNumberGroup prefix(String prefix)
    {
        this.prefix=prefix;
        return this;
    }
    public InputNumberGroup postfix(String postfix)
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
