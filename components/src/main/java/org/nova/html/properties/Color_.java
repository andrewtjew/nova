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

public class Color_
{
    static public record oklch(float lightness,float chroma,float hue)
    {
    }
    
    
    
    
    
    final private String value;
    public Color_(String value)
    {
        this.value=value;
    }
    static public Color_ rgba(int red,int green,int blue,float opacity)
    {
        return new Color_("rgba("+red+" "+green+" "+blue+" / "+opacity+")");
    }
    static public Color_ rgb(int red,int green,int blue)
    {
        return new Color_("rgb("+red+","+green+","+blue+")");
    }
    static public Color_ hsla(int hue,int saturation,int lightness,float opacity)
    {
        return new Color_("hsla("+hue+","+saturation+"%,"+lightness+"%,"+opacity+")");
    }
    static public Color_ hsl(int hue,int saturation,int lightness)
    {
        return new Color_("hsl("+hue+","+saturation+"%,"+lightness+"%)");
    }
    static public Color_ oklch(float lightness,float chroma,float hue)
    {
        return new Color_("hsl("+hue+" "+chroma+" "+lightness+")");
    }
    static public Color_ oklch(float lightness,float chroma,float hue,float opacity)
    {
        return new Color_("hsl("+hue+" "+chroma+" "+lightness+" / "+opacity+")");
    }
    static public Color_ value(String value)
    {
        return new Color_(value);
    }
    
    @Override
    public String toString()
    {
        return this.value;
    }
    
    
    static record float3(float a,float b,float c)
    {
    }
    
//    static class Parser
//    {
//        final private String value;
//        private int index;
//        public Parser(Color_ color)
//        {
//            this.value=color.toString();
//            this.index=0;
//        }
//        
//        public String format()
//        {
//            this.index=this.value.indexOf('(');
//            String format=this.value.substring(0,this.index);
//            this.index++;
//            return format;
//        }
//        
//        public float3 float3()
//        {
//            
//        }
//        
//        
//        
//        public Float getFloat()
//        {
//            for (int i=this.index;i<this.value.length();i++)
//            {
//                char c=this.value.charAt(i);
//                if ((Character.isDigit(c)||c=='.'))
//                {
//                    this.index=i;
//                    break;
//                }
//                if (Character.isSpace(c)==false)
//            }
//            for (int i=this.index;i<this.value.length();i++)
//            {
//                char c=this.value.charAt(i);
//                if ((Character.isDigit(c)||c=='.'))
//                {
//                    continue;
//                }
//                String number=this.value.substring(this.index,i);
//                
//                this.index=i;
//            }
//            return null;
//        }
//    }
//    
//    public oklch oklch()
//    {
//        if (this.value.startsWith("oklch")==false)
//        {
//            return null;
//        }
//        int start=this.value.indexOf('(');
//        if (start<0)
//        {
//            return null;
//        }
//        int end=this.vla
//    }
}
