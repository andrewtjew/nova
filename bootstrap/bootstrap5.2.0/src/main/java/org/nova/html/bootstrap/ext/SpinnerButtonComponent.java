package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.HtmlUtils;

public class SpinnerButtonComponent <ELEMENT extends SpinnerButtonComponent<ELEMENT>> extends ButtonComponent<ELEMENT>
{
    final private Item spinnerItem;
    final private TagElement<?> element;

    public SpinnerButtonComponent(TagElement<?> element,SpinnerType type)
    { 
        super("button");
        id();
        attr("type","button");
        position(Position.relative);
        this.spinnerItem=returnAddInner(new Item());
        this.spinnerItem.id();
        this.spinnerItem.style("display:none;position:absolute;top:50%;left:50%;transform: translate(-45%, -50%);");
        spinnerItem.returnAddInner(new Spinner("span", type, BreakPoint.sm));
        returnAddInner(element);
        element.id();
        this.element=element;
    }

    public ELEMENT onclick(String script)
    {
        super.onclick(
                HtmlUtils.js_classList_add(id(), "disabled")+";"
                +HtmlUtils.js_property(this.spinnerItem.id(), "style.display","inline-block")+";"
//                        +HtmlUtils.js_property(this.element.id(), "style.display","none")+";"+
                        +HtmlUtils.js_property(this.element.id(), "style.visibility","hidden")+";"+
                        script
                );
        return (ELEMENT)this;
    }    

    public String js_reset()
    {
        return HtmlUtils.js_property(this.spinnerItem.id(), "style.display","none")+";"
                +HtmlUtils.js_property(this.element.id(), "style.display","block")+";"
                +HtmlUtils.js_classList_remove(id(), "disabled")+";"
                ;
    }    

    public ELEMENT submit(FormElement<?> form)
    {
        this.onclick(HtmlUtils.js_submit(form));
        form.onsubmit(HtmlUtils.js_property(this.spinnerItem.id(), "style.display","inline-block")+";"+
                HtmlUtils.js_property(this.element.id(), "style.display","none")+";");
        return (ELEMENT)this;
    }
    
}
