package org.nova.html.ext;

import org.nova.html.StyleBuilder;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.properties.BorderStyle_;
import org.nova.html.properties.BorderBoxStyle_;
import org.nova.html.properties.Color_;
import org.nova.html.properties.Length_;
import org.nova.html.properties.BoxColor_;
import org.nova.html.properties.Style;
import org.nova.html.properties.Style;
import org.nova.html.properties.Unit_;

public class Spinner extends GlobalEventTagElement<Spinner> 
{
	final String className;
	public Spinner(String className) 
	{
		super("div");
		addClass(className);
		this.className=className;
	}
	public Spinner() 
	{
		this("spinner");
	}

//	public style style(Size size,Color color,Color background,Size borderSize,float speed)
//	{
//		StyleBuilder sb=new StyleBuilder();
//		Style style=new Style();
//		style.border(borderSize,new BorderStyleRect(border_style.solid),new ColorRect(background));
//		style.border_radius(new Size(50.0f,unit.percent));
//		style.border_top(borderSize,border_style.solid,color);
//		style.width(size);
//		style.height(size);
//		String key=id()+"-spin";
//		String animation=key+" "+speed+"s linear infinite";
//		style.add("animation", animation);
//		style.add("-webkit-animation", animation);
//		sb.begin("."+id());
//		sb.add(style);
//		sb.end();
//
//		
//		String spin="0%{transform: rotate(0deg);}100%{transform:rotate(360deg);}";
//		sb.begin("@keyframes "+key);
//		sb.add(spin);
//		sb.end();
//
//		String webKitSpin="0%{-webkit-transform: rotate(0deg);}100%{-webkit-transform:rotate(360deg);}";
//		sb.begin("@-webkit-keyframes "+key);
//		sb.add(webKitSpin);
//		sb.end();
//
//		return new style().addInner(sb.toString());
//	}
    public String style(Length_ size,Color_ color,Color_ background,Length_ borderSize,float speed)
    {
        StyleBuilder sb=new StyleBuilder();
        Style style=new Style();
        style.border(borderSize,new BorderBoxStyle_(BorderStyle_.solid),new BoxColor_(background));
        style.border_radius(new Length_(50.0f,Unit_.percent));
        style.border_top(borderSize,BorderStyle_.solid,color);
        style.width(size);
        style.height(size);
        String key=id()+"-spin";
        String animation=key+" "+speed+"s linear infinite";
        style.add("animation", animation);
        style.add("-webkit-animation", animation);
        sb.begin("."+id());
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
        return sb.toString();
//        return new style().addInner(sb.toString());
    }
	
	public String style(Length_ size)
	{
		return style(size,new Color_("#3498db"),new Color_("#f3f3f3"),new Length_(1.0f,Unit_.em),2.0f);
	}
	
}
