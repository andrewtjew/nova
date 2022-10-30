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

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.ext.Content;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.a;
import org.nova.html.tags.button_button;
import org.nova.html.tags.div;
import org.nova.html.tags.li;
import org.nova.html.tags.script;
import org.nova.html.tags.span;
import org.nova.html.tags.ul;

public class Carousel extends StyleComponent<Carousel>
{
    private div inner;
    private boolean indicators;
    private boolean controls;
    
    public Carousel()
    {
        super("div","carousel");
        attr("data-bs-ride","carousel");
        this.inner=returnAddInner(new div()).addClass("carousel-inner");
    }
    
    public Carousel slide()
    {
        addClass("slide");
        return this;
    }
    public Carousel fade()
    {
        addClass("carousel-fade");
        return this;
    }
    public Carousel indicators()
    {
        this.indicators=true;
        return this;
    }
    
    public Carousel add(CarouselItem item)
    {
        this.inner.addInner(item);
        return this;
    }
    
    
//    //This sets a fixed caption for all Items.
//    public Carousel set(CarouselCaption caption)
//    {
//        this.addInner(caption);
//        return this;
//    }
    
    public Carousel controls()
    {
        this.controls=true;
        return this;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (this.indicators)
        {
            div div=new div().addClass("carousel-indicators");
            for (int i=0;i<this.inner.getInners().size();i++)
            {
                button_button button=div.returnAddInner(new button_button()).attr("data-bs-target","#"+id()).attr("data-bs-slide-to",i);
                if (i==0)
                {
                    button.addClass("active");
                }
            }
            this.getInners().add(0,div);
            
        }
        if (this.controls)
        {
            button_button prev=new button_button().addClass("carousel-control-prev").attr("data-bs-target","#"+id()).attr("data-bs-slide","prev");
            prev.returnAddInner(new span()).addClass("carousel-control-prev-icon");
            prev.returnAddInner(new span()).addClass("visually-hidden").addInner("Previous");

            button_button next=new button_button().addClass("carousel-control-next").attr("data-bs-target","#"+id()).attr("data-bs-slide","next");
            next.returnAddInner(new span()).addClass("carousel-control-next-icon");
            next.returnAddInner(new span()).addClass("visually-hidden").addInner("Next");
            
            addInner(prev);
            addInner(next);
        }
        super.compose(composer);
    }    
    public org.nova.html.tags.script script()
    {
        return new script().addInner(HtmlUtils.js_call("new bootstrap.Carousel","#"+id()));
    }
    
    
}
