package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputSwitch;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.InputElement;
import org.nova.html.ext.Content;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.tags.div;

public class FormInput<INPUT extends Element> extends StyleComponent<FormInput<INPUT>>
{
    private INPUT input;
    final private Item validationMessage;
    
    public FormInput(BreakPoint breakPoint,Integer columns,String labelText,INPUT input,Element right)
    {
        super("div");
        id();
        
        if (breakPoint != null && columns != null)
        {
            addClass("col", breakPoint, columns);
        }
        else if (columns != null)
        {
            addClass("col", columns);
        }
        else if (breakPoint != null)
        {
            addClass("col", breakPoint);
        }
        
        if (input instanceof InputSwitch)
        {
            if (columns!=null)
            {
                this.align_self(AlignSelf.end).pb(2);
            }
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
        else if (input instanceof InputComponent<?>)
        {
            InputComponent<?> inputComponent=(InputComponent<?>)input;
            Item top=returnAddInner(new Item());
            top.returnAddInner(new Label(labelText)).form_label().for_(inputComponent);
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
                top.returnAddInner(new Label(labelText)).form_label();
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
    
    public FormInput(BreakPoint breakPoint,Integer columns,String labelText,INPUT input)
    {
        this(breakPoint,columns,labelText,input,null);
    }

    public FormInput(String labelText,INPUT input,Element right)
    {
        this(null,null,labelText,input,null);
    }

    public FormInput(String labelText,INPUT input)
    {
        this(labelText,input,null);
    }

    public FormInput(Integer columns,String labelText,INPUT input,Element right)
    {
        this(null,columns,labelText,input,right);
    }
    public FormInput(Integer columns,String labelText,INPUT input)
    {
        this(null,columns,labelText,input,null);
    }

    
    
    public INPUT input()
    {
        return this.input;
    }
    
    public FormInput<INPUT> setInput(INPUT input)
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
