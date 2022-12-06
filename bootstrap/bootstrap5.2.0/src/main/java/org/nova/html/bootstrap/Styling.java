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
package org.nova.html.bootstrap;

import org.nova.html.bootstrap.classes.Align;
import org.nova.html.bootstrap.classes.AlignItems;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.FontWeight;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Float_;
import org.nova.html.bootstrap.classes.FontStyle;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.Overflow;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.classes.Rounded;
import org.nova.html.bootstrap.classes.Size;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.bootstrap.classes.Translate;
import org.nova.html.elements.TagElement;

public interface Styling<ELEMENT>
{
//    final private TagElement<?> element;
    public ELEMENT addClass(Object class_,Object...fragments);
    public TagElement<?> getElement();


    public default ELEMENT col(BreakPoint breakPoint,int columns)
    {
        if (breakPoint==BreakPoint.xs)
        {
            return addClass("col",columns);
        }
        return addClass("col",breakPoint,columns);
    }
    public default ELEMENT col(BreakPoint breakPoint)
    {
        if (breakPoint==BreakPoint.xs)
        {
            return addClass("col");
        }
        return addClass("col",breakPoint);
    }
    public default ELEMENT col(int columns)
    {
        return addClass("col",columns);
    }
    public default ELEMENT col()
    {
        return addClass("col");
    }
    public default ELEMENT col_form_label()
    {
        return addClass("col-form-label");
    }
    public default ELEMENT row()
    {
        return addClass("row");
    }
    public default ELEMENT float_(BreakPoint breakPoint,Float_ value)
    {
        return addClass("float",breakPoint,value);
    }
    public default ELEMENT form_floating()
    {
        return addClass("form-floating");
    }
    public default ELEMENT form_range()
    {
        return addClass("form-range");
    }
    public default ELEMENT form_check()
    {
        return addClass("form-check");
    }
    public default ELEMENT form_check_label()
    {
        return addClass("form-check-label");
    }
    public default ELEMENT form_check_inline()
    {
        return addClass("form-check-inline");
    }
    public default ELEMENT form_switch()
    {
        return addClass("form-switch");
    }
    public default ELEMENT form_select()
    {
        return addClass("form-select");
    }
    public default ELEMENT form_label()
    {
        return addClass("form-label");
    }
    public default ELEMENT form_text()
    {
        return addClass("form-text");
    }
    public default ELEMENT form_control()
    {
        return addClass("form-control");
    }
    public default ELEMENT form_control(Size size)
    {
        return addClass("form-control",size.toString());
    }
    public default ELEMENT form_control(BreakPoint breakPoint)
    {
        return addClass("form-control",breakPoint);
    }
    public default ELEMENT invalid_feeback()
    {
        return addClass("invalid-feedback");
    }
    public default ELEMENT valid_feeback()
    {
        return addClass("valid-feedback");
    }
    public default ELEMENT bg(StyleColor value)
    {
        return addClass("bg",value);
    }
    public default ELEMENT bg_gradient(StyleColor value)
    {
        return addClass("bg","gradient",value);
    }
    public default ELEMENT text(StyleColor value)
    {
        return addClass("text",value);
    }
    public default ELEMENT text(TextAlign value)
    {
        return addClass("text",value);
    }
    public default ELEMENT text(BreakPoint breakPoint,TextAlign value)
    {
        return addClass("text",breakPoint,value);
    }
    public default ELEMENT text(Text value)
    {
        return addClass("text",value);
    }
//    public default ELEMENT font(Font value)
//    {
//        return addClass("fw",value);
//    }
    public default ELEMENT lead()
    {
        return addClass("lead");
    }
    public default ELEMENT small()
    {
        return addClass("small");
    }
    public default ELEMENT float_(Float_ value)
    {
        return addClass("float",value);
    }
    public default ELEMENT offset(int offset)
    {
        return addClass("offset",offset);
    }
    public default ELEMENT display(int size)
    {
        return addClass("display",size);
    }
    public default ELEMENT rounded()
    {
        return addClass("rounded");
    }
    public default ELEMENT rounded(int value)
    {
        return addClass("rounded",value);
    }
    public default ELEMENT rounded(Rounded rounded,int value)
    {
        return addClass("rounded",rounded,value);
    }
    public default ELEMENT rounded(Rounded rounded)
    {
        return addClass("rounded",rounded);
    }
    public default ELEMENT border()
    {
        return addClass("border");
    }
    public default ELEMENT border(Edge edge)
    {
        return addClass("border",edge);
    }
    public default ELEMENT border(Edge edge,boolean subtract)
    {
        if (subtract)
        {
            return addClass("border",edge,0);
        }
        return addClass("border",edge);
    }

    public default ELEMENT border(int width)
    {
        addClass("border");
        return addClass("border",width);
    }
    public default ELEMENT border(StyleColor color)
    {
        return addClass("border",color);
    }
    public default ELEMENT clearfix()
    {
        return addClass("clearfix");
    }
    public default ELEMENT flex(Flex flex)
    {
        return addClass("flex",flex);
    }
    public default ELEMENT flex(Flex flex,int value)
    {
        return addClass("flex",flex,value);
    }

    public default ELEMENT flex(BreakPoint breakPoint,Flex flex)
    {
        return addClass("flex",breakPoint,flex);
    }

    public default ELEMENT align_self(AlignSelf value)
    {
        return addClass("align-self",value);
    }
    
    public default ELEMENT align_self(BreakPoint breakPoint,AlignSelf value)
    {
        return addClass("align-self",breakPoint,value);
    }
    
    public default ELEMENT align_items(AlignItems value)
    {
        return addClass("align-items",value);
    }
    
    public default ELEMENT align(Align value)
    {
        return addClass("align",value);
    }
    
    public default ELEMENT order(int value)
    {
        return addClass("order",value);
    }
    
    public default ELEMENT me(BreakPoint breakPoint,int value)
    {
        return addClass("me",breakPoint,value);
    }
    public default ELEMENT me(int value)
    {
        return addClass("me",value);
    }
    public default ELEMENT ms(int value)
    {
        return addClass("ms",value);
    }
    public default ELEMENT mt(int value)
    {
        return addClass("mt",value);
    }
    public default ELEMENT mb(int value)
    {
        return addClass("mb",value);
    }
    public default ELEMENT mx(int value)
    {
        return addClass("mx",value);
    }
    public default ELEMENT my(int value)
    {
        return addClass("my",value);
    }
    public default ELEMENT m(int value)
    {
        return addClass("m",value);
    }
    //----
    public default ELEMENT mt(BreakPoint breakPoint,int value)
    {
        return addClass("mt",breakPoint,value);
    }
    public default ELEMENT mb(BreakPoint breakPoint,int value)
    {
        return addClass("mb",breakPoint,value);
    }
    public default ELEMENT mx(BreakPoint breakPoint,int value)
    {
        return addClass("mx",breakPoint,value);
    }
    public default ELEMENT my(BreakPoint breakPoint,int value)
    {
        return addClass("my",breakPoint,value);
    }
    public default ELEMENT m(BreakPoint breakPoint,int value)
    {
        return addClass("m",breakPoint,value);
    }
    
    public default ELEMENT mt_auto()
    {
        return addClass("mt","auto");
    }
    public default ELEMENT mb_auto()
    {
        return addClass("mb","auto");
    }
    public default ELEMENT mx_auto()
    {
        return addClass("mx","auto");
    }
    public default ELEMENT my_auto()
    {
        return addClass("my","auto");
    }
    public default ELEMENT me_auto()
    {
        return addClass("me","auto");
    }
    public default ELEMENT ms_auto()
    {
        return addClass("ms","auto");
    }

    public default ELEMENT pe(int value)
    {
        return addClass("pe",value);
    }
    public default ELEMENT ps(int value)
    {
        return addClass("ps",value);
    }
    public default ELEMENT pt(int value)
    {
        return addClass("pt",value);
    }
    public default ELEMENT pb(int value)
    {
        return addClass("pb",value);
    }
    public default ELEMENT px(int value)
    {
        return addClass("px",value);
    }
    public default ELEMENT py(int value)
    {
        return addClass("py",value);
    }
    public default ELEMENT p(int value)
    {
        return addClass("p",value);
    }
    public default ELEMENT pe_auto()
    {
        return addClass("pe","auto");
    }
    public default ELEMENT ps_auto()
    {
        return addClass("ps","auto");
    }
    public default ELEMENT pt_auto()
    {
        return addClass("pt","auto");
    }
    public default ELEMENT pb_auto()
    {
        return addClass("pb","auto");
    }
    public default ELEMENT px_auto()
    {
        return addClass("px","auto");
    }
    public default ELEMENT py_auto()
    {
        return addClass("py","auto");
    }

    public default ELEMENT d(Display display)
    {
        return addClass("d",display);
        
    }
    public default ELEMENT d(BreakPoint breakPoint,Display display)
    {
        return addClass("d",breakPoint,display);
        
    }
    
    public default ELEMENT w(int value)
    {
        return addClass("w",value);
    }
    public default ELEMENT mw(int value)
    {
        return addClass("mw",value);
    }
    
    public default ELEMENT h(int value)
    {
        return addClass("h",value);
    }
    public default ELEMENT h_auto()
    {
        return addClass("h","auto");
    }
    public default ELEMENT mh(int value)
    {
        return addClass("mh",value);
    }
    
    public default ELEMENT position(Position value)
    {
        return addClass("position",value);
        
    }
    public default ELEMENT overflow(Overflow value)
    {
        return addClass("overflow",value);
        
    }
    public default ELEMENT text_truncate()
    {
        return addClass("text-truncate");
        
    }
    
    public default ELEMENT justify_content(Justify value)
    {
        return addClass("justify-content",value);
    }
    
    public default ELEMENT justify_content(BreakPoint breakPoint,Justify value)
    {
        return addClass("justify-content",breakPoint,value);
    }
    
    public default ELEMENT fs(int value)
    {
        return addClass("fs",value);
    }
    public default ELEMENT fw(FontWeight value)
    {
        return addClass("fw",value);
    }
    public default ELEMENT fst(FontStyle value)
    {
        return addClass("fst",value);
    }
    public default ELEMENT top(int value)
    {
        return addClass("top",value);
    }
    public default ELEMENT bottom(int value)
    {
        return addClass("bottom",value);
    }
    public default ELEMENT start(int value)
    {
        return addClass("start",value);
    }
    public default ELEMENT translate(Translate value)
    {
        return addClass("translate",value);
    }
    public default ELEMENT visually_hidden()
    {
        return addClass("visually-hidden");
    }
    public default ELEMENT input_group()
    {
        return addClass("input-group");
    }
    public default ELEMENT btn_group()
    {
        return addClass("btn-group");
    }
    
//    public default ELEMENT input_group_append()
//    {
//        return addClass("input-group-append");
//    }
//    public default ELEMENT input_group_prepend()
//    {
//        return addClass("input-group-prepend");
//    }
    public default ELEMENT input_group_text()
    {
        return addClass("input-group-text");
    }
    public default ELEMENT align_items(AlignSelf value)
    {
        return addClass("align-items",value);
    }
    public default ELEMENT dropup(boolean center)
    {
        if (center)
        {
            return addClass("dropup-center");
        }
        return addClass("dropup");
    }
    public default ELEMENT dropdown(boolean center)
    {
        if (center)
        {
            return addClass("dropdown-center");
        }
        return addClass("dropdown");
    }
    public default ELEMENT dropstart()
    {
        return addClass("dropstart");
    }
    public default ELEMENT dropend()
    {
        return addClass("dropend");
    }
//    public default ELEMENT flex_wrap()
//    {
//        return addClass("flex-wrap");
//    }
//    public default ELEMENT flex_wrap_reverse()
//    {
//        return addClass("flex-wrap-reverse");
//    }    
    
}
