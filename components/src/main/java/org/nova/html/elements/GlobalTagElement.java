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

import org.nova.html.attributes.Style;
import org.nova.html.enums.dir;
import org.nova.html.enums.dropzone;
import org.nova.html.properties.Property;

public class GlobalTagElement<ELEMENT extends TagElement<ELEMENT>> extends TagElement<ELEMENT>
{
    public GlobalTagElement(String tag)
    {
        super(tag);
    }
    public GlobalTagElement(String tag,boolean noEndTag)
    {
        super(tag,noEndTag);
    }

    public ELEMENT accesskey(String value)
    {
        return attr("accesskey",value);
    }

    public ELEMENT contenteditable(boolean value)
    {
        return attr("contenteditable",value);
    }

    public ELEMENT contextmenu(String value)
    {
        return attr("contextmenu",value);
    }
    
//    public ELEMENT data(String attribute,Object value)
//    {
//        return attr("data-"+attribute,value);
//    }
    public ELEMENT dir(dir dir)
    {
        return attr("dir",dir.toString());
    }
    public ELEMENT draggable(boolean value)
    {
        return attr("draggable",value);
    }
    public ELEMENT dropzone(dropzone dropzone)
    {
        return attr("dropzone",dropzone.toString());
    }
    public ELEMENT hidden(boolean value)
    {
        if (value==true)
        {
            return attr("hidden");
        }
        return (ELEMENT)this;
    }
    public ELEMENT hidden()
    {
        return attr("hidden");
    }
    public ELEMENT lang(String value)
    {
        return attr("lang",value);
    }
    public ELEMENT spellcheck(boolean value)
    {
        return attr("spellcheck",value);
    }
    public ELEMENT style(String value)
    {
        return attr("style",value);
    }
    
    //Use style(property) instead
    @Deprecated
    public ELEMENT style(Style value)
    {
        return attr("style",value.toString());
    }
    public ELEMENT style(Property...properties)
    {
        if (properties.length==0)
        {
            return (ELEMENT)this;
        }
        StringBuilder sb=new StringBuilder();
        for (Property property:properties)
        {
            sb.append(property);
        }
        return attr("style",sb);
    }
    public ELEMENT tabindex(int value)
    {
        return attr("tabindex",value);
    }
    public ELEMENT title(String value)
    {
        return attr("title",value);
    }
    public ELEMENT translate(boolean value)
    {
        return attr("translate",value);
    }
}
