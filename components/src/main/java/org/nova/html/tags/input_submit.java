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
package org.nova.html.tags;

import org.nova.html.elements.InputElement;
import org.nova.html.elements.InputType;
import org.nova.html.enums.enctype;

public class input_submit extends InputElement<input_submit>
{
    public input_submit()
    {
        super(InputType.submit);
    }
    public input_submit formaction(String URL) //submit, image
    {
        return attr("formaction",URL);
    }
    public input_submit formenctype(enctype enctype) //submit, image
    {
        return attr("formenctype",enctype);
    }
    public input_submit formmethod(String value) //submit, image
    {
        return attr("formmethod",value);
    }
    public input_submit formnovalidate()  //submit
    {
        return attr("formnovalidate");
    }
    public input_submit formnovalidate(boolean formnovalidate)  //submit
    {
        if (formnovalidate)
        {
            return attr("formnovalidate");
        }
        return this;
    }
    public input_submit value(String text) //button, reset, submit, text, password, hidden, checkbox, radio, image
    {
        return attr("value",text);
    }
    
}
