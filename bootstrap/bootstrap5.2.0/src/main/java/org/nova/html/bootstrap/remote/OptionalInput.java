package org.nova.html.bootstrap.remote;

import java.time.LocalDate;

import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.InputComponent;
import org.nova.html.bootstrap.InputSwitch;
import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Select;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.option;
import org.nova.localization.CountryCode;
import org.nova.localization.CurrencyCode;
import org.nova.utils.TypeUtils;

public class OptionalInput<INPUT extends InputComponent<?>> extends Item
{
    final private INPUT input;

    public OptionalInput(String checkboxText,String namePrefix,INPUT input,boolean checked,boolean reversed)
    {
        d(Display.flex);
        this.input=input;
        Item checkboxContainer=returnAddInner(new Item()).form_check().pt(2).me(2);
        InputCheckbox inputCheckbox=checkboxContainer.returnAddInner(new InputCheckbox()).form_check_input().name(namePrefix+"-check").checked(checked);
        Label checkboxLabel=checkboxContainer.returnAddInner(new Label(checkboxText)).form_check_label().text(Text.nowrap);
        checkboxLabel.for_(inputCheckbox);
        
        Item inputTextContainer=returnAddInner(new Item()).flex(Flex.grow, 1);
        inputTextContainer.returnAddInner(input);
        input.form_control().name(namePrefix);

        inputCheckbox.onclick(HtmlUtils.js_call("nova.ui.toggleOptionalTextInput", inputCheckbox.id(),input.id(),reversed));

        input.disabled(checked!=reversed);
    }
    public INPUT input()
    {
        return this.input;
    }
}
