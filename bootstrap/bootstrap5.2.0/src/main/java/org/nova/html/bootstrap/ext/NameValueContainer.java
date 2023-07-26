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
package org.nova.html.bootstrap.ext;


import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.Container;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Row;
import org.nova.html.bootstrap.StyleTemplate;
import org.nova.html.bootstrap.Table;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.ext.TableRow;
import org.nova.html.tags.hr;
import org.nova.html.tags.tbody;
import org.nova.html.tags.td;

public class NameValueContainer extends Container
{
    final private StyleTemplate nameTemplate;
    final private StyleTemplate valueTemplate;
    private int nameSize;
    private int gutterSize;
    private tbody body;
    private boolean firstRow=true;
    
    public NameValueContainer(boolean fluid,int nameSize,StyleTemplate nameTemplate,StyleTemplate valueTemplate)
    {
        super(fluid);
        p(0);
        this.nameSize=nameSize;
        this.nameTemplate=nameTemplate;
        this.valueTemplate=valueTemplate;
    }
    public NameValueContainer(int nameSize,StyleTemplate nameTemplate,StyleTemplate valueTemplate)
    {
        this(true,nameSize,nameTemplate,valueTemplate);
    }
    public NameValueContainer(boolean fluid,int nameSize)
    {
        this(fluid,nameSize,null,null);
    }

    public NameValueContainer(int nameSize)
    {
        this(true,nameSize);
    }

    public NameValueContainer add(StyleTemplate nameTemplate,Object name,StyleTemplate valueTemplate,Object value)
    {
        Row row=this.returnAddInner(new Row()).align_items(AlignSelf.center);
        if (this.firstRow==false)
        {
            row.mt(2);
        }
        else
        {
            this.firstRow=false;
        }
        if (name instanceof Col==false)
        {
            name=new Col(this.nameSize).addInner(name);
        }
        if (value instanceof Col==false)
        {
            value=new Col().addInner(value);
        }
        
        row.addInner(StyleTemplate.apply(nameTemplate,name));
        row.addInner(StyleTemplate.apply(valueTemplate,value));
            
        return this;
    }
    public NameValueContainer add(Object name,Object value)
    {
        return add(this.nameTemplate,name,this.valueTemplate,value);
    }
    public NameValueContainer add(StyleTemplate nameTemplateOverride,Object name,Object value)
    {
        return add(nameTemplate,name,this.valueTemplate,value);
    }
    public NameValueContainer add(Object name,StyleTemplate valueTemplateOverride,Object value)
    {
        return add(this.nameTemplate,name,valueTemplate,value);
    }
    public NameValueContainer addDivider()
    {
        this.addInner(new hr());
        return this;
    }
    public NameValueContainer addLineSpace(int mt,int mb)
    {
        returnAddInner(new Row()).mt(mt).mb(mb);
        return this;
    }
    public NameValueContainer addLineSpace(int mt)
    {
        returnAddInner(new Row()).mt(mt);
        return this;
    }
    
}
