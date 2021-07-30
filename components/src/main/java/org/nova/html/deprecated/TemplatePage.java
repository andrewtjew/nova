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
package org.nova.html.deprecated;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.InnerElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.ext.Head;
import org.nova.html.tags.div;

public abstract class TemplatePage extends Element
{
    final private Template template;
    final private Head head;
    final private InnerElement<?> content;
    private QuotationMark quotationMark;
    
    protected abstract Template getStaticTemplate();
    
    public TemplatePage()
    {
        this.content=new div();
        this.head=new Head(); 
        this.template=getStaticTemplate().copy();
        this.quotationMark=QuotationMark.DOUBLE;
    }
    public Template getTemplate()
    {
        return this.template;
    }

    public InnerElement<?> content()
    {
        return this.content;
    }
    public Head head()
    {
        return this.head;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        composer.pushQuotationMark(this.quotationMark);
        try
        {
            this.template.compose(composer);
        }
        finally
        {
            composer.popQuotationMark();
        }
    }
    
    public void setQuotationMark(QuotationMark quotationMark)
    {
        this.quotationMark=quotationMark;
    }
}
