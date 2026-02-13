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
import org.nova.html.bootstrap.TipOption;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.remoting.CallBuilder;
import org.nova.html.remoting.FormQueryBuilder;
import org.nova.html.tags.script;
import org.nova.html.ext.ModalBackground;
import org.nova.html.properties.Length_;
import org.nova.http.client.PathAndQuery;


public class RemotingInputPopover extends Popover    
{
	public RemotingInputPopover(TagElement<?> toggler,ModalBackground background,Length_ width,PathAndQuery pathAndQuery,InputElement<?> inputElement,GlobalEventTagElement<?> acceptButton,GlobalEventTagElement<?> dismissButton,Placement placement) throws Throwable
	{
		super(toggler);
		RemotingInputPopover container=this;//.returnAddInner(new Item());
		QuotationMark mark=QuotationMark.APOS;

		String template;

		String widthText=width.toString();
		template="<div class='popover' role='tooltip' style='margin:0;padding:0;max-width:100% !important;width:"+widthText+";'><div class='popover-body'></div></div>";

		template=HtmlUtils.escapeQuotes(template);
		this.template(template);
//		pop.container("#"+valueElement.id());
//		this.container("#"+this.id());
		
		Item inputGroup=new Item().d_flex();
		inputGroup.addInner(inputElement);

		String js_close=js_popover(TipOption.hide)+";"+background.js_hide()+";";
		FormQueryBuilder query=new FormQueryBuilder(mark);
		query.value(inputElement);
		String js_submit=new CallBuilder(mark).js_post(pathAndQuery,query)+";"+js_close;
		acceptButton.onclick(js_submit);
		inputGroup.addInner(acceptButton);
		

		dismissButton.onclick(js_close);
		inputGroup.addInner(dismissButton);

		content(inputGroup);
		//trigger(Trigger.manual);
		placement(placement);

//        String script="$('#"+toggler.id()+"').on('show.bs.popover',function(){"+background.js_show()+";});";
//        +"document.getElementById("+mark+inputElement.id()+mark+").focus();"

        String showScript="$('#"+toggler.id()+"').on('show.bs.popover',function(){"+background.js_show()+";});";
        toggler.addInner(new script().addInner(showScript));
        String shownScript="$('#"+toggler.id()+"').on('shown.bs.popover',function(){document.getElementById('"+inputElement.id()+"').focus();});";//
        toggler.addInner(new script().addInner(shownScript));
		
	}
	public RemotingInputPopover(TagElement<?> toggler,ModalBackground background,Length_ width,PathAndQuery pathAndQuery,InputElement<?> inputElement,Placement placement) throws Throwable
	{
		this(toggler,background,width,pathAndQuery,inputElement
				,new Button().ml(1).color(StyleColor.success).addInner("&#x2713;")
				,new Button().ml(1).color(StyleColor.secondary).addInner("&#x1f5d9;")
				,placement
				);
	}
	public RemotingInputPopover(TagElement<?> toggler,ModalBackground background,Length_ width,PathAndQuery pathAndQuery,InputElement<?> inputElement) throws Throwable
	{
		this(toggler,background,width,pathAndQuery,inputElement,Placement.right);
	}
}
