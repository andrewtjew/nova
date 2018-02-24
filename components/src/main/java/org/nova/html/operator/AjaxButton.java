package org.nova.html.operator;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.nova.core.Pair;
import org.nova.html.Attribute;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.InnerElement;
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
        addInner(new script().type("text/javascript").addInner(sb));
        super.compose(builder);
//        builder.getOutputStream().write(sb.toString().getBytes(StandardCharsets.UTF_8));
        
    }
}
