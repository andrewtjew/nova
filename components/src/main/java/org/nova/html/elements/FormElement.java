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
package org.nova.html.elements;

import java.util.ArrayList;
import java.util.List;

import org.nova.html.enums.autocomplete;
import org.nova.html.enums.method;
import org.nova.html.enums.target;

public class FormElement<ELEMENT extends FormElement<ELEMENT>> extends GlobalEventTagElement<ELEMENT>
{
    private String action;
    private method method;
    public FormElement(method method)
    {
        super("form");
        attr("method",method);
        this.method=method;
    }
    public FormElement()
    {
        super("form");
        this.method=null;
    }
    
    public method method()
    {
        return this.method;
    }
    
    public  ELEMENT action(String URL) 
    {
        this.action=URL;
        return attr("action",URL);
    }
    
    public String action()
    {
        return this.action;
    }
    
    public ELEMENT autocomplete(autocomplete autocomplete) 
    {
        return attr("autocomplete",autocomplete);
    }
    public ELEMENT novalidate()  
    {
        return attr("novalidate");
    }
    public ELEMENT novalidate(boolean novalidate)  
    {
        if (novalidate)
        {
            return attr("novalidate");
        }
        return (ELEMENT)this;
    }
    public ELEMENT target(target target)
    {
        return attr("target",target);
    }
    public ELEMENT target(String framename)
    {
        return attr("target",framename);
    }
    public ELEMENT onsubmit(String script)
    {
        return attr("onsubmit",script);
    }
    public ELEMENT form_inline()
    {
        return addClass("form-inline");
    }
    
//    public String[] getAllInputIds()
//    {
//        List<Element> elements=this.getAllInners();
//        ArrayList<String> list=new ArrayList<String>();
//        for (Element element:elements)
//        {
//            if (element instanceof InputElement<?>)
//            {
//                list.add(((InputElement<?>)element).id());
//            }
//        }
//        return list.toArray(new String[list.size()]);
//    }
    
}
