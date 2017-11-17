package org.nova.html.elements;

import java.io.OutputStream;

public class StringComposer extends Composer
{
    final private StringBuilder sb;

    public StringComposer()
    {
        this.sb=new StringBuilder();
    }

    public StringComposer(StringBuilder sb)
    {
        this.sb=sb;
    }

    @Override
    public StringBuilder getStringBuilder()
    {
        return this.sb;
    }
    
}