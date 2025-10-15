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

import org.nova.html.ext.HtmlUtils;

public class Modal extends StyleComponent<Modal>
{
    public Modal()
    {
        super("div","modal");
    }
    
    public Modal fade()
    {
        addClass("fade");
        return this;
    }

    public Modal backdrop(boolean value)
    {
        attr("data-backdrop",value);
        return this;
    }
    public Modal backdrop()
    {
        return backdrop(true);
    }
    public Modal backdrop_static()
    {
        attr("data-backdrop","static");
        return this;
    }
    public Modal keyboard(boolean value)
    {
        attr("data-keyboard",value);
        return this;
    }
    public Modal keyboard()
    {
        return keyboard(true);
    }

    public Modal show(boolean value)
    {
        attr("data-show",value);
        return this;
    }
    public Modal show()
    {
        return show(true);
    }
    public Modal focus(boolean value)
    {
        attr("data-focus",value);
        return this;
    }
    public Modal focus()
    {
        return focus(true);
    }
    public static String js_show(String id)
    {
//        return HtmlUtils.js_call("alert",5);
        return HtmlUtils.js_call("$('#"+id+"').modal", "show");
    }
    public static String js_hide(String id)
    {
        return HtmlUtils.js_call("$('#"+id+"').modal", "hide");
    }
}
