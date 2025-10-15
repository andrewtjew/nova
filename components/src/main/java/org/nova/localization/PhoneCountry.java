package org.nova.localization;

public class PhoneCountry
{
    public CountryCode countryCode;
    public String phoneCountryCode;
    public PhoneCountry(String phoneCountryCode,CountryCode countryCode)
    {
        this.phoneCountryCode=phoneCountryCode;
        this.countryCode=countryCode;
    }
}
