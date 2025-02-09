package org.nova.localization;

import java.util.HashMap;

public enum PhoneCountryCode
{
    CA(new PhoneCountry("1",CountryCode.CA)),
    US(new PhoneCountry("1",CountryCode.US)),
    MY(new PhoneCountry("60",CountryCode.MY)),
    SG(new PhoneCountry("65",CountryCode.SG)),
    ;
    
    private PhoneCountry value;
    
    
    private PhoneCountryCode(PhoneCountry value)
    {
        this.value = value;
    }

    public PhoneCountry getValue()
    {
        return this.value;
    }
 
    public static PhoneCountryCode fromPhoneNumber(String phoneNumber)
    {
        for (var item:PhoneCountryCode.values())
        {
            if (phoneNumber.startsWith(item.value.phoneCountryCode))
            {
                return item;
            }
        }
        return null;
    }
    
}
