package org.nova.http.server;

import org.nova.html.elements.TagElement;

public interface StateHandling
{
    public TagElement<?> getHandlerElement(Context context) throws Throwable;
    public void setHandlerElement(TagElement<?> element) throws Throwable;
}
