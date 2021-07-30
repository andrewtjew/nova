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

public class ButtonDropdown extends ButtonComponent<ButtonDropdown>
{
    final private Item menu;
    private boolean split;
    private boolean menuRight;
    
    public ButtonDropdown(String label)
    {
        super("button");
        attr("type","button");
        attr("data-toggle","dropdown");
        addInner(label);
        this.menu=new Item();
    }
    
    public ButtonDropdown addToMenu(String label,String href)
    {
        this.menu.addInner(new a().addClass("dropdown-item").href(href).addInner(label));
        return this;
    }
    public ButtonDropdown addToMenu(Element element)
    {
        this.menu.addInner(element);
        return this;
    }

    public ButtonDropdown addDivider()
    {
        this.menu.addInner(new div().addClass("dropdown-divider"));
        return this;
    }
    public ButtonDropdown menuRight(boolean value)
    {
        this.menuRight=value;
        return this;
    }

    public ButtonDropdown split()
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
            this.menu.addClass(this.menuRight, "dropdown-menu-right");
        }
        
        if (this.split)
        {
            div group=new div().addClass("btn-group");
            group.addInner(new Element()
            {
                @Override
                public void compose(Composer composer) throws Throwable
                {
                    attr("data-toggle","dropdown");
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
            addClass("dropdown-toggle");
            super.compose(composer);
            this.menu.compose(composer);
        }
        
    }

}
