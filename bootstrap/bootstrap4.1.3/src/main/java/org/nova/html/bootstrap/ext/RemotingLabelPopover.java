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

import org.nova.html.attributes.Size;
import org.nova.html.attributes.Style;
import org.nova.html.attributes.display;
import org.nova.html.attributes.unit;
import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Popover;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.TipOption;
import org.nova.html.bootstrap.classes.Align;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.TextStyle;
import org.nova.html.bootstrap.classes.Trigger;
import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.Head;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.remoting.CallBuilder;
import org.nova.html.remoting.FormQueryBuilder;
import org.nova.html.tags.script;
import org.nova.html.ext.ModalBackground;
import org.nova.http.client.PathAndQuery;

public class RemotingLabelPopover extends Popover    
{
	public RemotingLabelPopover(TagElement<?> toggler,ModalBackground background,String js_onAccept,String label,GlobalEventTagElement<?> acceptButton,GlobalEventTagElement<?> dismissButton,Placement placement) throws Throwable
	{
		super(toggler);
		RemotingLabelPopover container=this;//.returnAddInner(new Item());
		QuotationMark mark=QuotationMark.APOS;

		String template;

		template="<div class='popover' role='tooltip' style='margin:0;padding:0;'><div class='popover-body'></div></div>";

		template=HtmlUtils.escapeQuotes(template);
		this.template(template);
//		pop.container("#"+valueElement.id());
//		this.container("#"+this.id());
		
		Item inputGroup=new Item().d_flex();
		inputGroup.addInner(new Item().mr(2).align_self(AlignSelf.center).addInner(label));

		String js_close=js_popover(TipOption.hide)+";"+background.js_hide()+";";
		FormQueryBuilder query=new FormQueryBuilder(mark);
		String js_submit=js_onAccept+";"+js_close;
		acceptButton.onclick(js_submit);
		inputGroup.addInner(acceptButton);
		

		dismissButton.onclick(js_close);
		inputGroup.addInner(dismissButton);

		content(inputGroup);
		//trigger(Trigger.manual);
		placement(placement);
		
		String script="$('#"+toggler.id()+"').on('show.bs.popover',function(){"+background.js_show()+";});";
		toggler.addInner(new script().addInner(script));
		
	}
	public RemotingLabelPopover(TagElement<?> toggler,ModalBackground background,String js_onAccept,String label,Placement placement) throws Throwable
	{
		this(toggler,background,js_onAccept,label
				,new Button().ml(1).color(StyleColor.success).addInner("&#x2713;")
				,new Button().ml(1).color(StyleColor.secondary).addInner("&#x1f5d9;")
				,placement
				);
	}
	public RemotingLabelPopover(TagElement<?> toggler,ModalBackground background,String js_onAccept,String label) throws Throwable
	{
		this(toggler,background,js_onAccept,label,Placement.right);
	}
}
