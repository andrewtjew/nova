package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.ButtonGroup;
import org.nova.html.bootstrap.DropdownMenu;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.DropdownTogglerButton;
import org.nova.html.bootstrap.classes.StyleColor;

public class DropdownButtonMenuGroup extends ButtonGroup
{
    final private Button button;
    final private DropdownMenu menu;
    
    public DropdownButtonMenuGroup(String buttonLabel)
    {
        this.button=returnAddInner(new Button(buttonLabel));
        this.menu=returnAddInner(new DropdownMenu(this.button));
    }
    public DropdownButtonMenuGroup()
    {
        this(null);
    }
    
    public DropdownButtonMenuGroup buttonToggles()
    {
        this.button().onclick(this.menu.js_dropdown_toggle());
        button.attr("data-toggle","dropdown");
        return this;
    }

    public Button button()
    {
        return button;
    }

    public DropdownMenu menu()
    {
        return menu;
    }
    
//    public DropdownButtonMenuGroup vertical()
//    {
//        addClass("btn-group-vertical");
//        return this;
//    }
    
    
}
