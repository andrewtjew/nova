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

public class Style
{
    final private StringBuilder sb;
    public Style()
    {
        this.sb=new StringBuilder();
    }
    public Style width(Size size)
    {
        sb.append("width:"+size+";");
        return this;
    }
    public Style text(String text)
    {
        sb.append(text);
        return this;
    }
    public Style height(Size size)
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
    public Style border(Size size,BorderStyleRect borderStyle,ColorRect color)
    {
        sb.append("border:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border(Size size,BorderStyleRect borderStyle)
    {
        sb.append("border:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_left(Size size,border_style borderStyle,Color color)
    {
        sb.append("border-left:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_top(Size size,border_style borderStyle,Color color)
    {
        sb.append("border-top:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_right(Size size,border_style borderStyle,Color color)
    {
        sb.append("border-right:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_bottom(Size size,border_style borderStyle,Color color)
    {
        sb.append("border-bottom:"+size+" "+borderStyle+" "+color+";");
        return this;
    }
    public Style border_left(Size size,border_style borderStyle)
    {
        sb.append("border-left:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_top(Size size,border_style borderStyle)
    {
        sb.append("border-top:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_right(Size size,border_style borderStyle)
    {
        sb.append("border-right:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_bottom(Size size,border_style borderStyle)
    {
        sb.append("border-bottom:"+size+" "+borderStyle+";");
        return this;
    }
    public Style border_left(Size size)
    {
        sb.append("border-left:"+size+";");
        return this;
    }
    public Style border_top(Size size)
    {
        sb.append("border-top:"+size+";");
        return this;
    }
    public Style border_right(Size size)
    {
        sb.append("border-right:"+size+";");
        return this;
    }
    public Style border_bottom(Size size)
    {
        sb.append("border-bottom:"+size+";");
        return this;
    }

    public Style border(Size size)
    {
        sb.append("border:"+size+";");
        return this;
    }

    public Style margin(Size size)
    {
        sb.append("margin:"+size+";");
        return this;
    }
    public Style margin_auto(Size size)
    {
        sb.append("margin:"+size+" auto;");
        return this;
    }
    public Style margin_auto()
    {
        sb.append("margin:0 auto;");
        return this;
    }
    public Style margin_top(Size size)
    {
        sb.append("margin-top:"+size+";");
        return this;
    }
    public Style margin_right(Size size)
    {
        sb.append("margin-right:"+size+";");
        return this;
    }
    public Style margin_bottom(Size size)
    {
        sb.append("margin-bottom:"+size+";");
        return this;
    }
    public Style margin_left(Size size)
    {
        sb.append("margin-left:"+size+";");
        return this;
    }
    public Style margin(Size top,Size right,Size bottom,Size left)
    {
        sb.append("margin:"+top+" "+right+" "+bottom+" "+left+";");
        return this;
    }
    public Style padding(Size size)
    {
        sb.append("padding:"+size+";");
        return this;
    }
    public Style padding_top(Size size)
    {
        sb.append("padding-top:"+size+";");
        return this;
    }
    public Style padding_right(Size size)
    {
        sb.append("padding-right:"+size+";");
        return this;
    }
    public Style padding_bottom(Size size)
    {
        sb.append("padding-bottom:"+size+";");
        return this;
    }
    public Style padding_left(Size size)
    {
        sb.append("padding-left:"+size+";");
        return this;
    }
    public Style padding(Size top,Size right,Size bottom,Size left)
    {
        sb.append("padding:"+top+" "+right+" "+bottom+" "+left+";");
        return this;
    }

    public Style display(display value)
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
    public Style flex_basis(Size value)
    {
        sb.append("flex-basis:"+value.toString()+";");
        return this;
    }
    public Style flex_basis(flex_basis value)
    {
        sb.append("flex-basis:"+value.toString()+";");
        return this;
    }
    /*
    public Style flex_flow(flex_direction direction,flex_wrap wrap)
    {
        sb.append("flex-flow:"+direction.toString()+" "+wrap.toString()+";");
        return this;
    }
    */
    public Style flex_direction(flex_direction value)
    {
        sb.append("flex-direction:"+value.toString()+";");
        return this;
    }
    public Style flex_wrap(flex_wrap value)
    {
        sb.append("flex-wrap:"+value.toString()+";");
        return this;
    }
    
    public Style position(position value)
    {
        sb.append("position:"+value+";");
        return this;
    }
    public Style float_(float_ value)
    {
        sb.append("float:"+value+";");
        return this;
    }
    public Style vertical_align(vertical_align value)
    {
        sb.append("vertical-align:"+value+";");
        return this;
    }
    public Style vertical_align(Size value)
    {
        sb.append("vertical-align:"+value+";");
        return this;
    }
    public Style text_decoration(text_decoration value)
    {
        sb.append("text-decoration:"+value+";");
        return this;
    }
    public Style text_align(text_align value)
    {
        sb.append("text-align:"+value+";");
        return this;
    }
    public Style text_align_last(text_align value)
    {
        sb.append("text-align-last:"+value+";");
        return this;
    }
    public Style list_style(list_style value)
    {
        sb.append("list-style:"+value+";");
        return this;
    }
    public Style z_index(int value)
    {
        sb.append("z-index:"+value+";");
        return this;
    }
    public Style top(Size value)
    {
        sb.append("top:"+value+";");
        return this;
    }
    public Style left(Size value)
    {
        sb.append("left:"+value+";");
        return this;
    }
    public Style font_weight(font_weight value)
    {
        sb.append("font-weight:"+value+";");
        return this;
    }
    public Style font_size(Size value)
    {
        sb.append("font-size:"+value+";");
        return this;
    }
    public Style overflow(overflow value)
    {
        sb.append("overflow:"+value+";");
        return this;
    }
    public Style overflow_x(overflow value)
    {
        sb.append("overflow-x:"+value+";");
        return this;
    }
    public Style overflow_y(overflow value)
    {
        sb.append("overflow-y:"+value+";");
        return this;
    }
    public Style white_space(white_space value)
    {
        sb.append("white-space:"+value+";");
        return this;
    }
    
    @Override
    public String toString()
    {
        return this.sb.toString();
    }
}
