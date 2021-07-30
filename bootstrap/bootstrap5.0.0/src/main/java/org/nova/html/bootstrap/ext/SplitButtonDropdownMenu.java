package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.ButtonDropdown;
import org.nova.html.bootstrap.ButtonGroup;
import org.nova.html.bootstrap.DropdownMenu;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.ToggleDropdownButton;
import org.nova.html.bootstrap.classes.Drop;
import org.nova.html.bootstrap.classes.StyleColor;

public class SplitButtonDropdownMenu extends StyleComponent<SplitButtonDropdownMenu>
{
    final private Button button;
    final private ToggleDropdownButton toggler;
    final private DropdownMenu menu;
    
    public SplitButtonDropdownMenu()
    {
        super("div","btn-group");
        addClass("dropdown");
        this.button=returnAddInner(new Button());
        this.toggler=returnAddInner(new ToggleDropdownButton());
        this.menu=returnAddInner(new DropdownMenu(this.toggler()));
    }
    
    public SplitButtonDropdownMenu buttonToggles()
    {
        this.button().onclick(this.menu.js_dropdown_toggle());
        button.data("toggle","dropdown");
//        this.menu.reference_parent(this.button);
        return this;
    }

    public Button button()
    {
        return button;
    }

    public ToggleDropdownButton toggler()
    {
        return toggler;
    }

    public DropdownMenu menu()
    {
        return menu;
    }
    
//    public SplitButtonDropdownMenu color(StyleColor color)
//    {
//        this.button().color(color);
//        this.toggler().color(color);
//        return this;
//    }
    public SplitButtonDropdownMenu static_()
    {
        this.toggler.attr("data-display","static");
        this.menu.attr("data-display","static");
        return this;
    }
    public SplitButtonDropdownMenu vertical()
    {
        addClass("btn-group-vertical");
        return this;
    }
    public SplitButtonDropdownMenu drop(Drop drop)
    {
        addClass(drop);
        return this;
    }
    public SplitButtonDropdownMenu dropright()
    {
        addClass("dropright");
        return this;
    }
    
    public SplitButtonDropdownMenu dropup()
    {
        addClass("dropup");
        return this;
    }
    
    public SplitButtonDropdownMenu dropleft()
    {
        addClass("dropleft");
        return this;
    }
    
    
    
}
