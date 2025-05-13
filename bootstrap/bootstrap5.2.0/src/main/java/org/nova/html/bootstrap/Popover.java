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

import org.nova.html.elements.TagElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.tags.script;

public class Popover extends TipComponent<Popover> 
{
    public Popover(TagElement<?> toggler)
    {
    	super(toggler);
        this.toggler.attr("data-bs-toggle","popover");
    }
    
    public String js_popover(TipOption option)
    {
 //   	return "bootstrap.Popover.getInstance(document.getElementById("+mark+this.toggler.id()+mark+")).toggle();";
        //   return "$("+mark+"#"+this.toggler.id()+mark+").popover("+mark+option+mark+")";
    	return "$('#"+this.toggler.id()+"').popover('"+option+"');";
    }
    public script script_popover(TipOption option)
    {
        return new script().addInner(new LiteralHtml(js_popover(option)));
    }
    public String js_popover()
    {
        return HtmlUtils.js_call("nova.bs.popover", this.toggler.id())+";";
    }

    public script script_popover()
    {
        return new script().addInner(new LiteralHtml(js_popover()));
    }
    
    public static script script_readyAll()
    {
        return new script().addInner(new LiteralHtml(js_readyAll()));
    }
    public static String js_readyAll()
    {
        return "var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle=\"popover\"]'));var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {return new bootstrap.Popover(popoverTriggerEl)});";
    }
    public static script script_dismiss()
    {
        return new script().addInner(new LiteralHtml("var popover = new bootstrap.Popover(document.querySelector('.popover-dismiss'), {trigger: 'focus'});"));
    }

    
    
}
