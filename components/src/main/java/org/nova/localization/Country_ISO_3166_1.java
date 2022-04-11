package org.nova.localization;

import java.util.Locale;

public class Country_ISO_3166_1
{
    final public String description;
    final public String alpha2Code;
    final public String alpha3Code;
    final public String numericCode;
    final public String displayName;
    final public String flagCode;
    
    public Country_ISO_3166_1(String description,String alpha2Code,String alpha3Code,String numericCode)
    {
        this.flagCode=null;
        this.description=description;
        this.alpha2Code=alpha2Code;
        this.alpha3Code=alpha3Code;
        this.numericCode=numericCode;
        this.displayName=description;
    }
    public Country_ISO_3166_1(String description,String alpha2Code,String alpha3Code,String numericCode,String flagCode)
    {
        this.flagCode=flagCode;
        this.description=description;
        this.alpha2Code=alpha2Code;
        this.alpha3Code=alpha3Code;
        this.numericCode=numericCode;
        this.displayName=description;
    }

    public String getDescription()
    {
        return description;
    }


    public String getAlpha2Code()
    {
        return alpha2Code;
    }

    public String getAlpha3Code()
    {
        return alpha3Code;
    }

    public String getNumericCode()
    {
        return numericCode;
    }

    public String getDisplayName()
    {
        return displayName;
    }
    
    public String getFlagCode()
    {
        return this.flagCode;
    }
}
