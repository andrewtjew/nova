package org.nova.localization;

import java.util.HashMap;

public enum Region_CA
{
    AB("Alberta",(short)0),
    BC("British Columbia",(short)1),
    MB("Manitoba",(short)2),
    NB("New Brunswick",(short)3),
    NL("Newfoundland and Labrador",(short)4),
    NT("Northwest Territories",(short)5),
    NS("Nova Scotia",(short)6),
    NU("Nunavut",(short)7),
    ON("Ontario",(short)8),
    PE("Prince Edward Island",(short)9),
    QC("Quebec",(short)10),
    SK("Saskatchewan",(short)11),
    YT("Yukon",(short)12),
    ;
    
    private short value;
    private String name;
    
    static private HashMap<String,Region_CA> NAME_MAP=constructNameMap();
    static private HashMap<String,Region_CA> constructNameMap()
    {
    	HashMap<String,Region_CA> map=new HashMap<String, Region_CA>();
    	for (Region_CA value:Region_CA.values())
    	{
    		map.put(value.name(), value);
    	}
    	return map;
    }
    
    private Region_CA(String name,short value)
    {
        this.name=name;
        this.value=value;
    }
    static public Region_CA from(String name)
    {
        return NAME_MAP.get(name);
    }
    
    public static Region_CA fromValue(short value)
    {
        for (Region_CA item:values())
        {
            if (item.value==value)
            {
                return item;
            }
        }
        return null;
    }

    public String getName()
    {
        return this.name;
    }
    public short getValue()
    {
        return this.value;
    }
}
