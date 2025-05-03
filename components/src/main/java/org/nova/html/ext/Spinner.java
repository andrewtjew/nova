package org.nova.html.ext;

import org.nova.html.StyleBuilder;
import org.nova.html.attributes.Style;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.properties.BorderStyle;
import org.nova.html.properties.BorderBoxStyle;
import org.nova.html.properties.Color;
import org.nova.html.properties.BoxColor;
import org.nova.html.properties.Size;
import org.nova.html.properties.Unit;

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
    public String style(Size size,Color color,Color background,Size borderSize,float speed)
    {
        StyleBuilder sb=new StyleBuilder();
        Style style=new Style();
        style.border(borderSize,new BorderBoxStyle(BorderStyle.solid),new BoxColor(background));
        style.border_radius(new Size(50.0f,Unit.percent));
        style.border_top(borderSize,BorderStyle.solid,color);
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
	
	public String style(Size size)
	{
		return style(size,new Color("#3498db"),new Color("#f3f3f3"),new Size(1.0f,Unit.em),2.0f);
	}
	
}
