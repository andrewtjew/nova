package org.nova.html.ext;

import org.apache.commons.text.StringEscapeUtils;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.localization.LocalTextResolver;

public class LocalText extends Element
{
    final private String keyText;

    final Object[] arguments;

    public LocalText(String text, Object... arguments)
    {
        super();
        this.keyText = text;
        this.arguments = arguments;
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        LocalTextResolver resolver = composer.getLocalTextResolver();
        if (resolver != null)
        {
            composer.getStringBuilder().append(resolver.resolve(this));
        }
        else
        {
            composer.getStringBuilder().append(StringEscapeUtils.escapeHtml4(this.keyText));
        }

    }

    public String getKeyText()
    {
        return this.keyText;
    }

    public Object[] getArguments()
    {
        return this.arguments;
    }
}
