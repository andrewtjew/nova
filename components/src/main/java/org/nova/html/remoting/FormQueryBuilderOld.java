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

import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.Head;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.input_checkbox;
import org.nova.html.tags.input_radio;
import org.nova.html.tags.script;
import org.nova.html.tags.select;
import org.nova.html.tags.textarea;

public class FormQueryBuilderOld
{
    private final String path;
    private final StringBuilder sb;
    
    public FormQueryBuilderOld(String path)
    {
        this.path=path;
        this.sb=new StringBuilder();
    }
    
    
    private void addName(String name)
    {
        if (this.sb.length()==0)
        {
            sb.append('"');
            sb.append(this.path+"?");
        }
        else
        {
            sb.append("+\"&");
        }
        sb.append(name);
        sb.append("=\"+");
    }
    
    private String getDocumentById(String id)
    {
        return "document.getElementById(\""+id+"\")";
    }
    public FormQueryBuilderOld addQuery(String name,String value)
    {
        if (value!=null)
        {
            addName(name);
            sb.append("\""+value+"\"");
        }
        return this;
    }
    
    public FormQueryBuilderOld addQuery(String name,textarea element)
    {
        addName(name);
        String id=element.id();
        sb.append(getDocumentById(id)+".value");
        return this;
    }
    public FormQueryBuilderOld addValueQuery(String name,TagElement<?> element)
    {
        addName(name);
        String id=element.id();
        sb.append(getDocumentById(id)+".value");
        return this;
    }
    public FormQueryBuilderOld addQuery(String name,input_checkbox element)
    {
        return addCheckedQuery(name,element);
    }
    public FormQueryBuilderOld addQuery(input_checkbox element)
    {
        return addCheckedQuery(element.name(),element);
    }
    public FormQueryBuilderOld addQuery(input_radio element)
    {
        return addCheckedQuery(element.name(),element);
    }
    public FormQueryBuilderOld addQuery(String name,input_radio element)
    {
        return addCheckedQuery(name,element);
    }
    public FormQueryBuilderOld addCheckedQuery(String name,TagElement<?> element)
    {
        addName(name);
        String id=element.id();
        sb.append(getDocumentById(id)+".checked");
        return this;
    }
    
    public FormQueryBuilderOld addQuery(select element)
    {
        addName(element.name());
        String id=element.id();
        String document=getDocumentById(id);
        this.sb.append(document+".options["+document+".selectedIndex].value");
        return this;
    }
    public FormQueryBuilderOld addQuery(InputElement<?> element)
    {
        addName(element.name());
        String id=element.id();
        String document=getDocumentById(id);
        sb.append(getDocumentById(id)+".value");
        return this;
    }
    
    public String generateWindowLocationAssignFunction(String functionName)
    {
        
        return "function "+functionName+"(){window.location.assign("+this.sb.toString()+");}";
    }
    public String generateWindowLocationAssign()
    {
        return "window.location.assign("+this.sb.toString()+");";
    }
    public String generate()
    {
        if (this.sb.length()==0)
        {
            return '"'+this.path+'"';
        }
        return this.sb.toString();
    }
    
    public script generateWindowLocationAssignScript(String functionName)
    {
        return new script().addInner(generateWindowLocationAssignFunction(functionName));
    }
    
    public FormQueryBuilderOld generateOnClick(GlobalEventTagElement<?> element,String functionName)
    {
        element.onclick(functionName+"("+HtmlUtils.escapeString(generate())+")");
        return this;
    }
    public FormQueryBuilderOld generateOnClickLocation(GlobalEventTagElement<?> element)
    {
        element.onclick("window.location="+HtmlUtils.escapeString(generate()));
        return this;
    }
    public FormQueryBuilderOld generateWindowLocationAssignScript(String name,Head head)
    {
        head.add(name, generateWindowLocationAssignScript(name));
        return this;
    }
}
