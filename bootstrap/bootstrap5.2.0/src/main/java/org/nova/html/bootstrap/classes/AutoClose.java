package org.nova.html.bootstrap.classes;

public enum AutoClose
{
    inside("inside"), 
    outside("outside"), 
    true_("true"), 
    false_("false"), 
    ;
    private String value;

    AutoClose(String value)
    {
        this.value = value;
    }

    public String toString()
    {
        return this.value;
    }
}
