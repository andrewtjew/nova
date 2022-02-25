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

import java.util.ArrayList;

import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.QuotationMark;
import org.nova.html.ext.Text;

public class TemplateComposer extends Composer
{
    final ArrayList<Element> elements;
    private StringBuilder sb;
    
    public TemplateComposer()
    {
        this(QuotationMark.DOUBLE);
    }
    
    public TemplateComposer(QuotationMark quotationMark)
    {
        super(quotationMark);
        this.elements=new ArrayList<>();
        this.sb=new StringBuilder();
    }
    
    public void end()
    {
        this.elements.add(new Text(this.sb.toString()));
    }
    
    @Override
    public StringBuilder getStringBuilder()
    {
    	return this.sb;
    }
    
    
    void mark(ReplaceMarker marker)
    {
        if (this.sb.length()>0)
        {
            this.elements.add(new Text(this.sb.toString()));
            this.sb=new StringBuilder();
        }
        this.elements.add(marker);
    }
    

}
