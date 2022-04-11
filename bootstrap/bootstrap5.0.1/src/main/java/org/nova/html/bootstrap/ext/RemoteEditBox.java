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
import org.nova.html.bootstrap.Styler;
import org.nova.html.bootstrap.TipOption;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.Placement;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.classes.Text;
import org.nova.html.bootstrap.classes.Trigger;
import org.nova.html.elements.Element;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.GlobalTagElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.tags.div;
import org.nova.html.tags.script;
import org.nova.html.ext.ModalBackground;
import org.nova.html.remote.Inputs;
import org.nova.html.remoting.CallBuilder;
import org.nova.html.remoting.FormQueryBuilder;
import org.nova.http.client.PathAndQuery;

import com.amazonaws.services.kinesisanalytics.model.Input;

public class RemoteEditBox extends StyleComponent<RemoteEditBox>   
{
    final private QuotationMark mark;
    final private Inputs inputs;
    final private InputElement<?> inputElement;
    final private StyleComponent<?> valueElement;
    private Element content;
    private ModalBackground background;
    private GlobalEventTagElement<?> acceptButton;
    private GlobalEventTagElement<?> dismissButton;
    private GlobalEventTagElement<?> editButton;
    
    private Placement placement;
    
//	public RemotingEditBox(ModalBackground background,StyleComponent<?> valueElement,String action,InputElement<?> inputElement,GlobalEventTagElement<?> editButton,GlobalEventTagElement<?> acceptButton,GlobalEventTagElement<?> dismissButton,Placement placement) throws Throwable
//	{
//        super("div",null);
//		this.d(Display.flex);
//		valueElement.flex(Flex.grow,1);
//		this.addInner(valueElement);
//
//		String template;
//        template="<div class='popover' role='tooltip' style='margin:0;padding:0;width:inherit;max-width:100% !important;'><div class='popover-body'></div></div>";
//		
//		editButton.style(new Style().display(display.none));
//
//		this.addInner(editButton);
//        QuotationMark mark=QuotationMark.DOUBLE;
//		this.onmouseover("document.getElementById("+mark+editButton.id()+mark+").style.display="+mark+"block"+mark);
//		this.onmouseleave("document.getElementById("+mark+editButton.id()+mark+").style.display="+mark+"none"+mark);
//
//		Item inputGroup=new Item().d(Display.flex).mx(0).px(0);
//		inputElement.addClass(new ClassBuilder().w(100).toString());
//		inputGroup.addInner(inputElement);
//		inputGroup.addInner(acceptButton);
//		inputGroup.addInner(dismissButton);
//
//		editButton.onclick(HtmlUtils.js_call("Remoting.openEditBox"
//                , template
//                ,background!=null?background.id():null
//                ,id()
//                ,acceptButton.id()
//                ,dismissButton.id()
//                ,editButton.id()
//                ,inputElement.id()
//                ,valueElement.id()
//                ,action
//                ,inputGroup.getHtml() //This cannot be called before the id() method, otherwise the ids won't be generated.
//                ,placement
//                ));
//
//
//	}
    public RemoteEditBox(QuotationMark mark,StyleComponent<?> valueElement,InputElement<?> inputElement,Inputs inputs)
    {
        super("div",null);
//        this.d(Display.flex);
//        valueElement.flex(Flex.grow,1);
        this.addInner(valueElement);

        this.mark=mark;
        this.placement=Placement.bottom;
        this.inputs=inputs;
        this.inputElement=inputElement;
        this.valueElement=valueElement;
    
    }
    public RemoteEditBox(QuotationMark mark,StyleComponent<?> valueElement,InputElement<?> inputElement)
    {
        this(mark,valueElement,inputElement,new Inputs());
    }
    public RemoteEditBox(StyleComponent<?> valueElement,InputElement<?> inputElement)
    {
        this(QuotationMark.DOUBLE,valueElement,inputElement,new Inputs());
    }
//    public RemoteEditBox(String value,InputElement<?> inputElement)
//    {
//        this(QuotationMark.DOUBLE,new Item().m(2).text(TextStyle.truncate).addInner(value),inputElement,new Inputs());
//    }

    public RemoteEditBox modalBackground(ModalBackground background)
    {
        this.background=background;
        return this;
    }
    public RemoteEditBox acceptButton(GlobalEventTagElement<?> acceptButton)
    {
        this.acceptButton=acceptButton;
        return this;
    }
    public RemoteEditBox editButton(GlobalEventTagElement<?> editButton)
    {
        this.editButton=editButton;
        return this;
    }
    public RemoteEditBox dismissButton(GlobalEventTagElement<?> dismissButton)
    {
        this.acceptButton=dismissButton;
        return this;
    }
    public RemoteEditBox placement(Placement placement)
    {
        this.placement=placement;
        return this;
    }
    

    public void post(String action) throws Throwable
    {

//        String template=new Item().addClass("popover").attr("role","tooltip").style(new Style().margin(new Size(0,unit.em)).padding(new Size(0,unit.em)))
//                .addInner(new Item().addClass("popover-body")).getHtml();
//        

//        if (this.acceptButton==null)
//        {
//            this.acceptButton=new Button().ml(1).color(StyleColor.success).addInner("&#x2713;");
//        }
//        if (this.dismissButton==null)
//        {
//            this.dismissButton=new Button().ml(1).color(StyleColor.secondary).addInner("&#x1f5d9;");
//        }
//        if (this.editButton==null)
//        {
//            this.editButton=new Button().ml(1).color(StyleColor.secondary).addInner("&#x270F;");
//        }
        this.editButton.style(new Style().display(display.none));
        this.addInner(this.editButton);

        this.onmouseover("document.getElementById("+mark+this.editButton.id()+mark+").style.display="+mark+"block"+mark);
        this.onmouseleave("document.getElementById("+mark+this.editButton.id()+mark+").style.display="+mark+"none"+mark);
        
        
        String template;
        template="<div class='popover' role='tooltip' style='margin:0;padding:0;width:inherit;max-width:100% !important;'><div class='popover-body'></div></div>";
        


        Item inputGroup=new Item();//.d(Display.flex).mx(0).px(0);
        Styler.style(inputElement).w(100);
        inputGroup.addInner(inputElement);
        inputGroup.addInner(acceptButton);
        inputGroup.addInner(dismissButton);

        editButton.onclick(HtmlUtils.js_call("nova.remote.openEditBox"
                , template
                ,background!=null?background.id():null
                ,id()
                ,acceptButton.id()
                ,dismissButton.id()
                ,editButton.id()
                ,inputElement.id()
                ,valueElement.id()
                ,action
                ,this.inputs.getContent()
                ,inputGroup.getHtml() //This cannot be called before the id() method, otherwise the ids won't be generated.
                ,placement
                ));

    }

}    
