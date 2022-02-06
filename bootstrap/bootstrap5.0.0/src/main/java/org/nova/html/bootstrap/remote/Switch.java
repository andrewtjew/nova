package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.FormCheck;
import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.ext.Content;
import org.nova.html.remote.Inputs;

public class Switch extends FormCheck
{
    final private Inputs inputs;
    public Switch(String name,boolean checked,String action) throws Throwable
    {
      switch_();
      InputCheckbox checkbox=addInputCheckbox(null).checked(checked).name(name);
      this.inputs=new Inputs();
      this.inputs.add(checkbox);
      String script=this.inputs.js_post(action,true);
      checkbox.onchange(script);
    }
    
}
