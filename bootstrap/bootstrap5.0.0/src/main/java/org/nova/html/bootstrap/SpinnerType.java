package org.nova.html.bootstrap;

public enum SpinnerType
{
    border("border"),
    grow("grow"),
    ;
    private String value;

    SpinnerType(String value)
    {
        this.value = value;
    }

    public String toString()
    {
        return this.value;
    }

}