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
import org.nova.html.enums.crossorigin;

public class img extends EmptyTagElement<img>
{
    public img()
    {
        super("img");
    }
    
    public img alt(String text)
    {
        return attr("alt",text);
    }

    public img crossorigin(crossorigin crossorigin)
    {
        return attr("crossorigin",crossorigin);
    }

    public img height(int height)
    {
        return attr("height",height);
    }
    public img ismap()
    {
        return attr("ismap");
    }
    public img ismap(boolean ismap)
    {
        if (ismap)
        {
            return attr("ismap");
        }
        return this;
    }
    public img longdesc(String URL)
    {
        return attr("longdesc",URL);
    }
    public img sizes(String sizes)
    {
        return attr("sizes",sizes);
    }
    public img src(String URL)
    {
        return attr("src",URL);
    }
    public img srcset(String value)
    {
        return attr("srcset",value);
    }
    public img usemap(String mapname)
    {
        return attr("usemap",mapname);
    }
    public img width(int width)
    {
        return attr("width",width);
    }
    public img style(String style)
    {
        return attr("style",style);
    }
        
    
}
