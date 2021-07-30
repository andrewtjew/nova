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

import org.nova.html.bootstrap.classes.DeviceClass;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Float_;
import org.nova.html.bootstrap.classes.Font;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.classes.Rounded;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Composer;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.InputType;
import org.nova.html.elements.TagElement;

public abstract class InputComponent<ELEMENT extends InputComponent<ELEMENT>> extends InputElement<ELEMENT> 
{
//    protected InputComponent(String componentClass,InputType inputType)
//    {
//        super()
    
//        addClass(componentClass);
//        attr("type",type);
//    }
    protected InputComponent(String tag,InputType inputType,String componentClass)
    {
        super(tag,inputType);
        addClass(componentClass);
    }

    protected InputComponent(InputType inputType,String componentClass)
    {
        super(inputType);
        addClass(componentClass);
    }
    
    protected InputComponent(String tag,InputType inputType)
    {
        this(tag,inputType,null);
    }
    
    protected InputComponent(InputType inputType)
    {
        super(inputType);
    }

    public ELEMENT form_control()
    {
        return addClass("form-control");
    }

    public ELEMENT addClass(ClassBuilder classBuilder)
    {
        addClass(classBuilder.toString());
        return (ELEMENT)this;
    }
    public ELEMENT bg(StyleColor value)
    {
        return addClass("bg",value);
    }
    public ELEMENT bg_gradient(StyleColor value)
    {
        return addClass("bg","gradient",value);
    }
    public ELEMENT text(StyleColor value)
    {
        return addClass("text",value);
    }
    public ELEMENT font(Font value)
    {
        return addClass("font",value);
    }
    /*
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
        return addClass("float",value);
    }
    public ELEMENT offset(int offset)
    {
        return addClass("offset",offset);
    }
    public ELEMENT display(int size)
    {
        return addClass("display",size);
    }
    public ELEMENT rounded()
    {
        return addClass("rounded");
    }
    public ELEMENT rounded(int value)
    {
        return addClass("rounded",value);
    }
    public ELEMENT rounded(Rounded value)
    {
        return addClass("rounded",value);
    }
    public ELEMENT border(Edge value)
    {
        return addClass("border",value);
    }
    public ELEMENT border(Edge value,int size)
    {
        return addClass("border",value,size);
    }
    public ELEMENT border()
    {
        return addClass("border");
    }
    public ELEMENT border(StyleColor color)
    {
        return addClass("border",color);
    }
    public ELEMENT flex(DeviceClass deviceClass,Flex flex)
    {
        return addClass("flex",deviceClass,flex);
    }

    public ELEMENT order(int value)
    {
        return addClass("order",value);
    }
    */
    public ELEMENT mr(DeviceClass deviceClass,int value)
    {
        return addClass("mr",deviceClass,value);
    }
    public ELEMENT mr(int value)
    {
        return addClass("mr",value);
    }
    public ELEMENT ml(int value)
    {
        return addClass("ml",value);
    }
    public ELEMENT mt(int value)
    {
        return addClass("mt",value);
    }
    public ELEMENT mb(int value)
    {
        return addClass("mb",value);
    }
    public ELEMENT mx(int value)
    {
        return addClass("mx",value);
    }
    public ELEMENT my(int value)
    {
        return addClass("my",value);
    }
    public ELEMENT m(int value)
    {
        return addClass("m",value);
    }
    public ELEMENT mr_auto()
    {
        return addClass("mr","auto");
    }
    public ELEMENT ml_auto()
    {
        return addClass("ml","auto");
    }
    public ELEMENT mt_auto()
    {
        return addClass("mt","auto");
    }
    public ELEMENT mb_auto()
    {
        return addClass("mb","auto");
    }
    public ELEMENT mx_auto()
    {
        return addClass("mx","auto");
    }
    public ELEMENT my_auto()
    {
        return addClass("my","auto");
    }

    public ELEMENT pr(int value)
    {
        return addClass("pr",value);
    }
    public ELEMENT pl(int value)
    {
        return addClass("pl",value);
    }
    public ELEMENT pt(int value)
    {
        return addClass("pt",value);
    }
    public ELEMENT pb(int value)
    {
        return addClass("pb",value);
    }
    public ELEMENT px(int value)
    {
        return addClass("px",value);
    }
    public ELEMENT py(int value)
    {
        return addClass("py",value);
    }
    public ELEMENT p(int value)
    {
        return addClass("p",value);
    }
    public ELEMENT pr_auto()
    {
        return addClass("pr","auto");
    }
    public ELEMENT pl_auto()
    {
        return addClass("pl","auto");
    }
    public ELEMENT pt_auto()
    {
        return addClass("pt","auto");
    }
    public ELEMENT pb_auto()
    {
        return addClass("pb","auto");
    }
    public ELEMENT px_auto()
    {
        return addClass("px","auto");
    }
    public ELEMENT py_auto()
    {
        return addClass("py","auto");
    }

    public ELEMENT d(Display display)
    {
        return addClass("d",display);
        
    }
    public ELEMENT d(DeviceClass deviceClass,Display display)
    {
        return addClass("d",deviceClass,display);
    }
    
    public ELEMENT w(int value)
    {
        return addClass("w",value);
    }
    public ELEMENT mw(int value)
    {
        return addClass("mw",value);
    }
    
    public ELEMENT h(int value)
    {
        return addClass("h",value);
    }
    public ELEMENT h_auto()
    {
        return addClass("h","auto");
    }
    public ELEMENT mh(int value)
    {
        return addClass("mh",value);
    }
    
    public ELEMENT position(Position value)
    {
        return addClass("position",value);
        
    }
}
