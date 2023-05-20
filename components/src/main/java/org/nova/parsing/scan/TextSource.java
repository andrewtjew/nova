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

public class TextSource extends Source
{
    static public class TextSnippet extends Snippet
    {
        final private String text;
        final private int start;
        final private int end;

        public TextSnippet(String text,int start,int end)
        {
            this.text=text;
            this.start=start;
            this.end=end;
        }
        
        @Override
        public String getTarget()
        {
            return this.text.substring(this.start,this.end);
        }
        @Override
        public String getBuffer()
        {
            return this.text;
        }
        @Override
        public int getTargetBufferPosition()
        {
            return this.start;
        }
        @Override
        public int getTargetAbsolutePosition()
        {
            return this.start;
        } 
    }

    private final String text;
    private final int length;
    private int index;
    private int beginIndex;
    
    public TextSource(String text)
    {
        this.text=text;
        this.length=text.length();
        this.index=0;
    }

    @Override
    public char next()
    {
        if (this.index>=this.length)
        {
            this.index=this.length+1;
            return 0;
        }
        return this.text.charAt(this.index++);
    }

    @Override
    public void begin(int revert)
    {
        this.beginIndex=this.index-revert;
    }

    @Override
    public void end(int revert)
    {
        this.index=this.index-revert;
    }

    @Override
    public void reset(int revert)
    {
        this.beginIndex-=revert;
        this.index=this.beginIndex;
    }


    @Override
    public String endContext()
    {
        return this.text;
    }

    @Override
    public void beginContext()
    {
    }

    @Override
    public int getContextPosition()
    {
        return this.index;
    }

    @Override
    public void revert()
    {
        this.index=this.beginIndex;
    }

    @Override
    public TextSnippet endAndGetSnippet(int revert)
    {
        end(revert);
        return new TextSnippet(this.text, this.beginIndex, this.index);
    }

    @Override
    public Snippet getEndSnippet()
    {
        return new TextSnippet(this.text, this.text.length(),this.text.length());
    }



}
