package org.nova.html.deprecated;
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
//package org.nova.html.deprecated;
//
//import org.nova.html.enums.link_rel;
//import org.nova.html.ext.Head;
//import org.nova.html.tags.a;
//import org.nova.html.tags.div;
//import org.nova.html.tags.link;
//
//public class VerticalMenu extends div
//{
//    public VerticalMenu(Head head,String id,String sourcePath,String cssFile)
//    {
//        if (id==null)
//        {
//            id=Integer.toString(this.hashCode());
//        }
//        id(id).addClass("verticalmenu");
//        if (head!=null)
//        {
//            head.add(VerticalMenu.class.getCanonicalName(),new link().rel(link_rel.stylesheet).type("text/css").href(sourcePath+cssFile));
//        }
//    }
//    public VerticalMenu(Head head,String id)
//    {
//        this(head,id, "/resources/html","/w3c/VerticalMenu/style.css");
//    }
//    
//    public VerticalMenu addMenuItem(String name,String href)
//    {
//        this.addInner(new a().href(href).addInner(name));
//        return this;
//    }
//}
