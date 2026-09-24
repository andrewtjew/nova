package org.nova.json;

public class Options
{
    public boolean ignoreUnknownFields=true;
    public boolean supportLocalDateField=true;
    public boolean supportLocalDateTime=true;
    
    public Options ignoreUnknownFields(boolean ignoreUnknownFields)
    {
        this.ignoreUnknownFields=ignoreUnknownFields;
        return this;
    }
    
}
