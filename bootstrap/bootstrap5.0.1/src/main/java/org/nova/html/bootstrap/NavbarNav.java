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

import org.nova.html.elements.Element;

public class NavbarNav extends StyleComponent<NavbarNav>
{
    public NavbarNav()
    {
        super("ul",null);
        addClass("navbar-nav");
    }
    public NavbarNav addItem(Element element)
    {
        returnAddInner(new NavItem()).addInner(element);
        return this;
    }

    /*
    public NavbarNav addLink(String label,String href,NavbarState state)
    {
        li li=returnAddInner(new li().addClass("nav-item"));
        a a=li.returnAddInner(new a());
        a.addInner(label).href(href);
        ClassBuilder cb=new ClassBuilder("nav-link");
        cb.add(state);
        a.addClass(cb.toString());
        return this;
    }
    public NavbarNav addLink(String label,String href)
    {
        return addLink(label,href,NavbarState.active);
    }
    
    public NavbarNav add(ButtonDropdown dropdown)
    {
        li li=returnAddInner(new li().addClass("nav-item dropdown"));
        li.returnAddInner(dropdown);
        return this;
    }

    public NavbarNav add(LinkDropdown dropdown)
    {
        li li=returnAddInner(new li().addClass("nav-item dropdown"));
        li.returnAddInner(dropdown);
        return this;
    }
    */
}
