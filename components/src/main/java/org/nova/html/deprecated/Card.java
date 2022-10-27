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
//import org.nova.html.enums.link_rel;
//import org.nova.html.ext.Head;
//import org.nova.html.tags.div;
//import org.nova.html.tags.link;
//import org.nova.html.tags.img;
//
//public class Card extends div
//{
//    final private img img;
//    final private div content;
//    
//    public Card(Head head,String id,String sourcePath,String cssFile)
//    {
//        id(id).addClass("card");
//        head.add(Card.class.getCanonicalName(), new link().rel(link_rel.stylesheet).type("text/css").href(sourcePath+cssFile));
//        this.img=this.returnAddInner(new img()).style("width:100%");
//        this.content=this.returnAddInner(new div()).addClass("container");
//    }
//    public Card(Head head,String id)
//    {
//        this(head,id, "/resources/html","/w3c/Card/card.css");
//    }
//
//    public Card setDimensions(int width,int height)
//    {
//        this.style("width:"+width+"px;height:"+height+"px;");
//        return this;
//    }
//    
//    public Card setDimensions(String width,String height)
//    {
//        this.style("width:"+width+";height:"+height+";");
//        return this;
//    }
//    
//    public div content()
//    {
//        return this.content;
//    }
// 
//    public img img()
//    {
//        return this.img;
//    }
//    
// }
