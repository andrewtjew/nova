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
import org.nova.html.attributes.text_align;
import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.Component;
import org.nova.html.bootstrap.Container;
import org.nova.html.bootstrap.FormLabel;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Row;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.Table;
import org.nova.html.bootstrap.TableRow;
import org.nova.html.bootstrap.classes.DeviceClass;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Float_;
import org.nova.html.bootstrap.classes.Font;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.elements.Element;
import org.nova.html.tags.b;
import org.nova.html.tags.hr;
import org.nova.html.tags.td;


public class NameValueList extends StyleComponent<NameValueList>
{
    final private Size width;

    public NameValueList(Size width)
    {
        super("div",null);
        this.width=width;
    }

    public NameValueList add(Object name, Element element)
    {
        Item item=returnAddInner(new Item());
        item.d_flex().justify_content(Justify.start);
        item.addInner(new Item().mr(2).text(TextAlign.right).addInner(new b().addInner(name)).style(new Style().width(this.width)));
        item.addInner(element);
        return this;
    }
    public NameValueList add(Object name, Object value)
    {
        Item item=returnAddInner(new Item());
        item.d_flex().justify_content(Justify.start);
        item.addInner(new Item().mr(2).text(TextAlign.right).addInner(new b().addInner(name)).style(new Style().width(this.width)));
        item.addInner(new Item().addInner(value));
        
        return this;
    }
    public NameValueList add(Integer topSpacing,Integer bottomSpacing,Object name, Object value)
    {
        Item item=returnAddInner(new Item());
        item.d_flex().justify_content(Justify.start);
        item.addInner(new Item().mr(2).text(TextAlign.right).addInner(new b().addInner(name)).style(new Style().width(this.width)));
        item.addInner(new Item().addInner(value));
        if (topSpacing!=null)
        {
            item.mt(topSpacing);
        }
        if (bottomSpacing!=null)
        {
            item.mb(bottomSpacing);
        }
        return this;
    }
    public NameValueList add(Integer topSpacing,Integer bottomSpacing,Object name, Element element)
    {
        Item item=returnAddInner(new Item());
        item.d_flex().justify_content(Justify.start);
        item.addInner(new Item().mr(2).text(TextAlign.right).addInner(new b().addInner(name)).style(new Style().width(this.width)));
        item.addInner(element);
        if (topSpacing!=null)
        {
            item.mt(topSpacing);
        }
        if (bottomSpacing!=null)
        {
            item.mb(bottomSpacing);
        }
        return this;
    }

}
