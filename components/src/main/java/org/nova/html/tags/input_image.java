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
import org.nova.html.enums.target;;

public class input_image extends InputElement<input_image>
{
    public input_image()
    {
        super(InputType.image);
    }

    public input_image alt(String text) //image
    {
        return attr("alt",text);
    }
    public input_image formenctype(enctype enctype) //submit, image
    {
        return attr("formenctype",enctype.toString());
    }
    public input_image formmethod(String value) //submit, image
    {
        return attr("formmethod",value);
    }
    public input_image formtarget(target target) //submit,image
    {
        return attr("target",target.toString());
    }
    public input_image formtarget(String framename)  //submit,image
    {
        return attr("target",framename);
    }
    public input_image height(String pixels) //image
    {
        return attr("height",pixels);
    }
}
