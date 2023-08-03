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

import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.EmptyTagElement;
import org.nova.html.enums.keytype;

public class keygen extends EmptyTagElement<keygen>
{
    public keygen()
    {
        super("keygen");
    }
    
    public keygen autofocus()
    {
        return attr("autofocus");
    }
    public keygen autofocus(boolean autofocus)
    {
        if (autofocus)
        {
            return attr("autofocus");
        }
        return this;
    }
    public keygen challenge()
    {
        return attr("challenge");
    }
    public keygen challenge(boolean challenge)
    {
        if (challenge)
        {
            return attr("challenge");
        }
        return this;
    }
    public keygen disabled()
    {
        return attr("disabled");
    }
    public keygen disabled(boolean disabled)
    {
        if (disabled)
        {
            return attr("disabled");
        }
        return this;
    }
    public keygen form(String form_id)
    {
        return attr("form",form_id);
    }
    public keygen keytype(keytype keytype)
    {
        return attr("keytype",keytype.toString());
    }
    public keygen name(String text)
    {
        return attr("name",text);
    }
    
}
