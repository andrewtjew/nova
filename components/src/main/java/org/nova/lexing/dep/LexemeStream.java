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
package org.nova.lexing.dep;

import java.util.List;

public class LexemeStream
{
    final List<Lexeme> lexemes;
    int index;

    public LexemeStream(List<Lexeme> lexemes)
    {
        this.lexemes=lexemes;
        this.index=0;
    }
    
    public void reset()
    {
        this.index=0;
    }

    public void back(int amount) throws Exception 
    {
        if (this.index<amount)
        {
            throw new Exception();
        }
        this.index-=amount;
    }
    public void forward(int amount) throws Exception 
    {
        if (this.index+amount>=this.lexemes.size())
        {
            throw new Exception();
        }
        this.index+=amount;
    }
    public Lexeme first() throws Exception
    {
        reset();
        return next();
    }
    public int index()
    {
        return this.index;
    }
    public void set(int index)
    {
        this.index=index;
    }
    
    public boolean isEnd()
    {
        return this.index>=this.lexemes.size();
    }
    public Lexeme next() throws Exception
    {
        if (this.index>=this.lexemes.size())
        {
            throw new Exception("End of stream");
        }
        return this.lexemes.get(this.index++);
    }
    public Lexeme current() throws Exception
    {
        if (this.index>=this.lexemes.size())
        {
            throw new Exception("End of stream");
        }
        return this.lexemes.get(this.index);
    }
    
    public boolean forwardToToken(Token token)
    {
        for (;this.index<this.lexemes.size();this.index++)
        {
            if (this.lexemes.get(this.index).getToken()==token)
            {
                return true;
            }
        }
        return false;
    }
    
}
