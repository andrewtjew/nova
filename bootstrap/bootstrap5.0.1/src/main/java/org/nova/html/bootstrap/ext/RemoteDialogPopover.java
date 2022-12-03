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
import org.nova.html.bootstrap.TipOption;
import org.nova.html.bootstrap.classes.Align;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.Boundary;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.bootstrap.classes.Trigger;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.Head;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.script;
import org.nova.html.ext.ModalBackground;
import org.nova.html.remote.Inputs;
import org.nova.html.remoting.CallBuilder;
import org.nova.html.remoting.FormQueryBuilder;
import org.nova.http.client.PathAndQuery;

import com.sun.jna.platform.win32.AccCtrl;

public class RemoteDialogPopover
{
    final private QuotationMark mark;
    final private GlobalEventTagElement<?> toggler;
    final private Inputs inputs;
    private Element content;
    private ModalBackground background;
    private GlobalEventTagElement<?> acceptButton;
    private GlobalEventTagElement<?> dismissButton;
    private Placement placement;
    
	public RemoteDialogPopover(QuotationMark mark,GlobalEventTagElement<?> toggler,Inputs inputs)
	{
	    this.mark=mark;
	    this.toggler=toggler;
	    this.placement=Placement.right;
	    this.inputs=inputs;
	}
    public RemoteDialogPopover(QuotationMark mark,GlobalEventTagElement<?> toggler)
    {
        this(mark,toggler,new Inputs());
    }
    public RemoteDialogPopover(GlobalEventTagElement<?> toggler,Inputs inputs)
    {
        this(QuotationMark.DOUBLE,toggler,inputs);
    }
    public RemoteDialogPopover(GlobalEventTagElement<?> toggler)
    {
        this(QuotationMark.DOUBLE,toggler,new Inputs());
    }
	
    public RemoteDialogPopover content(Element content)
    {
        this.content=content;
        return this;
    }
    public RemoteDialogPopover content(InputElement<?> content)
    {
        this.content=content;
        this.inputs.add(content);
        return this;
    }
    public RemoteDialogPopover content(String label)
    {
        return content(new Item().addInner(label));
    }
    public RemoteDialogPopover modalBackground(ModalBackground background)
    {
        this.background=background;
        return this;
    }
    public RemoteDialogPopover acceptButton(GlobalEventTagElement<?> acceptButton)
    {
        this.acceptButton=acceptButton;
        return this;
    }
    public RemoteDialogPopover dismissButton(GlobalEventTagElement<?> dismissButton)
    {
        this.acceptButton=dismissButton;
        return this;
    }
    public RemoteDialogPopover placement(Placement placement)
    {
        this.placement=placement;
        return this;
    }
	

    public void post(String action) throws Throwable
    {
        String template=new Item().addClass("popover").attr("role","tooltip").style(new Style().margin(new Size(0,unit.em)).padding(new Size(0,unit.em)))
                .addInner(new Item().addClass("popover-body")).getHtml();
        
//        if (this.acceptButton==null)
//        {
//            this.acceptButton=new Button().ml(1).color(StyleColor.success).addInner("&#x2713;");
//        }
//        if (this.dismissButton==null)
//        {
//            this.dismissButton=new Button().ml(1).color(StyleColor.secondary).addInner("&#x1f5d9;");
//        }
//
        
        Item inputGroup=new Item();//.d(Display.flex).mx(0).px(0);
        inputGroup.addInner(this.content);
                
        inputGroup.addInner(acceptButton);
        if (this.dismissButton!=null)
        {
            inputGroup.addInner(dismissButton);
        }
        String t=this.inputs.getContent();
        toggler.onclick(HtmlUtils.js_call(mark,"nova.remote.openDialogPopover"
                ,template
                ,background!=null?background.id():null
                ,toggler.id()
                ,acceptButton.id()
                ,dismissButton.id()
                ,action
                ,this.inputs.getContent()
                ,inputGroup.getHtml()
                ,placement
                ));
    }

}
