package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputSwitch;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.AlignItems;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.InputType;
import org.nova.html.ext.LiteralHtml;

public class FormComponent<ELEMENT extends InputComponent<ELEMENT>> extends InputComponent<ELEMENT>
{
    public FormComponent(FormCol formCol)
    {
        super(InputType.button,"aa");
        id();
        if (formCol!=null)
        {
            if (formCol.breakPoint != null)
            {
                if (formCol.columns>0)
                {
                    addClass("col", formCol.breakPoint, formCol.columns);
                }
                else if (formCol.auto)
                {
                    addClass("col", formCol.breakPoint, "auto");
                }
                else
                {
                    addClass("col", formCol.breakPoint);
                }
            }
            else 
            {
                if (formCol.columns>0)
                {
                    addClass("col", formCol.columns);
                }
                else if (formCol.auto)
                {
                    addClass("col", "auto");
                }
                else
                {
                    addClass("col");
                }
            }
        }
    }
    
}
