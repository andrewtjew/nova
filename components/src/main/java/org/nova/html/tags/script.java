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
import org.nova.html.enums.crossorigin;
import org.nova.html.enums.character_set;

public class script extends GlobalEventTagElement<script>
{
    public script()
    {
        super("script");
    }
    
    public script integrity(String code)
    {
        return attr("integrity",code);
    }
    public script crossorigin(crossorigin crossorigin)
    {
        return attr("crossorigin",crossorigin.toString());
    }
    public script async()
    {
        return attr("async","async");
    }
    public script async(boolean async)
    {
        if (async)
        {
            return attr("async");
        }
        return this;
    }
    public script charset(character_set character_set)
    {
        return attr("charset",character_set);
    }
    public script defer()
    {
        return attr("defer");
    }
    public script defer(boolean defer)
    {
        if (defer)
        {
            return attr("defer");
        }
        return this;
    }
    public script src(String URL)
    {
        URL=Element.replaceURL(URL);
        return attr("src",URL);
    }
    public script type(String media_type)
    {
        return attr("type",media_type);
    }
}
