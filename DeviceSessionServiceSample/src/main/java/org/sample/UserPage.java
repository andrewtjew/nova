package org.sample;

import org.nova.html.bootstrap.Modal;
import org.nova.html.ext.Page;
import org.nova.html.tags.script;

public class UserPage extends Page
{
//    final private Modal modal;

    final private Modal modal;
    
    public UserPage(Service service,UserSession session) throws Throwable
    {
        this.head().addInner(new script().src("/$/html/js/nova-device/nova-device.js"));

        this.modal=this.body().returnAddInner(new Modal());
        this.modal.backdrop_static();
        modal.id("modal");
    }
    
    public Modal getModal()
    {
        return this.modal;
    }
}
