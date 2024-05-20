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

import java.io.IOException;
import java.util.ArrayList;

import org.nova.html.deprecated.TemplateComposer;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.LocalTextResolver;
import org.nova.html.elements.QuotationMark;
import org.nova.html.ext.LocalText;

public class TemplateComposer extends Composer
{
    final private ArrayList<Section> sections;
    private StringBuilder sb;
    
    public TemplateComposer()
    {
        this(QuotationMark.DOUBLE);
    }
    
    public TemplateComposer(QuotationMark quotationMark)
    {
        super(quotationMark);
        this.sections=new ArrayList<>();
    }
    
    public void processInsertKey(InsertKey insertKey) throws IOException
    {
        this.sections.add(new StaticSection(this.sb.toString()));
        this.sections.add(new InsertSection(insertKey.getKey()));
        this.sb=new StringBuilder();
    }
    
    private Template compose_(Element element) throws Throwable 
    {
        this.sb=new StringBuilder(); 
        element.compose(this);
        if (this.sb.length()>0)
        {
            this.sections.add(new StaticSection(this.sb.toString()));
        }
        return new Template(this.sections.toArray(new Section[this.sections.size()]));
    }

    @Override
    public StringBuilder getStringBuilder()
    {
        return this.sb;
    }

    static public Template build(Element element) throws Throwable
    {
        return new TemplateComposer().compose_(element);
    }

    @Override
    public LocalTextResolver getLocalTextResolver()
    {
        return null;
    }
}
