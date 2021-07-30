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

public abstract class Element
{
    abstract public void compose(Composer composer) throws Throwable;

    private QuotationMark mark=QuotationMark.DOUBLE;
    @Override
    public String toString()
    {
        return getHtml();
    }
    
    public String getHtml(Composer composer)
    {
        try
        {
            compose(composer);
            return composer.getStringBuilder().toString();
        }
        catch(Throwable t)
        {
            throw new RuntimeException(t);
        }
   }
    public String getHtml()
    {
        return getHtml(mark);
    }
    public void setQuotationMark(QuotationMark mark)
    {
        this.mark=mark;
    }
    public QuotationMark getQuotationMark()
    {
        return this.mark;
    }
    
    public String getHtml(QuotationMark quotationMark)
    {
        return getHtml(new StringComposer(quotationMark));
   }

    static public String HREF_LOCAL_DIRECTORY=null;
    
    static protected String replaceURL(String URL)
    {
        if (HREF_LOCAL_DIRECTORY!=null)
        {
            if (URL!=null)
            {
                if (URL.indexOf(':')>=0)
                {
                    return HREF_LOCAL_DIRECTORY+"/"+URL;
                }
            }
        }
        return URL;
    }
    
}
