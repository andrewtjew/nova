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
package org.nova.html.operator;

import java.util.ArrayList;

import org.nova.core.Pair;
import org.nova.html.elements.Composer;
import org.nova.html.tags.button_button;
import org.nova.html.tags.script;

public class AjaxButton extends button_button
{
    final private String id;
	final private String url;
	private boolean async=true;
	private String type="get";
	private ArrayList<Pair<String,String>> queryInputs;
	
	public AjaxButton(String id,String text,String url)
	{
	    this.id=id;
        id(id);
        addInner(text);
		this.url=url;
		this.queryInputs=new ArrayList<>();
	}
	public AjaxButton async(boolean async)
	{
		this.async=async;
		return this;
	}
	public AjaxButton type(String type)
	{
		this.type=type;
		return this;
	}
	public AjaxButton val(String parameterName,String inputId)
	{
		this.queryInputs.add(new Pair<String, String>(parameterName, "':$('#"+inputId+"').val()"));
		return this;
	}
	public AjaxButton prop(String parameterName,String inputId,String prop)
	{
		this.queryInputs.add(new Pair<String, String>(parameterName, "':$('#"+inputId+"').prop('"+prop+"')"));
		return this;
	}
	public AjaxButton parameter(String parameterName,String value)
	{
		this.queryInputs.add(new Pair<String, String>(parameterName, "':'"+value+"'"));
		return this;
	}
    @Override
    public void compose(Composer builder) throws Throwable
    {
//        builder.getOutputStream().write(toString().getBytes(StandardCharsets.UTF_8));
        StringBuilder sb=new StringBuilder();
        sb.append("$('#"+id+"').click(function(){$.ajax({type:'");
        sb.append(this.type);
        sb.append("',url:'");
        sb.append(this.url);
        sb.append("',dataType:'json',data:{");
        boolean commaNeeded=false;
        for (Pair<String,String> item:this.queryInputs)
        {
            if (commaNeeded==false)
            {
                sb.append("'");
                commaNeeded=true;
            }
            else
            {
                sb.append(",'");
            }
            sb.append(item.getName());
            sb.append(item.getValue());
        }
        sb.append("},async:");
        sb.append(async);
        sb.append(",success:function(data){$.each(data,function(key,value){$('#'+key).html(value);})} });});");
        addInner(new script().type("text/javascript").addInner(sb.toString()));
        super.compose(builder);
//        builder.getOutputStream().write(sb.toString().getBytes(StandardCharsets.UTF_8));
        
    }
}
