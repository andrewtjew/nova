package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.ButtonComponent;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.ext.HtmlUtils;

public class SpinnerButton extends ButtonComponent<SpinnerButton>
{
    final private Spinner spinner;
    final private Span labelSpan;
    final private Span spinnerSpan;
    public SpinnerButton(String label,SpinnerType type)
    { 
        super("button");
        attr("type","button");
        this.labelSpan=returnAddInner(new Span()).addInner(label);
        this.spinnerSpan=returnAddInner(new Span());
        this.spinnerSpan.style("display:none;");
        this.spinner=this.spinnerSpan.returnAddInner(new Spinner(type,BreakPoint.sm));
    }
    public SpinnerButton(String label)
    {
        this(label,SpinnerType.border);
    }
    public SpinnerButton onclick(String script)
    {
        super.onclick(
                HtmlUtils.js_setElementProperty(this.spinnerSpan.id(), "style.display","block")+";"
                +HtmlUtils.js_setElementProperty(this.labelSpan.id(), "style.display","none")+";"
                        +script
                );
        return this;
    }    

    
}
