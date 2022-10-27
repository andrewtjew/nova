package org.nova.localization;

import java.util.Locale;

public class Language_ISO_639_1
{
    public Language_ISO_639_1(String description,String code,String alternate,String locale)
    {
        this.description=description;
        this.code=code;
        this.alternate=alternate;
        this.locale=new Locale(locale);

    	switch (code)
    	{
    	case "zh-Hans":
    		this.displayName="简体中文";
    	break;
    	case"zh-Hant":
    		this.displayName="繁体中文";
    	break;
    	
    	default:
    		this.displayName=this.locale.getDisplayLanguage(this.locale);
    	}
    }
    public Language_ISO_639_1(String description,String code)
    {
        this(description,code,null,code);
    }
    final public String description; //To show in app
    final public String code;
    final public String alternate;
    final public Locale locale;
    final public String displayName; //Name in native language
    
}
