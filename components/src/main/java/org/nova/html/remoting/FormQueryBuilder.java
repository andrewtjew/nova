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
package org.nova.html.remoting;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.TagElement;
import org.nova.html.tags.input_checkbox;
import org.nova.html.tags.input_hidden;
import org.nova.html.tags.input_number;
import org.nova.html.tags.input_text;
import org.nova.html.tags.script;
import org.nova.html.tags.select;
import org.nova.http.client.PathAndQuery;

public class FormQueryBuilder
{
//    final static String QUOTE="&#34;";
    final private String QUOTE;
    final private FormQueryBuilder parent;
    final private StringBuilder sb=new StringBuilder();
    private HashMap<String,ArrayList<String>> radios;
    
    public FormQueryBuilder(QuotationMark mark)
    {
    	this(mark,null);
    }

    public FormQueryBuilder()
    {
        this(QuotationMark.SINGLE);
    }

    public FormQueryBuilder(QuotationMark mark,FormQueryBuilder parent)
    {
        this.parent=parent;
        this.QUOTE=mark.toString();
    }
    public FormQueryBuilder(FormQueryBuilder parent)
    {
    	this(QuotationMark.SINGLE,parent);
    }
    
    private void addName(String name)
    {
        if (this.sb.length()>0)
        {
            sb.append("+"+QUOTE+"&");
        }
        sb.append(name);
        sb.append("="+QUOTE+"+");
    }
    /*
    public FormQueryBuilder addValueQuery(String name,Object value) throws Throwable
    {
        if (value==null)
        {
            return this;
        }
        addName(name);
        this.sb.append(QUOTE+value+QUOTE);
        return this;
    }
    */
    
    public FormQueryBuilder value(String name,String id,String value)
    {
        addName(name);
        this.sb.append("document.getElementById("+QUOTE+id+QUOTE+")."+value);
        return this;
    }
    
    public FormQueryBuilder add(String name,Object value) throws UnsupportedEncodingException
    {
        if (value==null)
        {
            return this;
        }
        if (this.sb.length()>0)
        {
            sb.append("+"+QUOTE+"&");
        }
        sb.append(name);
        sb.append("=");
        this.sb.append(URLEncoder.encode(value.toString(), "UTF-8"));
        this.sb.append(QUOTE);
        return this;
    }
    public FormQueryBuilder checked(InputElement<?> element)
    {
        return value(element.name(),element.id(),"checked");
    }
    public FormQueryBuilder checked(input_checkbox element)
    {
        return value(element.name(),element.id(),"checked");
    }
    public FormQueryBuilder radio(InputElement<?> element)
    {
        String name=element.name();
        if (this.radios==null)
        {
            this.radios=new HashMap<>();
        }
        ArrayList<String> list=this.radios.get(name);
        if (list==null)
        {
            list=new ArrayList<>();
            this.radios.put(name, list);
        }
        list.add(element.id());
        return this;
    }
    public FormQueryBuilder value(InputElement<?> element)
    {
        return value(element.name(),element.id(),"value");
    }
    public FormQueryBuilder value(input_number element)
    {
        return value(element.name(),element.id(),"value");
    }
    public FormQueryBuilder value(input_text element)
    {
        return value(element.name(),element.id(),"value");
    }
    public FormQueryBuilder value(input_hidden element)
    {
        return value(element.name(),element.id(),"value");
    }
    public FormQueryBuilder value(String name,TagElement<?> element)
    {
        return value(name,element.id(),"value");
    }
    public FormQueryBuilder selected(select element)
    {
        String document="document.getElementById("+QUOTE+element.id()+QUOTE+")";
        addName(element.name());
        this.sb.append(document+".options["+document+".selectedIndex].value");
        return this;
    }
    public FormQueryBuilder selected(String name,TagElement<?> element)
    {
        String document="document.getElementById("+QUOTE+element.id()+QUOTE+")";
        addName(name);
        this.sb.append(document+".options["+document+".selectedIndex].value");
        return this;
    }

    /*
    public String generateFormQuery(String path)
    {
        return 
    }
    */

    void addRadioValue(int index,ArrayList<String> ids)
    {
        String id=ids.get(index);
        String element="document.getElementById("+QUOTE+id+QUOTE+")";
        if (index==ids.size()-1)
        {
            this.sb.append(element+".checked?"+element+".value:5");
        }
        else
        {
            this.sb.append(element+".checked?"+element+".value:(");
            addRadioValue(index+1, ids);
            this.sb.append(")");
        }
    }
    
    public String js_query(PathAndQuery pathAndQuery)
    {
        if (this.radios!=null)
        {
            for (Entry<String, ArrayList<String>> entry:this.radios.entrySet())
            {
                addName(entry.getKey());
                this.sb.append('(');
                addRadioValue(0,entry.getValue());
                this.sb.append(')');

            }
            this.radios=null;
        }
        if (this.sb.length()==0)
        {
            if (this.parent!=null)
            {
                return this.parent.js_query(pathAndQuery);
            }
            return QUOTE+pathAndQuery.toString()+QUOTE;
        }
        if (this.parent!=null)
        {
            if (this.parent.sb.length()>0)
            {
                if (this.sb.length()==0)
                {
                    return this.parent.js_query(pathAndQuery);
                }
                return this.parent.js_query(pathAndQuery)+'+'+QUOTE+'&'+this.sb.toString();
            }
        }
        if (pathAndQuery.containQueries())
        {
            return QUOTE+pathAndQuery.toString()+"&"+this.sb.toString();
        }
        return QUOTE+pathAndQuery.toString()+"?"+this.sb.toString();
    }

    
//    public FormQueryBuilder onClickLocation(String path,GlobalEventTagElement<?> element)
//    {
//        element.onclick("window.location="+generateFormQuery(path));
//        return this;
//    }
//
//    public FormQueryBuilder onClickCall(String function,String path,GlobalEventTagElement<?> element)
//    {
//        element.onclick(function+"("+generateFormQuery(path)+")");
//        return this;
//    }

    public String js_location(PathAndQuery pathAndQuery)
    {
        return "window.location="+js_query(pathAndQuery);
    }

    public String js_call(String function,PathAndQuery pathAndQuery)
    {
        return function+"("+js_query(pathAndQuery)+")";
    }
    
    public script generateScript(String functionName,PathAndQuery pathAndQuery)
    {
        String text="function "+functionName+"(){"+js_location(pathAndQuery)+";}";
        return new script().addInner(text);
    }
    
}
