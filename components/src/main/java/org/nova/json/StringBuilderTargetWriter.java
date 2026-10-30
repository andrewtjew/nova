/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.text.StringEscapeUtils;

public class StringBuilderTargetWriter extends TargetWriter
{
    final private StringBuilder stringBuffer;
    
    public StringBuilderTargetWriter(int capacity)
    {
        this.stringBuffer=new StringBuilder(capacity);
    }
    public void write(char character) throws Throwable
    {
        this.stringBuffer.append(character);
    }
    public void writeSeperator(boolean needComma) throws Throwable
    {
        if (needComma)
        {
           this.stringBuffer.append(',');
        }
    }
    public void write(String string) throws Throwable
    {
        this.stringBuffer.append(string);
    }
    public void writeEnum(String string) throws Throwable
    {
        this.stringBuffer.append('"');
        this.stringBuffer.append(string);
        this.stringBuffer.append('"');
    }
    public void writeNull() throws Throwable
    {
        write("null");
    }
    public void writeString(String string) throws Throwable
    {
        this.stringBuffer.append('"');
        for (int index=0;index<string.length();index++)
        {
            char c=string.charAt(index);
            if (c=='\\')
            {
                this.stringBuffer.append(c);
                this.stringBuffer.append(c);
            }
            else if (c>'"')
            { 
                this.stringBuffer.append(c);
            }
            else if (c=='"')
            {
                this.stringBuffer.append('\\');
                this.stringBuffer.append('"');
            }
            else if (c=='\b')
            {
                this.stringBuffer.append('\\');
                this.stringBuffer.append('b');
            }
            else if (c=='\f')
            {
                this.stringBuffer.append('\\');
                this.stringBuffer.append('f');
            }
            else if (c=='\n')
            {
                this.stringBuffer.append('\\');
                this.stringBuffer.append('n');
            }
            else if (c=='\r')
            {
                this.stringBuffer.append('\\');
                this.stringBuffer.append('r');
            }
            else if (c=='\t')
            {
                this.stringBuffer.append('\\');
                this.stringBuffer.append('t');
            }
            else
            {
                this.stringBuffer.append(c);
            }
        }
        this.stringBuffer.append('"');
    }
    public String getString()
    {
        return this.stringBuffer.toString();
    }
}
