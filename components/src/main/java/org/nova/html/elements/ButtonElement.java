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
package org.nova.html.elements;

public class ButtonElement<ELEMENT extends ButtonElement<ELEMENT>> extends GlobalEventTagElement<ELEMENT>
{
    protected ButtonElement()
    {
        super("button");
    }

    public ELEMENT autofocus()
    {
        return attr("autofocus");
    }
    public ELEMENT disabled()
    {
        return attr("disabled");
    }
    @SuppressWarnings("unchecked")
    public ELEMENT disabled(boolean disabled)
    {
        if (disabled)
        {
            attr("disabled");
        }
        return (ELEMENT)this;
    }
    public ELEMENT form(String form_id)
    {
        return attr("form",form_id);
    }
    public ELEMENT form(FormElement<?> element)
    {
        return attr("form",element.id());
    }
    public ELEMENT name(String text)
    {
        return attr("name",text);
    }
    public ELEMENT value(Object value)
    {
        return attr("value",value);
    }

}
