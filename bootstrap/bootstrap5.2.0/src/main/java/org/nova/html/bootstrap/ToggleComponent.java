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

public abstract class ToggleComponent<ELEMENT extends ToggleComponent<ELEMENT>> extends StyleComponent<ELEMENT> 
{
    public ToggleComponent(String tag, String componentClass)
    {
        super(tag, componentClass);
    }

    @SuppressWarnings("unchecked")
    public ELEMENT toggleCollapse(NavbarCollapse target)
    {
        attr("data-toggle","collapse");
        attr("data-target","#"+target.id());
        return (ELEMENT)this;
    }
    @SuppressWarnings("unchecked")
    public ELEMENT toggleCollapse(Collapse collapse)
    {
        attr("data-toggle","collapse");
        attr("data-target","#"+collapse.id());
        return (ELEMENT)this;
    }
    public ELEMENT toggleCollapse(Collapse collapse,String collapseClass)
    {
        attr("data-toggle","collapse");
        attr("data-target","."+collapseClass);
        return (ELEMENT)this;
    }
    
    /*
    public ELEMENT toggleModal(Modal modal)
    {
        data("toggle","modal");
        data("target","#"+modal.id());
        return (ELEMENT)this;
    }    

    public ELEMENT toggleDropdownMenu(DropdownMenu dropdownMenu)
    {
        data("toggle","dropdown");
        addClass("dropdown-toggle");
        return (ELEMENT)this;
    } 

    public ELEMENT toggleTabContent(TabContent tabContent)
    {
        data("bs-toggle","list");
        data("bs-target","#"+tabContent.id());
        return (ELEMENT)this;
    }
    */   
    public ELEMENT toggleTabPane(TabPane tabPane)
    {
        String id=tabPane.id();
        attr("data-bs-toggle","tab");
        attr("data-bs-targetref","#"+id);
        attr("href","#"+id);
        attr("role","tab");
        return (ELEMENT)this;
    }
}
