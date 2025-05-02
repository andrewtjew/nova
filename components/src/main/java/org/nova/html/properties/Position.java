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

public class Position
{
    final unit unit;
    final double value;
    public Position(double value,unit unit)
    {
        this.value=value;
        this.unit=unit;
    }
    public Position(double value)
    {
        this(value,null);
    }
    @Override
    public String toString()
    {
        if (unit!=null)
        {
            return this.value+this.unit.toString();
        }
        return Double.toString(this.value);
        
    }
    public double value()
    {
        return value;
    }
    public unit unit()
    {
        return unit;
    }
    
    /*
    static public Size rem(double size)
    {
    	return new Size(size,org.nova.html.properties.unit.rem);
    }
    static public Size px(double size)
    {
        return new Size(size,org.nova.html.properties.unit.px);
    }
    */
}
