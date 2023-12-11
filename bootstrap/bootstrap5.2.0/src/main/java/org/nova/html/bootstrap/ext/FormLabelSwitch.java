package org.nova.html.bootstrap.ext;



import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.Text;

public class FormLabelSwitch extends StyleComponent<FormLabelSwitch>
{
    private InputCheckbox inputCheckbox;
    private Label label;

    public FormLabelSwitch(String label,String name)
    {
        super("div", null);
//        addClass("form-check");
        addClass("form-switch");
        this.inputCheckbox = returnAddInner(new InputCheckbox()).name(name).form_check_input();
        this.label=returnAddInner(new Label(label).text(Text.nowrap)).for_(this.inputCheckbox).form_check_label().ms(2);
    }
    
    public InputCheckbox inputCheckbox()
    {
        return this.inputCheckbox;
    }
    
    public Label label()
    {
        return this.label;
    }
    
}
