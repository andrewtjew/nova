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
import org.nova.html.bootstrap.classes.DeviceClass;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Float_;
import org.nova.html.bootstrap.classes.Font;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.classes.Rounded;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.bootstrap.classes.TextStyle;
import org.nova.html.elements.Composer;
import org.nova.html.properties.Property;
import org.nova.html.properties.Style;

public abstract class StyleComponent<ELEMENT extends StyleComponent<ELEMENT>> extends Component<ELEMENT> 
{
    private String color;
    private boolean outline;
    
    public StyleComponent(String tag,String componentClass)
    {
        super(tag,componentClass);
    }
    public StyleComponent(String tag,String componentClass,boolean noEndTag)
    {
        super(tag,componentClass,noEndTag);
    }
    @SuppressWarnings("unchecked")
    public ELEMENT color(StyleColor value)
    {
        this.color=value.toString();
        return (ELEMENT)this;
    }
    public ELEMENT color(String value)
    {
        this.color=value;
        return (ELEMENT)this;
    }

    @SuppressWarnings("unchecked")
    public ELEMENT outline()
    {
        this.outline=true;
        return (ELEMENT)this;
    }
    public ELEMENT col(DeviceClass deviceClass,int columns)
    {
        return  buildClass("col",deviceClass,columns);
    }
    public ELEMENT col(DeviceClass deviceClass)
    {
    //    this.deviceClass=deviceClass;
        return  buildClass("col",deviceClass);
    }
    public ELEMENT col(int columns)
    {
        return  buildClass("col",columns);
    }
    public ELEMENT col()
    {
        return addClass("col");
    }
    public ELEMENT float_(DeviceClass deviceClass,Float_ value)
    {
        return  buildClass("float",deviceClass,value);
    }
    public ELEMENT form_control()
    {
        return addClass("form-control");
    }
    public ELEMENT form_text()
    {
        return addClass("form-text");
    }

/*
    protected StyleColor color()
    {
        return this.color;
    }
*/
    public ELEMENT bg(StyleColor value)
    {
        return  buildClass("bg",value);
    }
    public ELEMENT bg_gradient(StyleColor value)
    {
        return  buildClass("bg","gradient",value);
    }
    public ELEMENT text(StyleColor value)
    {
        return  buildClass("text",value);
    }
    public ELEMENT text(TextAlign value)
    {
        return  buildClass("text",value);
    }
    public ELEMENT text(DeviceClass deviceClass,TextAlign value)
    {
        return  buildClass("text",deviceClass,value);
    }
    public ELEMENT text(TextStyle value)
    {
        return  buildClass("text",value);
    }
    public ELEMENT font(Font value)
    {
        return  buildClass("font",value);
    }
    public ELEMENT lead()
    {
        return addClass("lead");
    }
    public ELEMENT small()
    {
        return addClass("small");
    }
    public ELEMENT float_(Float_ value)
    {
        return  buildClass("float",value);
    }
    public ELEMENT offset(int offset)
    {
        return  buildClass("offset",offset);
    }
    public ELEMENT display(int size)
    {
        return  buildClass("display",size);
    }
    public ELEMENT rounded()
    {
        return addClass("rounded");
    }
    public ELEMENT rounded(int value)
    {
        return  buildClass("rounded",value);
    }
    public ELEMENT rounded(Rounded value)
    {
        return  buildClass("rounded",value);
    }
    public ELEMENT border(Edge value)
    {
        return  buildClass("border",value);
    }
    public ELEMENT border(Edge value,int size)
    {
        return  buildClass("border",value,size);
    }
    public ELEMENT border(int size)
    {
        return  buildClass("border",size);
        
    }
    public ELEMENT border()
    {
        return addClass("border");
    }
    public ELEMENT border(StyleColor color)
    {
        addClass("border");
        return  buildClass("border",color);
    }
    public ELEMENT border(String color)
    {
//        addClass("border");
        return  buildClass("border",color);
//        return (ELEMENT)this;
    }
    public ELEMENT clearfix()
    {
        return addClass("clearfix");
    }
    public ELEMENT flex(Flex flex)
    {
        return  buildClass("flex",flex);
    }
    public ELEMENT flex(Flex flex,int value)
    {
        return  buildClass("flex",flex,value);
    }

    public ELEMENT flex(DeviceClass deviceClass,Flex flex)
    {
        return  buildClass("flex",deviceClass,flex);
    }

    @Deprecated
    public ELEMENT d_flex()
    {
        return addClass("d-flex");
    }

    @Deprecated
    public ELEMENT d_inline_flex()
    {
        return addClass("d-inline-flex");
    }

    @Deprecated
    public ELEMENT d_flex(DeviceClass deviceClass)
    {
        return  buildClass("d",deviceClass,"flex");
    }

    @Deprecated
    public ELEMENT d_inline_flex(DeviceClass deviceClass)
    {
        return  buildClass("d",deviceClass,"inline-flex");
    }


    public ELEMENT align_self(AlignSelf value)
    {
        return  buildClass("align-self",value);
    }
    
    public ELEMENT align_self(DeviceClass deviceClass,AlignSelf value)
    {
        return  buildClass("align-self",deviceClass,value);
    }
    
    public ELEMENT align_items(AlignItems value)
    {
        return  buildClass("align-items",value);
    }
    
    public ELEMENT align(Align value)
    {
        return  buildClass("align",value);
    }
    
    public ELEMENT order(int value)
    {
        return  buildClass("order",value);
    }
    
    public ELEMENT mr(DeviceClass deviceClass,int value)
    {
        return  buildClass("mr",deviceClass,value);
    }
    public ELEMENT mr(int value)
    {
        return  buildClass("mr",value);
    }
    public ELEMENT ml(int value)
    {
        return  buildClass("ml",value);
    }
    public ELEMENT mt(int value)
    {
        return  buildClass("mt",value);
    }
    public ELEMENT mb(int value)
    {
        return  buildClass("mb",value);
    }
    public ELEMENT mx(int value)
    {
        return  buildClass("mx",value);
    }
    public ELEMENT my(int value)
    {
        return  buildClass("my",value);
    }
    public ELEMENT m(int value)
    {
        return  buildClass("m",value);
    }
    public ELEMENT mr_auto()
    {
        return  buildClass("mr","auto");
    }
    public ELEMENT ml_auto()
    {
        return  buildClass("ml","auto");
    }
    public ELEMENT mt_auto()
    {
        return  buildClass("mt","auto");
    }
    public ELEMENT mb_auto()
    {
        return  buildClass("mb","auto");
    }
    public ELEMENT mx_auto()
    {
        return  buildClass("mx","auto");
    }
    public ELEMENT my_auto()
    {
        return  buildClass("my","auto");
    }

    public ELEMENT pr(int value)
    {
        return  buildClass("pr",value);
    }
    public ELEMENT pl(int value)
    {
        return  buildClass("pl",value);
    }
    public ELEMENT pt(int value)
    {
        return  buildClass("pt",value);
    }
    public ELEMENT pb(int value)
    {
        return  buildClass("pb",value);
    }
    public ELEMENT px(int value)
    {
        return  buildClass("px",value);
    }
    public ELEMENT py(int value)
    {
        return  buildClass("py",value);
    }
    public ELEMENT p(int value)
    {
        return  buildClass("p",value);
    }
    public ELEMENT pr_auto()
    {
        return  buildClass("pr","auto");
    }
    public ELEMENT pl_auto()
    {
        return  buildClass("pl","auto");
    }
    public ELEMENT pt_auto()
    {
        return  buildClass("pt","auto");
    }
    public ELEMENT pb_auto()
    {
        return  buildClass("pb","auto");
    }
    public ELEMENT px_auto()
    {
        return  buildClass("px","auto");
    }
    public ELEMENT py_auto()
    {
        return  buildClass("py","auto");
    }

    public ELEMENT d(Display display)
    {
        return  buildClass("d",display);
        
    }
    public ELEMENT d(DeviceClass deviceClass,Display display)
    {
        return  buildClass("d",deviceClass,display);
        
    }
    
    public ELEMENT w(int value)
    {
        return  buildClass("w",value);
    }
    public ELEMENT mw(int value)
    {
        return  buildClass("mw",value);
    }
    
    public ELEMENT h(int value)
    {
        return  buildClass("h",value);
    }
    public ELEMENT h_auto()
    {
        return  buildClass("h","auto");
    }
    public ELEMENT mh(int value)
    {
        return  buildClass("mh",value);
    }
    
    public ELEMENT position(Position value)
    {
        return  buildClass("position",value);
        
    }
    
    public ELEMENT justify_content(Justify value)
    {
        return  buildClass("justify-content",value);
    }
    public ELEMENT popover(String title,String content)
    {
        attr("data-toggle","popover");
        title(title);
        attr("data-content",content);
        return (ELEMENT)this;
    }
    public ELEMENT popover(String content)
    {
        attr("data-toggle","popover");
        attr("data-content",content);
        return (ELEMENT)this;
    }
    public ELEMENT placement(Placement placement)
    {
        attr("data-placement",placement);
        return (ELEMENT)this;
    }
    public ELEMENT tooltip(String title)
    {
        attr("data-toggle","tooltip");
        title(title);
        return (ELEMENT)this;
    }
    
    
    
    
    //--
    protected void composeThis(Composer composer) throws Throwable
    {
        if (this.outline||this.color!=null)
        {
             buildClass(getComponentClass(),this.outline?"outline":null,this.color);
        }
        super.compose(composer);
    }

    
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        composeThis(composer);
    }

    public ELEMENT style(String value)
    {
        return attr("style",value);
    }
    public ELEMENT style(Property property)
    {
        return attr("style",property);
    }
    public ELEMENT style(Style value)
    {
        return attr("style",value.toString());
    }
    
}
