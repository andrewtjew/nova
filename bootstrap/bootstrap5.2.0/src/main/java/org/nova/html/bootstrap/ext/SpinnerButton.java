package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.elements.FormElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;

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
        this.spinner.addInner(new LiteralHtml("&nbsp;")); //to make button height the same when the label is replaced by the spinner.
    }
    public SpinnerButton(String label)
    {
        this(label,SpinnerType.border);
    }
    public SpinnerButton onclick(String script)
    {
        super.onclick(
                HtmlUtils.js_property(this.spinner.id(), "style.display","inline-block")+";"+
        HtmlUtils.js_property(this.labelSpan.id(), "style.display","none")+";"+
                        script
                );
        return this;
    }    

    public String js_hideSpinner()
    {
        return HtmlUtils.js_property(this.spinner.id(), "style.display","none")+";"+
                HtmlUtils.js_property(this.labelSpan.id(), "style.display","block")+";";
    }    

    public SpinnerButton submit(FormElement<?> form)
    {
        this.onclick(HtmlUtils.js_submit(form));
        form.onsubmit(HtmlUtils.js_property(this.spinner.id(), "style.display","inline-block")+";"+
                HtmlUtils.js_property(this.labelSpan.id(), "style.display","none")+";");
        return this;
    }
    
}
