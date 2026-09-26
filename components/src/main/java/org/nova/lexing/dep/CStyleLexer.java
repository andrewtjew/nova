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

public class CStyleLexer extends Lexer
{
    public CStyleLexer(Source source, String[] operators, String[] seperators)
    {
        super(source
                , new String[]{"=","+","-","*","/","%","++","--","!","==","!=",">",">=","<","<=","&&","||","?",":","~","<<",">>","&","^"}
                , new char[]{';',',','{','}','(',')'}
                , new String[]{"null"}
                , true,true
                );
    }

    @Override
    public boolean match(char c)
    {
        if (matchComment(c))
        {
            return true;
        }
        if (matchNumber(c))
        {
            return true;
        }
        if (matchCharacter(c))
        {
            return true;
        }
        if (matchDoubleQuotedString(c))
        {
            return true;
        }
        if (matchPunctuator(c))
        {
            return true;
        }
        if (matchOperator(c))
        {
            return true;
        }
        if (matchIdentifierOrKeyword(c))
        {
            return true;
        }
        return false;
    }

}
