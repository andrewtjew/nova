///*******************************************************************************
// * Copyright (C) 2017-2019 Kat Fung Tjew
// * 
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// * 
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// * 
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// ******************************************************************************/
//package org.nova.html.deprecated;
//
//import org.nova.html.elements.GlobalEventTagElement;
//import org.nova.html.elements.TagElement;
//import org.nova.html.enums.link_rel;
//import org.nova.html.ext.Head;
//import org.nova.html.tags.div;
//import org.nova.html.tags.link;
//import org.nova.html.tags.script;
//import org.nova.html.tags.span;
//
//public class ModalBox extends div
//{
//    final private String id;
//    final private div box;
//    final private div content;
//    final private div header;
//    final private div footer;
//    public ModalBox(Head head,String id,String header,String footer,String sourcePath,String cssFile)
//    {
//        this.id=id;
//        id(id);
//        addClass("modal");
//        if (head!=null)
//        {
//            head.add(ModalBox.class.getCanonicalName(),new link().rel(link_rel.stylesheet).type("text/css").href(sourcePath+cssFile));
//        }
//        this.addInner(new script().addInner("window.onclick=function(event){if (event.target==document.getElementById('"+id+"')){getElementById('"+id+"').style.display='none';}}"));
//        this.box=this.returnAddInner(new div()).addClass("modal-content");
//        this.header=this.box.returnAddInner(new div()).addClass("modal-header");
//        this.header.addInner(new span().addClass("modal-close").addInner("&times;").onclick("document.getElementById('"+this.id+"').style.display='none'"));
//        this.header.addInner(new span().addInner(header));
//        this.content=this.box.returnAddInner(new div()).addClass("modal-body");
//        if (footer!=null)
//        {
//            this.footer=this.box.returnAddInner(new div()).addClass("modal-footer").addInner(footer);
//        }
//        else
//        {
//            this.footer=null;
//        }
//    }
//    public ModalBox(Head head,String id,String title,String footer)
//    {
//        this(head,id,title,footer,"/resources/html","/w3c/ModalBox/modal.css");
//    }
//    
//    public ModalBox(Head head,String id,String title)
//    {
//        this(head,id,title,null);
//    }
//
//    public div content()
//    {
//        return this.content;
//    }
//    
//    public div box()
//    {
//        return this.box;
//    }
//    
//    public ModalBox openBy(GlobalEventTagElement<? extends TagElement<?>> element)
//    {
//        element.onclick("document.getElementById('"+this.id+"').style.display='block'");
//        return this;
//    }
//}
