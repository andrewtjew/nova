package org.nova.localization;

import org.nova.html.ext.LocalText;

public class LocalTextArgumentResolver extends LocalTextResolver
{

    @Override
    protected String translate(LocalText localText)
    {
        return localText.getKeyText();
    }

}
