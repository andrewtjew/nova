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
package org.nova.html.elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.nova.core.NameObject;
import org.nova.html.ext.HtmlUtils;
import org.nova.json.ObjectMapper;


public class TagElement<ELEMENT extends TagElement<ELEMENT>> extends NodeElement<ELEMENT>
{
    private String id;
    final private String tag;
    final private boolean noEndTag;
    final private StringBuilder classBuilder;
    final private ArrayList<NameObject> attributes;
    
    public TagElement(String tag,boolean noEndTag)
    {
        this.tag=tag;
        this.noEndTag=noEndTag;
        this.classBuilder=new StringBuilder();
        this.attributes=new ArrayList<NameObject>();
    }
    
    public TagElement(String tag)
    {
        this(tag,false);
    }
    public List<NameObject> getAttributes()
    {
        return this.attributes;
    }
    public String getTag()
    {
        return this.tag;
    }
    
//    public ELEMENT addClass(String class_)
//    {
//        if (class_!=null)
//        {
//            if (this.classBuilder.length()>0)
//            {
//                this.classBuilder.append(' ');
//            }
//            this.classBuilder.append(class_);
//        }
//        return (ELEMENT) this;
//    }
    
    public ELEMENT addClass(Object class_,Object...fragments)
    {
        if (class_!=null)
        {
            if (this.classBuilder.length()>0)
            {
                this.classBuilder.append(' ');
            }
            this.classBuilder.append(class_);
            if (fragments!=null)
            {
                if (class_!=null)
                {
                    for (Object fragment:fragments)
                    {
                        if (fragment!=null)
                        {
                            this.classBuilder.append('-').append(fragment);
                        }
                    }
                }
            }
        }
        return (ELEMENT)this;
    }
    
    
    public ELEMENT id(String value)
    {
        if (value!=null)
        {
            this.id=value;
        }
        return (ELEMENT) this;
    }
    public String id()
    {
        if (this.id==null)
        {
            this.id="_"+this.hashCode();
            return this.id;
        }
        return this.id;
    }
    @SuppressWarnings("unchecked")
    public ELEMENT attr(String name,Object value)
    {
        if (value!=null)
        {
            this.attributes.add(new NameObject(name,value));
        }
        return (ELEMENT) this;
    }
    public ELEMENT attr(NameObject attr)
    {
        if (attr!=null)
        {
            this.attributes.add(attr);
        }
        return (ELEMENT) this;
    }

    @SuppressWarnings("unchecked")
    public ELEMENT attr(String name)
    {
        this.attributes.add(new NameObject(name,null));
        return (ELEMENT) this;
    }

    public String class_()
    {
        return this.classBuilder.toString();
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (this.classBuilder.length()>0)
        {
            attr("class",this.classBuilder.toString());
        }
        attr("id",this.id);

        
        StringBuilder composerStringBuilder=composer.getStringBuilder();
        composerStringBuilder.append('<').append(this.tag);

        QuotationMark mark=composer.getQuotationMark();
        for (NameObject item:this.attributes)
        {
            composerStringBuilder.append(' ').append(item.getName());
            Object value=item.getValue();
            if (value!=null)
            {
                Class<?> type=value.getClass();
                if (type==String.class)
                {
                    composerStringBuilder.append("=").append(mark).append(value).append(mark);
                }
                else if ((type.isPrimitive())
                        ||(type.isEnum())
                        ||(type==Long.class)
                        ||(type==Float.class)
                        ||(type==Double.class)
                        ||(type==Boolean.class)
                        ||(type==Integer.class)
                        ||(type==BigDecimal.class)
                        ||(type==Byte.class)
                        ||(type==Short.class)
                        
                        )
                {
                    composerStringBuilder.append("=").append(mark).append(value).append(mark);
                }
                else
                {
                    String text=ObjectMapper.writeObjectToString(value).replace("\"", "&#34;");
//                    composerStringBuilder.append("=").append(mark).append(text).append(mark);
                    composerStringBuilder.append("=").append(mark).append(text).append(mark);
                }
            }
            
        }
        composerStringBuilder.append('>');
        if (this.noEndTag==false)
        {
            super.compose(composer);
            composerStringBuilder=composer.getStringBuilder();
            composerStringBuilder.append("</").append(this.tag).append('>'); //end tag
        }
    }
}
