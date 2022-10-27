package org.nova.localization;

public class Country_ISO_3166_1
{
    final public String name;
    final public String alpha2Code;
    final public String alpha3Code;
    final public String numericCode;
    final public String displayName;
    final public String flagCode;
    
    public Country_ISO_3166_1(String name,String alpha2Code,String alpha3Code,String numericCode)
    {
        this.flagCode=null;
        this.name=name;
        this.alpha2Code=alpha2Code;
        this.alpha3Code=alpha3Code;
        this.numericCode=numericCode;
        this.displayName=name;
    }
    public Country_ISO_3166_1(String name,String alpha2Code,String alpha3Code,String numericCode,String flagCode)
    {
        this.flagCode=flagCode;
        this.name=name;
        this.alpha2Code=alpha2Code;
        this.alpha3Code=alpha3Code;
        this.numericCode=numericCode;
        this.displayName=name;
    }
    public Country_ISO_3166_1(String name,String alpha2Code,String alpha3Code,String numericCode,String flagCode,String displayName)
    {
        this.flagCode=flagCode;
        this.name=name;
        this.alpha2Code=alpha2Code;
        this.alpha3Code=alpha3Code;
        this.numericCode=numericCode;
        this.displayName=displayName;
    }

    public String getName()
    {
        return name;
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
