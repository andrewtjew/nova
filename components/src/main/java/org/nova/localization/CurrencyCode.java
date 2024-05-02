package org.nova.localization;

import java.util.HashMap;

public enum CurrencyCode
{
    CAD(new Currency_ISO_4217("Canadian dollar","CAD","124",2,"$")),
    USD(new Currency_ISO_4217("United States dollar","USD","840",2,"$")),
    MYR(new Currency_ISO_4217("Malaysian ringgit","MYR","458",2,"RM")),
    EUR(new Currency_ISO_4217("Euro","EUR","978",2,"€")),
    GBP(new Currency_ISO_4217("Pound sterling","GBP","826",2,"£")),
    CNY(new Currency_ISO_4217("Renminbi","GBP","156",2,"¥")),
    SGD(new Currency_ISO_4217("Singapore dollar","SGD","702",2,"$")),
    ;
    
    private Currency_ISO_4217 value;
    
    static private HashMap<String,CurrencyCode> NAME_MAP=constructNameMap();
    
    static private HashMap<String,CurrencyCode> constructNameMap()
    {
    	HashMap<String,CurrencyCode> map=new HashMap<String, CurrencyCode>();
    	for (CurrencyCode value:CurrencyCode.values())
    	{
    		map.put(value.name(), value);
    	}
    	return map;
    }
    
    static public CurrencyCode from(String name)
    {
    	return NAME_MAP.get(name);
    }
    
    
    private CurrencyCode(Currency_ISO_4217 value)
    {
        this.value = value;
    }

    public Currency_ISO_4217 getValue()
    {
        return this.value;
    }
    
}
