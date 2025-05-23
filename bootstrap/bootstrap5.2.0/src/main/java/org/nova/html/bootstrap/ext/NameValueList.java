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


import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.StyleTemplate;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.TagElement;
import org.nova.html.properties.Length;
import org.nova.html.tags.hr;

public class NameValueList extends Item
{
	final private Length nameSize;
    final private StyleTemplate nameTemplate;
    final private StyleTemplate valueTemplate;
    final private Integer lineSpace;
    final private Object divider;
    private int lines;
    
    
    public NameValueList(Length nameSize,StyleTemplate nameTemplate,Object divider,StyleTemplate valueTemplate,Integer lineSpace)
    {
    	this.nameSize=nameSize;
        this.nameTemplate=nameTemplate;
        this.valueTemplate=valueTemplate;
        this.lineSpace=lineSpace;
        this.divider=divider;
    }
    public NameValueList(Length nameSize,StyleTemplate nameTemplate,StyleTemplate valueTemplate,Integer lineSpace)
    {
        this(nameSize,nameTemplate,null,valueTemplate,lineSpace);
    }
    public NameValueList(Length nameSize,StyleTemplate nameTemplate,StyleTemplate valueTemplate)
    {
        this(nameSize,nameTemplate,null,valueTemplate,null);
    }
    public NameValueList(Length nameSize,Integer lineSpace)
    {
        this(nameSize,null,null,null,lineSpace);
    }
    public NameValueList(Length nameSize)
    {
        this(nameSize,null,null,null,null);
    }
    public NameValueList(Length nameSize,Object divider)
    {
        this(nameSize,null,divider,null,null);
    }

    public NameValueList add(StyleTemplate nameTemplate,Object name,Object divider,StyleTemplate valueTemplate,Object value)
    {
        if ((lines>0)&&(this.lineSpace!=null))
        {
            addLineSpace(this.lineSpace);
        }
        lines++;
//        tr.addClass("table-striped");
    	Item row=returnAddInner(new Item()).d(Display.flex);
        if (name instanceof TagElement)
        {
            GlobalEventTagElement<?> tagElement=(GlobalEventTagElement<?>)name;
            tagElement.style("width:"+this.nameSize.toString()+";white-space:nowrap;");
            row.addInner(StyleTemplate.apply(nameTemplate,tagElement));
        }
        else
        {
        	Label tagElement=new Label().addInner(name);
            tagElement.style("width:"+this.nameSize.toString()+"!important;white-space:nowrap;");
            row.addInner(StyleTemplate.apply(nameTemplate,tagElement));
        }
        if (divider==null)
        {
            divider=this.divider;
            if (divider!=null)
            {
                row.addInner(divider);
            }
        }
        if (value instanceof TagElement)
        {
            GlobalEventTagElement<?> tagElement=(GlobalEventTagElement<?>)value;
            row.addInner(StyleTemplate.apply(valueTemplate,tagElement));
        }
//        else if (value instanceof String)
//        {
//            
//        }
        else
        {
        	Label tagElement=new Label().addInner(value);
//            tagElement.style("width:"+this.nameSize.toString()+";");
            row.addInner(StyleTemplate.apply(valueTemplate,tagElement));
        }
            
        return this;
    }
    
    public NameValueList addDivider()
    {
        this.addInner(new hr());
        return this;
    }
    public NameValueList addLineSpace(int space)
    {
        Item row=returnAddInner(new Item()).mt(space);
        return this;
    }
    
    public NameValueList add(Object name,Object value)
    {
        return add(name,null,value);
    }
    public NameValueList add(Object name,Object divider,Object value)
    {
        return add(this.nameTemplate,name,divider,this.valueTemplate,value);
    }
    public NameValueList add(StyleTemplate nameTemplateOverride,Object name,Object divider,Object value)
    {
        return add(nameTemplate,name,divider,this.valueTemplate,value);
    }
    public NameValueList add(Object name,Object divider,StyleTemplate valueTemplateOverride,Object value)
    {
        return add(this.nameTemplate,name,divider,valueTemplate,value);
    }
}
