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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.nova.html.ext.HtmlUtils;
import org.nova.json.ObjectMapper;
import org.nova.testing.Debugging;


public class TagElement<ELEMENT extends TagElement<ELEMENT>> extends NodeElement<ELEMENT>
{
    String id;
    final private String tag;
    final private boolean noEndTag;
    final private StringBuilder classBuilder;
//    final private ArrayList<NameObject> attributes;
    final private HashMap<String,Object> attributes;
    
    final public static int INCLUDE_STACK_TRACE_LEVELS=0;
    final static String STACK_TRACE_KEY="java-source"; 
//    final static String STACK_TRACE_KEY="title"; //Use this to view the stack traces by using the mouse, but clashes with html elements using the title attribute
    
    public TagElement(String tag,boolean noEndTag)
    {
        this.tag=tag;
        this.noEndTag=noEndTag;
        this.classBuilder=new StringBuilder();
        this.attributes=new HashMap<>();
        if (Debugging.ENABLE&&INCLUDE_STACK_TRACE_LEVELS>0)
        {
            StackTraceElement[] stackTraceElements=Thread.currentThread().getStackTrace();
            StringBuilder sb=new StringBuilder();
            for (int i=0;i<stackTraceElements.length;i++)
            {
                StackTraceElement stackTraceElement=stackTraceElements[i];
                String className=stackTraceElement.getClassName();
                if (className.startsWith("org.nova"))
                {
                    continue;
                }
                else if (className.startsWith("java"))
                {
                    continue;
                }
                for (int j=0;j<INCLUDE_STACK_TRACE_LEVELS;j++)
                {
                    if (j+i>=stackTraceElements.length)
                    {
                        break;
                    }
                    sb.append(className+stackTraceElement.getMethodName()+"("+stackTraceElement.getFileName()+"."+stackTraceElement.getLineNumber()+");");
                }
                break;
            }
            attr(STACK_TRACE_KEY,sb.toString());
        }
        
    }
    
    public TagElement(String tag)
    {
        this(tag,false);
    }
    public Map<String,Object> getAttributes()
    {
        return this.attributes;
    }
    public String getTag()
    {
        return this.tag;
    }
    
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
    
    static private AtomicLong ID=new AtomicLong();
    
    public String id()
    {
        if (this.id==null)
        {
            this.id="_"+ID.getAndIncrement();
            return this.id;
        }
        return this.id;
    }
    @SuppressWarnings("unchecked")
    public ELEMENT attr(String name,Object value)
    {
        if (value!=null)
        {
            this.attributes.put(name,value);
        }
        return (ELEMENT) this;
    }

    @SuppressWarnings("unchecked")
    public ELEMENT attr(String name)
    {
        this.attributes.put(name,null);
        return (ELEMENT) this;
    }
//    public ELEMENT clearAttributes()
//    {
//        this.attributes.clear();
//        return (ELEMENT) this;
//    }

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
        for (Entry<String, Object> entry:this.attributes.entrySet())
        {
            composerStringBuilder.append(' ').append(entry.getKey());
            Object value=entry.getValue();
            if (value!=null)
            {
                Class<?> type=value.getClass();
                if (type==String.class)
                {
                    composerStringBuilder.append("=").append(mark).append(HtmlUtils.escapeAttributeString(value.toString())).append(mark);
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
                    String text=ObjectMapper.writeObjectToString(value);//.replace("\"", "&#34;");
                    composerStringBuilder.append("=").append(mark).append(HtmlUtils.escapeAttributeString(text)).append(mark);
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
