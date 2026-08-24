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
import org.nova.html.bootstrap.classes.AlignContent;
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
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.properties.FlexDirection;

public interface Styling<ELEMENT>
{
//    final private TagElement<?> element;
    public ELEMENT addClass(String class_);
    public GlobalTagElement<?> getElement();

    
    public default ELEMENT buildClass(Object prefix,Object... parts)
    {
        StringBuilder sb=new StringBuilder(prefix.toString());
        for (var part:parts)
        {
            if (part==null)
            {
                continue;
            }
            sb.append('-');
            sb.append(part);
        }
        return addClass(sb.toString());
    }
    
    public default ELEMENT col(BreakPoint breakPoint,int columns)
    {
        if (breakPoint==BreakPoint.xs)
        {
            return buildClass("col",columns);
        }
        return buildClass("col",breakPoint,columns);
    }
    public default ELEMENT table_responsive()
    {
        return buildClass("table-responsive");
    }
    public default ELEMENT table_responsive(BreakPoint breakPoint)
    {
        return buildClass("table-responsive",breakPoint);
    }
    public default ELEMENT col(BreakPoint breakPoint)
    {
        if (breakPoint==BreakPoint.xs)
        {
            return buildClass("col");
        }
        return buildClass("col",breakPoint);
    }
    public default ELEMENT col(int columns)
    {
        return buildClass("col",columns);
    }
    public default ELEMENT col()
    {
        return buildClass("col");
    }
    public default ELEMENT col_form_label()
    {
        return buildClass("col-form-label");
    }
    public default ELEMENT col_auto()
    {
        return buildClass("col-auto");
    }
    public default ELEMENT row()
    {
        return buildClass("row");
    }
    public default ELEMENT float_(BreakPoint breakPoint,Float_ value)
    {
        return buildClass("float",breakPoint,value);
    }
    public default ELEMENT form_floating()
    {
        return buildClass("form-floating");
    }
    public default ELEMENT form_range()
    {
        return buildClass("form-range");
    }
    public default ELEMENT form_check()
    {
        return buildClass("form-check");
    }
    public default ELEMENT form_check_label()
    {
        return buildClass("form-check-label");
    }
    public default ELEMENT form_check_inline()
    {
        return buildClass("form-check-inline");
    }
    public default ELEMENT form_switch()
    {
        return buildClass("form-switch");
    }
    public default ELEMENT form_select()
    {
        return buildClass("form-select");
    }
    public default ELEMENT form_label()
    {
        return buildClass("form-label");
    }
    public default ELEMENT form_text()
    {
        return buildClass("form-text");
    }
    public default ELEMENT form_control()
    {
        return buildClass("form-control");
    }
    public default ELEMENT form_control(Size size)
    {
        return buildClass("form-control",size.toString());
    }
    public default ELEMENT form_control(BreakPoint breakPoint)
    {
        return buildClass("form-control",breakPoint);
    }
    public default ELEMENT invalid_feedback()
    {
        return buildClass("invalid-feedback");
    }
    public default ELEMENT valid_feedback()
    {
        return buildClass("valid-feedback");
    }
    public default ELEMENT bg(StyleColor value,double opacity)
    {
        getElement().style("--bs-bg-opacity:"+opacity+";");
        return buildClass("bg",value);
    }
    public default ELEMENT bg(StyleColor value)
    {
        return buildClass("bg",value);
    }
    public default ELEMENT bg_gradient(StyleColor value)
    {
        buildClass("bg-gradient");
        return buildClass("bg",value);
    }
    public default ELEMENT text(StyleColor value)
    {
        return buildClass("text",value);
    }
    public default ELEMENT text_bg(StyleColor value)
    {
        return buildClass("text-bg",value);
    }
    public default ELEMENT text(TextAlign value)
    {
        return buildClass("text",value);
    }
    public default ELEMENT text(BreakPoint breakPoint,TextAlign value)
    {
        return buildClass("text",breakPoint,value);
    }
    public default ELEMENT text(Text value)
    {
        return buildClass("text",value);
    }
//    public default ELEMENT font(Font value)
//    {
//        return addClass("fw",value);
//    }
    public default ELEMENT lead()
    {
        return buildClass("lead");
    }
    public default ELEMENT small()
    {
        return buildClass("small");
    }
    public default ELEMENT float_(Float_ value)
    {
        return buildClass("float",value);
    }
    public default ELEMENT offset(int offset)
    {
        return buildClass("offset",offset);
    }
    public default ELEMENT display(int size)
    {
        return buildClass("display",size);
    }
    public default ELEMENT rounded()
    {
        return buildClass("rounded");
    }
    public default ELEMENT rounded(int value)
    {
        return buildClass("rounded",value);
    }
    public default ELEMENT rounded(Rounded rounded,int value)
    {
        return buildClass("rounded",rounded,value);
    }
    public default ELEMENT rounded(Rounded rounded)
    {
        return buildClass("rounded",rounded);
    }
    public default ELEMENT border()
    {
        return buildClass("border");
    }
    public default ELEMENT border(Edge edge)
    {
        return buildClass("border",edge);
    }
    public default ELEMENT shadow()
    {
        return buildClass("shadow");
    }
    public default ELEMENT shadow(Size value)
    {
        return buildClass("shadow",value.toString());
    }
    public default ELEMENT border(Edge edge,boolean subtract)
    {
        if (subtract)
        {
            return buildClass("border",edge,0);
        }
        return buildClass("border",edge);
    }

    public default ELEMENT border(int width)
    {
        buildClass("border");
        return buildClass("border",width);
    }
    public default ELEMENT border(StyleColor color)
    {
        return buildClass("border",color);
    }
    public default ELEMENT clearfix()
    {
        return buildClass("clearfix");
    }
    public default ELEMENT flex_direction(FlexDirection direction)
    {
        return buildClass("flex-direction",direction);
    }

    public default ELEMENT flex(Flex flex)
    {
        return buildClass("flex",flex);
    }
    public default ELEMENT flex(Flex flex,int value)
    {
        return buildClass("flex",flex,value);
    }

    public default ELEMENT flex(BreakPoint breakPoint,Flex flex)
    {
        return buildClass("flex",breakPoint,flex);
    }

    public default ELEMENT align_self(AlignSelf value)
    {
        return buildClass("align-self",value);
    }
    
    public default ELEMENT align_self(BreakPoint breakPoint,AlignSelf value)
    {
        return buildClass("align-self",breakPoint,value);
    }
    
    public default ELEMENT align_items(AlignItems value)
    {
        return buildClass("align-items",value);
    }
    
    public default ELEMENT align(Align value)
    {
        return buildClass("align",value);
    }
    
    public default ELEMENT order(int value)
    {
        return buildClass("order",value);
    }
    
    public default ELEMENT me(BreakPoint breakPoint,int value)
    {
        return buildClass("me",breakPoint,value);
    }
    public default ELEMENT me(int value)
    {
        return buildClass("me",value);
    }
    public default ELEMENT ms(int value)
    {
        return buildClass("ms",value);
    }
    public default ELEMENT mt(int value)
    {
        return buildClass("mt",value);
    }
    public default ELEMENT mb(int value)
    {
        return buildClass("mb",value);
    }
    public default ELEMENT mx(int value)
    {
        return buildClass("mx",value);
    }
    public default ELEMENT my(int value)
    {
        return buildClass("my",value);
    }
    public default ELEMENT m(int value)
    {
        return buildClass("m",value);
    }
    //----
    public default ELEMENT mt(BreakPoint breakPoint,int value)
    {
        return buildClass("mt",breakPoint,value);
    }
    public default ELEMENT mb(BreakPoint breakPoint,int value)
    {
        return buildClass("mb",breakPoint,value);
    }
    public default ELEMENT mx(BreakPoint breakPoint,int value)
    {
        return buildClass("mx",breakPoint,value);
    }
    public default ELEMENT my(BreakPoint breakPoint,int value)
    {
        return buildClass("my",breakPoint,value);
    }
    public default ELEMENT m(BreakPoint breakPoint,int value)
    {
        return buildClass("m",breakPoint,value);
    }
    
    public default ELEMENT mt_auto()
    {
        return buildClass("mt","auto");
    }
    public default ELEMENT mb_auto()
    {
        return buildClass("mb","auto");
    }
    public default ELEMENT mx_auto()
    {
        return buildClass("mx","auto");
    }
    public default ELEMENT my_auto()
    {
        return buildClass("my","auto");
    }
    public default ELEMENT me_auto()
    {
        return buildClass("me","auto");
    }
    public default ELEMENT ms_auto()
    {
        return buildClass("ms","auto");
    }

    public default ELEMENT pe(int value)
    {
        return buildClass("pe",value);
    }
    public default ELEMENT ps(int value)
    {
        return buildClass("ps",value);
    }
    public default ELEMENT pt(int value)
    {
        return buildClass("pt",value);
    }
    public default ELEMENT pb(int value)
    {
        return buildClass("pb",value);
    }
    public default ELEMENT px(int value)
    {
        return buildClass("px",value);
    }
    public default ELEMENT py(int value)
    {
        return buildClass("py",value);
    }
    public default ELEMENT p(int value)
    {
        return buildClass("p",value);
    }
    public default ELEMENT pe_auto()
    {
        return buildClass("pe","auto");
    }
    public default ELEMENT ps_auto()
    {
        return buildClass("ps","auto");
    }
    public default ELEMENT pt_auto()
    {
        return buildClass("pt","auto");
    }
    public default ELEMENT pb_auto()
    {
        return buildClass("pb","auto");
    }
    public default ELEMENT px_auto()
    {
        return buildClass("px","auto");
    }
    public default ELEMENT py_auto()
    {
        return buildClass("py","auto");
    }

    public default ELEMENT d(Display display)
    {
        return buildClass("d",display);
        
    }
    public default ELEMENT d(BreakPoint breakPoint,Display display)
    {
        return buildClass("d",breakPoint,display);
        
    }
    
    public default ELEMENT w(int value)
    {
        return buildClass("w",value);
    }
    public default ELEMENT mw(int value)
    {
        return buildClass("mw",value);
    }
    
    public default ELEMENT h(int value)
    {
        return buildClass("h",value);
    }
    public default ELEMENT h_auto()
    {
        return buildClass("h","auto");
    }
    public default ELEMENT mh(int value)
    {
        return buildClass("mh",value);
    }
    
    public default ELEMENT position(Position value)
    {
        return buildClass("position",value);
        
    }
    public default ELEMENT overflow(Overflow value)
    {
        return buildClass("overflow",value);
        
    }
    public default ELEMENT text_truncate()
    {
        return buildClass("text-truncate");
        
    }
    
    public default ELEMENT justify_content(Justify value)
    {
        return buildClass("justify-content",value);
    }
    
    public default ELEMENT justify_content(BreakPoint breakPoint,Justify value)
    {
        return buildClass("justify-content",breakPoint,value);
    }
    
    public default ELEMENT fs(int value)
    {
        return buildClass("fs",value);
    }
    public default ELEMENT fw(FontWeight value)
    {
        return buildClass("fw",value);
    }
    public default ELEMENT fst(FontStyle value)
    {
        return buildClass("fst",value);
    }
    public default ELEMENT top(int value)
    {
        return buildClass("top",value);
    }
    public default ELEMENT bottom(int value)
    {
        return buildClass("bottom",value);
    }
    public default ELEMENT start(int value)
    {
        return buildClass("start",value);
    }
    public default ELEMENT translate(Translate value)
    {
        return buildClass("translate",value);
    }
    public default ELEMENT visually_hidden()
    {
        return buildClass("visually-hidden");
    }
    public default ELEMENT input_group()
    {
        return buildClass("input-group");
    }
    public default ELEMENT has_validation()
    {
        return buildClass("has-validation");
    }
    public default ELEMENT btn_group()
    {
        return buildClass("btn-group");
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
        return buildClass("input-group-text");
    }
    public default ELEMENT align_content(AlignContent value)
    {
        return buildClass("align-content",value);
    }
//    public default ELEMENT align_items(AlignSelf value)
//    {
//        return addClass("align-items",value);
//    }
    public default ELEMENT dropup()
    {
        return buildClass("dropup");
    }
    public default ELEMENT dropdown()
    {
        return buildClass("dropdown");
    }
    public default ELEMENT dropup_center()
    {
        return buildClass("dropup-center");
    }
    public default ELEMENT dropdown_center()
    {
        return buildClass("dropdown-center");
    }
    public default ELEMENT dropdown_toggle()
    {
        return buildClass("dropdown-toggle");
    }
    public default ELEMENT dropstart()
    {
        return buildClass("dropstart");
    }
    public default ELEMENT dropend()
    {
        return buildClass("dropend");
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
