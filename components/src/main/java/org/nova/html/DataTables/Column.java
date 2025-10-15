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
package org.nova.html.DataTables;

import org.nova.html.properties.Length_;
import org.nova.html.properties.Style;

public class Column
{
    public Boolean searchable;
    //public String cellType;
    public String className;
    public String contentPadding;
    //public String createdCell;
    public String defaultContent;
    public String name;
    public Boolean orderable;
    private String width;
    public Boolean visible;
    
    public Column searchable(boolean searchable)
    {
        this.searchable=searchable;
        return this;
    }
    public Column className(String className)
    {
        this.className=className;
        return this;
    }
    public Column contentPadding(String contentPadding)
    {
        this.contentPadding=contentPadding;
        return this;
    }
    public Column defaultContent(String defaultContent)
    {
        this.defaultContent=defaultContent;
        return this;
    }
    public Column name(String name)
    {
        this.name=name;
        return this;
    }
    public Column orderable(boolean orderable)
    {
        this.orderable=orderable;
        return this;
    }
    public Column width(Length_ width)
    {
        this.width=width.toString();
        return this;
    }
    
}
