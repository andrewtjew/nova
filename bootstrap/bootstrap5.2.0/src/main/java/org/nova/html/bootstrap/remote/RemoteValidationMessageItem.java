package org.nova.html.bootstrap.remote;

import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.elements.Composer;

public class RemoteValidationMessageItem extends StyleComponent<RemoteValidationMessageItem> 
{
    public RemoteValidationMessageItem()
    {
        super("div",null);
        id();
    }
    @Override
    public void compose(Composer composer) throws Throwable
    {
        super.compose(composer);
        clearInners();
    }

}
