package org.nova.html.bootstrap.ext;

import org.nova.html.attributes.Size;
import org.nova.html.attributes.unit;
import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.elements.Element;
import org.nova.html.elements.FormElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.Text;

public class SpinnerButton extends ButtonComponent<SpinnerButton>
{
    final private Spinner spinner;
    final private Span labelSpan;

    public SpinnerButton(Element labelElement,SpinnerType type,Size width)
    { 
        super("button");
        id();
        attr("type","button");
        this.spinner=returnAddInner(new Spinner("span", type, BreakPoint.sm));
        this.spinner.id();
        this.spinner.style("display:none;");
        if (width!=null)
        {
            style("width:"+width.value()+width.unit()+";");
        }
        this.labelSpan=returnAddInner(new Span()).addInner(labelElement);
        this.labelSpan.id();
    }
    public SpinnerButton(String label,SpinnerType type)
    {
        this(new Text(label),type,new Size(label.length()+1,unit.em));
    }
    public SpinnerButton(String label)
    {
        this(label,SpinnerType.border);
    }
    public SpinnerButton onclick(String script)
    {
        super.onclick(
                HtmlUtils.js_classList_add(id(), "disabled")+";"
                +HtmlUtils.js_property(this.spinner.id(), "style.display","inline-block")+";"
                +HtmlUtils.js_property(this.labelSpan.id(), "style.display","none")+";"+
                        script
                );
        return this;
    }    

    public String js_reset()
    {
        return HtmlUtils.js_property(this.spinner.id(), "style.display","none")+";"
                +HtmlUtils.js_property(this.labelSpan.id(), "style.display","block")+";"
                +HtmlUtils.js_classList_remove(id(), "disabled")+";"
                ;
    }    

    public SpinnerButton submit(FormElement<?> form)
    {
        this.onclick(HtmlUtils.js_submit(form));
        form.onsubmit(HtmlUtils.js_property(this.spinner.id(), "style.display","inline-block")+";"+
                HtmlUtils.js_property(this.labelSpan.id(), "style.display","none")+";");
        return this;
    }
    
}
