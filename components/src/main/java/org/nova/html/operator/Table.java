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

import org.nova.html.attributes.Style;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Head;
import org.nova.html.tags.link;
import org.nova.html.tags.table;
import org.nova.html.tags.tbody;
import org.nova.html.tags.tr;

public class Table extends Element
{
    final private table table;
    final private tbody tbody;
    private TableHeader header;
    private TableFooter footer;
    
    public Table()
    {
        this(null,"Table",null);
    }
    
    private static String DEFAULT_CSS_FILE_PATH="/resources/html/widgets/Table/style.css";
    
    static public link DEFAULT_CSS_LINK()
    {
        return new link().rel(link_rel.stylesheet).type("text/css").href(DEFAULT_CSS_FILE_PATH);
    }
    
    
    public Table(Head head,String class_,String cssFilePath)
    {
        this.table=new table();
        this.tbody=new tbody();//.style("text-align:left;");

        this.table.addClass(class_);
        link link=new link().rel(link_rel.stylesheet).type("text/css").href(cssFilePath);
        if (head!=null)
        {
            head.add(class_,link);
        }
        else
        {
            this.table().addInner(link);
        }
    }
    public Table(Head head,String class_)
    {
        this(head,class_,DEFAULT_CSS_FILE_PATH);
    }
    public Table(Head head)
    {
        this(head,"Table");
    }
    
    public table table()
    {
        return this.table;
    }
    public tbody tbody()
    {
        return this.tbody;
    }
    
    public Table style(Style value)
    {
        this.table.style(value);
        return this;
    }
    
    public Table style(String value)
    {
        this.table.style(value);
        return this;
    }
    
    public void setHeader(TableHeader header)
    {
        this.header=header;
    }
    public void setHeader(Object...objects)
    {
        this.header=new TableHeader().add(objects);
    }
    public TableRow returnAddRow()
    {
        TableRow row=new TableRow();
        this.tbody.addInner(row);
        return row;
    }
    public void addRow(TableRow row)
    {
        this.tbody.addInner(row);
    }
    public void addRow(tr row)
    {
        this.tbody.addInner(row);
    }
    public void addRow(Object...objects)
    {
        this.tbody.addInner(new TableRow().add(objects));
    }
    public void setFooter(TableFooter footer)
    {
        this.footer=footer;
    }
    public void setFooterInline(Object...objects)
    {
        this.footer=new TableFooter().add(objects);
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        this.table.addInner(this.header);
        this.table.addInner(this.tbody);
        this.table.addInner(this.footer);
        composer.compose(this.table);
    }
}
