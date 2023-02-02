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

import org.nova.html.enums.link_rel;
import org.nova.html.ext.Content;
import org.nova.html.ext.Head;
import org.nova.html.tags.button_button;
import org.nova.html.tags.div;
import org.nova.html.tags.link;

public class Accordion extends Content
{
    final private button_button button;
    final private div content;
    public Accordion(Head head,String id,boolean opened,String heading,String cssFilePath)
    {
        if (id==null)
        {
            id=Integer.toString(this.hashCode());
        }
        if (head!=null)
        {
       //     head.add(Accordion.class.getCanonicalName(),new script().src("/resources/html/js/accordion.js"));
            head.add(Accordion.class.getCanonicalName(),new link().rel(link_rel.stylesheet).type("text/css").href(cssFilePath));
        }
        if (opened==false)
        {
            this.button=returnAddInner(new button_button()).addClass("accordion");
            this.button.onclick("this.classList.toggle('active');var panel=this.nextElementSibling;if (panel.style.maxHeight){panel.style.maxHeight=null;}else{panel.style.maxHeight=10*panel.scrollHeight+'px';}");
//            this.button.onclick("openAccordions();");
            this.content=returnAddInner(new div()).addClass("accordion-content").id(id);
        }
        else
        {
            this.button=returnAddInner(new button_button()).addClass("accordion active");
            //We need to set "panel.style.maxHeight=panel.scrollHeight+'px';" twice to really set the value. It seems like setting it once is just setting the initial starting value for the transition engine and reading the value immediately just reads the intermediate value;
            //This hack is needed for both Chrome and Edge.
            this.button.onclick("this.classList.toggle('active');var panel=this.nextElementSibling;if (!panel.style.maxHeight){panel.style.maxHeight=panel.scrollHeight+'px';panel.style.maxHeight=panel.scrollHeight+'px';}if (panel.style.maxHeight!='0px'){panel.style.maxHeight=0;}else{panel.style.maxHeight=panel.scrollHeight+'px';}");
            this.content=returnAddInner(new div()).addClass("accordion-content-open").id(id);
        }
        this.button.addInner(heading);
        
    }
    public Accordion(Head head,String id,boolean opened,String heading)
    {
        this(head,id, opened, heading, "/resources/html/w3c/Accordion/style.css");
    }
    public Accordion(Head head,boolean opened,String heading)
    {
        this(head,null, opened, heading);
    }
    public button_button button()
    {
        return this.button;
    }
    public div content()
    {
        return this.content;
    }

}
