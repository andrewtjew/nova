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
package org.nova.parsing.scan.dep;

public class Lexeme
{
    private final String value;
    private final Token token;
    private final Snippet snippet;
    private final String modifier;

    public Lexeme(Token token,String value,Snippet snippet)
    {
        this(token,value,null,snippet);
    }
    public Lexeme(Token token,String value,String modifier,Snippet snippet)
    {
        this.token=token;
        this.value=value;
        this.modifier=modifier;
        this.snippet=snippet;
    }
    public String getValue()
    {
        return value;
    }
    public Token getToken()
    {
        return token;
    }
    public Snippet getSnippet()
    {
        return this.snippet;
    }
    public boolean isOperator(String text)
    {
        if (this.token!=Token.OPERATOR)
        {
            return false;
        }
        return this.value.equals(text);
    }
    public boolean isPunctuator(String text)
    {
        if (this.token!=Token.PUNCTUATOR)
        {
            return false;
        }
        return this.value.equals(text);
    }
    public boolean isString(String text)
    {
        if (this.token!=Token.STRING)
        {
            return false;
        }
        return this.value.equals(text);
    }
    public boolean isCaseInsenstiveWord(String text)
    {
        if (this.token!=Token.TEXT)
        {
            return false;
        }
        return this.value.equalsIgnoreCase(text);
    }
    public boolean isText(String text)
    {
        if (this.token!=Token.TEXT)
        {
            return false;
        }
        return this.value.equals(text);
    }
    public boolean isError()
    {
        return this.token==Token.ERROR;
    }
    public boolean isToken(Token token)
    {
        return (this.token==token);
    }
    public String getModifier()
    {
        return this.modifier;
    }
}
