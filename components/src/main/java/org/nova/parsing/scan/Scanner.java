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

public class Scanner
{
    protected final Source source;
    public Scanner(Source source)
    {
        this.source = source;
    }
    public Lexeme produceTerminatedText(char... terminators) throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            for (char t:terminators)
            {
                if (t==c)
                {
                    Snippet snippet=this.source.endAndGetSnippet(1);
                    return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
                }
            }
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Lexeme(Token.STRING, snippet.getTarget(),snippet);
    }
    public Lexeme produceTerminatedTextAndSkipTerminator(char... terminators) throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            for (char t:terminators)
            {
                if (t==c)
                {
                    Snippet snippet=this.source.endAndGetSnippet(1);
                    this.source.next();
                    return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
                }
            }
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Lexeme(Token.TEXT,snippet.getTarget() ,snippet);
    }
    public Lexeme produceEnd() throws Throwable
    {
        Snippet snippet=this.source.getEndSnippet();
        return new Lexeme(Token.END,snippet.getTarget() ,snippet);
    }
    public Lexeme produceError(int revert)
    {
        Snippet snippet=this.source.endAndGetSnippet(revert);
        return new Lexeme(Token.ERROR, snippet.getTarget(),snippet);
    }
    public Lexeme produceToken(Token token,int revert)
    {
        Snippet snippet=this.source.endAndGetSnippet(revert);
        return new Lexeme(token, snippet.getTarget(),snippet);
    }
    public Lexeme produceJSONString() throws Throwable
    {
        return produceQuotedEscapeString('"');
    }
    public Lexeme produceJavaString() throws Throwable
    {
        return produceQuotedEscapeString('"');
    }
    public Lexeme produceQuotedEscapeString(char endCharacter) throws Throwable
    {
        StringBuilder sb = new StringBuilder();
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (c != '\\')
            {
                if (c == endCharacter)
                {
                    this.source.end(0);
                    return new Lexeme(Token.STRING, sb.toString(), this.source.endAndGetSnippet(0));
                }
                else if (c == '\n')
                {
                    return new Lexeme(Token.ERROR, "Invalid newline character in string.", this.source.endAndGetSnippet(1));
                }
                sb.append(c);
                continue;
            }
            c = this.source.next();
            if (c == 'u')
            {
                StringBuilder unicode=new StringBuilder();
                for (int i = 0; i < 4; i++)
                {
                    c = this.source.next();
                    if (c==0)
                    {
                        return new Lexeme(Token.ERROR, "Premature end of string.", this.source.endAndGetSnippet(0));
                    }
                    if (Character.isDigit(c) || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F')))
                    {
                        unicode.append(c);
                        continue;
                    }
                    return new Lexeme(Token.ERROR, "Invalid unicode escape character in string.", this.source.endAndGetSnippet(0));
                }
                sb.append((char) Integer.parseInt(unicode.toString(), 16));
                continue;
            }
            if (c == 'r')
            {
                sb.append('\r');
            }
            else if (c == 'n')
            {
                sb.append('\n');
            }
            else if (c == endCharacter)
            {
                sb.append(endCharacter);
            }
            else if (c == '\\')
            {
                sb.append(c);
            }
            else if (c == 't')
            {
                sb.append('\t');
            }
            else
            {
                return new Lexeme(Token.ERROR, "Invalid escape character in string.", this.source.endAndGetSnippet(0));
            }
        }
        return new Lexeme(Token.ERROR, "Premature end of string.", this.source.endAndGetSnippet(0));
    }
    public Lexeme produceSimpleString(char endCharacter) throws Throwable
    {
        StringBuilder sb = new StringBuilder();
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (c != '\\')
            {
                if (c == endCharacter)
                {
                    return new Lexeme(Token.STRING, sb.toString(), this.source.endAndGetSnippet(0));
                }
                else if (c == '\n')
                {
                    return new Lexeme(Token.ERROR, "Invalid newline character in string.", this.source.endAndGetSnippet(1));
                }
                sb.append(c);
                continue;
            }
            char cc = this.source.next();
            if (cc==0)
            {
                break;
            }
            if (cc == endCharacter)
            {
                sb.append(endCharacter);
            }
            else
            {
                sb.append(c);
                sb.append(cc);
            }
        }
        return new Lexeme(Token.ERROR, "Premature end of string.", this.source.endAndGetSnippet(0));
    }
    public Lexeme produceCommaSeparatedValue() throws Throwable
    {
        StringBuilder sb = new StringBuilder();
        char first=this.source.next();
        if (first!=0)
        {
            if (first==',')
            {
                return new Lexeme(Token.STRING, "", this.source.endAndGetSnippet(1));
            }
            if (first=='"')
            {
                for (char c=this.source.next();c!=0;c=this.source.next())
                {
                    if (c == '"')
                    {
                        char cc=this.source.next();
                        if (cc==',')
                        {
                            return new Lexeme(Token.STRING, sb.toString(), this.source.endAndGetSnippet(1));
                        }
                        if (cc=='"')
                        {
                            sb.append(c);
                        }
                    }
                    else if (c == '\n')
                    {
                        return new Lexeme(Token.ERROR, "Invalid newline character in string.", this.source.endAndGetSnippet(1));
                    }
                    sb.append(c);
                }
            }
            else
            {
                sb.append(first);
                for (char c=this.source.next();c!=0;c=this.source.next())
                {
                    if ((c==',')||(c=='\n')||(c=='\r'))
                    {
                        return new Lexeme(Token.STRING, sb.toString(), this.source.endAndGetSnippet(1));
                    }
                    sb.append(c);
                }
            }
        }
        return new Lexeme(Token.ERROR, "Premature end of string.", this.source.endAndGetSnippet(0));
    }

    public Lexeme produceJavaSuffixNumber(boolean fraction) throws Throwable
    {
        boolean base=!fraction;
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (Character.isDigit(c))
            {
                base=true;
                continue;
            }
            if ((c=='.')&&(fraction==false))
            {
                fraction=true;
                continue;
            }
            else if ((c=='l')||(c=='L')||(c=='f')||(c=='F')||(c=='d')||(c=='D'))
            {
                if (base==false)
                {
                    return new Lexeme(Token.ERROR, "Invalid number.", this.source.endAndGetSnippet(1));
                }
                Snippet snippet=this.source.endAndGetSnippet(0);
                return new Lexeme(Token.NUMBER, snippet.getTarget(),new String(new char[]{c}),snippet);
            }
            else if ((c=='e')||(c=='E'))
            {
                c=this.source.next();
                if ((c=='-')||(c=='+'))
                {
                    c=this.source.next();
                }
                if (c==0)
                {
                    return new Lexeme(Token.ERROR, "Invalid number.", this.source.endAndGetSnippet(1));
                }
                if (Character.isDigit(c)==false)
                {
                    return new Lexeme(Token.ERROR, "Invalid number.", this.source.endAndGetSnippet(0));
                }
                for (c=this.source.next();Character.isDigit(c);c=this.source.next())
                {
                }
                if ((c=='f')||(c=='F')||(c=='d')||(c=='D'))
                {
                    if (base==false)
                    {
                        return new Lexeme(Token.ERROR, "Invalid number.", this.source.endAndGetSnippet(0));
                    }
                    Snippet snippet=this.source.endAndGetSnippet(0);
                    return new Lexeme(Token.NUMBER, snippet.getTarget(),new String(new char[]{c}),snippet);
                }
            }
            break;
        }
        if (base==false)
        {
            return new Lexeme(Token.ERROR, "Invalid number.", this.source.endAndGetSnippet(1));
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Lexeme(Token.NUMBER, snippet.getTarget(),snippet);
    }
    
    public Lexeme produceNumber(boolean fraction) throws Throwable
    {
        boolean base=!fraction;
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (Character.isDigit(c))
            {
                base=true;
                continue;
            }
            if ((c=='.')&&(fraction==false))
            {
                fraction=true;
                continue;
            }
            else if ((c=='e')||(c=='E'))
            {
                c=this.source.next();
                if ((c=='-')||(c=='+'))
                {
                    c=this.source.next();
                }
                if (c==0)
                {
                    return new Lexeme(Token.ERROR, "Invalid number.", this.source.endAndGetSnippet(1));
                }
                if (Character.isDigit(c)==false)
                {
                    return new Lexeme(Token.ERROR, "Invalid number.", this.source.endAndGetSnippet(0));
                }
                for (c=this.source.next();Character.isDigit(c);c=this.source.next())
                {
                }
            }
            break;
        }
        if (base==false)
        {
            return new Lexeme(Token.ERROR, "Invalid number.", this.source.endAndGetSnippet(1));
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Lexeme(Token.NUMBER, snippet.getTarget(),snippet);
    }
    
    public Lexeme expectPunctuator(char punctuator) throws Throwable
    {
        char c=skipWhiteSpaceAndBegin();
        if (c==punctuator)
        {
            Snippet snippet=this.source.endAndGetSnippet(0);
            return new Lexeme(Token.PUNCTUATOR, snippet.getTarget(),snippet);
        }
        Snippet snippet=this.source.endAndGetSnippet(0);
        return new Lexeme(Token.ERROR, "Invalid punctuator",snippet);
    }
    
    //Faster than expectPunctuator in case we are not interested 
    public Lexeme skipPunctuator(char punctuator) throws Throwable
    {
        char c=skipWhiteSpaceAndBegin();
        if (c==punctuator)
        {
            this.source.end(0);
            return null;
        }
        Snippet snippet=this.source.endAndGetSnippet(0);
        return new Lexeme(Token.ERROR, "Invalid punctuator",snippet);
    }
    
    public Lexeme produceJavaIdentifier() throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (Character.isJavaIdentifierPart(c)==false)
            {
                break;
            }
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
    }
/*
    public Lexeme expectWord(String word) throws Throwable
    {
        skipWhiteSpaceAndBegin();
        Lexeme lexeme=produceWord();
        if (word.equals(lexeme.getValue()))
        {
            return lexeme;
        }
        return new Lexeme(Token.ERROR,lexeme.getValue(),lexeme.getSnippet());
    }
    public Lexeme expectWordIgnoreCase(String word) throws Throwable
    {
        skipWhiteSpaceAndBegin();
        Lexeme lexeme=produceWord();
        if (word.equalsIgnoreCase(lexeme.getValue()))
        {
            return lexeme;
        }
        return new Lexeme(Token.ERROR,lexeme.getValue(),lexeme.getSnippet());
    }
*/
    public Lexeme expectWord() throws Throwable
    {
        char c=skipWhiteSpaceAndBegin();
        if (Character.isLetter(c)==false)
        {
            return new Lexeme(Token.ERROR,null,end(1));
        }
        return produceWord();
    }
    
    public Lexeme produceWord() throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (Character.isLetterOrDigit(c)==false)
            {
                break;
            }
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
    }
    
    public Lexeme produceEnclosedJSONText(char startCharacter,char endCharacter) throws Throwable
    {
        int level = 0;
        boolean inString = false;
        boolean inStringEscape = false;
        for (;;)
        {
            char c = this.source.next();
            if (c==0)
            {
                return new Lexeme(Token.ERROR, "Premature end-of-text.", this.source.endAndGetSnippet(1));
            }
            if (inString == true)
            {
                if (c == '\\')
                {
                    inStringEscape = true;
                }
                else if (c == '"')
                {
                    inString = false;
                    /*
                    for (;;)
                    {
                        c = this.source.next();
                        if ((c==0)||(c==',')||(c==':')||(c=='}')||(c==']'))
                        {
                            break;
                        }
                        System.out.print(c);
                        if (Character.isWhitespace(c)==false)
                        {
                            return new Lexeme(Token.ERROR, "Invalid text after string.", this.source.endAndGetSnippet(1));
                        }
                    }
                    */
                }
            }
            else if (inStringEscape)
            {
                inStringEscape = false;
            }
            else
            {
                if (c == '"')
                {
                    inString = true;
                }
                else if (c == startCharacter)
                {
                    level++;
                }
                else if (c == endCharacter)
                {
                    if (level == 0)
                    {
                        Snippet snippet=this.source.endAndGetSnippet(0);
                        return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
                    }
                    level--;
                }
            }
        }
    }
    
    public Lexeme produceDelimitedText(char delimiter,boolean includeDelimiter) throws Throwable
    {
        for (;;)
        {
            char c = this.source.next();
            if (c==0)
            {
                return new Lexeme(Token.ERROR, "Premature end-of-text", this.source.endAndGetSnippet(1));
            }
            if (c==delimiter)
            {
                if (includeDelimiter)
                {
                    Snippet snippet=this.source.endAndGetSnippet(0);
                    return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
                }
                else
                {
                    Snippet snippet=this.source.endAndGetSnippet(1);
                    return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
                }
            }
        }
    }
    
    public Lexeme produceSlashSlashComment() throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if ((c == '\r')||(c=='\n'))
            {
                break;
            }
        }
        Snippet snippet=this.source.endAndGetSnippet(1);
        return new Lexeme(Token.TEXT, snippet.getTarget(),snippet);
    }
    public Lexeme produceNestableSlashStarComment() throws Throwable
    {
        boolean insideString = false;
        int level = 0;
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (insideString)
            {
                if (c == '"')
                {
                    insideString = false;
                }
                else if (c == '\\')
                {
                    c = this.source.next();
                }
            }
            else if (c == '"')
            {
                insideString = true;
            }
            else if (c == '*')
            {
                c=this.source.next();
                if (c == '/')
                {
                    if (level == 0)
                    {
                        Snippet snippet=this.source.endAndGetSnippet(0);
                        String value=snippet.getTarget();
                        return new Lexeme(Token.TEXT, value.substring(2, value.length()-2),snippet);
                    }
                    level--;
                }
            }
            else if (c == '/')
            {
                c=this.source.next();
                if (c == '*')
                {
                    level++;
                }
            }
        }
        return new Lexeme(Token.ERROR, "Invalid comment.", this.source.endAndGetSnippet(1));
    }
    
    public Lexeme produceJavaSlashStarComment() throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (c == '*')
            {
                c=this.source.next();
                if (c == '/')
                {
                    Snippet snippet=this.source.endAndGetSnippet(0);
                    String value=snippet.getTarget();
                    return new Lexeme(Token.TEXT, value.substring(2, value.length()-2),snippet);
                }
            }
        }
        return new Lexeme(Token.ERROR, "Invalid comment.", this.source.endAndGetSnippet(1));
    }

    
    public void skipWhiteSpace() throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (Character.isWhitespace(c)==false)
            {
                this.source.end(1);
                return;
            }
        }
    }
    public char skip(int amount) throws Throwable
    {
        char c=0;
        for (int i=0;i<amount;i++)
        {
            c=this.source.next();
            if (c==0)
            {
                break;
            }
        }
        return c;
    }
    
    public char skipWhiteSpaceAndBegin() throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (Character.isWhitespace(c)==false)
            {
                if (c!=65279) //BOM or zero width space
                {
                    this.source.begin(1);
                    return c;
                }
            }
        }
        return 0;
    }
    public char begin() throws Throwable
    {
        this.source.begin(0);
        return this.source.next();
    }
    
    public void revert()
    {
        this.source.revert();
    }
    public char read() throws Throwable
    {
        return this.source.next();
    }
    public char skipWhiteSpaceAndRead() throws Throwable
    {
        for (char c=this.source.next();c!=0;c=this.source.next())
        {
            if (Character.isWhitespace(c)==false)
            {
                return c;
            }
        }
        return 0;
    }
    public Snippet end(int revert)
    {
        return this.source.endAndGetSnippet(revert);
    }
}
