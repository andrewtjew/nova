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

import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.elements.Composer;
import org.nova.html.ext.Content;
import org.nova.html.tags.a;
import org.nova.html.tags.div;
import org.nova.html.tags.li;
import org.nova.html.tags.script;
import org.nova.html.tags.span;
import org.nova.html.tags.ul;

public class Accordion extends StyleComponent<Accordion>
{
    public Accordion()
    {
        super("div","accordion");
    }
    
    public Accordion add(String title,String text)
    {
        Card card=returnAddInner(new Card());
        Button button=card.returnAddInner(new Button()).addInner(title);
//        button.rounded(0);
//        button.text(TextAlign.left);
        button.addClass("btn-accordion");
        
        Collapse collapse=returnAddInner(new Collapse());
        collapse.linkDataParent(card);
        CardBody body=collapse.returnAddInner(new CardBody());
//        body.border();
//        body.addInner(text);
        
        button.attr("data-toggle","collapse");
        button.attr("data-target","#"+collapse.id());
        
        return this;
    }
    
    
}
