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
package org.nova.html.properties;

@Deprecated
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
    public Style width(Length_ size)
    {
        sb.append(new width(size));
        return this;
    }
    public Style height(Length_ size)
    {
        sb.append("height:"+size+";");
        return this;
    }
    public Style color(Color_ color)
    {
        sb.append("color:"+color+";");
        return this;
    }
//    public Style text(String text)
//    {
//        sb.append(text);
//        return this;
//    }
    public Style background_color(Color_ color)
    {
        sb.append(new background_color(color));
        return this;
    }
    public Style border(Length_ size,BorderBoxStyle_ borderStyle,BoxColor_ color)
    {
        sb.append("border:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border(Length_ size,BorderBoxStyle_ borderStyle)
    {
        sb.append("border:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_left(Length_ size,BorderStyle_ borderStyle,Color_ color)
    {
        sb.append("border-left:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_top(Length_ size,BorderStyle_ borderStyle,Color_ color)
    {
        sb.append("border-top:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_right(Length_ size,BorderStyle_ borderStyle,Color_ color)
    {
        sb.append("border-right:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_bottom(Length_ size,BorderStyle_ borderStyle,Color_ color)
    {
        sb.append("border-bottom:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_left(Length_ size,BorderStyle_ borderStyle)
    {
        sb.append("border-left:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_top(Length_ size,BorderStyle_ borderStyle)
    {
        sb.append("border-top:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_right(Length_ size,BorderStyle_ borderStyle)
    {
        sb.append("border-right:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_bottom(Length_ size,BorderStyle_ borderStyle)
    {
        sb.append("border-bottom:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_left(Length_ size)
    {
        sb.append("border-left:"+size+";");
        return this;
    }
    public Style border_top(Length_ size)
    {
        sb.append("border-top:"+size+";");
        return this;
    }
    public Style border_right(Length_ size)
    {
        sb.append("border-right:"+size+";");
        return this;
    }
    public Style border_bottom(Length_ size)
    {
        sb.append("border-bottom:"+size+";");
        return this;
    }

    public Style border(Length_ size)
    {
        sb.append("border:"+size+";");
        return this;
    }
    public Style border_radius(Length_ size)
    {
        sb.append("border-radius:"+size+";");
        return this;
    }

    public Style margin(Length_ size)
    {
        sb.append("margin:"+size+";");
        return this;
    }
    public Style margin_auto(Length_ size)
    {
        sb.append("margin:"+size+" auto;");
        return this;
    }
    public Style margin_auto()
    {
        sb.append("margin:0 auto;");
        return this;
    }
    public Style margin_top(Length_ size)
    {
        sb.append("margin-top:"+size+";");
        return this;
    }
    public Style margin_right(Length_ size)
    {
        sb.append("margin-right:"+size+";");
        return this;
    }
    public Style margin_bottom(Length_ size)
    {
        sb.append("margin-bottom:"+size+";");
        return this;
    }
    public Style margin_left(Length_ size)
    {
        sb.append("margin-left:"+size+";");
        return this;
    }
    public Style margin(Length_ top,Length_ right,Length_ bottom,Length_ left)
    {
        sb.append("margin:"+top+" "+right+" "+bottom+" "+left+";");
        return this;
    }
    public Style padding(Length_ size)
    {
        sb.append("padding:"+size+";");
        return this;
    }
    public Style padding_top(Length_ size)
    {
        sb.append("padding-top:"+size+";");
        return this;
    }
    public Style padding_right(Length_ size)
    {
        sb.append("padding-right:"+size+";");
        return this;
    }
    public Style padding_bottom(Length_ size)
    {
        sb.append("padding-bottom:"+size+";");
        return this;
    }
    public Style padding_left(Length_ size)
    {
        sb.append("padding-left:"+size+";");
        return this;
    }
    public Style padding(Length_ top,Length_ right,Length_ bottom,Length_ left)
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
    public Style flex_basis(Length_ value)
    {
        sb.append("flex-basis:"+value.toString()+";");
        return this;
    }
    public Style flex_basis(FlexBasis_ value)
    {
        sb.append("flex-basis:"+value.toString()+";");
        return this;
    }

    public Style flex_direction(FlexDirection_ value)
    {
        sb.append("flex-direction:"+value.toString()+";");
        return this;
    }
    public Style flex_wrap(FlexWrap_ value)
    {
        sb.append("flex-wrap:"+value.toString()+";");
        return this;
    }
    
    public Style position(Position_ value)
    {
        sb.append("position:"+value+";");
        return this;
    }
    public Style float_(Float_ value)
    {
        sb.append("float:"+value+";");
        return this;
    }
    public Style vertical_align(VerticalAlign_ value)
    {
        sb.append("vertical-align:"+value+";");
        return this;
    }
    public Style vertical_align(Length_ value)
    {
        sb.append("vertical-align:"+value+";");
        return this;
    }
    public Style text_decoration(TextDecoration_ value)
    {
        sb.append("text-decoration:"+value+";");
        return this;
    }
    public Style text_align(TextAlign_ value)
    {
        sb.append("text-align:"+value+";");
        return this;
    }
    public Style text_align_last(TextAlign_ value)
    {
        sb.append("text-align-last:"+value+";");
        return this;
    }
    public Style list_style(ListStyle_ value)
    {
        sb.append("list-style:"+value+";");
        return this;
    }
    public Style z_index(int value)
    {
        sb.append("z-index:"+value+";");
        return this;
    }
    public Style top(Length_ value)
    {
        sb.append("top:"+value+";");
        return this;
    }
    public Style left(Length_ value)
    {
        sb.append("left:"+value+";");
        return this;
    }
    public Style font_weight(FontWeight_ value)
    {
        sb.append("font-weight:"+value+";");
        return this;
    }
    public Style font_size(Length_ value)
    {
        sb.append("font-size:"+value+";");
        return this;
    }
    public Style overflow(Overflow_ value)
    {
        sb.append("overflow:"+value+";");
        return this;
    }
    public Style overflow_x(Overflow_ value)
    {
        sb.append("overflow-x:"+value+";");
        return this;
    }
    public Style overflow_y(Overflow_ value)
    {
        sb.append("overflow-y:"+value+";");
        return this;
    }
    public Style white_space(WhiteSpace_ value)
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
