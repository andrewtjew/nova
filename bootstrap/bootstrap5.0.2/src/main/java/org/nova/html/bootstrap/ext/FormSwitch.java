package org.nova.html.bootstrap.ext;



import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.StyleComponent;

public class FormSwitch extends StyleComponent<FormSwitch>
{
    private InputCheckbox inputCheckbox;
    private Label label;

    public FormSwitch(String label,String name)
    {
        super("div", null);
        addClass("form-check");
        addClass("form-switch");
        this.inputCheckbox = returnAddInner(new InputCheckbox()).name(name).form_check_input();
        this.label=returnAddInner(new Label(label)).for_(this.inputCheckbox).form_check_label();
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
