package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.remote.Inputs;

public class Switch extends StyleComponent<Switch>
{
    final private Inputs inputs;
    final private InputCheckbox inputCheckBox;
    
    public Switch(String name,boolean checked,String action) throws Throwable
    {
      super("div",null);
      addClass("form-check");
      addClass("form-switch");
      this.inputCheckBox=returnAddInner(new InputCheckbox()).checked(checked).name(name).form_check_input();
      this.inputs=new Inputs();
      this.inputs.add(inputCheckBox);
      String script=this.inputs.js_post(action);
      inputCheckBox.onchange(script);
    }
    
    public InputCheckbox inputCheckbox()
    {
        return this.inputCheckBox;
    }
    
}
