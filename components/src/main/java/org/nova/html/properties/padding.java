/*******************************************************************************
Property.java * Copyright (C) 2017-2019 Kat Fung Tjew
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
package org.nova.html.properties;

public class padding extends Property
{
    public padding(Length size)
    {
        super("padding:"+size.toString()+";");
    }
    public padding(double top,double right,double bottom,double left,Unit unit)
    {
        super("padding-top:"+new Length(top,unit).toString()
                +";padding-right:"+new Length(right,unit).toString()
                +";padding-bottom:"+new Length(bottom,unit).toString()
                +";padding-left:"+new Length(left,unit).toString()
                +";"
                );
    }
    public padding(double size,Unit unit)
    {
        this(new Length(size,unit));
    }
    public padding(double size)
    {
        this(new Length(size,null));
    }
}
