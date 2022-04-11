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

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.tags.a;
import org.nova.html.tags.div;

public class LinkDropdown extends ButtonComponent<LinkDropdown>
{
    final private Item menu;
    private boolean split;
    private boolean usehref;
    private boolean menuRight;
    
    public LinkDropdown(String label)
    {
        this(label,null);
    }
    public LinkDropdown(String label,String href)
    {
        super("a");
        this.usehref=href!=null;
        attr("role","button");
        attr("href",href);
        addInner(label);
        this.menu=new Item();
    }

    public Item menu()
    {
        return this.menu;
    }
    
    public LinkDropdown addToMenu(String label,String href)
    {
//        this.menu.addInner(new LinkButton(href).addInner(label).outline().color(StyleColor.primary));
        this.menu.addInner(new a().addClass("dropdown-item").href(href).addInner(label));
        return this;
    }
    public LinkDropdown addToMenu(Element element)
    {
        this.menu.addInner(element);
        return this;
    }

    public LinkDropdown addDivider()
    {
        this.menu.addInner(new div().addClass("dropdown-divider"));
        return this;
    }
    public LinkDropdown menuRight(boolean value)
    {
        this.menuRight=value;
        return this;
    }
    
    public LinkDropdown split()
    {
        this.split=true;
        return this;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        this.menu.addClass("dropdown-menu");
        if (this.menuRight)
        {
            this.menu.addClass("dropdown-menu-right");
        }
        
        if (this.split)
        {
            div group=new div().addClass("btn-group");
            group.addInner(new Element()
            {
                @Override
                public void compose(Composer composer) throws Throwable
                {
                    if (usehref==false)
                    {
                        attr("data-toggle","dropdown");
                    }
                    composeThis(composer);
                    Button splitter=new Button().attr("data-toggle","dropdown").attr("aria-haspopup",true).attr("aria-expanded",false);
                    splitter.addClass(class_());
                    splitter.addClass("dropdown-toggle");
                    splitter.addClass("dropdown-toggle-split");
                    splitter.compose(composer);
                    menu.compose(composer);
                }
            });
            group.compose(composer);
        }
        else
        {
            attr("data-toggle","dropdown");
            addClass("dropdown-toggle");
            super.compose(composer);
            this.menu.compose(composer);
        }
        
    }

}
