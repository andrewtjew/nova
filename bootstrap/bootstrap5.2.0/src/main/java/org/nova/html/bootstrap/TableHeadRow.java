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

import org.nova.html.elements.TagElement;
import org.nova.html.tags.th;

public class TableHeadRow extends StyleComponent<TableHeadRow>
{
    public class TableRow extends StyleComponent<TableRow>
    {
        public TableRow()
        {
            super("tr",null,true);
        }
        
        public TableRow addWithStyle(StyleTemplate template,Object...objects)
        {
            for (Object object:objects)
            {
                if (object instanceof TagElement<?>)
                {
                    TagElement<?> tagElement=(TagElement<?>)object;
                    template.applyTo(tagElement);
                    if (tagElement.getTag().equals("th"))
                    {
                        addInner(tagElement);
                        continue;
                    }
                }
                addInner(new th().addInner(object));
            }
            return this;
        }
    
        public TableRow add(Object...objects)
        {
            for (Object object:objects)
            {
                if (object instanceof TagElement<?>)
                {
                    TagElement<?> tagElement=(TagElement<?>)object;
                    if (tagElement.getTag().equals("th"))
                    {
                        addInner(tagElement);
                        continue;
                    }
                }
                addInner(new th().addInner(object));
            }
            return this;
        }
    
    }

    public TableHeadRow()
    {
        super("tr",null,true);
    }
    
    public TableHeadRow addWithStyle(StyleTemplate template,Object...objects)
    {
        for (Object object:objects)
        {
            if (object instanceof TagElement<?>)
            {
                TagElement<?> tagElement=(TagElement<?>)object;
                template.applyTo(tagElement);
                if (tagElement.getTag().equals("th"))
                {
                    addInner(tagElement);
                    continue;
                }
            }
            addInner(new th().addInner(object));
        }
        return this;
    }

    public TableHeadRow add(Object...objects)
    {
        for (Object object:objects)
        {
            if (object instanceof TagElement<?>)
            {
                TagElement<?> tagElement=(TagElement<?>)object;
                if (tagElement.getTag().equals("th"))
                {
                    addInner(tagElement);
                    continue;
                }
            }
            addInner(new th().addInner(object));
        }
        return this;
    }

}
