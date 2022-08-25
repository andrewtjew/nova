package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.ButtonGroup;
import org.nova.html.bootstrap.DropdownMenu;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.DropdownTogglerButton;
import org.nova.html.bootstrap.classes.StyleColor;

public class DropdownSplitMenuButtonGroup extends ButtonGroup
{
    final private Button button;
    final private DropdownTogglerButton toggler;
    final private DropdownMenu menu;
    
    public DropdownSplitMenuButtonGroup(boolean dropstart)
    {
        if (dropstart)
        {
            this.toggler=returnAddInner(new DropdownTogglerButton());
            this.menu=returnAddInner(new DropdownMenu(this.toggler(),true));
            this.button=returnAddInner(new Button());
            dropstart();
        }
        else
        {
            this.button=returnAddInner(new Button());
            this.toggler=returnAddInner(new DropdownTogglerButton());
            this.menu=returnAddInner(new DropdownMenu(this.toggler(),true));
        }
    }
    
    public DropdownSplitMenuButtonGroup buttonToggles()
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
    
    public DropdownSplitMenuButtonGroup static_()
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
//    public DropdownSplitMenuButtonGroup drop(Drop drop)
//    {
//        addClass(drop);
//        return this;
//    }
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
