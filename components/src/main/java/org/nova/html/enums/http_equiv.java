package org.nova.html.enums;

public enum http_equiv
{
    content_type("content-type"),
    default_style("default-style"),
    refresh("refresh"),
    ;
    private String value;
    http_equiv(String value)
    {
        this.value=value;
    }
    public String toString()
    {
        return this.value;
    }
}