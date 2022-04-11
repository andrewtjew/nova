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
import org.nova.html.bootstrap.classes.ComponentClass;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Float_;
import org.nova.html.bootstrap.classes.Font;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.Overflow;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.classes.Rounded;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.elements.TagElement;

//public class ClassBuilder
//{
//    final StringBuilder sb;
//    
//    public ClassBuilder(Object class_)
//    {
//        this.sb=new StringBuilder();
//        addClass(class_);
//    }
//    public ClassBuilder()
//    {
//        this(null);
//    }
//    public ClassBuilder addClass(Object class_)
//    {
//        if (class_!=null)
//        {
//            if (sb.length()>0)
//            {
//                sb.append(' ');
//            }
//            sb.append(class_);
//        }
//        return (ClassBuilder)this;
//    }
//
//    public ClassBuilder addIf(boolean test,Object class_)
//    {
//        if (test)
//        {
//            addClass(class_);
//        }
//        return (ClassBuilder)this;
//    }
//    public ClassBuilder addIf(boolean test,Object class_,Object...fragments)
//    {
//        if (test)
//        {
//            addClass(class_,fragments);
//        }
//        return (ClassBuilder)this;
//    }
//    public ClassBuilder addClass(Object class_,Object...fragments)
//    {
//        addClass(class_);
//        addFragments(fragments);
//        return (ClassBuilder)this;
//    }
//    public ClassBuilder addFragments(Object...fragments)
//    {
//        for (Object fragment:fragments)
//        {
//            if (fragment!=null)
//            {
//                if (sb.length()>0)
//                {
//                    sb.append('-');
//                }
//                sb.append(fragment);
//            }
//        }
//        return (ClassBuilder)this;
//    }
//    public  ClassBuilder addFragmentsIf(boolean test,Object...fragments)
//    {
//        if (test)
//        {
//            addFragments(fragments);
//        }
//        return (ClassBuilder)this;
//    }
//    
//    @Override
//    public String toString()
//    {
//        return this.sb.toString();
//    }
//    
//    public <ELEMENT extends TagElement> ELEMENT applyTo(ELEMENT element)
//    {
//        element.addClass(toString());
//        return element;
//    }
//    
//    //Style 
//    public ClassBuilder ComponentClass(ComponentClass componentClass)
//    {
//        return addClass(componentClass);
//    }
//
//    //Style 
//    public ClassBuilder ComponentClass(ComponentClass componentClass,StyleColor color)
//    {
//        addClass(componentClass);
//        return addClass(componentClass+"-"+color);
//    }
//    
//    public ClassBuilder text(StyleColor color)
//    {
//        return addClass("text",color);
//    }
//    public ClassBuilder text(TextAlign value)
//    {
//        return addClass("text",value);
//    }
//    public ClassBuilder text(DeviceClass deviceClass,TextAlign value)
//    {
//        return addClass("text",deviceClass,value);
//    }
//    public ClassBuilder text(TextStyle value)
//    {
//        return addClass("text",value);
//    }
//    public ClassBuilder font(Font value)
//    {
//        return addClass("font",value);
//    }
//    public ClassBuilder lead()
//    {
//        return addClass("lead");
//    }
//    public ClassBuilder small()
//    {
//        return addClass("small");
//    }
//    public ClassBuilder initialism()
//    {
//        return addClass("initialism");
//    }
//    public ClassBuilder list_unstyled()
//    {
//        return addClass("list-unstyled");
//    }
//    public ClassBuilder list_inline()
//    {
//        return addClass("list-inline");
//    }
//    public ClassBuilder pre_scrollable()
//    {
//        return addClass("pre-scrollable");
//    }
//    
//    public ClassBuilder bg(StyleColor color)
//    {
//        return addClass("bg",color);
//    }
//    public ClassBuilder bg(boolean gradient,StyleColor color)
//    {
//        return addClass("bg","gradient",color);
//    }
//    
//    public ClassBuilder col(DeviceClass deviceClass,int columns)
//    {
//        return addClass("col",deviceClass,columns);
//    }
//    public ClassBuilder col(DeviceClass deviceClass)
//    {
//        return addClass("col",deviceClass);
//    }
//    public ClassBuilder col(int columns)
//    {
//        return addClass("col",columns);
//    }
//    public ClassBuilder col()
//    {
//        return addClass("col");
//    }
//    public ClassBuilder float_(DeviceClass deviceClass,Float_ value)
//    {
//        return addClass("float",deviceClass,value);
//    }
//    public ClassBuilder float_(Float_ value)
//    {
//        return addClass("float",value);
//    }
//    public ClassBuilder offset(int offset)
//    {
//        return addClass("offset",offset);
//    }
//    public ClassBuilder display(int size)
//    {
//        return addClass("display",size);
//    }
//    public ClassBuilder form_control()
//    {
//        return addClass("form-control");
//    }
//    public ClassBuilder form_control(DeviceClass deviceClass)
//    {
//        return addClass("form-control",deviceClass);
//    }
//    public ClassBuilder form_check()
//    {
//        return addClass("form-check");
//    }
//    public ClassBuilder form_check_inline()
//    {
//        return addClass("form-check-inline");
//    }
//    public ClassBuilder rounded()
//    {
//        return addClass("rounded");
//    }
//    public ClassBuilder rounded(int value)
//    {
//        return addClass("rounded",value);
//    }
//    public ClassBuilder rounded(Rounded value)
//    {
//        return addClass("rounded",value);
//    }
//    public ClassBuilder border(Edge value)
//    {
//        return addClass("border",value);
//    }
//    public ClassBuilder border(Edge value,int size)
//    {
//        return addClass("border",value,size);
//    }
//    public ClassBuilder border()
//    {
//        return addClass("border");
//    }
//    public ClassBuilder border(StyleColor color)
//    {
//        return addClass("border",color);
//    }
//    public ClassBuilder clearfix()
//    {
//        return addClass("clearfix");
//    }
//    
//
//    public ClassBuilder flex(Flex flex)
//    {
//        return addClass("flex",flex);
//    }
//
//    public ClassBuilder flex(Flex flex,int number)
//    {
//        return addClass("flex",flex,number);
//    }
//
//
//    public ClassBuilder justify_content(Justify value)
//    {
//        return addClass("justify-content",value);
//    }
//    
//    public ClassBuilder align_items(AlignSelf value)
//    {
//        return addClass("align-items",value);
//    }
//    
//    public ClassBuilder align_self(AlignSelf value)
//    {
//        return addClass("align-self",value);
//    }
//    
//    public ClassBuilder align_self(DeviceClass deviceClass,AlignSelf value)
//    {
//        return addClass("align-self",deviceClass,value);
//    }
//    public ClassBuilder align_items(AlignItems value)
//    {
//        return addClass("align-items",value);
//    }
//    
//    public ClassBuilder align(Align value)
//    {
//        return addClass("align",value);
//    }
//    
//    public ClassBuilder wrap()
//    {
//        return addClass("flex-wrap");
//    }
//    
//    public ClassBuilder wrap_reverse()
//    {
//        return addClass("flex-wrap-reverse");
//    }
//    
//    public ClassBuilder order(int value)
//    {
//        return addClass("order",value);
//    }
////-------------------------------------------
//    public ClassBuilder bg_gradient(StyleColor value)
//    {
//        return addClass("bg","gradient",value);
//    }
//    
//    public ClassBuilder mr(int value)
//    {
//        return addClass("mr",value);
//    }
//    public ClassBuilder ml(int value)
//    {
//        return addClass("ml",value);
//    }
//    public ClassBuilder mt(int value)
//    {
//        return addClass("mt",value);
//    }
//    public ClassBuilder mb(int value)
//    {
//        return addClass("mb",value);
//    }
//    public ClassBuilder mx(int value)
//    {
//        return addClass("mx",value);
//    }
//    public ClassBuilder my(int value)
//    {
//        return addClass("my",value);
//    }
//    public ClassBuilder m(int value)
//    {
//        return addClass("m",value);
//    }
//    public ClassBuilder mr_auto()
//    {
//        return addClass("mr","auto");
//    }
//    public ClassBuilder ml_auto()
//    {
//        return addClass("ml","auto");
//    }
//    public ClassBuilder mt_auto()
//    {
//        return addClass("mt","auto");
//    }
//    public ClassBuilder mb_auto()
//    {
//        return addClass("mb","auto");
//    }
//    public ClassBuilder mx_auto()
//    {
//        return addClass("mx","auto");
//    }
//    public ClassBuilder my_auto()
//    {
//        return addClass("my","auto");
//    }
//
//    public ClassBuilder pr(int value)
//    {
//        return addClass("pr",value);
//    }
//    public ClassBuilder pl(int value)
//    {
//        return addClass("pl",value);
//    }
//    public ClassBuilder pt(int value)
//    {
//        return addClass("pt",value);
//    }
//    public ClassBuilder pb(int value)
//    {
//        return addClass("pb",value);
//    }
//    public ClassBuilder px(int value)
//    {
//        return addClass("px",value);
//    }
//    public ClassBuilder py(int value)
//    {
//        return addClass("py",value);
//    }
//    public ClassBuilder p(int value)
//    {
//        return addClass("p",value);
//    }
//    public ClassBuilder pr_auto()
//    {
//        return addClass("pr","auto");
//    }
//    public ClassBuilder pl_auto()
//    {
//        return addClass("pl","auto");
//    }
//    public ClassBuilder pt_auto()
//    {
//        return addClass("pt","auto");
//    }
//    public ClassBuilder pb_auto()
//    {
//        return addClass("pb","auto");
//    }
//    public ClassBuilder px_auto()
//    {
//        return addClass("px","auto");
//    }
//    public ClassBuilder py_auto()
//    {
//        return addClass("py","auto");
//    }
//
//    public ClassBuilder d(Display display)
//    {
//        return addClass("d",display);
//        
//    }
//    public ClassBuilder d(DeviceClass deviceClass,Display display)
//    {
//        return addClass("d",deviceClass,display);
//        
//    }
//    
//    public ClassBuilder w(int value)
//    {
//        return addClass("w",value);
//    }
//    public ClassBuilder mw(int value)
//    {
//        return addClass("mw",value);
//    }
//    
//    public ClassBuilder h(int value)
//    {
//        return addClass("h",value);
//    }
//    public ClassBuilder mh(int value)
//    {
//        return addClass("mh",value);
//    }
//    
//    public ClassBuilder position(Position value)
//    {
//        return addClass("position",value);
//        
//    }
//    
//    public ClassBuilder overflow(Overflow value)
//    {
//        return addClass("overflow",value);
//        
//    }
//    
//    public ClassBuilder checkbox_inline()
//    {
//        return addClass("checkbox-inline");
//    }
//    public ClassBuilder radio_inline()
//    {
//        return addClass("radio-inline");
//    }
//    
//}
