package org.nova.html.templating;

import org.nova.localization.LanguageCode;

public interface TemplateGenerator
{
    public Template generate(LanguageCode languageCode);
}
