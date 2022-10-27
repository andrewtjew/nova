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
package org.nova.html.elements;

import java.util.ArrayList;
import java.util.List;

import org.nova.html.ext.Text;

public class NodeElement<ELEMENT extends NodeElement<ELEMENT>> extends Element
{
    protected ArrayList<Element> inners=null; 

    @SuppressWarnings("unchecked")
    public ELEMENT addInner(Element element)
    {
        if (element==null)
        {
            return (ELEMENT)this;
        }
        if (this.inners==null)
        {
            this.inners=new ArrayList<>(); 
        }
        this.inners.add(element);
        return (ELEMENT)this;
    }

    public ELEMENT copyToAndClearInners(NodeElement<?> target)
    {
        target.inners=this.inners;
        this.inners=null;
        return (ELEMENT)this;
    }
    
    public ELEMENT setInner(Element element)
    {
        this.inners=new ArrayList<>();
        return addInner(element);
    }
    @SuppressWarnings("unchecked")
    public ELEMENT addInners(Element...elements)
    {
        for (Element element:elements)
        {
            addInner(element);
        }
        return (ELEMENT)this;
    }
    @SuppressWarnings("unchecked")
    public ELEMENT addInner(Object object)
    {
        if (object!=null)
        {
            if (object instanceof Element)
            {
                return addInner((Element)object);
            }
            return addInner(new Text(object.toString()));
        }
        return (ELEMENT)this;
    }
    public <RETURN extends Element> RETURN returnAddInner(RETURN element)
    {
        addInner(element);
        return element;
    }
    public List<Element> getInners()
    {
        if (this.inners==null)
        {
            this.inners=new ArrayList<>(); 
        }
        return this.inners;
    }
    
//    private void getAllInners(List<Element> results,List<Element> list)
//    {
//        if (list==null)
//        {
//            return;
//        }
//        for (Element element:list)
//        {
//            results.add(element);
//            if (element instanceof NodeElement)
//            {
//                getAllInners(results,((NodeElement)element).getInners());
//            }
//        }
//    }
    
//    public List<Element> getAllInners()
//    {
//        List<Element> results=new ArrayList<Element>();
//        getAllInners(results,getInners());
//        return results;
//    }
    
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (this.inners!=null)
        {
            for (Element inner:this.inners)
            {
                inner.compose(composer);
            }
        }
    }
}
