package org.nova.html.tags;

import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.enums.preload;

import com.nimbusds.jose.jwk.KeyType;

public class map extends GlobalEventTagElement<map>
{
    public map()
    {
        super("map");
    }
    
    public map name(String text)
    {
        return attr("name",text);
    }
    
}