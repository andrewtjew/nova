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

import org.nova.html.elements.InputType;
import org.nova.html.enums.autocomplete;
import org.nova.html.tags.input_checkbox;

public class FormCheckInput extends InputComponent<FormCheckInput>
{
    public FormCheckInput(boolean radio)
    {
        super(radio?InputType.radio:InputType.checkbox,"form-check-input");
        
    }
    public FormCheckInput()
    {
        this(false);
        
    }
    public FormCheckInput checked() //checkbox or radio
    {
        return attr("checked");
    }
    public FormCheckInput checked(boolean checked)
    {
        if (checked)
        {
            attr("checked");
        }
        return this;
    }
    public FormCheckInput required()  //text, search, url, tel, email, password, date pickers, number, checkbox, radio, and file.
    {
        return attr("required");
    }
    public FormCheckInput required(boolean required)
    {
        if (required)
        {
            attr("required");
        }
        return this;
    }
    public FormCheckInput value(Object text) //button, reset, submit, text, password, hidden, checkbox, radio, image
    {
        return attr("value",text);
    }
}

