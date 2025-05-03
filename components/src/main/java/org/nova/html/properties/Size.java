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
package org.nova.html.properties;

//Just the same as position but works better with properties like width and height.
public class Size extends Position
{
    final private boolean important;
    public Size(double value,boolean important)
    {
        super(value);
        this.important=important;
    }
    public Size(double value)
    {
        this(value,false);
    }
    public Size(double value,Unit unit,boolean important)
    {
        super(value,unit);
        this.important=important;
    }
    public Size(double value,Unit unit)
    {
        this(value,unit,false);
    }
    public boolean important()
    {
        return this.important;
    }
    @Override
    public String toString()
    {
        if (this.important)
        {
            return super.toString()+" !important";
        }
        return super.toString();
        
    }
}
