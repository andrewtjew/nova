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
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.elements.Element;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.properties.Length;

public class NameValueColumns extends Item
{
    final private Item names;
    final private Item values;
    final private Element divider;
    final private TextAlign nameAlign;
    
    public NameValueColumns(Length nameSize,TextAlign nameAlign,StyleTemplate rowTemplate,Element divider)
    {
        d(Display.flex);
        this.names=returnAddInner(new Item()).flex(Flex.column).d(Display.flex);
        this.values=returnAddInner(new Item()).flex(Flex.column).d(Display.flex);
        if (rowTemplate!=null)
        {
            StyleTemplate.apply(rowTemplate,this.names);
            StyleTemplate.apply(rowTemplate,this.values);
        }
        this.nameAlign=nameAlign;
        if (nameSize!=null)
        {
            this.names.style("width:"+nameSize.toString()+";");
        }
        this.divider=divider;
    }
    public NameValueColumns(Length nameSize,TextAlign nameAlign,Element divider)
    {
        this(nameSize,nameAlign,null,divider);
    }
    public NameValueColumns(Length nameSize,TextAlign nameAlign)
    {
        this(nameSize,nameAlign,new LiteralHtml("&nbsp;:&nbsp;"));
    }
    public NameValueColumns()
    {
        this(null,TextAlign.end);
    }

    public NameValueColumns add(String name,Object value)
    {
        Label nameLabel=this.names.returnAddInner(new Label()).addInner(name).text(Text.nowrap);
        if (nameAlign!=null)
        {
            nameLabel.text(nameAlign);
        }
        Label valueLabel=this.values.returnAddInner(new Label());
        if (this.divider!=null)
        {
            valueLabel.addInner(this.divider);
        }
        valueLabel.addInner(value).text(Text.nowrap);
        return this;
    }
}
