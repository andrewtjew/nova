package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;

public class FloatingLabel extends Item
{
    final private String label;
    public FloatingLabel(String label)
    {
        addClass("form-floating");
        this.label=label;
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        Label label=returnAddInner(new Label(this.label));
        for (Element element:this.getInners())
        {
            if (element instanceof InputComponent<?>)
            {
                InputComponent<?>input=(InputComponent<?>)element;
                input.attr("placeholder",".");
                label.for_(input);
                break;
            }
        }
        super.compose(composer);
    }
}