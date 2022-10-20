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
    final protected Options options;
    
    static class Delay
    {
        public Integer show;
        public Integer hide;
    }
    
    static class Options
    {
        public String container;
        public String content;
        public Delay delay;
        public boolean html;
        public boolean animation;
        public Placement placement;
        public String selector;
        public String template;
        public String title;
        public String trigger;
//        public int offset;
        public String boundary;
        public boolean sanitize;
    }
    
    protected TipComponent(TagElement<?> toggler)
    {
        this.toggler=toggler;
        this.options=null;
    }
    
    protected TipComponent()
    {
        this.toggler=null;
        this.options=new Options();
    }
    
    @SuppressWarnings("unchecked")
	public TIP title(String title)
    {
        this.toggler.attr("title",title);
        return (TIP)this;
    }
    
    public TIP template(String template)
    {
        if (this.toggler!=null)
        {
            this.toggler.attr("data-bs-template",template);
        }
        else
        {
            this.options.template=template;
        }
           
        return (TIP)this;
    }
    public TIP animation()
    {
    	return animation(true);
    }

    public TIP animation(boolean animation)
    {
        if (this.toggler!=null)
        {
            this.toggler.attr("data-bs-animation",animation);
        }
        else
        {
            this.options.animation=animation;
        }
        return (TIP)this;
    }

    public TIP content(String content)
    {
        if (this.toggler!=null)
        {
            this.toggler.attr("data-bs-content",content);
        }
        else
        {
            this.options.content=content;
        }
        return (TIP)this;
    }

    public TIP content(QuotationMark quotationMark,Element element) throws Throwable
    {
        
        String content=element.getHtml(new StringComposer(quotationMark));
        if (this.toggler!=null)
        {
            this.toggler.attr("data-bs-html",true);
            this.toggler.attr("data-bs-content",content);
        }
        else
        {
            this.options.html=true;
            this.options.content=content;
        }

        return (TIP)this;
    }
    public TIP content(Element element) throws Throwable
    {
        return content(QuotationMark.QOUT,element);
    }

    public TIP trigger(Trigger trigger) throws Exception
    {
        if (this.toggler!=null)
        {
            this.toggler.attr("data-bs-trigger",trigger.toString());
        }
        else
        {
            this.options.trigger=trigger.toString();
        }
        return (TIP)this;
    }
    
    public TIP placement(Placement placement)
    {
        if (this.toggler!=null)
        {
            this.toggler.attr("data-bs-placement",placement);
        }
        else
        {
            this.options.placement=placement;
        }
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
            this.toggler.attr("data-bs-delay",delay);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
        return (TIP)this;
    }
    
    public TIP container(String container)
    {
        this.toggler.attr("data-bs-container",container);
        return (TIP)this;
    }
    
    public TIP container(GlobalTagElement<?> element)
    {
        this.toggler.attr("data-bs-container","#"+element.id());
        return (TIP)this;
    }
    
    public TIP offset(int offsetX,int offsetY)
    {
        this.toggler.attr("data-bs-offset",offsetX+"px "+offsetY+"px");
        return (TIP)this;
    }
    public TIP boundary(Boundary boundary)
    {
    	this.toggler.attr("data-bs-boundary",boundary);
        return (TIP)this;
    }
}
