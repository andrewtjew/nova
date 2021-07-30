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

public class InputNumber extends InputComponent<InputNumber>
{
    public InputNumber()
    {
        super(InputType.number);
    }
    public InputNumber max(double number)
    {
        return attr("max",number);
    }
    public InputNumber max(long number)
    {
        return attr("max",number);
    }
    public InputNumber min(double number)
    {
        return attr("min",number);
    }
    public InputNumber min(long number)
    {
        return attr("min",number);
    }
    public InputNumber step(double number) //number, range, date, datetime, datetime-local, month, time and week.
    {
        return attr("step",number);
    }
    public InputNumber step(long number) //number, range, date, datetime, datetime-local, month, time and week.
    {
        return attr("step",number);
    }

    public InputNumber required()  //text, search, url, tel, email, password, date pickers, number, checkbox, radio, and file.
    {
        return attr("required");
    }
    public InputNumber required(boolean required)
    {
        if (required)
        {
            attr("required");
        }
        return this;
    }
    public InputNumber value(Object value)
    {
        return attr("value",value);
    }
    public InputNumber size(int number) //text, search, tel, url, email, and password.
    {
        return attr("size",number);
    }
    public InputNumber placeholder(Object value) //text, search, url, tel, email, and password.
    {
        return attr("placeholder",value);
    }
    public InputNumber autocomplete(autocomplete autocomplete) //text, search, url, tel, email, password, datepickers, range, and color.
    {
        return attr("autocomplete",autocomplete);
    }
    public InputNumber autocomplete(boolean autocomplete)
    {
        if (autocomplete)
        {
            attr("autocomplete");
        }
        return this;
    }
}

