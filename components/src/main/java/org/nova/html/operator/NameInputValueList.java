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
package org.nova.html.operator;

import java.util.ArrayList;

import org.nova.core.NameValue;
import org.nova.html.attributes.Size;
import org.nova.html.attributes.Style;
import org.nova.html.attributes.display;
import org.nova.html.attributes.font_weight;
import org.nova.html.attributes.text_align;
import org.nova.html.attributes.unit;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.ext.Text;
import org.nova.html.tags.div;

public class NameInputValueList extends div
{
    final private ArrayList<NameValue<Element>> list;
    private int longest;
    final private Size leftWidth;
    final private Size rightWidth;
    

    public NameInputValueList()
    {
        this(null,null);
    }
    public NameInputValueList(Size leftWidth,Size rightWidth)
    {
        style("display:block;");
        this.list=new ArrayList<>();
        this.longest=0;
        this.leftWidth=leftWidth;
        this.rightWidth=rightWidth;
    }
    public NameInputValueList add(String name,Element element)
    {
        this.list.add(new NameValue<Element>(name,element));
        if (name!=null)
        {
            if (name.length()>longest)
            {
                this.longest=name.length();
            }
        }
        return this;
    }
    public NameInputValueList add(String name,Object value)
    {
        if (value!=null)
        {
            return add(name, new Text(value.toString()));
        }
        return add(name, new Text(null));
    }
    @Override
    public void compose(Composer builder) throws Throwable
    {
        Size leftWidth=this.leftWidth==null?new Size((int)((this.longest+2)),unit.em):this.leftWidth;
        Size rightWidth=this.rightWidth==null?new Size(100,unit.percent):this.rightWidth;
       for (int i=0;i<this.list.size();i++)
       {
           NameValue<Element> item=this.list.get(i);

           div line=returnAddInner(new div().style("display:flex;text-align:right;width:100%;margin:0.25em;"));
           String label=item.getName();
           if (label==null)
           {
               label="";
           }
           else
           {
               label+=":";
           }
           line.addInner(new div().style(new Style().width(leftWidth).padding_right(new Size(0.5,unit.em)).margin_left(new Size(0.5,unit.em)).font_weight(font_weight.bold)).addInner(label));
                   //"width:"+width+"em;padding-right:0.5em;margin-left:0.5em;font-weight:bold;").addInner(label));
//           line.addInner(new div().style("width:100%;text-align:left;display:block;").addInner(item.getValue()));
//           line.addInner(new div().style("width:100%;text-align:left;display:block;").addInner(item.getValue()).addInner(new div().style("display:none;padding:0.5em;margin-bottom:0.5em;background-color:#fdd;")));
           line.addInner(new div().style(new Style().width(rightWidth).text_align(text_align.left).display(display.block)).addInner(item.getValue()).addInner(new div().style("display:none;padding:0.5em;margin-bottom:0.5em;background-color:#fdd;")));
       }
       super.compose(builder);
    }
    public int size()
    {
        return this.list.size();
    }
}
