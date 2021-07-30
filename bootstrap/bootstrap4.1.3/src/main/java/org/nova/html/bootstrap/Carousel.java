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
import org.nova.html.ext.Content;
import org.nova.html.tags.a;
import org.nova.html.tags.div;
import org.nova.html.tags.li;
import org.nova.html.tags.script;
import org.nova.html.tags.span;
import org.nova.html.tags.ul;

public class Carousel extends StyleComponent<Carousel>
{
    private a prev;
    private a next;
    private Content items;
    private Content indicators;
    private int count;
    private boolean enableIndicators;
    
    private CarouselCaption caption;
    
    public Carousel()
    {
        super("div","carousel");
        attr("data-ride","carousel");
        this.items=new Content();
        this.indicators=new Content();
    }
    
    public Carousel slide()
    {
        addClass("slide");
        return this;
    }
    
    public Carousel add(CarouselItem item)
    {
        this.items.addInner(item);
        li indicatorItem=new li().attr("data-target","#"+id()).attr("data-slide-to",count++);
        if (item.active)
        {
            indicatorItem.addClass("active");
        }
        this.indicators.returnAddInner(indicatorItem);
        return this;
    }
    
    //This sets a fixed caption for all Items.
    public Carousel set(CarouselCaption caption)
    {
        this.addInner(caption);
        return this;
    }
    
    public Carousel indicators()
    {
        this.enableIndicators=true;
        return this;
    }
    
    public Carousel controls()
    {
        this.prev=new a().addClass("carousel-control-prev").href("#"+id()).attr("role","button").attr("data-slide","prev");
        this.prev.returnAddInner(new span()).addClass("carousel-control-prev-icon");

        this.next=new a().addClass("carousel-control-next").href("#"+id()).attr("role","button").attr("data-slide","next");
        this.next.returnAddInner(new span()).addClass("carousel-control-next-icon");
        return this;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (this.count>0)
        {
            if (this.enableIndicators)
            {
                ul ul=returnAddInner(new ul()).addClass("carousel-indicators");
                ul.attr("data-target","#"+id());
                ul.returnAddInner(this.indicators);
            }
            div inner=returnAddInner(new div()).addClass("carousel-inner");
            inner.addInner(this.items);
            addInner(this.prev);
            addInner(this.next);
        }
        super.compose(composer);
        composer.compose(this.caption);
    }
    
    public org.nova.html.tags.script script()
    {
        return new script().addInner("$(document).ready(function(){$('.carousel').carousel();});");
    }
    
    
}
