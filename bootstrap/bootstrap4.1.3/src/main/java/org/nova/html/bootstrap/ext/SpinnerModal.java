package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Modal;
import org.nova.html.bootstrap.ModalBody;
import org.nova.html.bootstrap.ModalContent;
import org.nova.html.bootstrap.ModalDialog;
import org.nova.html.bootstrap.ModalFooter;
import org.nova.html.bootstrap.ModalHeader;
import org.nova.html.bootstrap.Spinner;
import org.nova.html.bootstrap.SpinnerType;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.DeviceClass;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.remoting.ModalOption;

public class SpinnerModal extends Modal
{
    public SpinnerModal(boolean centered,Element message)
    {
        this(centered,message,new Spinner(SpinnerType.border).text(StyleColor.white),null);
    }
    public SpinnerModal(boolean centered,Element message,Spinner spinner,DeviceClass deviceClass)
    {
        ModalDialog dialog=returnAddInner(new ModalDialog());
        
        if (centered)
        {
            dialog.centered();
        }
        if (deviceClass!=null)
        {
            dialog.deviceClass(deviceClass);
        }
        ModalBody body=dialog.returnAddInner(new ModalBody());
        Item item=body.returnAddInner(new Item()).justify_content(Justify.center).d_flex();
        item.addInner(spinner);
        item.addInner(message);
        
    }
    
    
    public String js_option(ModalOption option)
    {
        return "$('#"+id()+"').modal('"+option+"');";
    }
}