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
import org.nova.html.attributes.Color;
import org.nova.html.attributes.Size;
import org.nova.html.attributes.Style;
import org.nova.html.attributes.border_style;
import org.nova.html.attributes.text_align;
import org.nova.html.attributes.unit;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.ext.Text;
import org.nova.html.tags.div;

public class NameValueList extends GlobalEventTagElement<NameValueList>
{
    final private ArrayList<NameValue<Element>> list;
    private int longest;
    final private Size leftWidth;
    final private boolean frame;
    
    public NameValueList(Size leftWidth,boolean frame)
    {
        super("div");
        style("display:block;");
        this.list=new ArrayList<>();
        this.longest=0;
        this.leftWidth=leftWidth;
        this.frame=frame;
    }
    public NameValueList(Size leftWidth)
    {
        this(leftWidth,true);
    }
    public NameValueList()
    {
        this(null,true);
    }
    public NameValueList(boolean frame)
    {
        this(null,frame);
    }
    public NameValueList add(String name,Element element)
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
    public NameValueList add(String name,Object value)
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
       int width=(int)(this.longest*0.7f+1);
       Size size=this.leftWidth!=null?this.leftWidth:new Size(width,unit.em);
       Style valueStyle=new Style()
       .text_align(text_align.left)
       .width(new Size(100,unit.percent))
//       .border_right(new Size(0.1,unit.em),border_style.solid,Color.rgb(176, 176, 176))
       .margin_right(new Size(0.25,unit.em))
       .padding(new Size(0.6,unit.em),new Size(0.2,unit.em),new Size(0.2,unit.em),new Size(0,unit.em));

       for (int i=0;i<this.list.size();i++)
       {
           NameValue<Element> item=this.list.get(i);

           div line=returnAddInner(new div());
           if (this.frame)
           {
               if (i==0)
               {
                   line.style("display:flex;text-align:right;width:100%;border:1px solid #bbb;");
               }
               else if (i%2==0)
               {
                   line.style("display:flex;text-align:right;width:100%;border-bottom:1px solid #bbb;border-left:1px solid #bbb;border-right:1px solid #bbb;");
               }
               else 
               {
                   line.style("display:flex;text-align:right;width:100%;background-color:#f9f9f9;border-bottom:1px solid #bbb;border-left:1px solid #bbb;border-right:1px solid #bbb;");
               }
           }
           else
           {
               line.style("display:flex;text-align:right;");
           }
           String label=item.getName();
           if (label==null)
           {
               label="";
           }
           if (this.frame)
           {
               line.addInner(new div().style
                       (
                           new Style()
                           .width(size)
                           .border_right(new Size(0.1,unit.em),border_style.solid,Color.rgb(176, 176, 176))
                           .margin_right(new Size(0.25,unit.em))
                           .padding(new Size(0.6,unit.em),new Size(0.2,unit.em),new Size(0.2,unit.em),new Size(0,unit.em))
                       )
                       .addInner(label));
           }
           else
           {
               line.addInner(new div().style
                       (
                           new Style()
                           .width(size)
                           .margin_right(new Size(0.25,unit.em))
                           .padding(new Size(0.6,unit.em),new Size(0.2,unit.em),new Size(0.2,unit.em),new Size(0,unit.em))
                       )
                       .addInner(label));
           }
           line.addInner(new div().style(valueStyle).addInner(item.getValue()));
       }
       super.compose(builder);
    }
}
