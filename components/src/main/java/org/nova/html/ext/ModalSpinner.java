package org.nova.html.ext;

import org.nova.html.StyleBuilder;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.properties.BorderStyle_;
import org.nova.html.properties.BorderBoxStyle_;
import org.nova.html.properties.Color_;
import org.nova.html.properties.Display_;
import org.nova.html.properties.Length_;
import org.nova.html.properties.BoxColor_;
import org.nova.html.properties.Overflow_;
import org.nova.html.properties.Position_;
import org.nova.html.properties.Style;
import org.nova.html.properties.Unit_;
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

	public style style(Length_ spinnerSize,Length_ spinnerTop,Color_ spinnerColor,Color_ spinnerBackground,Length_ spinnerBorderSize,float speed,int z_index,Color_ modalBackground)
	{
		StyleBuilder sb=new StyleBuilder();
		{
	        Style style=new Style()
	        		.position(Position_.fixed)
	        		.z_index(z_index)
	        		.left(new Length_(0,Unit_.px))
	           		.top(new Length_(0,Unit_.px))
	        		.width(new Length_(100,Unit_.percent))
	           		.height(new Length_(100,Unit_.percent))
	           		.overflow(Overflow_.auto)
	           		.background_color(modalBackground);
	        		style.display(Display_.none);
	        
			sb.begin("."+this.rootName);
			sb.add(style);
			sb.end();
			
		}
		{
			Style style=new Style();
			style.border(spinnerBorderSize,new BorderBoxStyle_(BorderStyle_.solid),new BoxColor_(spinnerBackground));
			style.border_radius(new Length_(50.0f,Unit_.percent));
			style.border_top(spinnerBorderSize,BorderStyle_.solid,spinnerColor);
			style.width(spinnerSize);
			style.height(spinnerSize);
			String key=this.rootName+"-spin";
			String animation=key+" "+speed+"s linear infinite";
			style.add("animation", animation);
			style.add("-webkit-animation", animation);
			style.position(Position_.fixed);
			style.left(new Length_(50.0,Unit_.percent));
			style.top(spinnerTop);
			style.margin_left(new Length_(-spinnerSize.value()/2,spinnerSize.unit()));
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
	public style style(Length_ spinnerSize,Length_ spinnerTop)
	{
		return style(spinnerSize,spinnerTop,new Color_("#3498db"),new Color_("#f3f3f3"),new Length_(spinnerSize.value()/7.5,spinnerSize.unit()),1.0f,10000,Color_.rgba(64, 64,64,0.5f));
	}
	public style style(Length_ size)
	{
		return style(size,new Length_(25.0f,Unit_.percent));
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
