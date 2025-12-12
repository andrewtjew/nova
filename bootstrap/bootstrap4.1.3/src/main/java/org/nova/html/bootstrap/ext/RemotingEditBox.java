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
package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Popover;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.TipOption;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextStyle;
import org.nova.html.bootstrap.classes.Trigger;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.remoting.CallBuilder;
import org.nova.html.remoting.FormQueryBuilder;
import org.nova.html.tags.script;
import org.nova.html.ext.ModalBackground;
import org.nova.html.properties.Display_;
import org.nova.html.properties.Length_;
import org.nova.html.properties.Style;
import org.nova.http.client.PathAndQuery;

public class RemotingEditBox extends StyleComponent<RemotingEditBox>   
{
	public RemotingEditBox(ModalBackground background,Length_ width,StyleComponent<?> valueElement,PathAndQuery pathAndQuery,InputElement<?> inputElement,GlobalEventTagElement<?> editButton,GlobalEventTagElement<?> acceptButton,GlobalEventTagElement<?> dismissButton,boolean editBottom) throws Throwable
	{
        super("div",null);
//		style(new Style().z_index(background.z_index()+1).width(width));
		style(new Style().width(width));
		RemotingEditBox container=this;//.returnAddInner(new Item());
		container.d_flex();//.justify_content(Justify.center);
		valueElement.flex(Flex.grow,1);
		container.addInner(valueElement);
//		inputElement.style("width:1000px;");
		QuotationMark mark=QuotationMark.APOS;

		Popover pop=new Popover(container);
		String template;

		String widthText=width.toString();
		template="<div class='popover' role='tooltip' style='margin:0;padding:0;max-width:100% !important;width:"+widthText+";'><div class='popover-body'></div></div>";

		template=HtmlUtils.escapeQuotes(template);
		pop.template(template);
//		pop.container("#"+valueElement.id());
		
		pop.container("#"+this.id());
		String showVariable=this.id()+"_show";
		
		
		editButton.style(new Style().display(Display_.none));
		editButton.onclick("document.getElementById("+mark+editButton.id()+mark+").style.display="+mark+"none"+mark+";"
				+pop.js_popover(TipOption.show)+";"
				+"document.getElementById("+mark+inputElement.id()+mark+").focus();"
                +"document.getElementById("+mark+inputElement.id()+mark+").value=document.getElementById("+mark+valueElement.id()+mark+").innerText;"
//                +"alert(document.getElementById("+mark+valueElement.id()+mark+").innerHtml);"
				+"document.getElementById("+mark+inputElement.id()+mark+").select();"
				+"document.getElementById("+mark+editButton.id()+mark+").style.display="+mark+"none"+mark+";"
				+showVariable+"=true;"
				+background.js_show(QuotationMark.APOS)+";");
		container.addInner(editButton);
		container.onmouseover("if ("+showVariable+"==false){document.getElementById("+mark+editButton.id()+mark+").style.display="+mark+"block"+mark+";}");
		container.onmouseleave("document.getElementById("+mark+editButton.id()+mark+").style.display="+mark+"none"+mark);

		Item inputGroup=new Item().d_flex();
		inputGroup.addInner(inputElement);

		String js_close=pop.js_popover(TipOption.hide)+";"+background.js_hide()+";"+showVariable+"=false;";
		FormQueryBuilder query=new FormQueryBuilder(mark);
		query.value(inputElement);
		query.add("_valueElementId",valueElement.id());
		pathAndQuery.addQuery("_valueElementId",valueElement.id());
		String js_submit=new CallBuilder(mark).js_post(pathAndQuery,query)+";"+js_close;
		acceptButton.onclick(js_submit);
		inputGroup.addInner(acceptButton);
		
		dismissButton.onclick(js_close);
		inputGroup.addInner(dismissButton);

        pop.content(inputGroup);
		pop.trigger(Trigger.manual);
		if (editBottom)
		{
			pop.placement(Placement.bottom);
		}
		else
		{
			pop.placement(Placement.top);
		}

		this.addInner(new script().addInner(showVariable+"=false;"));
		


	}
	public RemotingEditBox(ModalBackground background,Length_ width,StyleComponent<?> valueElement,PathAndQuery pathAndQuery,InputElement<?> inputElement,boolean editBottom) throws Throwable
	{
		this(background,width,valueElement,pathAndQuery,inputElement
				,new Button().ml(1).color(StyleColor.secondary).addInner("&#x270F;")
				,new Button().ml(1).color(StyleColor.success).addInner("&#x2713;")
				,new Button().ml(1).color(StyleColor.secondary).addInner("&#x1f5d9;")
				,editBottom
				);
	}
	public RemotingEditBox(ModalBackground background,Length_ width,Object value,PathAndQuery pathAndQuery,InputElement<?> inputElement) throws Throwable
	{
		this(background,width,new Item().m(2).text(TextStyle.truncate).addInner(value),pathAndQuery,inputElement,false);
	}
}
