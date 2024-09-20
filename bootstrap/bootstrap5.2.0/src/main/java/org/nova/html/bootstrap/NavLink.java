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
import org.nova.html.enums.target;

public class NavLink extends ToggleComponent<NavLink>
{
    public NavLink()
    {
        super("a","nav-link");
    }
    public NavLink(String label,String href)
    {
        this();
        attr("href",href);
        addInner(label);
    }
    public NavLink(String label)
    {
        this(label,null);
    }
    public NavLink active()
    {
        addClass("active");
        return this;
    }
    public NavLink disabled()
    {
        addClass("disabled");
        return this;
    }
    public NavLink target(target target)
    {
        return attr("target",target.toString());
    }
    public NavLink target(String target)
    {
        attr("target",target);
        return this;
    }
    public NavLink tab(String target)
    {
        attr("data-bs-toggle","tab");
        attr("data-bs-target",target);
        return this;
    }
    public NavLink href(String href)
    {
        attr("href",href);
        return this;
    }
    boolean composing=false;
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (composing==false)
        {
            composing=true;
            NavItem item=new NavItem();
            item.addInner(this);
            item.compose(composer);
            return;
        }
        super.compose(composer);
    }    
    
}
