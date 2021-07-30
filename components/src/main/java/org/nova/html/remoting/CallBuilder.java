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

import org.nova.html.elements.Element;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.Head;
import org.nova.html.tags.script;
import org.nova.http.client.PathAndQuery;

public class CallBuilder
{
    public CallBuilder(QuotationMark mark,Head head)
    {
        head.add("_"+this.hashCode(), new script().src("/resources/html/js/remoting.js"));
        this.mark=mark;
    }
    public CallBuilder(Head head)
    {
    	this(QuotationMark.SINGLE,head);
    }
    
    final private QuotationMark mark;
    
    public CallBuilder(QuotationMark mark)
    {
    	this.mark=mark;
    }
    
    public CallBuilder()
    {
    	this(QuotationMark.SINGLE);
    }

    /*
    public String generateGet(String path,FormQueryBuilder formQueryBuilder)
    {
        return "org.nova.html.remoting.get("+formQueryBuilder.generateFormQuery(path)+")";
    }
    public String generatePost(String path,FormQueryBuilder formQueryBuilder)
    {
        return "org.nova.html.remoting.post("+formQueryBuilder.generateFormQuery(path)+")";
    }
    */
    public String js_get(PathAndQuery pathAndQuery,FormQueryBuilder formQueryBuilder)
    {
        return "org.nova.html.remoting.get("+formQueryBuilder.js_query(pathAndQuery)+")";
    }
    public String js_post(PathAndQuery pathAndQuery,FormQueryBuilder formQueryBuilder)
    {
        return "org.nova.html.remoting.post("+formQueryBuilder.js_query(pathAndQuery)+")";
    }
    public String js_post(String path,FormQueryBuilder formQueryBuilder)
    {
        return js_post(new PathAndQuery(path),formQueryBuilder);
    }
    /*
    public String generateOneGet(PathAndQuery pathAndQuery,FormQueryBuilder formQueryBuilder)
    {
        return "org.nova.html.remoting.get("+formQueryBuilder.generateFormQuery(pathAndQuery)+")";
    }
    public String generateOnePost(PathAndQuery pathAndQuery,FormQueryBuilder formQueryBuilder)
    {
        return "(function(){this.disabled=true;org.nova.html.remoting.post("+formQueryBuilder.generateFormQuery(pathAndQuery)+";)();";
    }
    */
    
    public String js_get(PathAndQuery pathAndQuery)
    {
        return "org.nova.html.remoting.get("+this.mark+pathAndQuery.toString()+this.mark+")";
    }
    public String js_post(PathAndQuery pathAndQuery)
    {
        return "org.nova.html.remoting.post("+this.mark+pathAndQuery.toString()+this.mark+")";
    }
//--
    /*
    public String generateScheduleGet(String path,FormQueryBuilder formQueryBuilder,String timerName,int intervalMs)
    {
        return "org.nova.html.remoting.scheduleGet("+formQueryBuilder.generateFormQuery(path)+",'"+timerName+"',"+intervalMs+")";
    }
    public String generateSchedulePost(String path,FormQueryBuilder formQueryBuilder,String timerName,int intervalMs)
    {
        return "org.nova.html.remoting.schedulePost("+formQueryBuilder.generateFormQuery(path)+",'"+timerName+"',"+intervalMs+")";
    }
    */
    public String js_scheduleGet(PathAndQuery pathAndQuery,FormQueryBuilder formQueryBuilder,String timerName,int intervalMs)
    {
        return "org.nova.html.remoting.scheduleGet("+formQueryBuilder.js_query(pathAndQuery)+",'"+timerName+"',"+intervalMs+")";
    }
    public String js_schedulePost(PathAndQuery pathAndQuery,FormQueryBuilder formQueryBuilder,String timerName,int intervalMs)
    {
        return "org.nova.html.remoting.schedulePost("+formQueryBuilder.js_query(pathAndQuery)+",'"+timerName+"',"+intervalMs+")";
    }
    
    public String js_scheduleGet(PathAndQuery pathAndQuery,String timerName,int intervalMs)
    {
        return "org.nova.html.remoting.scheduleGet('"+pathAndQuery.toString()+"','"+timerName+"',"+intervalMs+")";
    }
    public String js_schedulePost(PathAndQuery pathAndQuery,String timerName,int intervalMs)
    {
        return "org.nova.html.remoting.schedulePost('"+pathAndQuery.toString()+"','"+timerName+"',"+intervalMs+")";
    }


}
