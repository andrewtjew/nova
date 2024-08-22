package org.sample;

import org.nova.html.bootstrap.Modal;
import org.nova.html.ext.Page;

public class UserPage extends Page
{
//    final private Modal modal;

    final private Modal modal;
    
    public UserPage(Service service,UserSession session) throws Throwable
    {
        this.modal=this.body().returnAddInner(new Modal());
        this.modal.backdrop_static();
        modal.id("modal");
    }
    
    public Modal getModal()
    {
        return this.modal;
    }
}
