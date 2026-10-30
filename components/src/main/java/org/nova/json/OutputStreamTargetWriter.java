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
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.text.StringEscapeUtils;

public class OutputStreamTargetWriter extends TargetWriter
{
    final private OutputStream stream;
    
    public OutputStreamTargetWriter(OutputStream stream)
    {
        this.stream=stream;
    }
    public void write(char character) throws Throwable
    {
        this.stream.write(character);
    }
    public void writeSeperator(boolean needComma) throws Throwable
    {
        if (needComma)
        {
           this.stream.write(',');
        }
    }
    public void write(String string) throws Throwable
    {
//        PrintWriter writer=new PrintWriter(this.stream,false,StandardCharsets.UTF_8);
//        writer.write(string);
        this.stream.write(string.getBytes(StandardCharsets.UTF_8));
    }
    public void writeEnum(String string) throws Throwable
    {
        this.stream.write('"');
        write(string);
        this.stream.write('"');
    }
    public void writeNull() throws Throwable
    {
        write("null");
    }
    public void writeString(String string) throws Throwable
    {
        PrintWriter writer=new PrintWriter(this.stream,false,StandardCharsets.UTF_8);
        writer.append('"');
        for (int index=0;index<string.length();index++)
        {
            char c=string.charAt(index);
            if (c>'"')
            { 
                writer.append(c);
                if (c=='\\')
                {
                    writer.append(c);
                }
            }
            else if (c=='"')
            {
                writer.append('\\');
                writer.append('"');
            }
            else if (c=='\b')
            {
                writer.append('\\');
                writer.append('b');
            }
            else if (c=='\f')
            {
                writer.append('\\');
                writer.append('f');
            }
            else if (c=='\n')
            {
                writer.append('\\');
                writer.append('n');
            }
            else if (c=='\r')
            {
                writer.append('\\');
                writer.append('r');
            }
            else if (c=='\t')
            {
                writer.append('\\');
                writer.append('t');
            }
            else
            {
                writer.append(c);
            }
        }
        writer.append('"');
//        CharBuffer charBuffer = CharBuffer.wrap(writer);
//        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
//        this.stream.write(byteBuffer.array(), 0, byteBuffer.remaining());
    }
    
}
