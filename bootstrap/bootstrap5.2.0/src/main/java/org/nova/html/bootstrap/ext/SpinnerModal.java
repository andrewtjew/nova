package org.nova.html.bootstrap.ext;

//public class SpinnerModal extends Modal
//{
//    public SpinnerModal(boolean centered,Element message)
//    {
//        this(centered,message,new Spinner(SpinnerType.border).text(StyleColor.white),null);
//    }
//    public SpinnerModal(boolean centered,Element message,Spinner spinner,DeviceClass deviceClass)
//    {
//        ModalDialog dialog=returnAddInner(new ModalDialog());
//        
//        if (centered)
//        {
//            dialog.centered();
//        }
//        if (deviceClass!=null)
//        {
//            dialog.deviceClass(deviceClass);
//        }
//        ModalBody body=dialog.returnAddInner(new ModalBody());
//        Item item=body.returnAddInner(new Item()).justify_content(Justify.center).d(Display.flex);
//        item.addInner(spinner);
//        item.addInner(message);
//        
//    }
//    
//    
//    public String js_option(ModalOption option)
//    {
//        return "$('#"+id()+"').modal('"+option+"');";
//    }
//}