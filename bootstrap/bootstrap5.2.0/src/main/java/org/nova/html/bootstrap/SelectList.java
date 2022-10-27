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
import org.nova.html.tags.label;
import org.nova.html.tags.option;
import org.nova.html.tags.select;


public class SelectList extends TagElement<SelectList>
{
    final private select select;
    
    public SelectList(String id,String label) throws Exception
    {
        super("div");
        addClass("form-group");
        if (label!=null)
        {
            addInner(new label().for_(id).addInner(label));
        }
        this.select=returnAddInner(new select()).id(id);
    }
    
    public SelectList(String id) throws Exception
    {
        this(id,null);
    }
    public SelectList add(option option)
    {
        this.select.addInner(option);
        return this;
    }
    public SelectList add(Object option)
    {
        this.select.addInner(new option().addInner(option));
        return this;
    }
    public SelectList add(Object value,Object option)
    {
        this.select.addInner(new option().addInner(option).value(value));
        return this;
    }
    public SelectList add(Object value,Object option,boolean selected)
    {
        this.select.addInner(new option().addInner(option).value(value).selected(selected));
        return this;
    }
    public select select()
    {
        return this.select;
    }
}
