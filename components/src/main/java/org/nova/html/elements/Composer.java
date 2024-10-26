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

import java.util.Stack;

import org.nova.localization.LocalTextResolver;

public abstract class Composer
{
    private QuotationMark quotationMark;
    private Stack<QuotationMark> stack;
    public abstract StringBuilder getStringBuilder();
    public abstract LocalTextResolver getLocalTextResolver();
    
    public Composer(QuotationMark quotationMark)
    {
        this.quotationMark=quotationMark;
    }
    public QuotationMark getQuotationMark()
    {
        return this.quotationMark;
    }
    public void pushQuotationMark(QuotationMark quotationMark)
    {
        if (this.stack==null)
        {
            this.stack=new Stack<>();
        }
        this.stack.push(this.quotationMark);
        this.quotationMark=quotationMark;
    }
    public QuotationMark popQuotationMark()
    {
        this.quotationMark=this.stack.pop();
        return this.quotationMark;
    }
    
    public void compose(Element element) throws Throwable
    {
        if (element!=null)
        {
            element.compose(this);
        }
    }
    
}
