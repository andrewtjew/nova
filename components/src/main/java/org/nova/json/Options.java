package org.nova.json;

public class Options
{
    public boolean ignoreUnknownFields=true;
    public boolean supportLocalDateFields=true;
    public boolean suppoertLocalDateTimeFields=true;
    
    public Options ignoreUnknownFields(boolean ignoreUnknownFields)
    {
        this.ignoreUnknownFields=ignoreUnknownFields;
        return this;
    }
    
}
