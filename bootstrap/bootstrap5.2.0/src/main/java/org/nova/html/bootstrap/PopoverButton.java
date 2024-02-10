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

import org.nova.html.bootstrap.TipComponent.Delay;
import org.nova.html.bootstrap.TipComponent.Options;
import org.nova.html.bootstrap.classes.Boundary;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.Trigger;
import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.StringComposer;
import org.nova.html.elements.TagElement;

public class PopoverButton extends ButtonComponent<PopoverButton>
{
    public PopoverButton()
    { 
        super("button");
        this.attr("data-bs-toggle","popover");        
        this.options=new Options();
    }
    public PopoverButton(String label)
    { 
        super("button");
        this.attr("data-bs-toggle","popover");        
        addInner(label);
        this.options=new Options();
    }
    
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
    
    
    public PopoverButton template(String template)
    {
        this.options.template=template;
        return this;
    }
    public PopoverButton animation()
    {
        return animation(true);
    }

    public PopoverButton animation(boolean animation)
    {
        attr("data-bs-animation",animation);
        return this;
    }

    public PopoverButton content(String content)
    {
        attr("data-bs-content",content);
        return this;
    }

    public PopoverButton content(QuotationMark quotationMark,Element element) throws Throwable
    {
        
        String content=element.getHtml(new StringComposer(quotationMark));
        this.attr("data-bs-html",true);
        this.attr("data-bs-content",content);
        return this;
    }
    public PopoverButton content(Element element) throws Throwable
    {
        return content(QuotationMark.QOUT,element);
    }

    public PopoverButton trigger(Trigger trigger) throws Exception
    {
        this.attr("data-bs-trigger",trigger.toString());
        return this;
    }
    
    public PopoverButton placement(Placement placement)
    {
        this.attr("data-bs-placement",placement);
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public PopoverButton delay(Integer show,Integer hide)
    {
        Delay delay=new Delay();
        delay.show=show;
        delay.hide=hide;
        this.attr("data-bs-delay",delay);
        return this;
    }
    
    public PopoverButton container(String container)
    {
        this.attr("data-bs-container",container);
        return this;
    }
    
    public PopoverButton container(GlobalTagElement<?> element)
    {
        this.attr("data-bs-container","#"+element.id());
        return this;
    }
    
    public PopoverButton offset(int offsetX,int offsetY)
    {
        this.attr("data-bs-offset",offsetX+"px "+offsetY+"px");
        return this;
    }
    public PopoverButton boundary(Boundary boundary)
    {
        this.attr("data-bs-boundary",boundary);
        return this;
    }


}
