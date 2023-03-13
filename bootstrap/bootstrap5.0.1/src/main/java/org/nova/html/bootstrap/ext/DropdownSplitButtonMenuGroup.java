package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.ButtonGroup;
import org.nova.html.bootstrap.DropdownMenu;
import org.nova.html.bootstrap.DropdownTogglerButton;
import org.nova.html.bootstrap.classes.Drop;

public class DropdownSplitButtonMenuGroup extends ButtonGroup
{
    final private Button button;
    final private DropdownTogglerButton toggler;
    final private DropdownMenu menu;
    
    public DropdownSplitButtonMenuGroup(String buttonLabel)
    {
        this.button=returnAddInner(new Button(buttonLabel));
        this.toggler=returnAddInner(new DropdownTogglerButton());
        this.menu=returnAddInner(new DropdownMenu(this.toggler(),true));
    }
    
    public DropdownSplitButtonMenuGroup buttonToggles()
    {
        this.button().onclick(this.menu.js_dropdown_toggle());
        button.attr("data-toggle","dropdown");
//        this.menu.reference_parent(this.button);
        return this;
    }

    public Button button()
    {
        return button;
    }

    public DropdownTogglerButton toggler()
    {
        return toggler;
    }

    public DropdownMenu menu()
    {
        return menu;
    }
    
    public DropdownSplitButtonMenuGroup static_()
    {
        this.toggler.attr("data-bs-display","static");
        this.menu.attr("data-bs-display","static");
        return this;
    }
//    public DropdownSplitButtonMenuGroup vertical()
//    {
//        addClass("btn-group-vertical");
//        return this;
//    }
    public DropdownSplitButtonMenuGroup drop(Drop drop)
    {
        addClass(drop);
        return this;
    }
//    public DropdownSplitButtonMenuGroup dropright()
//    {
//        addClass("dropright");
//        return this;
//    }
//    
//    public DropdownSplitButtonMenuGroup dropup()
//    {
//        addClass("dropup");
//        return this;
//    }
//    
//    public DropdownSplitButtonMenuGroup dropleft()
//    {
//        addClass("dropleft");
//        return this;
//    }
    
    
    
}
