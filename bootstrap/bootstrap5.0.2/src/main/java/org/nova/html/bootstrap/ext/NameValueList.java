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


import org.nova.html.attributes.Size;
import org.nova.html.attributes.Style;
import org.nova.html.attributes.display;
import org.nova.html.attributes.text_align;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.StyleTemplate;
import org.nova.html.bootstrap.Table;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Float_;
import org.nova.html.bootstrap.classes.Font;
import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.TableRow;
import org.nova.html.tags.tbody;
import org.nova.html.tags.td;

public class NameValueList extends Item
{
	final private Size nameSize;
    final private StyleTemplate nameTemplate;
    final private StyleTemplate valueTemplate;
    
    public NameValueList(Size nameSize,StyleTemplate nameTemplate,StyleTemplate valueTemplate)
    {
    	this.nameSize=nameSize;
        this.nameTemplate=nameTemplate;
        this.valueTemplate=valueTemplate;
    }
    public NameValueList(Size nameSize)
    {
        this(nameSize,null,null);
    }

    public NameValueList add(StyleTemplate nameTemplate,Object name,StyleTemplate valueTemplate,Object value)
    {
//        tr.addClass("table-striped");
    	Item row=returnAddInner(new Item()).d(Display.flex);
        if (name instanceof TagElement)
        {
            GlobalEventTagElement<?> tagElement=(GlobalEventTagElement<?>)name;
            tagElement.style("width:"+this.nameSize.toString()+";");
            row.addInner(StyleTemplate.apply(nameTemplate,tagElement));
        }
        else
        {
        	Label tagElement=new Label().addInner(name);
            tagElement.style("width:"+this.nameSize.toString()+";");
            row.addInner(StyleTemplate.apply(nameTemplate,tagElement));
        }
        if (value instanceof TagElement)
        {
            GlobalEventTagElement<?> tagElement=(GlobalEventTagElement<?>)value;
            row.addInner(StyleTemplate.apply(valueTemplate,tagElement));
        }
        else
        {
        	Label tagElement=new Label().addInner(value);
            tagElement.style("width:"+this.nameSize.toString()+";");
            row.addInner(StyleTemplate.apply(valueTemplate,tagElement));
        }
            
        return this;
    }
    public NameValueList add(Object name,Object value)
    {
        return add(this.nameTemplate,name,this.valueTemplate,value);
    }
    public NameValueList add(StyleTemplate nameTemplateOverride,Object name,Object value)
    {
        return add(nameTemplate,name,this.valueTemplate,value);
    }
    public NameValueList add(Object name,StyleTemplate valueTemplateOverride,Object value)
    {
        return add(this.nameTemplate,name,valueTemplate,value);
    }
}
