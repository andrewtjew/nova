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
package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Nav;
import org.nova.html.bootstrap.NavItem;
import org.nova.html.bootstrap.NavLink;
import org.nova.html.bootstrap.TabContent;
import org.nova.html.bootstrap.TabPane;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;

public class TabDocument extends TabContent
{
    final private Nav nav;
//    final private TabContent tabContent;
    
    
    public TabDocument(boolean fill)
    {
        this.nav=new Nav().tabs();
        if (fill)
        {
            this.nav.fill();
        }
//        this.nav.attr("role","tablist");
        //this.tabContent=new TabContent();
    }
    
    public TabPane returnTabPane(String label,boolean show,boolean active,boolean disabled)
    {
        NavItem navItem=this.nav.returnAddInner(new NavItem());
        TabPane tabPane=returnAddInner(new TabPane()).fade();
        NavLink navLink=navItem.returnAddInner(new NavLink()).addInner(label);
        navLink.attr("data-toggle", "tab");
        navLink.attr("href", "#"+tabPane.id());
        
        
        
        
        if (active)
        {
            tabPane.active();
            navLink.active();
        }
        if (show)
        {
            tabPane.show();
        }
        if (disabled)
        {
           navLink.disabled();
        }
        return tabPane;
    }
    
    public TabPane returnTabPane(String label,boolean activeAndShow)
    {
        return returnTabPane(label,activeAndShow,activeAndShow,false);
    }

    public TabDocument add(NavLink navLink,TabPane tabPane)
    {
        NavItem navItem=this.nav.returnAddInner(new NavItem());
        navItem.addInner(navLink);
        navLink.attr("data-toggle", "tab");
        navLink.attr("href", "#"+tabPane.id());
        
        addInner(tabPane);
        return this;
    }
    
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        this.nav.compose(composer);
        super.compose(composer);
    }
    
}
