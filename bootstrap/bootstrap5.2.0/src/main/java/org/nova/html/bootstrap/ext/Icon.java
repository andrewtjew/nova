package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.tags.i;

public class Icon extends StyleComponent<Icon>
{
    public Icon(String name)
    {
    	super("i",null);
        addClass("bi",name);
    }
}
