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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public abstract class Lexer
{
    class OperatorTree
    {
        private HashMap<Character,OperatorTree> leaves;
        private boolean terminator; 
        
        public OperatorTree()
        {
            this.leaves=null;
        }

        public boolean isTerminator()
        {
            return terminator;
        }
        public void setAsTerminator()
        {
            this.terminator=true;
        }
        public OperatorTree getLeave(char c,boolean caseSensitive)
        {
            if (this.leaves==null)
            {
                return null;
            }
            if (caseSensitive==false)
            {
                if ((c>='A')&&(c<='Z'))
                {
                    c=(char)(c+'a'-'A');
                }
            }
            return this.leaves.get(c);
        }

        public void add(String s,boolean caseSensitive)
        {
            if (caseSensitive==false)
            {
                s=s.toLowerCase();
            }
            build(s.toCharArray(),0);
        }
        
        private void build(char[] array,int index)
        {
            char c=array[index];
            if (this.leaves==null)
            {
                this.leaves=new HashMap<>();
            }
            OperatorTree childNode=this.leaves.get(c);
            if (childNode==null)
            {
                childNode=new OperatorTree();
                this.leaves.put(c, childNode);
            }
            if (index<array.length-1)
            {
                childNode.build(array, index+1);;
            }
            else
            {
                childNode.setAsTerminator();
            }
        }
    }
    
    final private HashSet<Character> punctuators;
    final private HashSet<String> keywords;
    final private boolean caseSensitive;
    final private Source source;
    
    final private String text;
    final private int length;
    private int position;
    private int mark;
    private boolean ignoreEndOfLine;
    
    private ArrayList<Lexeme> lexemes;
    private ArrayList<Lexeme> comments;
    private ArrayList<Lexeme> extras;
    private ArrayList<Lexeme> stream;

    private final OperatorTree operatorTree;
    
    public Lexer(Source source,String[] operators,char[] punctuators,String[] keywords,boolean caseSensitive,boolean ignoreEndOfLine)
    {
        this.ignoreEndOfLine=ignoreEndOfLine;
        this.source=source;
        this.text=source.getText();
        this.length=this.text.length();
        this.punctuators=new HashSet<>();
        if (punctuators!=null)
        {
            for (char separator:punctuators)
            {
                this.punctuators.add(separator);
            }
        }
        this.operatorTree=new OperatorTree();
        if (operators!=null)
        {
            for (String operator:operators)
            {
                this.operatorTree.add(operator,caseSensitive);
            }
        }
        this.caseSensitive=caseSensitive;
        this.keywords=new HashSet<>();
        if (keywords!=null)
        {
            for (String keyword:keywords)
            {
                if (caseSensitive)
                {
                    this.keywords.add(keyword);
                }
                else
                {
                    this.keywords.add(keyword.toLowerCase());
                }
            }
        }
        this.lexemes=new ArrayList<>();
        this.extras=new ArrayList<>();
        this.comments=new ArrayList<>();
        this.stream=new ArrayList<>();
    }
    
    private boolean addLexeme(Lexeme lexeme)
    {
        this.lexemes.add(lexeme);
        this.stream.add(lexeme);
        return true;
    }

    private boolean addComment()
    {
        Lexeme lexeme=new Lexeme(this.source,Token.COMMENT,null,this.mark,this.position);
        this.comments.add(lexeme);
        this.stream.add(lexeme);
        return true;
    }

    private boolean addExtra(String message)
    {
        Lexeme lexeme=new Lexeme(this.source,Token.EXTRA,message,this.mark,this.position);
        this.extras.add(lexeme);
        this.stream.add(lexeme);
        return true;
    }
    
    private boolean addExtraNumber(String message)
    {
        for (;;)
        {
            char c=next();
            if ((c==0)||(c=='\n')||Character.isWhitespace(c)||(this.punctuators.contains(c)))
            {
                break;
            }
        }
        back(1);
        return addExtra(message);
    }

    private boolean addExtraUnrecognized(String message)
    {
        for (;;)
        {
            char c=next();
            if ((c==0)||(c=='\n')||Character.isWhitespace(c)||(this.punctuators.contains(c)))
            {
                break;
            }
        }
        back(1);
        return addExtra(message);
    }

    private boolean addExtraDateTime(String message)
    {
        for (;;)
        {
            char c=next();
            if ((c>='0')&&(c<='9'))
            {
                continue;
            }
            if ((c=='+')||(c=='-')||(c=='.')||(c==':'))
            {
                continue;
            }
            break;
        }
        back(1);
        return addExtra(message);
    }

    private boolean addExtraString(String message,char endCharacter)
    {
        for (;;)
        {
            char c=next();
            if ((c==0)||(c=='\n')||(c==endCharacter))
            {
                break;
            }
        }
        back(1);
        return addExtra(message);
    }

    private boolean addExtraCharacter(String message)
    {
        for (;;)
        {
            char c=next();
            if ((c==0)||(c=='\n')||(c=='\''))
            {
                break;
            }
        }
        back(1);
        return addExtra(message);
    }


    public abstract boolean match(char first);

    int getNumberOfDigits()
    {
        int digits=0;
        for (char c=next();c!=0;c=next())
        {
            if ((c<'0')||(c>'9'))
            {
                break;
            }
            digits++;
        }
        back(1);
        return digits;
    }
    int getValue(int start,int digits)
    {
        int value=0;
        for (int i=0;i<digits;i++)
        {
            value=value*10+this.text.charAt(start+i)-'0';
        }
        return value;
    }
    
    public boolean matchDateTime(char first)
    {
        if ((first<'1')||(first>='9'))
        {
            return reset();
        }
        if (getNumberOfDigits()+1!=4)
        {
            return reset();
        }

        if (next()!='-')
        {
            return reset();
        }
        if (getNumberOfDigits()!=2)
        {
            return reset();
        }
        
        if (next()!='-')
        {
            return reset();
        }
        if (getNumberOfDigits()!=2)
        {
            return reset();
        }

        if (next()!='T')
        {
            return reset();
        }
        
        if (getNumberOfDigits()!=2)
        {
            return addExtraDateTime("Invalid datetime value. Hour part is not two digits.");
        }
        if (next()!=':')
        {
            return addExtraDateTime("Invalid datetime value caused by incorrect hour or minute form.");
        }
        if (getNumberOfDigits()!=2)
        {
            return addExtraDateTime("Invalid datetime value. Minute part is not two digits.");
        }

        char c=next();
        if (c==':')
        {
            if (getNumberOfDigits()!=2)
            {
                return addExtraDateTime("Invalid datetime value. Second part is not two digits.");
            }
            c=next();
        }

        if (c=='.')
        {
            if (getNumberOfDigits()<1)
            {
                return addExtraDateTime("Invalid datetime value. Fraction part is missing.");
            }
            c=next();
        }

        if (c=='Z')
        {
        }
        else if ((c=='-')||(c=='+'))
        {
            if (getNumberOfDigits()!=2)
            {
                return addExtraDateTime("Invalid datetime value. Hour offset part is not two digits.");
            }
            c=next();
            if (c==':')
            {
                if (getNumberOfDigits()!=2)
                {
                    return addExtraDateTime("Invalid datetime value. Minute offset part is not two digits.");
                }

                c=next();
                if (c==':')
                {
                    if (getNumberOfDigits()!=2)
                    {
                        return addExtraDateTime("Invalid datetime value. Second offset part is not two digits.");
                    }

                    c=next();
                    if (c=='.')
                    {
                        if (getNumberOfDigits()<1)
                        {
                            return addExtraDateTime("Invalid datetime value. Second offset part is not two digits.");
                        }
                    }
                    else
                    {
                        back(1);
                    }
                }
                else
                {
                    back(1);
                }
            }
            else
            {
                back(1);
            }
        }
        else if (Character.isLetterOrDigit(c))
        {
            return addExtraDateTime("Invalid datetime value caused by extra trailing characters.");
        }
        else
        {
            back(1);
        }
        String text=this.text.substring(this.mark,this.position);
        if (text.length()<=19)
        {
            text=text+"Z";
        }
        try
        {
            ZonedDateTime dateTime=ZonedDateTime.parse(text);
            return addLexeme(new Lexeme(source,Token.DATETIME,dateTime,this.mark,this.position));
        }
        catch (Throwable t)
        {
            String message="Invalid datetime value. "+t.getMessage();
            return addExtraDateTime(message);
        }
    }
    
    public void process()
    {
        if (this.ignoreEndOfLine==false)
        {
            for (char c=markSpaces();c!=0;c=markSpaces())
            {
                if (match(c))
                {
                    continue;
                }
                addExtraUnrecognized("Unable to handle character");
            }
        }
        else 
        {
            for (char c=mark();c!=0;c=mark())
            {
                if (match(c))
                {
                    continue;
                }
                addExtraUnrecognized("Unable to handle character");
            }
        }
    }
    public List<Lexeme> getLexemes()
    {
        return this.lexemes;
    }
    public List<Lexeme> getComments()
    {
        return this.comments;
    }
    public List<Lexeme> getExtras()
    {
        return this.extras;
    }
    
    protected char next()
    {
        if (this.position>=length)
        {
            this.position=length+1;
            return 0;
        }
        return this.text.charAt(this.position++);
    }
    
    protected char markSpaces()
    {
        for (this.mark=this.position;this.mark<this.text.length();this.mark++)
        {
            char c=this.text.charAt(this.mark);
            if (c=='\n')
            {
                addLexeme(new Lexeme(this.source,Token.END_OF_LINE,c,this.mark,this.mark+1));
            }
            else if (Character.isWhitespace(c)==false)
            {
                if (c!=65279) //BOM or zero width space
                {
                    this.position=this.mark+1;
                    return c;
                }
            }
        }
        return 0;
    }
    
    protected char mark()
    {
        for (this.mark=this.position;this.mark<this.text.length();this.mark++)
        {
            char c=this.text.charAt(this.mark);
            if (Character.isWhitespace(c)==false)
            {
                if (c!=65279) //BOM or zero width space
                {
                    this.position=this.mark+1;
                    return c;
                }
            }
        }
        return 0;
    }
    protected void back(int amount)
    {
        this.position-=amount;
    }
    protected boolean reset()
    {
        this.position=this.mark+1;
        return false;
    }

    protected boolean matchIdentifierOrKeyword(char first)
    {
        if (Character.isJavaIdentifierStart(first)==false)
        {
            return false;
        }
        for (char c=next();c!=0;c=next())
        {
            if (Character.isJavaIdentifierPart(c)==false)
            {
                break;
            }
        }
        back(1);
        String value=this.text.substring(this.mark, this.position);
        if (this.caseSensitive)
        {
            if (this.keywords.contains(value))
            {
                return addLexeme(new Lexeme(this.source,Token.KEYWORD,value,this.mark,this.position));
            }
        }
        else
        {
            if (this.keywords.contains(value.toLowerCase()))
            {
                return addLexeme(new Lexeme(this.source,Token.KEYWORD,value,this.mark,this.position));
            }
        }
        return addLexeme(new Lexeme(this.source,Token.IDENTIFIER,value,this.mark,this.position));
    }

    protected boolean matchIdentifier(char first)
    {
        if (Character.isJavaIdentifierStart(first)==false)
        {
            return false;
        }
        for (char c=next();c!=0;c=next())
        {
            if (Character.isJavaIdentifierPart(c)==false)
            {
                break;
            }
        }
        back(1);
        String value=this.text.substring(this.mark, this.position);
        return addLexeme(new Lexeme(this.source,Token.IDENTIFIER,value,this.mark,this.position));
    }

    protected boolean matchText(char first)
    {
        if (Character.isWhitespace(first))
        {
            return false;
        }
        for (char c=next();c!=0;c=next())
        {
            if (Character.isWhitespace(c))
            {
                break;
            }
        }
        back(1);
        String value=this.text.substring(this.mark, this.position);
        return addLexeme(new Lexeme(this.source,Token.TEXT,value,this.mark,this.position));
    }

    protected boolean matchTextOrKeyword(char first)
    {
        if (Character.isWhitespace(first))
        {
            return false;
        }
        for (char c=next();c!=0;c=next())
        {
            if (Character.isWhitespace(c))
            {
                break;
            }
        }
        back(1);
        String value=this.text.substring(this.mark, this.position);
        if (this.caseSensitive)
        {
            if (this.keywords.contains(value))
            {
                return addLexeme(new Lexeme(this.source,Token.KEYWORD,value,this.mark,this.position));
            }
        }
        else
        {
            if (this.keywords.contains(value.toLowerCase()))
            {
                return addLexeme(new Lexeme(this.source,Token.KEYWORD,value,this.mark,this.position));
            }
        }
        return addLexeme(new Lexeme(this.source,Token.TEXT,value,this.mark,this.position));
    }

    //operators must be non letter based (&&, >=, etc) or letter based (and, or, not, etc)
    protected boolean matchOperator(char first)
    {
        OperatorTree child;
        for (OperatorTree node=this.operatorTree.getLeave(first, this.caseSensitive);node!=null;node=child)
        {
            char c=next();
            child=node.getLeave(c, this.caseSensitive);
            if (child==null)
            {
                if (node.isTerminator())
                {
                    if (Character.isLetter(first)==false)
                    {
                        back(1);
                        return addLexeme(new Lexeme(this.source,Token.OPERATOR,this.text.substring(this.mark, this.position),this.mark,this.position));
                    }
                    if (Character.isLetter(c)==false)
                    {
                        back(1);
                        return addLexeme(new Lexeme(this.source,Token.OPERATOR,this.text.substring(this.mark, this.position),this.mark,this.position));
                    }
                }
            }
        }
        return reset();
    }
    
    protected boolean matchPunctuator(char first)
    {
        if (this.punctuators.contains(first)==false)
        {
            return false;
        }
        return addLexeme(new Lexeme(this.source,Token.PUNCTUATOR,first,this.mark,this.position));
    }
    
    protected boolean matchNumber(char first)
    {
        boolean fraction;
        boolean base;
        if (first=='.')
        {
            fraction=true;
            base=false;
        }
        else if ((first>='0')&&(first<='9'))
        {
            base=true;
            fraction=false;
        }
        else
        {
            return false;
        }
        
        char c;
        for (c=next();c!=0;c=next())
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
            if ((c=='e')||(c=='E'))
            {
                c=next();
                if ((c=='-')||(c=='+'))
                {
                    c=next();
                }
                if (c==0)
                {
                    return addExtraNumber("Incomplete number caused by end of text.");
                }
                if (Character.isDigit(c)==false)
                {
                    return addExtraNumber("Invalid number caused by invalid character.");
                }
                for (c=next();Character.isDigit(c);c=next())
                {
                }
            }
            break;
        }
        if (base==false)
        {
            return reset();
        }
        if (Character.isJavaIdentifierStart(c))
        {
            return reset();
        }
        int end=this.position-1;
        if (fraction)
        {
            back(1);
            try
            {
                double value=Double.parseDouble(this.text.substring(this.mark, this.position));
                return addLexeme(new Lexeme(this.source,Token.FLOAT,value,this.mark,this.position));
            }
            catch (Throwable t)
            {
                return addExtraNumber("Unexpected exception when parsing float.");
            }
        }
        String modifier=null;
        if ((c!='L')&&(c!='l'))
        {
            back(1);
        }
        else
        {
            modifier="L";
        }
        try
        {
            String text=this.text.substring(this.mark, end);
            long value=Long.parseLong(text);
            return addLexeme(new Lexeme(this.source,Token.INTEGER,value,modifier,this.mark,this.position));
        }
        catch (Throwable t)
        {
            return addExtraNumber("Unexpected exception when parsing integer.");
        }
    }

    protected boolean matchCommaNumber(char first)
    {
        boolean fraction;
        boolean base;
        if (first=='.')
        {
            fraction=true;
            base=false;
        }
        else if ((first>='0')&&(first<='9'))
        {
            base=true;
            fraction=false;
        }
        else
        {
            return false;
        }
        StringBuilder sb=new StringBuilder();
        sb.append(first);
        char c;
        for (c=next();c!=0;c=next())
        {
            if (Character.isDigit(c))
            {
                sb.append(c);
                base=true;
                continue;
            }
            if ((c==',')&&(base))
            {
                continue;
            }
            if ((c=='.')&&(fraction==false))
            {
                sb.append(c);
                fraction=true;
                continue;
            }
            if ((c=='e')||(c=='E'))
            {
                sb.append(c);
                c=next();
                if ((c=='-')||(c=='+'))
                {
                    sb.append(c);
                    c=next();
                }
                if (c==0)
                {
                    return addExtraNumber("Incomplete number caused by end of text.");
                }
                if (Character.isDigit(c)==false)
                {
                    return addExtraNumber("Invalid number caused by invalid character.");
                }
                for (c=next();Character.isDigit(c);c=next())
                {
                    sb.append(c);
                }
            }
            break;
        }
        if (base==false)
        {
            return reset();
        }
        if (Character.isJavaIdentifierStart(c))
        {
            return reset();
        }
        if (fraction)
        {
            back(1);
            try
            {
                double value=Double.parseDouble(sb.toString());
                return addLexeme(new Lexeme(this.source,Token.FLOAT,value,this.mark,this.position));
            }
            catch (Throwable t)
            {
                return addExtraNumber("Unexpected exception when parsing float.");
            }
        }
        String modifier=null;
        if ((c!='L')&&(c!='l'))
        {
            back(1);
        }
        else
        {
            modifier="L";
        }
        try
        {
            long value=Long.parseLong(sb.toString());
            return addLexeme(new Lexeme(this.source,Token.INTEGER,value,modifier,this.mark,this.position));
        }
        catch (Throwable t)
        {
            return addExtraNumber("Unexpected exception when parsing integer.");
        }
    }
    
    protected boolean matchCharacter(char first)
    {
        if (first!='\'')
        {
            return false;
        }
        char c=next();
        if (c=='\\')
        {
            c=next();
            if ((c!='\\')||(c!='\''))
            {
                return addExtraCharacter("Invalid character caused by incorrect escape");
            }
        }
        char end=next();
        if (end!='\'')
        {
            return addExtraCharacter("Invalid character caused by incorrect end of character");
        }
        return addLexeme(new Lexeme(this.source,Token.CHARACTER,c,this.mark,this.position));
    }
    protected boolean matchDoubleQuotedString(char first)
    {
        if (first!='"')
        {
            return false;
        }
        return produceString(first);
    }

    protected boolean matchSingleQuotedString(char first)
    {
        if (first!='\'')
        {
            return false;
        }
        return produceString(first);
    }
    
    private boolean produceString(char endCharacter)
    {
        StringBuilder sb = new StringBuilder();
        for (char c=next();c!=0;c=next())
        {
            if (c != '\\')
            {
                if (c == endCharacter)
                {
                    return addLexeme(new Lexeme(this.source,Token.STRING,sb.toString(),this.mark,this.position));
                }
                else if (c == '\n')
                {
                    return addExtraString("Invalid string caused by new line",endCharacter);
                }
                sb.append(c);
                continue;
            }
            c = next();
            if (c == 'u')
            {
                StringBuilder unicode=new StringBuilder();
                for (int i = 0; i < 4; i++)
                {
                    c = next();
                    if (c==0)
                    {
                        return addExtraString("Incomplete string caused by end of text",endCharacter);
                    }
                    if (Character.isDigit(c) || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F')))
                    {
                        unicode.append(c);
                        continue;
                    }
                    return addExtraString("Invalid string caused by invalid unicode escape",endCharacter);
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
                return addExtraString("Invalid String caused by invalid escape character",endCharacter);
            }
        }
        return addExtra("Incomplete String caused by end of text");
    }

    protected boolean matchSQLString(char first)
    {
        if (first!='\'')
        {
            return false;
        }
        char endCharacter='\'';
        StringBuilder sb = new StringBuilder();
        for (char c=next();c!=0;c=next())
        {
            if (c == endCharacter)
            {
                c=next();
                if (c!=endCharacter)
                {
                    back(1);
                    return addLexeme(new Lexeme(this.source,Token.STRING,sb.toString(),this.mark,this.position));
                }
            }
            sb.append(c);
        }
        return addExtra("Incomplete String caused by end of text");
    }

    protected boolean matchComment(char first)
    {
        if (first!='/')
        {
            return false;
        }
        char next=next();
        if (next=='*')
        {
            return produceNestableSlashStarComment();
        }
        else if (next=='/')
        {
            return produceSlashSlashComment();
        }
        else
        {
            back(1);
            return false;
        }
    }
    
    private boolean produceSlashSlashComment() 
    {
        for (char c=next();c!=0;c=next())
        {
            if ((c == '\r')||(c=='\n'))
            {
                break;
            }
        }
        return addComment();
    }
    public boolean produceNestableSlashStarComment() 
    {
        boolean insideString = false;
        int level = 0;
        for (char c=next();c!=0;c=next())
        {
            if (insideString)
            {
                if (c == '"')
                {
                    insideString = false;
                }
                else if (c == '\\')
                {
                    c = next();
                }
            }
            else if (c == '"')
            {
                insideString = true;
            }
            else if (c == '*')
            {
                c = next();
                if (c == '/')
                {
                    if (level == 0)
                    {
                        return addComment();
                    }
                    level--;
                }
            }
            else if (c == '/')
            {
                c = next();
                if (c == '*')
                {
                    level++;
                }
            }
        }
        return addExtra("Incomplete comment caused by end of text");
    }
    
}
