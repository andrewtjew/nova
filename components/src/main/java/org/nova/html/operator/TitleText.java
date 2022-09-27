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

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.tags.span;

public class TitleText extends Element
{
    final private String text;
    final private String title;

    public TitleText(String text,int maxDisplayLength)
    {
        if (text!=null)
        {
            if (text.length()<=maxDisplayLength)
            {
                this.text=text;
                this.title=null;
            }
            else
            {
                this.title=text;
                this.text=text.substring(0, maxDisplayLength)+"...";
            }
        }
        else
        {
            this.text=null;
            this.title=null;
        }
    }
    public TitleText(String title,String text)
    {
        this.title=title;
        this.text=text;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (text!=null)
        {
            if (this.title==null)
            {
                span element=new span();
                element.addInner(text);
                element.compose(composer);
            }
            else
            {
                span element=new span();
                element.title(title.replace("\"", "&quot;")).addInner(text);
                element.compose(composer);
            }
        }
    }
}
