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
package org.nova.html.deprecated;

import org.nova.html.attributes.Style;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Head;
import org.nova.html.ext.Text;
import org.nova.html.tags.a;
import org.nova.html.tags.link;

public class LinkButton extends Element
{
    final private a a;
    public LinkButton(Head head,String href,Element label,String title,String cssPath)
    {
        this.a=new a().href(href);
        String class_="MoreLink";
        this.a.addClass(class_);
        link link=new link().rel(link_rel.stylesheet).type("text/css").href(cssPath);
        head.add(class_,link);
        this.a.addInner(label);
        this.a.title(title);
    }
    public LinkButton(Head head,String href,Element label,String title)
    {
        this(head,href,label,title,"/resources/html/widgets/LinkButton/style.css");
    }
    public LinkButton(Head head,String href,Element label)
    {
        this(head,href,label,null);
    }
    public LinkButton(Head head,String href,String label)
    {
        this(head,href,new Text(label),null);
    }
    
    public LinkButton style(String style)
    {
        a.style(style);
        return this;
    }

    public LinkButton style(Style style)
    {
        a.style(style);
        return this;
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.compose(this.a);
    }

}
