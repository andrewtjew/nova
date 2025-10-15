/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.html.bootstrap;

import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.ThemeColor;
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.a;
import org.nova.html.tags.div;

public class DropdownMenu extends StyleComponent<DropdownMenu>
{
    final private StyleComponent<?> toggler;
    
    public DropdownMenu(StyleComponent<?> toggler)
    {
        this(toggler,false);
    }
    public DropdownMenu(StyleComponent<?> toggler,boolean split)
    {
        super("ul", "dropdown-menu");
        this.toggler=toggler;
        if (toggler!=null)
        {
            toggler.addClass("dropdown-toggle");
            toggler.attr("data-bs-toggle","dropdown");
            if (split)
            {
                toggler.addClass("dropdown-toggle-split");
            }
        }
    }
    
    public DropdownMenu end()
    {
        addClass("dropdown-menu-end");
        return this;
    }
    public DropdownMenu end(BreakPoint deviceClass)
    {
        addClass("dropdown-menu",deviceClass,"end");
        return this;
    }
    
    public DropdownMenu static_()
    {
        this.toggler.attr("data-bs-display","static");
        return this;
    }
    
    public DropdownMenu right(BreakPoint deviceClass)
    {
        addClass("dropdown-menu",deviceClass,"right");
        return this;
    }
    public DropdownMenu left(BreakPoint deviceClass)
    {
        addClass("dropdown-menu",deviceClass,"left");
        return this;
    }
    public DropdownMenu addItem(String label,String URL)
    {
        returnAddInner(new a()).addClass("dropdown-item").href(URL).addInner(label);
        return this;
    }
    public DropdownMenu addDivider()
    {
        returnAddInner(new div()).addClass("dropdown-divider");
        return this;
    }

    public DropdownMenu reference_toggle()
    {
        this.toggler.attr("data-bs-reference","toggle");
        return this;
    }
 
    public DropdownMenu reference_parent()
    {
        this.toggler.attr("data-bs-reference","parent");
        return this;
    }
    public DropdownMenu reference(GlobalTagElement<?> element)
    {
        this.toggler.attr("data-bs-reference","#"+element.id());
        return this;
    }
 
    public DropdownMenu reference_toggle(StyleComponent<?> button)
    {
        this.toggler.attr("data-bs-reference","toggle");
        return this;
    }
 

    public DropdownMenu flip(boolean value)
    {
        this.toggler.attr("data-bs-flip",value);
        return this;
    }
    public DropdownMenu rootBoundary_window()
    {
        this.toggler.attr("data-bs-root-boundary","window");
//        attr("data-bs-boundary","window");
        return this;
    }
    public DropdownMenu rootBoundary_viewport()
    {
        this.toggler.attr("data-bs-root-boundary","viewport");
//        attr("data-bs-boundary","viewport");
        return this;
    }
    public DropdownMenu boundary(TagElement<?> element)
    {
        this.toggler.attr("data-bs-boundary","#"+element.id());
        return this;
    }

    public DropdownMenu autoClose(boolean value)
    {
        this.toggler.attr("data-bs-auto-close",value);
        return this;
    }
 
    public DropdownMenu autoClose_inside()
    {
        this.toggler.attr("data-bs-auto-close","inside");
        return this;
    }
    public DropdownMenu autoClose_outside()
    {
        this.toggler.attr("data-bs-auto-close","outside");
        return this;
    }
    
    public DropdownMenu color(ThemeColor value)
    {
        addClass("dropdown-menu",value.toString());
        return this;
    } 
    public DropdownMenu reference(StyleComponent<?> button,GlobalTagElement<?> element)
    {
        this.toggler.attr("data-bs-reference","#"+element.id());
        return this;
    }
 
    public String js_toggle()
    {
        return "bootstrap.Dropdown.getOrCreateInstance(document.getElementById('"+this.id()+"')).toggle();";
    }
    public String js_hide()
    {
        return "bootstrap.Dropdown.getOrCreateInstance(document.getElementById('"+this.id()+"')).hide();";
    }
    public String js_show()
    {
        return "bootstrap.Dropdown.getOrCreateInstance(document.getElementById('"+this.id()+"')).show();";
    }
    public String js_update()
    {
        return "bootstrap.Dropdown.getOrCreateInstance(document.getElementById('"+this.id()+"')).update();";
    }
    
}
