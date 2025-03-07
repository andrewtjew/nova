package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.InputPassword;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.elements.Composer;
import org.nova.html.ext.HtmlUtils;

public class InputPasswordWithToggle extends InputPassword
{
    boolean outer=false;
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (outer==false)
        {
            outer=true;
            Item group=new Item();
            group.position(Position.relative);
            group.returnAddInner(this);
            Button button=group.returnAddInner(new Button()).tabindex(-1).border(0).position(Position.absolute);
            Icon icon=button.returnAddInner(new Icon("eye-fill"));
            button.onclick(HtmlUtils.js_call("nova.ui.password.toggleVisibility",this.id(),icon.id()));
            button.style("top:0.50em;right:0.75em;z-index:1;margin:0;padding:0;");
            composer.compose(group);
        }
        else
        {
            outer=false;
            super.compose(composer);
        }
    }    
}
