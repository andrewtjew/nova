package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.elements.FormElement;
import org.nova.html.ext.HtmlUtils;

public class SpinnerButton extends ButtonComponent<SpinnerButton>
{
    final private Spinner spinner;
    final private Span labelSpan;
//    final private Span spinnerSpan;
    public SpinnerButton(String label,SpinnerType type)
    { 
        super("button");
//        d(Display.flex);
        attr("type","button");
        this.spinner=returnAddInner(new Spinner("span", type, BreakPoint.sm));
        this.spinner.style("display:none;");
        this.labelSpan=returnAddInner(new Span()).addInner(label);
//        addInner("label");
    }
    public SpinnerButton(String label)
    {
        this(label,SpinnerType.border);
    }
    public SpinnerButton onclick(String script)
    {
        super.onclick(
                HtmlUtils.js_setElementProperty(this.spinner.id(), "style.display","inline-block")+";"+
        HtmlUtils.js_setElementProperty(this.labelSpan.id(), "style.display","none")+";"+
                        script
                );
        return this;
    }    

    public SpinnerButton submit(FormElement<?> form)
    {
        this.onclick(HtmlUtils.js_submit(form));
        form.onsubmit(                HtmlUtils.js_setElementProperty(this.spinner.id(), "style.display","inline-block")+";"+
                HtmlUtils.js_setElementProperty(this.labelSpan.id(), "style.display","none")+";");
        return this;
    }
    
}
