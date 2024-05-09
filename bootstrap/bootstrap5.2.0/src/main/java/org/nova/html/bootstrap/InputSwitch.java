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

import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.elements.Composer;
import org.nova.html.elements.InputType;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.div;

public class InputSwitch extends InputComponent<InputSwitch>
{
    public InputSwitch()
    {
        super(InputType.checkbox);
    }
    public InputSwitch checked() 
    {
        return attr("checked");
    }
    public InputSwitch checked(Boolean checked)
    {
        if (checked==null)
        {
            return this;
        }
        if (checked)
        {
            attr("checked");
        }
        return this;
    }
    public InputSwitch required()
    {
        attr("required");
        return this;
    }
    public InputSwitch value(String text) //button, reset, submit, text, password, hidden, checkbox, radio, image
    {
        return attr("value",text);
    }
    public InputSwitch value(Object value)
    {
        if (value==null)
        {
            return this;
        }
        return attr("value",value.toString());
    }
    public InputSwitch form_check_input()
    {
        addClass("form-check-input");
        return this;
    }

    boolean outer=false;
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (outer==false)
        {
            outer=true;
            div div=new div();
            div.addClass("form-check");
            div.addClass("form-switch");
            div.addInner(this);
            composer.compose(div);
        }
        else
        {
            outer=false;
            super.compose(composer);
        }
    }        
}

