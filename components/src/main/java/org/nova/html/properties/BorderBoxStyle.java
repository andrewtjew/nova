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

public class BorderBoxStyle
{
    final private String value;
    
    public BorderBoxStyle(BorderStyle top,BorderStyle right,BorderStyle bottom,BorderStyle left)
    {
        if (right!=null)
        {
            if (bottom!=null)
            {
                if (left!=null)
                {
                    this.value=top+" "+right+" "+bottom+" "+left;
                    return;
                }
                this.value=top+" "+right+" "+bottom; 
                return;
            }
            this.value=top+" "+right; 
            return;
        }
        this.value=top.toString(); 
    }
    public BorderBoxStyle(BorderStyle top,BorderStyle right,BorderStyle bottom)
    {
        this(top,right,bottom,null);
    }
    public BorderBoxStyle(BorderStyle top,BorderStyle right)
    {
        this(top,right,null);
    }
    public BorderBoxStyle(BorderStyle value)
    {
        this(value,null);
    }
    @Override
    public String toString()
    {
        return this.value;
    }
    
}
