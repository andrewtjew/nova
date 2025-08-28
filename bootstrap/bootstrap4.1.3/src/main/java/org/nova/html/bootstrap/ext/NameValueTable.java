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


import org.nova.html.bootstrap.Table;
import org.nova.html.bootstrap.TableRow;
import org.nova.html.bootstrap.classes.Float_;
import org.nova.html.bootstrap.classes.Font;
import org.nova.html.properties.Style;
import org.nova.html.properties.TextAlign_;
import org.nova.html.tags.td;

public class NameValueTable extends Table
{
    public NameValueTable add(Object name,Object value)
    {
        TableRow tr=new TableRow();
        addRow(tr);
        tr.add(new Span().font(Font.weight_bold).float_(Float_.right).addInner(name),value);
        return this;
    }
    public NameValueTable add(Object name,Object value,TextAlign_ valueAlign)
    {
        TableRow tr=new TableRow();
        addRow(tr);
        tr.add(new Span().font(Font.weight_bold).float_(Float_.right).addInner(name),new td().style(new Style().text_align(valueAlign)).addInner(value));
        return this;
    }
    public NameValueTable add(Object name,Object value,Style valueStyle)
    {
        TableRow tr=new TableRow();
        addRow(tr);
        tr.add(new Span().font(Font.weight_bold).float_(Float_.right).addInner(name),new td().style(valueStyle).addInner(value));
        return this;
    }
    public NameValueTable add(Object name,Object value,String valueStyle)
    {
        TableRow tr=new TableRow();
        addRow(tr);
        tr.add(new Span().font(Font.weight_bold).float_(Float_.right).addInner(name),new td().style(valueStyle).addInner(value));
        return this;
    }
}
