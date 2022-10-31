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
package org.nova.html.bootstrap.ext;


import org.nova.html.bootstrap.StyleTemplate;
import org.nova.html.bootstrap.Table;
import org.nova.html.ext.TableRow;
import org.nova.html.tags.tbody;
import org.nova.html.tags.td;

public class NameValueTable extends Table
{
    final private StyleTemplate nameTemplate;
    final private StyleTemplate valueTemplate;
    private tbody body;
    
    public NameValueTable(StyleTemplate nameTemplate,StyleTemplate valueTemplate)
    {
        this.nameTemplate=nameTemplate;
        this.valueTemplate=valueTemplate;
        this.body=returnAddInner(new tbody());
    }
    public NameValueTable()
    {
        this(null,null);
    }

    public NameValueTable add(StyleTemplate nameTemplate,Object name,StyleTemplate valueTemplate,Object value)
    {
        if ((nameTemplate!=null)&&(name instanceof td==false))
        {
            name=new td().addInner(name);
        }
        if ((valueTemplate!=null)&&(value instanceof td==false))
        {
            value=new td().addInner(value);
        }
        
//        tr.addClass("table-striped");
        TableRow tr=this.body.returnAddInner(new TableRow());
        tr.add(StyleTemplate.apply(nameTemplate,name));
        tr.add(StyleTemplate.apply(valueTemplate,value));
            
        return this;
    }
    public NameValueTable add(Object name,Object value)
    {
        return add(this.nameTemplate,name,this.valueTemplate,value);
    }
    public NameValueTable add(StyleTemplate nameTemplateOverride,Object name,Object value)
    {
        return add(nameTemplate,name,this.valueTemplate,value);
    }
    public NameValueTable add(Object name,StyleTemplate valueTemplateOverride,Object value)
    {
        return add(this.nameTemplate,name,valueTemplate,value);
    }
}
