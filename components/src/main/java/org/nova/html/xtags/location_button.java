package org.nova.html.xtags;

import org.nova.html.tags.button_button;

public class location_button extends button_button
{
    public location_button(String location)
    {
        onclick("window.location='"+location+"'");
    }
    
}