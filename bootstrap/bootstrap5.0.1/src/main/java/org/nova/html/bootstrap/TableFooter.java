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
package org.nova.html.bootstrap;

import org.nova.html.attributes.Style;
import org.nova.html.elements.Element;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.TableRow;
import org.nova.html.tags.td;
import org.nova.html.tags.th;
import org.nova.html.tags.tr;

public class TableFooter extends StyleComponent<TableFooter>
{
    public TableFooter()
    {
        super("tfoot",null);
    }

    public TableFooter addRowWithStyle(StyleTemplate template,Object...objects)
    {
        returnAddInner(new TableHeaderRow()).addWithStyle(template, objects);
        return this;
    }

    public TableFooter addRow(Object...objects)
    {
        returnAddInner(new TableHeaderRow()).add(objects);
        return this;
    }}