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

import java.util.Map.Entry;

import org.nova.core.NameObject;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.div;

public class StyleTemplate implements Styling<StyleTemplate>
{
    final private div template;

    public StyleTemplate()
    {
        this.template=new div();
    }

    @Override
    public StyleTemplate addClass(Object class_,Object...fragments)
    {
        if (fragments!=null)
        {
            if (class_!=null)
            {
                StringBuilder sb=new StringBuilder(class_.toString());
                for (Object fragment:fragments)
                {
                    if (fragment!=null)
                    {
                        sb.append('-').append(fragment);
                    }
                }
                this.template.addClass(sb.toString());
            }
        }
        return this;
    }

    @Override
    public TagElement<?> getElement()
    {
        return this.template;
    }
    
    public StyleTemplate applyTo(TagElement<?> element)
    {
        if (element!=null)
        {            
            element.addClass(this.template.class_());
            for (Entry<String, Object> entry:this.template.getAttributes().entrySet())
            {
                if (entry.getValue()!=null)
                {
                    element.attr(entry.getKey(),entry.getValue());
                }
                else
                {
                    element.attr(entry.getKey());
                }
            }
        }
        return this;
    }
    

    public static Object apply(StyleTemplate template,Object object)
    {
        if (template!=null)
        {
            if (object instanceof TagElement)
            {
                TagElement<?> tagElement=(TagElement<?>)object;
                template.applyTo(tagElement);
                return tagElement;
            }
        }
        return object;
    }
}
