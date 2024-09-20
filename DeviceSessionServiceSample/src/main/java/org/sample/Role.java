package org.sample;

public enum Role
{
    User((short)0),
    Signin((short)1),
    Signup((short)2),
    Administrator((short)3),
    ;

    private short value;

    Role(short value)
    {
        this.value = value;
    }

    public short getValue()
    {
        return this.value;
    }

    public static Role fromValue(short value)
    {
        for (Role type : Role.values())
        {
            if (type.getValue() == value)
            {
                return type;
            }
        }
        return null;
    }

    
}
