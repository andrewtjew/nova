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

public class Color
{
    final private String color;
    public Color(String color)
    {
        this.color=color;
    }
    static public Color rgba(int red,int green,int blue,float opacity)
    {
        return new Color("rgba("+red+","+green+","+blue+","+opacity+")");
    }
    static public Color rgb(int red,int green,int blue)
    {
        return new Color("rgb("+red+","+green+","+blue+")");
    }
    static public Color hsla(int hue,int saturation,int lightness,float opacity)
    {
        return new Color("hsla("+hue+","+saturation+"%,"+lightness+"%,"+opacity+")");
    }
    static public Color hsl(int hue,int saturation,int lightness)
    {
        return new Color("hsl("+hue+","+saturation+"%,"+lightness+"%)");
    }
    static public Color value(String value)
    {
        return new Color(value);
    }
    
    @Override
    public String toString()
    {
        return this.color;
    }
}
