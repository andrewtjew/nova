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
import org.nova.html.enums.http_equiv;
import org.nova.html.enums.name;
import org.nova.html.enums.character_set;

public class meta extends EmptyTagElement<meta>
{
    public meta()
    {
        super("meta");
    }
    
    public meta charset(character_set character_set)
    {
        return attr("charset",character_set);
    }
    public meta charset(String charset)
    {
        return attr("charset",charset);
    }
    public meta content(String text)
    {
        return attr("content",text);
    }
    public meta http_equiv(http_equiv http_equiv)
    {
        return attr("http-equiv",http_equiv);
    }
    public meta http_equiv_content(http_equiv http_equiv,String content)
    {
        attr("http-equiv",http_equiv);
        return attr("content",content);
    }
    public meta name(name name)
    {
        return attr("name",name);
    }
    
}
