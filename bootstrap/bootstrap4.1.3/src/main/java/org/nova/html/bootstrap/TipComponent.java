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

import org.nova.html.bootstrap.classes.Boundary;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.Trigger;
import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.StringComposer;
import org.nova.html.elements.TagElement;


public class TipComponent<TIP extends TipComponent<TIP>>
{
    final protected TagElement<?> toggler;
    
    static class Delay
    {
        public Integer show;
        public Integer hide;
    }
    
    protected TipComponent(TagElement<?> toggler,String tipType)
    {
    	this.toggler=toggler;
        this.toggler.attr("data-toggle",tipType);
    }
    
    @SuppressWarnings("unchecked")
	public TIP title(String title)
    {
        this.toggler.attr("title",title);
        return (TIP)this;
    }
    
    public TIP template(String template)
    {
//        this.toggler.attr("data-template",template,QuotationMark.SINGLE);
        this.toggler.attr("data-template",template);
        return (TIP)this;
    }
    public TIP animation()
    {
    	return animation(true);
    }

    public TIP animation(boolean animation)
    {
    	if (animation)
    	{
    		this.toggler.attr("data-animation",animation);
    	}
        return (TIP)this;
    }

    public TIP content(String content)
    {
        this.toggler.attr("data-content",content);
//        this.toggler.attr("data-content",content,QuotationMark.SINGLE);
        return (TIP)this;
    }

    public TIP content(QuotationMark quotationMark,Element element,boolean html) throws Throwable
    {
        if (html)
        {
            this.toggler.attr("data-html",true);
        }
        String content=element.getHtml(new StringComposer(quotationMark));
//        StringComposer composer=new StringComposer();
//        composer.compose(element);
//        String content2=composer.getStringBuilder().toString();
//        content="<button type=\"button\" class=\"btn ml-1 btn-primary\" class=\"btn ml-1 btn-primary btn-primary\">World</button>";
//        content=HtmlUtils.toHtmlText(content);
//<button type=\"button\" class=\"btn ml-1 btn-primary\" class=\"btn ml-1 btn-primary btn-primary\">&#x2713;</button><button type=\"button\" class=\"btn ml-1 btn-secondary\" class=\"btn ml-1 btn-secondary btn-secondary\">&#x1f5d9;</button>
//      content=HtmlUtils.toHtmlText(content);
//        this.toggler.attr("data-content",content,QuotationMark.SINGLE);
        this.toggler.attr("data-content",content);
        return (TIP)this;
    }
    public TIP content(Element element) throws Throwable
    {
        return content(QuotationMark.QOUT,element,true);
    }

//    @Description("Order is important. Use (hover,focus) and not (focus,hover) to allow buttons on the popover to be clicked.")
//    public TIP trigger(Trigger...triggers) throws Exception
//    {
//        if (triggers.length>1)
//        {
//            for (Trigger trigger:triggers)
//            {
//                if (trigger==Trigger.manual)
//                {
//                    throw new Exception("manual cannot be combined.");
//                }
//            }
//        }
//        this.toggler.attr("data-trigger",Utils.combine(triggers, " "));
//        return (TIP)this;
//    }

    public TIP trigger(Trigger trigger) throws Exception
    {
        this.toggler.attr("data-trigger",trigger.toString());
        return (TIP)this;
    }
    
    public TIP placement(Placement placement)
    {
        this.toggler.attr("data-placement",placement);
        return (TIP)this;
    }
    
    @SuppressWarnings("unchecked")
    public TIP delay(Integer show,Integer hide)
    {
        Delay delay=new Delay();
        delay.show=show;
        delay.hide=hide;
        try
        {
//            this.toggler.attr("data-delay",delay,QuotationMark.SINGLE); //ATTR
            this.toggler.attr("data-delay",delay);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
        return (TIP)this;
    }
    
    public TIP container(String container)
    {
        this.toggler.attr("data-container",container);
        return (TIP)this;
    }
    
    public TIP container(GlobalTagElement<?> element)
    {
        this.toggler.attr("data-container","#"+element.id());
        return (TIP)this;
    }
    
    public TIP offset(int offsetX,int offsetY)
    {
        this.toggler.attr("data-offset",offsetX+"px "+offsetY+"px");
        return (TIP)this;
    }
    public TIP boundary(Boundary boundary)
    {
    	this.toggler.attr("data-boundary",boundary);
        return (TIP)this;
    }
}
