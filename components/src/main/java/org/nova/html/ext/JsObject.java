package org.nova.html.ext;

import org.nova.json.ObjectMapper;

public class JsObject
{
    private final String content;
    public JsObject(String content)
    {
        this.content=content;
    }
    public JsObject(Object object) throws Throwable
    {
        this.content=ObjectMapper.writeObjectToString(object);
    }
    @Override 
    public String toString()
    {
        return this.content;
    }
}