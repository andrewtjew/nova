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

import org.nova.html.tags.script;

public class Modal extends StyleComponent<Modal>
{
    public Modal()
    {
        super("div","modal");
        tabindex(-1);
    }
    
    public Modal fade()
    {
        addClass("fade");
        return this;
    }

    public Modal backdrop(boolean value)
    {
        attr("data-bs-backdrop",value);
        return this;
    }
    public Modal backdrop()
    {
        return backdrop(true);
    }
    public Modal backdrop_static()
    {
        attr("data-bs-backdrop","static");
        return this;
    }
    public Modal keyboard(boolean value)
    {
        attr("data-bs-keyboard",value);
        return this;
    }
    public Modal keyboard()
    {
        return keyboard(true);
    }

    public Modal focus(boolean value)
    {
        attr("data-bs-focus",value);
        return this;
    }
    public Modal focus()
    {
        return focus(true);
    }
    public Modal show()
    {
        this.returnAddInner(new script()).addInner(js_show());
        return this;
    }

    public String js_show()
    {
        return "bootstrap.Modal.getOrCreateInstance(document.getElementById('"+this.id()+"')).show();";
    }
    public String js_hide()
    {
        return "bootstrap.Modal.getOrCreateInstance(document.getElementById('"+this.id()+"')).hide();";
    }
    public String js_toggle()
    {
        return "bootstrap.Modal.getOrCreateInstance(document.getElementById('"+this.id()+"')).toggle();";
    }
    public static String js_show(String id)
    {
        return "bootstrap.Modal.getOrCreateInstance(document.getElementById('"+id+"')).show();";
    }
    public static String js_hide(String id)
    {
        return "bootstrap.Modal.getOrCreateInstance(document.getElementById('"+id+"')).hide();";
    }
    public static String js_toggle(String id)
    {
        return "bootstrap.Modal.getOrCreateInstance(document.getElementById('"+id+"')).toggle();";
    }
}
