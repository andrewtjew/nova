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

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.Container;
import org.nova.html.bootstrap.Row;
import org.nova.html.bootstrap.classes.DeviceClass;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Font;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextAlign;
import org.nova.html.elements.Element;

public class NameValueContainer extends Container
{
    final private DeviceClass deviceClass;
    final private int nameSize;

    public NameValueContainer(DeviceClass deviceClass, int nameSize)
    {

        this.deviceClass = deviceClass;
        this.nameSize = nameSize;
    }

    public NameValueContainer add(Object name, Object value, TextAlign valueAlign)
    {
        Row row = returnAddInner(new Row()).my(1);
        addName(row,name);
//        row.returnAddInner(new Col(this.deviceClass, this.nameSize)).pt(1).addInner(name).text(StyleColor.dark).text(TextAlign.right);
        row.returnAddInner(new Col(this.deviceClass, 12 - this.nameSize)).py(1).addInner(value).text(valueAlign);
        return this;
    }

    public NameValueContainer add(Object name, Element element)
    {
        Row row = returnAddInner(new Row()).my(1);
        addName(row,name);
//        row.returnAddInner(new Col(this.deviceClass, this.nameSize)).pt(1).addInner(name).text(StyleColor.dark).text(TextAlign.right);
        row.returnAddInner(new Col(this.deviceClass, 12 - this.nameSize)).py(1).addInner(element);
        return this;
    }

    private void addName(Row row, Object name)
    {
        row.returnAddInner(new Col(this.deviceClass, this.nameSize)).pt(1).text(TextAlign.right).font(Font.weight_bold).addInner(name);

    }

    public NameValueContainer add(Object name, Object value)
    {
        return add(name, value, TextAlign.left);
    }

    public NameValueContainer addLine(Object value, TextAlign valueAlign)
    {
        Row row = returnAddInner(new Row()).my(2);
        row.returnAddInner(new Col(this.deviceClass, 12)).py(1).text(StyleColor.dark).font(Font.weight_bold).addInner(value).text(valueAlign);
        return this;
    }

    public NameValueContainer addLine(Object value)
    {
        return addLine(value, TextAlign.center);
    }

    public NameValueContainer addSpace()
    {
        Row row = returnAddInner(new Row()).my(1).addInner("&nbsp;");
        return this;
    }

    public NameValueContainer addLine()
    {
        Row row = returnAddInner(new Row());
        row.border(Edge.bottom);
        return this;
    }
}
