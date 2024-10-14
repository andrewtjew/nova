package org.nova.localization;

import java.util.HashMap;


public enum Region_US
{
    AL("Alabama",(short)0),
    AK("Alaska",(short)1),
    AZ("Arizona",(short)2),
    AR("Arkansas",(short)3),
    CA("California",(short)4),
    CO("Colorado",(short)5),
    CT("Connecticut",(short)6),
    DE("Delaware",(short)7),
    FL("Florida",(short)8),
    GA("Georgia",(short)9),
    HI("Hawaii",(short)10),
    ID("Idaho",(short)11),
    IL("Illinois",(short)12),
    IN("Indiana",(short)13),
    IA("Iowa",(short)14),
    KS("Kansas",(short)15),
    KY("Kentucky",(short)16),
    LA("Louisiana",(short)17),
    ME("Maine",(short)18),
    MD("Maryland",(short)19),
    MA("Massachusetts",(short)20),
    MI("Michigan",(short)21),
    MN("Minnesota",(short)22),
    MS("Mississippi",(short)23),
    MO("Missouri",(short)24),
    MT("Montana",(short)25),
    NE("Nebraska",(short)26),
    NV("Nevada",(short)27),
    NH("New Hampshire",(short)28),
    NJ("New Jersey",(short)29),
    NM("New Mexico",(short)30),
    NY("New York",(short)31),
    NC("North Carolina",(short)32),
    ND("North Dakota",(short)33),
    OH("Ohio",(short)34),
    OK("Oklahoma",(short)35),
    OR("Oregon",(short)36),
    PA("Pennsylvania",(short)37),
    RI("Rhode Island",(short)38),
    SC("South Carolina",(short)39),
    SD("South Dakota",(short)40),
    TN("Tennessee",(short)41),
    TX("Texas",(short)42),
    UT("Utah",(short)43),
    VT("Vermont",(short)44),
    VA("Virginia",(short)45),
    WA("Washington",(short)46),
    WV("West Virginia",(short)47),
    WI("Wisconsin",(short)48),
    WY("Wyoming",(short)49),
    ;
    
    private short value;
    private String name;
    
    static private HashMap<String,Region_US> NAME_MAP=constructNameMap();
    static private HashMap<String,Region_US> constructNameMap()
    {
    	HashMap<String,Region_US> map=new HashMap<String, Region_US>();
    	for (Region_US value:Region_US.values())
    	{
    		map.put(value.name(), value);
    	}
    	return map;
    }
    
    private Region_US(String name,short value)
    {
        this.name=name;
        this.value=value;
    }
    static public Region_US from(String name)
    {
        return NAME_MAP.get(name);
    }
    
    public static Region_US fromValue(short value)
    {
        for (Region_US item:values())
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
