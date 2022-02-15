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
package org.nova.parsing.scan;

import java.io.IOException;
import java.io.InputStreamReader;

import org.nova.collections.CharArrayBox;

public class InputStreamReaderSource extends Source
{
    static public class ReaderSnippet extends Snippet
    {
        final private char[] buffer;
        final private int start;
        final private int end;
        final private int position;

        public ReaderSnippet(char[] buffer,int position,int start,int end)
        {
            this.buffer=buffer;
            this.position=position;
            this.start=start;
            this.end=end;
        }
        
        @Override
        public String getTarget()
        {
            return new String(this.buffer,this.start,this.end-this.start);
        }
        @Override
        public String getBuffer()
        {
            return new String(this.buffer,0,end);
        }
        @Override
        public int getTargetBufferPosition()
        {
            return this.start;
        }
        @Override
        public int getTargetAbsolutePosition()
        {
            return this.position;
        } 
    }
    
    final private InputStreamReader inputStreamReader;
    final private char[] inputBuffer;
    private int inputBufferIndex;
    private int inputBufferSize;
    
    private CharArrayBox textBuffer;
    private int textBufferEndIndex=0;
    private int textBufferIndex;
    private int textBufferBeginIndex;
    private int textBufferBeginContextIndex;
    private int position;
    private int line;
    private int totalBytesProcessed;
    
    public InputStreamReaderSource(InputStreamReader inputStreamReader,int initialTextBufferSize,int inputBufferSize) throws IOException
    {
        this(inputStreamReader,new CharArrayBox(initialTextBufferSize),new char[inputBufferSize]);
    }

    public InputStreamReaderSource(InputStreamReader inputStreamReader,CharArrayBox textBuffer,char[] inputBuffer) throws IOException
    {
        this.inputStreamReader=inputStreamReader;
        this.inputBuffer=inputBuffer;
        this.textBuffer=textBuffer;
        this.inputBufferSize=read();
    }
    
    private int read() throws IOException
    {
        int totalRead=0;
        for (;;)
        {
            int read=inputStreamReader.read(this.inputBuffer,totalRead,this.inputBuffer.length-totalRead);
            if (read<0)
            {
                break;
            }
            totalRead+=read;
            if (totalRead==this.inputBuffer.length)
            {
                break;
            }
        }
        this.totalBytesProcessed+=totalRead;
        return totalRead;
    }
    
    @Override
    public char next() throws Throwable
    {
        if (this.textBufferIndex<this.textBufferEndIndex)
        {
            char c=this.textBuffer.get(this.textBufferIndex++);
            if (c=='\n')
            {
                this.line++;
            }
            return c;
        }
        if (this.textBufferEndIndex==this.textBuffer.get().length)
        {
            this.textBuffer.grow();
        }
        if (this.inputBufferIndex==this.inputBufferSize)
        {
            this.inputBufferIndex=0;
            this.inputBufferSize=read();
        }
        if (this.inputBufferSize==0)
        {
            return 0;
        }
        char c=this.inputBuffer[this.inputBufferIndex++];
        this.textBuffer.set(this.textBufferEndIndex,c);
        this.textBufferIndex++;
        this.textBufferEndIndex++;
        if (c=='\n')
        {
            this.line++;
        }
        return c;
    }

    @Override
    public void begin(int revert)
    {
        this.textBufferBeginIndex=this.textBufferIndex-revert;
    }

    @Override
    public void end(int revert)
    {
        this.textBufferIndex-=revert;
    }

    @Override
    public String endContext()
    {
        this.position+=this.textBufferIndex;
        String context=new String(this.textBuffer.get(),this.textBufferBeginContextIndex,this.textBufferEndIndex-this.textBufferBeginContextIndex);
        this.textBufferIndex=this.textBufferBeginIndex=this.textBufferEndIndex=0;
        return context;
    }
    
    @Override
    public void beginContext()
    {
        this.textBufferBeginContextIndex=this.textBufferIndex;
    }

    public long getTotalBytesProcessed()
    {
        return this.totalBytesProcessed;
    }
    public int getLine()
    {
        return this.line;
    }
    public int getPosition()
    {
        return this.position;
    }

    @Override
    public int getContextPosition()
    {
        return this.textBufferBeginIndex;
    }

    @Override
    public void revert()
    {
        this.textBufferIndex=this.textBufferBeginIndex;
    }

    @Override
    public Snippet endAndGetSnippet(int revert)
    {
        end(revert);
        return new ReaderSnippet(this.textBuffer.get(), this.position, this.textBufferBeginIndex, this.textBufferIndex);
    }

    @Override
    public Snippet getEndSnippet()
    {
        return new ReaderSnippet(this.textBuffer.get(), this.position, this.inputBufferSize,this.inputBufferSize);
    }

}
