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
package org.nova.html.attributes;

import org.nova.html.properties.BorderBoxStyle;
import org.nova.html.properties.BorderStyle;
import org.nova.html.properties.BoxColor;
import org.nova.html.properties.Color;
import org.nova.html.properties.Display_;
import org.nova.html.properties.FlexBasis;
import org.nova.html.properties.FlexDirection;
import org.nova.html.properties.FlexWrap;
import org.nova.html.properties.Float_;
import org.nova.html.properties.FontWeight;
import org.nova.html.properties.ListStyle;
import org.nova.html.properties.Overflow;
import org.nova.html.properties.Position;
import org.nova.html.properties.Property;
import org.nova.html.properties.Length;
import org.nova.html.properties.TextAlign;
import org.nova.html.properties.TextDecoration;
import org.nova.html.properties.VerticalAlign;
import org.nova.html.properties.WhiteSpace;

public class Style
{
    final private StringBuilder sb;
    public Style()
    {
        this.sb=new StringBuilder();
    }
    public Style property(Property...properties)
    {
        for (Property property:properties)
        {
            this.sb.append(property);
        }
        return this;
    }
    public Style width(Length size)
    {
        sb.append("width:"+size+";");
        return this;
    }
    public Style text(String text)
    {
        sb.append(text);
        return this;
    }
    public Style height(Length size)
    {
        sb.append("height:"+size+";");
        return this;
    }
    public Style color(Color color)
    {
        sb.append("color:"+color+";");
        return this;
    }
    public Style background_color(Color color)
    {
        sb.append("background-color:"+color+";");
        return this;
    }
    public Style border(Length size,BorderBoxStyle borderStyle,BoxColor color)
    {
        sb.append("border:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border(Length size,BorderBoxStyle borderStyle)
    {
        sb.append("border:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_left(Length size,BorderStyle borderStyle,Color color)
    {
        sb.append("border-left:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_top(Length size,BorderStyle borderStyle,Color color)
    {
        sb.append("border-top:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_right(Length size,BorderStyle borderStyle,Color color)
    {
        sb.append("border-right:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_bottom(Length size,BorderStyle borderStyle,Color color)
    {
        sb.append("border-bottom:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_left(Length size,BorderStyle borderStyle)
    {
        sb.append("border-left:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_top(Length size,BorderStyle borderStyle)
    {
        sb.append("border-top:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_right(Length size,BorderStyle borderStyle)
    {
        sb.append("border-right:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_bottom(Length size,BorderStyle borderStyle)
    {
        sb.append("border-bottom:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_left(Length size)
    {
        sb.append("border-left:"+size+";");
        return this;
    }
    public Style border_top(Length size)
    {
        sb.append("border-top:"+size+";");
        return this;
    }
    public Style border_right(Length size)
    {
        sb.append("border-right:"+size+";");
        return this;
    }
    public Style border_bottom(Length size)
    {
        sb.append("border-bottom:"+size+";");
        return this;
    }

    public Style border(Length size)
    {
        sb.append("border:"+size+";");
        return this;
    }
    public Style border_radius(Length size)
    {
        sb.append("border-radius:"+size+";");
        return this;
    }

    public Style margin(Length size)
    {
        sb.append("margin:"+size+";");
        return this;
    }
    public Style margin_auto(Length size)
    {
        sb.append("margin:"+size+" auto;");
        return this;
    }
    public Style margin_auto()
    {
        sb.append("margin:0 auto;");
        return this;
    }
    public Style margin_top(Length size)
    {
        sb.append("margin-top:"+size+";");
        return this;
    }
    public Style margin_right(Length size)
    {
        sb.append("margin-right:"+size+";");
        return this;
    }
    public Style margin_bottom(Length size)
    {
        sb.append("margin-bottom:"+size+";");
        return this;
    }
    public Style margin_left(Length size)
    {
        sb.append("margin-left:"+size+";");
        return this;
    }
    public Style margin(Length top,Length right,Length bottom,Length left)
    {
        sb.append("margin:"+top+" "+right+" "+bottom+" "+left+";");
        return this;
    }
    public Style padding(Length size)
    {
        sb.append("padding:"+size+";");
        return this;
    }
    public Style padding_top(Length size)
    {
        sb.append("padding-top:"+size+";");
        return this;
    }
    public Style padding_right(Length size)
    {
        sb.append("padding-right:"+size+";");
        return this;
    }
    public Style padding_bottom(Length size)
    {
        sb.append("padding-bottom:"+size+";");
        return this;
    }
    public Style padding_left(Length size)
    {
        sb.append("padding-left:"+size+";");
        return this;
    }
    public Style padding(Length top,Length right,Length bottom,Length left)
    {
        sb.append("padding:"+top+" "+right+" "+bottom+" "+left+";");
        return this;
    }

    public Style display(Display_ value)
    {
        sb.append("display:"+value.toString()+";");
        return this;
    }
    public Style flex_growth(int value)
    {
        sb.append("flex-growth:"+value+";");
        return this;
    }
    public Style flex_shrink(int value)
    {
        sb.append("flex-shrink:"+value+";");
        return this;
    }
    public Style flex_basis(Length value)
    {
        sb.append("flex-basis:"+value.toString()+";");
        return this;
    }
    public Style flex_basis(FlexBasis value)
    {
        sb.append("flex-basis:"+value.toString()+";");
        return this;
    }

    public Style flex_direction(FlexDirection value)
    {
        sb.append("flex-direction:"+value.toString()+";");
        return this;
    }
    public Style flex_wrap(FlexWrap value)
    {
        sb.append("flex-wrap:"+value.toString()+";");
        return this;
    }
    
    public Style position(Position value)
    {
        sb.append("position:"+value+";");
        return this;
    }
    public Style float_(Float_ value)
    {
        sb.append("float:"+value+";");
        return this;
    }
    public Style vertical_align(VerticalAlign value)
    {
        sb.append("vertical-align:"+value+";");
        return this;
    }
    public Style vertical_align(Length value)
    {
        sb.append("vertical-align:"+value+";");
        return this;
    }
    public Style text_decoration(TextDecoration value)
    {
        sb.append("text-decoration:"+value+";");
        return this;
    }
    public Style text_align(TextAlign value)
    {
        sb.append("text-align:"+value+";");
        return this;
    }
    public Style text_align_last(TextAlign value)
    {
        sb.append("text-align-last:"+value+";");
        return this;
    }
    public Style list_style(ListStyle value)
    {
        sb.append("list-style:"+value+";");
        return this;
    }
    public Style z_index(int value)
    {
        sb.append("z-index:"+value+";");
        return this;
    }
    public Style top(Length value)
    {
        sb.append("top:"+value+";");
        return this;
    }
    public Style left(Length value)
    {
        sb.append("left:"+value+";");
        return this;
    }
    public Style font_weight(FontWeight value)
    {
        sb.append("font-weight:"+value+";");
        return this;
    }
    public Style font_size(Length value)
    {
        sb.append("font-size:"+value+";");
        return this;
    }
    public Style overflow(Overflow value)
    {
        sb.append("overflow:"+value+";");
        return this;
    }
    public Style overflow_x(Overflow value)
    {
        sb.append("overflow-x:"+value+";");
        return this;
    }
    public Style overflow_y(Overflow value)
    {
        sb.append("overflow-y:"+value+";");
        return this;
    }
    public Style white_space(WhiteSpace value)
    {
        sb.append("white-space:"+value+";");
        return this;
    }
    public Style add(String name,String value)
    {
        sb.append(name+":"+value+";");
        return this;
    }
    
    @Override
    public String toString()
    {
        return this.sb.toString();
    }
}
