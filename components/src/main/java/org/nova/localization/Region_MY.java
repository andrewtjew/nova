package org.nova.localization;

import java.util.HashMap;

public enum Region_MY
{
    JHR("Johor",(short)0),
    KDH("Kedah",(short)1),
    KUL("Kuala Lumpur",(short)2),
    KTN("Kelantan",(short)3),
    LBN("Labuan",(short)4),
    MLK("Malacca",(short)5),
    NSN("Negeri Sembilan",(short)6),
    PHG("Pahang",(short)7),
    PNG("Penang",(short)8),
    PRK("Perak",(short)9),
    PLS("Perlis",(short)10),
    PJY("Putrajaya",(short)11),
    SBH("Sabah",(short)12),
    SWK("Sarawak",(short)13),
    SGR("Selangor",(short)14),
    TRG("Terengganu",(short)15),
    ;
    
    private short value;
    private String name;
    
    static private HashMap<String,Region_MY> NAME_MAP=constructNameMap();
    static private HashMap<String,Region_MY> constructNameMap()
    {
    	HashMap<String,Region_MY> map=new HashMap<String, Region_MY>();
    	for (Region_MY value:Region_MY.values())
    	{
    		map.put(value.name(), value);
    	}
    	return map;
    }
    
    private Region_MY(String name,short value)
    {
        this.name=name;
        this.value=value;
    }
    static public Region_MY from(String name)
    {
        return NAME_MAP.get(name);
    }
    
    public static Region_MY fromValue(short value)
    {
        for (Region_MY item:values())
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
