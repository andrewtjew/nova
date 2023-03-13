package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Modal;
import org.nova.html.bootstrap.ModalBody;
import org.nova.html.bootstrap.ModalDialog;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Element;
import org.nova.html.remoting.ModalOption;
import org.nova.html.tags.script;

public class SpinnerModal extends Modal
{
    public SpinnerModal(boolean centered,Element message)
    {
        this(centered,message,new Spinner(SpinnerType.border).text(StyleColor.white));
    }
    public SpinnerModal(boolean centered,Element message,Spinner spinner)
    {
        ModalDialog dialog=returnAddInner(new ModalDialog());
        
        if (centered)
        {
            dialog.centered();
        }
        ModalBody body=dialog.returnAddInner(new ModalBody());
        {
	        Item item=body.returnAddInner(new Item()).justify_content(Justify.center).d(Display.flex);
	        item.addInner(spinner);
        }
        {
	        Item item=body.returnAddInner(new Item()).justify_content(Justify.center).d(Display.flex);
        	item.addInner(message);
        }
    }
    public String js_option(ModalOption option)
    {
        return "$('#"+id()+"').modal('"+option+"');";
    }
    public script js_on(String onName)
    {
    	return new script().addInner(onName+"=function(){"+js_option(ModalOption.show)+";}");
    }
    public script js_onbeforeunload()
    {
    	return js_on("window.onbeforeunload");
    }
    
}