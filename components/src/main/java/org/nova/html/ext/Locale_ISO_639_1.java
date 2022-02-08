package org.nova.html.ext;

import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

public class Locale_ISO_639_1
{
    public Locale_ISO_639_1(String description,String code,String alternate,String locale)
    {
        this.description=description;
        this.code=code;
        this.alternate=alternate;
        this.locale=LocaleUtils.toLocale(locale);
    }
    public Locale_ISO_639_1(String description,String code)
    {
        this(description,code,null,code);
    }
    final public String description;
    final public String code;
    final public String alternate;
    final public Locale locale;
}
