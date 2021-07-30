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

import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.tags.div;
import org.nova.html.tags.label;
import org.nova.html.tags.option;
import org.nova.html.tags.select;


public class SelectList extends Element
{
    final private div div;
    final private select select;
    private StyleColor backgroundColor;
    private StyleColor textColor;
    private Integer borderSize;
    
    public SelectList(String id,String label)
    {
        this.div=new div().addClass("form-group");
        if (label!=null)
        {
            div.addInner(new label().for_(id).addInner(label));
        }
        this.select=this.div.returnAddInner(new select()).id(id);
    }
   
    public SelectList backgroundColor(StyleColor value)
    {
        this.backgroundColor=value;
        return this;
    }
    
    public SelectList textColor(StyleColor value)
    {
        this.textColor=value;
        return this;
    }
    
    public SelectList borderSize(int value)
    {
        this.borderSize=value;
        return this;
    }
    
    public SelectList(String id)
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
    @Override
    public void compose(Composer composer) throws Throwable
    {
        ClassBuilder cb=new ClassBuilder("form-control");
        cb.addIf(this.backgroundColor!=null,"bg",this.backgroundColor);
        cb.addIf(this.textColor!=null,"text",this.textColor);
        if (this.borderSize!=null)
        {
            cb.addClass("border",this.borderSize);
        }
        this.select.addClass(cb.toString());
        
        composer.compose(this.div);
    }
    
    public select select()
    {
        return this.select;
    }
}
