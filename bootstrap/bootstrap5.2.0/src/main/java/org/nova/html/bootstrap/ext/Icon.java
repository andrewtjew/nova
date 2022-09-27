package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.StyleComponent;

public class Icon extends StyleComponent<Icon>
{
    public Icon(String name)
    {
    	super("i",null);
        addClass("bi",name);
    }
}
