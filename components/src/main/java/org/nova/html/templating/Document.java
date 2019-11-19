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
package org.nova.html.templating;

import java.util.HashMap;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.ext.Content;

public class Document extends Element
{
    final HashMap<String,Content> map;
    final Content content;
    
    public Document(Template template)
    {
        this.content=new Content();
        this.map=new HashMap<>();
        for (Element element:template.elements)
        {
            if (element instanceof InsertMarker)
            {
                InsertMarker marker=(InsertMarker)element;
                Content markerContent=new Content();
                this.map.put(marker.name,markerContent);
                this.content.addInner(markerContent);
            }
            else
            {
                this.content.addInner(element);
            }
        }
    }
    
    public <E extends Element> E fill(String name,E element)
    {
        if (element!=null)
        {
            this.map.get(name).addInner(element);
        }
        return element;
    }

    public Object fill(String name,Object object)
    {
        if (object!=null)
        {
            this.map.get(name).addInner(object);
        }
        return object;
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.compose(this.content);
    }

}
