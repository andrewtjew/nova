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

import org.nova.html.elements.InputElement;
import org.nova.html.elements.InputType;
import org.nova.html.elements.TagElement;
import org.nova.html.enums.autocomplete;

public class InputComponent<ELEMENT extends InputComponent<ELEMENT>> extends InputElement<ELEMENT> implements Styling<ELEMENT>
{
    protected InputComponent(String tag,InputType inputType,String componentClass)
    {
        super(tag,inputType);
        addClass(componentClass);
    }

    protected InputComponent(InputType inputType,String componentClass)
    {
        super(inputType);
        addClass(componentClass);
    }
    
    protected InputComponent(String tag,InputType inputType)
    {
        this(tag,inputType,null);
    }
    
    protected InputComponent(InputType inputType)
    {
        super(inputType);
    }

    public ELEMENT btn_check()
    {
        return addClass("btn-check");
    }

    public ELEMENT autocomplete(autocomplete autocomplete) //text, search, url, tel, email, password, datepickers, range, and color.
    {
        return attr("autocomplete",autocomplete);
    }
    public ELEMENT autocomplete(boolean autocomplete)
    {
        if (autocomplete)
        {
            attr("autocomplete");
        }
        return (ELEMENT)this;
    }
    
    @Override
    public TagElement<?> getElement()
    {
        return this;
    }

    
}
