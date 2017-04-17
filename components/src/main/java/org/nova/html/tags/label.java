package org.nova.html.tags;

import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.enums.preload;

import com.nimbusds.jose.jwk.KeyType;

public class label extends GlobalEventTagElement<label>
{
    public label()
    {
        super("label");
    }
    
    public label for_(String element_id)
    {
        return attr("for",element_id);
    }
    public label form(String form_id)
    {
        return attr("form",form_id);
    }
    
}