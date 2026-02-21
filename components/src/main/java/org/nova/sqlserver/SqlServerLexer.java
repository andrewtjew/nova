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
package org.nova.sqlserver;

import org.nova.parsing.scan.Lexeme;
import org.nova.parsing.scan.ScanException;
import org.nova.parsing.scan.Scanner;
import org.nova.parsing.scan.Snippet;
import org.nova.parsing.scan.Source;
import org.nova.parsing.scan.Token;

public class SqlServerLexer extends Scanner
{

    public SqlServerLexer(Source source)
    {
        super(source);
        // TODO Auto-generated constructor stub
    }

    public Lexeme expectWordWithBracketsRemoved() throws Throwable
    {
        char c=this.skipWhiteSpaceAndBegin();
        if (c=='[')
        {
            this.begin();
            for (c=this.source.next();c!=']';c=this.source.next())
            {
                if (c==0)
                {
                    Snippet snippet=this.source.endAndGetSnippet(0);
                    throw new ScanException("Invalid name", new Lexeme(Token.ERROR, snippet.getTarget(),snippet));
                }
            }
            Snippet snippet=this.source.endAndGetSnippet(1);
            this.source.next();
            return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
        }
        return produceJavaIdentifier();
    }

    public Lexeme expectTableName() throws Throwable
    {
        Lexeme lexeme=expectWordWithBracketsRemoved();
        if (lexeme.isError())
        {
            return lexeme;
        }
        char c=this.skipWhiteSpaceAndBegin();
        if (c=='.')
        {
            lexeme=expectWordWithBracketsRemoved();
            if (lexeme.isError())
            {
                return lexeme;
            }
        }
        return lexeme;
    }
    
    public void skipComments() throws Throwable
    {
        for (;;)
        {
            char c=skipWhiteSpaceAndBegin();
            if ((c!='-')&&(c!='/'))
            {
                this.end(1);
                return;
            }
            if (c=='/')
            {
                this.produceNestableSlashStarComment();
            }
            else 
            {
                c=this.read();
                if (c=='-')
                {
                    this.produceDelimitedText('\n', false);
                }
                else
                {
                    throw new ScanException("Invalid comment", new Lexeme(Token.ERROR,null,this.end(0)));
                }
            }
        }        
    }
}
