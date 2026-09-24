package org.nova.json;


public abstract class DocumentWriter
{
    abstract public void begin(char character) throws Throwable;
    abstract public void end(char character) throws Throwable;
    
    abstract public void writeKeySection(char[] characters) throws Throwable;
    abstract public void writeSeperator(boolean needComma) throws Throwable;
    abstract public void writeValue(String string) throws Throwable;
    abstract public void writeNull() throws Throwable;
    abstract public void writeString(String string) throws Throwable;
    
}
