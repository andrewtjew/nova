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

import org.nova.html.deprecated.Template;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.ext.Text;

public class Template extends Element 
{
    final Section[] sections;
    final ElementMap map;
    
    Template(Section[] sections)
    {
        this.sections=sections;
        this.map=new ElementMap();
    }
    
    public Template(Template template)
    {
        this.sections=template.sections;
        this.map=new ElementMap();
        this.map.putAll(template.map);
    }
    
    public Template(Element element) throws Throwable
    {
        this(TemplateComposer.build(element));
    }
    
    public void fill(String key,Element element)
    {
        this.map.put(key, element);
    }

    public void fill(String key,String text)
    {
        this.map.put(key, new Text(text));
    }
    public Template copy()
    {
        return new Template(this);
    }


    @Override
    public void compose(Composer composer) throws Throwable
    {
        for (Section section:this.sections)
        {
            section.write(composer, this.map);
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        for (Section section:this.sections)
        {
            sb.append(section);
        }
        return sb.toString();
    }
    
    
}
