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
package org.nova.html.ext;
import org.nova.html.tags.html;
import org.nova.html.attributes.Color;
import org.nova.html.attributes.Size;
import org.nova.html.attributes.Style;
import org.nova.html.attributes.display;
import org.nova.html.attributes.overflow;
import org.nova.html.attributes.position;
import org.nova.html.attributes.unit;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.ext.DocType;
import org.nova.html.ext.Head;
import org.nova.html.tags.body;
import org.nova.html.tags.div;

public class ModalBackground extends GlobalEventTagElement<div>
{
	final private int z_index;
    public ModalBackground(int z_index,Color color,boolean show)
    {
        super("div");
        Style style=new Style()
        		.position(position.fixed)
        		.z_index(z_index)
        		.left(new Size(0,unit.px))
           		.top(new Size(0,unit.px))
        		.width(new Size(100,unit.percent))
           		.height(new Size(100,unit.percent))
           		.overflow(overflow.auto)
           		.background_color(color);
        if (show)
        {
        	style.display(display.block);
        }
        else
        {
        	style.display(display.none);
        }
        this.style(style);
        this.z_index=z_index;
    }
    public ModalBackground(Color color)
    {
    	this(1,color,false);
    }
    public ModalBackground()
    {
    	this(Color.rgba(0, 0, 0, 0.0f));
    }
    public String js_show(QuotationMark mark)
    {
    	return "document.getElementById("+mark+id()+mark+").style.display="+mark+"block"+mark;
    }
    public int z_index()
    {
    	return this.z_index;
    }
    public String js_show()
    {
    	return js_show(QuotationMark.SINGLE);
    }
    public String js_hide(QuotationMark mark)
    {
    	return "document.getElementById("+mark+id()+mark+").style.display="+mark+"none"+mark;
    }
    public String js_hide()
    {
    	return js_hide(QuotationMark.APOS);
    }
}
