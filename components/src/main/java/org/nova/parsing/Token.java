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
package org.nova.parsing;

public class Token
{
    private final TokenType type;
    private final Snippet snippet;
    final private NumericType numericType;
    final private IntegerSize integerSize;
    private final String message;
    private final int sourceIndex;

    public Token(TokenType type,Snippet snippet)
    {
        this.type=type;
        this.snippet=snippet;
        this.numericType=null;
        this.message=null;
        this.integerSize=null;
        this.sourceIndex=0;
    }
    public Token(TokenType type,Snippet snippet,String message,int sourceIndex)
    {
        this.type=type;
        this.snippet=snippet;
        this.message=message;
        this.numericType=null;
        this.integerSize=null;
        this.sourceIndex=sourceIndex;
    }
    public Token(TokenType type,Snippet snippet,NumericType numericType,IntegerSize integerSize)
    {
        this.type=type;
        this.snippet=snippet;
        this.message=null;
        this.numericType=numericType;
        this.integerSize=integerSize;
        this.sourceIndex=0;
    }
    public TokenType getType()
    {
        return type;
    }
    public NumericType getNumericType()
    {
        return numericType;
    }
    public Snippet getSnippet()
    {
        return this.snippet;
    }
    public String getMessage()
    {
        return this.message;
    }
    public IntegerSize getIntegerSize()
    {
        return this.integerSize;
    }
    public int getSourceIndex()
    {
        return this.sourceIndex;
    }
}
