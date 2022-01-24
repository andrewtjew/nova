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
package org.nova.html.operator;

import org.nova.html.attributes.Style;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.NodeElement;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Content;
import org.nova.html.ext.Head;
import org.nova.html.tags.div;
import org.nova.html.tags.link;

public class Panel extends Element
{
    final private div panel;
    final private div content;
    final private div header;
    final private String title;
    final private Content leftHeaderElements;
    final private Content rightHeaderElements;
    
    public Panel(Head head,String class_,String cssFilePath,String title)
    {
        this.title=title;
        this.panel=new div().addClass(class_);
        this.header=this.panel.returnAddInner(new div().addClass(class_+"-Heading"));
        this.content=this.panel.returnAddInner(new div().addClass(class_+"-Content"));
        if (head!=null)
        {
            head.add(class_,new link().rel(link_rel.stylesheet).type("text/css").href(cssFilePath));
        }
        this.rightHeaderElements=new Content();
        this.leftHeaderElements=new Content();
    }
    
    public NodeElement<?> content()
    {
        return this.content;
    }
    public NodeElement<?> header()
    {
        return this.header;
    }
    public Panel style(Style style)
    {
        this.content.style(style);
        return this;
    }
    public Panel style(String style)
    {
        this.content.style(style);
        return this;
    }
    
    public Panel addRightInHeader(Element element)
    {
        this.rightHeaderElements.addInner(new div().addInner(element).style("float:right"));
        return this;
    }
    public Panel addLeftInHeader(Element element)
    {
        this.leftHeaderElements.addInner(new div().addInner(element).style("float:left"));
        return this;
    }
    @Override
    public void compose(Composer composer) throws Throwable
    {
        this.header.addInner(this.leftHeaderElements);
        this.header.addInner(title);
        this.header.addInner(this.rightHeaderElements);
        composer.compose(this.panel);
        
    }
}
