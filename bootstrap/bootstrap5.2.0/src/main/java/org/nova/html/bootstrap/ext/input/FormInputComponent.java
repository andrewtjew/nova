package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputRadio;
import org.nova.html.bootstrap.InputRange;
import org.nova.html.bootstrap.InputSwitch;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;

public class FormInputComponent<INPUT extends Element> extends StyleComponent<FormInputComponent<INPUT>>
{
    private INPUT input;
    final private Item validationMessage;
    
    public FormInputComponent(FormCol formCol,String labelText,INPUT input,Element right)
    {
        super("div");
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
        
        if (input instanceof InputSwitch)
        {
            this.align_self(AlignSelf.end).pb(2);
            InputSwitch inputSwitch=(InputSwitch)input;
            Item row=returnAddInner(new Item()).d(Display.flex);
            if (right!=null)
            {
                row.justify_content(Justify.between);
                Item left=row.returnAddInner(new Item());
                this.input=left.returnAddInner(input);
                left.returnAddInner(new Label(labelText).text(Text.nowrap)).for_(inputSwitch).form_check_label().ps(2);
                row.returnAddInner(right);
            }
            else
            {
                this.input=row.returnAddInner(input);
                row.returnAddInner(new Label(labelText).text(Text.nowrap)).for_(inputSwitch).form_check_label().ps(2);
            }
        }
        else if (input instanceof InputCheckbox)
        {
            this.align_self(AlignSelf.end).pb(2);
            InputCheckbox inputCheckbox=(InputCheckbox)input;
            Item row=returnAddInner(new Item()).d(Display.flex);
            if (right!=null)
            {
                row.justify_content(Justify.between);
                Item left=row.returnAddInner(new Item());
                this.input=left.returnAddInner(input);
                left.returnAddInner(new Label(labelText).text(Text.nowrap)).for_(inputCheckbox).form_check_label().ps(2);
                row.returnAddInner(right);
            }
            else
            {
                this.input=row.returnAddInner(input);
                row.returnAddInner(new Label(labelText).text(Text.nowrap)).for_(inputCheckbox).form_check_label().ps(2);
            }
        }
        else if (input instanceof InputRadio)
        {
            this.align_self(AlignSelf.end).pb(2);
            InputRadio inputRadio=(InputRadio)input;
            Item row=returnAddInner(new Item()).d(Display.flex);
            if (right!=null)
            {
                row.justify_content(Justify.between);
                Item left=row.returnAddInner(new Item());
                this.input=left.returnAddInner(input);
                left.returnAddInner(new Label(labelText).text(Text.nowrap)).for_(inputRadio).form_check_label().ps(2);
                row.returnAddInner(right);
            }
            else
            {
                this.input=row.returnAddInner(input);
                row.returnAddInner(new Label(labelText).text(Text.nowrap)).for_(inputRadio).form_check_label().ps(2);
            }
        }
        else if (input instanceof InputRange)
        {
            Item top=returnAddInner(new Item());
            top.returnAddInner(new Label(labelText));//.form_label().for_(inputComponent);
            if (right!=null)
            {
                top.justify_content(Justify.between).d(Display.flex);
                top.returnAddInner(right);
            }
            this.input=returnAddInner(input);
        }
        else if (input instanceof InputComponent<?>)
        {
            InputComponent<?> inputComponent=(InputComponent<?>)input;
            Item top=returnAddInner(new Item());
            top.returnAddInner(new Label(labelText));//.form_label().for_(inputComponent);
            if (right!=null)
            {
                top.justify_content(Justify.between).d(Display.flex);
                top.returnAddInner(right);
            }
            inputComponent.form_control();
            this.input=returnAddInner(input);
        }
        else
        {
            Item top=returnAddInner(new Item());
            if (labelText!=null)
            {
                top.returnAddInner(new Label(labelText));
            }
            else
            {
                this.align_self(AlignSelf.end);
            }
            if (right!=null)
            {
                top.justify_content(Justify.between).d(Display.flex);
                top.returnAddInner(right);
            }
            this.input=returnAddInner(input);
            
        }
        d(Display.block);
        this.validationMessage=returnAddInner(new Item());
        this.validationMessage.w(100).px(1).id();
        mb(3);
    }
    
    public FormInputComponent(FormCol col,String labelText,INPUT input)
    {
        this(col,labelText,input,null);
    }

    public INPUT input()
    {
        return this.input;
    }
    
    public FormInputComponent<INPUT> setInput(INPUT input)
    {
        this.input=input;
        return this;
    }

    public void setValidationErrorMessage(String message)
    {
        this.validationMessage.clearInners();
        this.validationMessage.returnAddInner(new Item()).addInner(message).w(100).bg(StyleColor.warning).px(2);
    }
    @Override
    public void compose(Composer composer) throws Throwable
    {
        super.compose(composer);
        this.validationMessage.clearInners();
    }            
    
}
