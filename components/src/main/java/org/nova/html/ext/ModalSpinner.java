package org.nova.html.ext;

import org.nova.html.StyleBuilder;
import org.nova.html.attributes.Style;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.properties.BorderStyle;
import org.nova.html.properties.BorderBoxStyle;
import org.nova.html.properties.Color;
import org.nova.html.properties.BoxColor;
import org.nova.html.properties.Display_;
import org.nova.html.properties.Overflow;
import org.nova.html.properties.Position;
import org.nova.html.properties.Length;
import org.nova.html.properties.Unit;
import org.nova.html.tags.div;
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

	public style style(Length spinnerSize,Length spinnerTop,Color spinnerColor,Color spinnerBackground,Length spinnerBorderSize,float speed,int z_index,Color modalBackground)
	{
		StyleBuilder sb=new StyleBuilder();
		{
	        Style style=new Style()
	        		.position(Position.fixed)
	        		.z_index(z_index)
	        		.left(new Length(0,Unit.px))
	           		.top(new Length(0,Unit.px))
	        		.width(new Length(100,Unit.percent))
	           		.height(new Length(100,Unit.percent))
	           		.overflow(Overflow.auto)
	           		.background_color(modalBackground);
	        		style.display(Display_.none);
	        
			sb.begin("."+this.rootName);
			sb.add(style);
			sb.end();
			
		}
		{
			Style style=new Style();
			style.border(spinnerBorderSize,new BorderBoxStyle(BorderStyle.solid),new BoxColor(spinnerBackground));
			style.border_radius(new Length(50.0f,Unit.percent));
			style.border_top(spinnerBorderSize,BorderStyle.solid,spinnerColor);
			style.width(spinnerSize);
			style.height(spinnerSize);
			String key=this.rootName+"-spin";
			String animation=key+" "+speed+"s linear infinite";
			style.add("animation", animation);
			style.add("-webkit-animation", animation);
			style.position(Position.fixed);
			style.left(new Length(50.0,Unit.percent));
			style.top(spinnerTop);
			style.margin_left(new Length(-spinnerSize.value()/2,spinnerSize.unit()));
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
	public style style(Length spinnerSize,Length spinnerTop)
	{
		return style(spinnerSize,spinnerTop,new Color("#3498db"),new Color("#f3f3f3"),new Length(spinnerSize.value()/7.5,spinnerSize.unit()),1.0f,10000,Color.rgba(64, 64,64,0.5f));
	}
	public style style(Length size)
	{
		return style(size,new Length(25.0f,Unit.percent));
	}
	
	
    public String js_show()
    {
        return HtmlUtils.js_call("document.getElementById",id())+".style.display='block'";
    }
    public String js_hide()
    {
        return HtmlUtils.js_call("document.getElementById",id())+".style.display='none'";
    }

//    public script js_showBusyServer(int delay)
//    {
//        id("busy-server-modal");
//        
//        //window.visibilitychange is used to display spinner after delay,onpageshow is used to hide spinner if page is shown using browser back button. 
//        String script="window.visibilitychange=function(){"+js_hide()+";};window.onbeforeunload=function(){setTimeout(function(){"+js_show()+";},"+delay+");};window.addEventListener('pageshow',function(event){var traversal=event.persisted||(typeof window.performance!='undefined'&&window.performance.getEntriesByType('navigation')[0].entryType==='back_forward');if(traversal){window.location.reload();}});";
//        return new script().addInner(script);
//    }
}
