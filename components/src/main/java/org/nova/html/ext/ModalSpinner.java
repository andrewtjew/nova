package org.nova.html.ext;

import org.nova.html.StyleBuilder;
import org.nova.html.attributes.BorderStyleRect;
import org.nova.html.attributes.Color;
import org.nova.html.attributes.ColorRect;
import org.nova.html.attributes.Size;
import org.nova.html.attributes.Style;
import org.nova.html.attributes.border_style;
import org.nova.html.attributes.display;
import org.nova.html.attributes.overflow;
import org.nova.html.attributes.position;
import org.nova.html.attributes.unit;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.tags.div;
import org.nova.html.tags.script;
import org.nova.html.tags.style;

public class ModalSpinner extends GlobalEventTagElement<ModalSpinner>
{
	final String rootName;
	public ModalSpinner(String rootName) 
	{
		super("div");
		addClass(rootName);
		this.rootName=rootName;
		returnAddInner(new div()).addClass(rootName+"-spinner");
	}
	
	public ModalSpinner() 
	{
		this("modal-spinner");
	}

	public style style(Size spinnerSize,Size spinnerTop,Color spinnerColor,Color spinnerBackground,Size spinnerBorderSize,float speed,int z_index,Color modalBackground)
	{
		StyleBuilder sb=new StyleBuilder();
		{
	        Style style=new Style()
	        		.position(position.fixed)
	        		.z_index(z_index)
	        		.left(new Size(0,unit.px))
	           		.top(new Size(0,unit.px))
	        		.width(new Size(100,unit.percent))
	           		.height(new Size(100,unit.percent))
	           		.overflow(overflow.auto)
	           		.background_color(modalBackground);
	        		style.display(display.none);
	        
			sb.begin("."+this.rootName);
			sb.add(style);
			sb.end();
			
		}
		{
			Style style=new Style();
			style.border(spinnerBorderSize,new BorderStyleRect(border_style.solid),new ColorRect(spinnerBackground));
			style.border_radius(new Size(50.0f,unit.percent));
			style.border_top(spinnerBorderSize,border_style.solid,spinnerColor);
			style.width(spinnerSize);
			style.height(spinnerSize);
			String key=this.rootName+"-spin";
			String animation=key+" "+speed+"s linear infinite";
			style.add("animation", animation);
			style.add("-webkit-animation", animation);
			style.position(position.fixed);
			style.left(new Size(50.0,unit.percent));
			style.top(spinnerTop);
			style.margin_left(new Size(-spinnerSize.value()/2,spinnerSize.unit()));
//			style.add("transform", "translate(-100%,0)");
			//style.add("transform", "translate(-100%,0)");
			
			sb.begin("."+this.rootName+"-spinner");
			sb.add(style);
			sb.end();
	
			
			String spin="0%{transform: rotate(0deg);}100%{transform:rotate(360deg);}";
			sb.begin("@keyframes "+key);
			sb.add(spin);
			sb.end();
	
			String webKitSpin="0%{-webkit-transform: rotate(0deg);}100%{-webkit-transform:rotate(360deg);}";
			sb.begin("@-webkit-keyframes "+key);
			sb.add(webKitSpin);
			sb.end();
		}
		return new style().addInner(sb.toString());
	}
	public style style(Size spinnerSize,Size spinnerTop)
	{
		return style(spinnerSize,spinnerTop,new Color("#3498db"),new Color("#f3f3f3"),new Size(spinnerSize.value()/7.5,spinnerSize.unit()),1.0f,10000,Color.rgba(64, 64,64,0.5f));
	}
	public style style(Size size)
	{
		return style(size,new Size(25.0f,unit.percent));
	}
	
	
    public String js_show(QuotationMark mark)
    {
    	return "document.getElementById("+mark+id()+mark+").style.display="+mark+"block"+mark;
    }
    public String js_show()
    {
    	return js_show(QuotationMark.SINGLE);
    }
    public String js_hide(QuotationMark mark)
    {
    	return "document.getElementById("+mark+id()+mark+").style.display="+mark+"none"+mark;
    }
    public String js_hide()
    {
    	return js_hide(QuotationMark.APOS);
    }

    public script js_on(String onName,int delay)
    {
      return new script().addInner(onName+"=function(){setTimeout(function(){"+js_show()+";},"+delay+");}");
    }
    public script js_onbeforeunload(int delay)
    {
    	return js_on("window.visibilitychange=function(){"+js_hide()+";};window.onbeforeunload",delay);
    }
	
}
