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

public class StringBufferWriter extends DocumentWriter
{
    final private StringBuffer buffer;
    
    public StringBufferWriter(int capacity)
    {
        this.buffer=new StringBuffer(capacity);
    }
    public void begin(char character) throws Throwable
    {
        this.buffer.append(character);
    }
    public void end(char character) throws IOException
    {
        this.buffer.append(character);
    }
    
    public void writeKeySection(char[] characters) throws Throwable
    {
        this.buffer.append(characters);
    }
    public void writeSeperator(boolean needComma) throws Throwable
    {
        if (needComma)
        {
            this.buffer.append(',');
        }
    }
    public void writeValue(String string) throws Throwable
    {
        this.buffer.append(string);
    }
    public void writeNull() throws Throwable
    {
        this.buffer.append("null");
    }
    public void writeString(String string) throws Throwable
    {
        this.buffer.append('"');
        for (int index=0;index<string.length();index++)
        {
            char c=string.charAt(index);
            if (c=='\\')
            {
                this.buffer.append(c);
                this.buffer.append(c);
            }
            else if (c>'"')
            { 
                this.buffer.append(c);
            }
            else if (c=='"')
            {
                this.buffer.append('\\');
                this.buffer.append('"');
            }
            else if (c=='\b')
            {
                this.buffer.append('\\');
                this.buffer.append('b');
            }
            else if (c=='\f')
            {
                this.buffer.append('\\');
                this.buffer.append('f');
            }
            else if (c=='\n')
            {
                this.buffer.append('\\');
                this.buffer.append('n');
            }
            else if (c=='\r')
            {
                this.buffer.append('\\');
                this.buffer.append('r');
            }
            else if (c=='\t')
            {
                this.buffer.append('\\');
                this.buffer.append('t');
            }
            else
            {
                this.buffer.append(c);
            }
        }
        this.buffer.append('"');
    }
    public String toString()
    {
        return this.buffer.toString();
    }
}
