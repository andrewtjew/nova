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

import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.enums.a_rel;
import org.nova.html.enums.target;

public class a extends GlobalEventTagElement<a>
{
    public a()
    {
        super("a");
    }
    
    public a download(String filename)
    {
        return attr("download",filename);
    }
    public a href(String URL)
    {
        URL=Element.replaceURL(URL);
        return attr("href",URL);
    }
    public a hreflang(String language_code)
    {
        return attr("hreflang",language_code);
    }
    public a media(String media_query)
    {
        return attr("media",media_query);
    }
    public a rel(a_rel rel)
    {
        return attr("rel",rel.toString());
    }
    public a target(target target)
    {
        return attr("target",target.toString());
    }
    public a target(String framename)
    {
        return attr("target",framename);
    }
    public a type(String media_type)
    {
        return attr("type",media_type);
    }
}
