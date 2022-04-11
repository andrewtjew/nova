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

import org.nova.html.deprecated.Table;
import org.nova.html.elements.Element;
import org.nova.html.tags.td;
import org.nova.html.tags.tr;


//public class TwoColumnTable extends Table
//{
//    final private td seperator;
//    
//    public TwoColumnTable(String seperator)
//    {
//        this.seperator=seperator!=null?new td().addInner(seperator):null;
//    }
//    
//    public void addItems(Element first,Element second)
//    {
//        this.addRow(new tr().addInners(new td().addInner(first),this.seperator,new td().addInner(second)));
//    }
//    public void addItems(Object first,Element second)
//    {
//        this.tbody().addInner(new tr().addInners(new td().addInner(first),this.seperator,new td().addInner(second)));
//    }
//    public void addItems(Element first,Object second)
//    {
//        this.tbody().addInner(new tr().addInners(new td().addInner(first),this.seperator,new td().addInner(second)));
//    }
//    public void addItems(Object first,Object second)
//    {
//        this.tbody().addInner(new tr().addInners(new td().addInner(first),this.seperator,new td().addInner(second)));
//    }
//    public void addSecond(Element second,boolean noSeperator)
//    {
//        tr tr=new tr().addInner(new td());
//        if (this.seperator!=null)
//        {
//            if (noSeperator)
//            {
//                tr.addInner(new td());
//            }
//            else
//            {
//                tr.addInner(this.seperator);
//            }
//        }
//        tr.addInner(new td().addInner(second));
//        this.tbody().addInner(tr);
//    }
//    public void addFirstItem(Element first,boolean noSeperator)
//    {
//        tr tr=new tr().addInner(new td().addInner(first));
//        if (this.seperator!=null)
//        {
//            if (noSeperator)
//            {
//                tr.addInner(new td());
//            }
//            else
//            {
//                tr.addInner(this.seperator);
//            }
//        }
//        tr.addInner(new td());
//        this.tbody().addInner(tr);
//    }
//    
//
//}
