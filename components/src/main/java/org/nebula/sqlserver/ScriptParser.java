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
package org.nebula.sqlserver;

import java.util.ArrayList;

import org.nova.parsing.scan.Lexeme;
import org.nova.parsing.scan.ScanException;
import org.nova.parsing.scan.Snippet;
import org.nova.parsing.scan.TextSource;

public class ScriptParser
{
    final private ArrayList<Function> functions; 
    final private ArrayList<Procedure> procedures; 
    final private ArrayList<Table> tables ;
    final private String text;
    
    private ScriptParser(String text)
    {
        this.text=text;
        this.functions=new ArrayList<>();
        this.procedures=new ArrayList<>();
        this.tables=new ArrayList<>();
    }
    
    private SqlServerObjects process() throws Throwable
    {
        parse();
        return new SqlServerObjects(this.functions.toArray(
                new Function[this.functions.size()])
               ,this.procedures.toArray(new Procedure[this.procedures.size()])
               ,this.tables.toArray(new Table[this.tables.size()])
        );
    }

    static final String MARKER="\nGO";
    static final String BEGIN_COMMENT="/*";
    static final String END_COMMENT="*/";
    
    private void parse() throws Throwable
    {
        String[] blocks=this.text.split(MARKER);
        for (String block:blocks)
        {
            TextSource source=new TextSource(block);
            SqlServerLexer lexer=new SqlServerLexer(source);
            try
            {
                lexer.skipComments();
                if (lexer.begin()==0)
                {
                    continue;
                }
                lexer.end(1);
                Lexeme lexeme=lexer.expectWord();
                if (lexeme.isError())
                {
                    throw new ScanException("Block verb expected", lexeme);
                }
                if (lexeme.isCaseInsenstiveWord("USE"))
                {
                    continue;
                }
                if (lexeme.isCaseInsenstiveWord("ALTER"))
                {
                    continue;
                }
                if (lexeme.isCaseInsenstiveWord("IF"))
                {
                    continue;
                }
                if (lexeme.isCaseInsenstiveWord("SET"))
                {
                    continue;
                }
                if (lexeme.isCaseInsenstiveWord("ENABLE"))
                {
                    continue;
                }
                if (lexeme.isCaseInsenstiveWord("CREATE"))
                {
                    parse_CREATE(lexer,block);
                    continue;
                }
                System.out.println("ScriptParser: Cannot process: "+lexeme.getValue());
            }
            catch (ScanException ex)
            {
                Lexeme lexeme=ex.getLexeme();
                Snippet snippet=lexeme.getSnippet();
                int bufferSize=80;
                int start=snippet.getTargetAbsolutePosition()-bufferSize;
                int end=start+2*bufferSize;
                int errorPosition=bufferSize;
                if (start<0)
                {
                    errorPosition+=start;
                    start=0;
                }
                if (end>block.length())
                {
                    end=block.length();
                }
                String errorText=block.substring(start,end);
                for (int i=0;i<errorText.length();i++)
                {
                    if (i==errorPosition)
                    {
                        System.out.print("<<"+ex.getMessage()+":"+snippet.getTarget()+">>");
                        i+=snippet.getTarget().length()-1;
                    }
                    else
                    {
                        System.out.print(errorText.charAt(i));
                    }
                    
                }
                throw ex;
            }
        }
    }
    
    
    void parse_CREATE(SqlServerLexer lexer,String block) throws Throwable
    {
        Lexeme lexeme=lexer.expectWord();
        if (lexeme.isError())
        {
            throw new ScanException("Object name expected", lexeme);
        }
        if (lexeme.isCaseInsenstiveWord("TABLE"))
        {
            parse_CREATE_TABLE(lexer,block);
            return;
        }
        if (lexeme.isCaseInsenstiveWord("FUNCTION"))
        {
            parse_CREATE_FUNCTION(lexer,block);
            return;
        }
        if (lexeme.isCaseInsenstiveWord("PROCEDURE"))
        {
            parse_CREATE_PROCEDURE(lexer,block);
            return;
        }
    }
    
    void parse_CREATE_FUNCTION(SqlServerLexer lexer,String block) throws Throwable
    {
        Lexeme owner=null;
        Lexeme name=lexer.expectWordWithBracketsRemoved();
        char c=lexer.skipWhiteSpaceAndBegin();
        if (c=='.')
        {
            lexer.end(0);
            owner=name;
            name=lexer.expectWordWithBracketsRemoved();
        }
        Function function=new Function(block,owner==null?null:owner.getValue(), name.getValue(), block);
        this.functions.add(function);
    }
    
    void parse_CREATE_PROCEDURE(SqlServerLexer lexer,String block) throws Throwable
    {
        Lexeme owner=null;
        Lexeme name=lexer.expectWordWithBracketsRemoved();
        char c=lexer.skipWhiteSpaceAndBegin();
        if (c=='.')
        {
            lexer.end(0);
            owner=name;
            name=lexer.expectWordWithBracketsRemoved();
        }
        Procedure procedure=new Procedure(block,owner==null?null:owner.getValue(), name.getValue(), block);
        this.procedures.add(procedure);
    }
    
    void parse_CREATE_TABLE(SqlServerLexer lexer,String block) throws Throwable
    {
        Lexeme owner=null;
        Lexeme tableName=lexer.expectWordWithBracketsRemoved();
        char c=lexer.skipWhiteSpaceAndBegin();
        if (c=='.')
        {
            lexer.end(0);
            owner=tableName;
            tableName=lexer.expectWordWithBracketsRemoved();
        }
        else
        {
            lexer.end(1);
        }
        lexer.expectPunctuator('(');
        ArrayList<Column> columns=new ArrayList<>();
        
        for (c=lexer.skipWhiteSpaceAndBegin();c!=')';)
        {
            if (c!='[')
            {
                lexer.revert();
                Lexeme lexeme=lexer.produceJavaIdentifier();
                if (lexeme.isError())
                {
                    throw new ScanException("type attribute expected", lexeme);
                }
                if (lexeme.isCaseInsenstiveWord("CONSTRAINT"))
                {
                    break;
                }                
                throw new ScanException("???", lexeme);
            }
            lexer.revert();
            Lexeme name=lexer.expectWordWithBracketsRemoved();
            
            Lexeme type=lexer.expectWordWithBracketsRemoved();
            boolean identity=false;
            Lexeme identityStart=null;
            Lexeme identityIncrement=null;
            Lexeme size=null;
            boolean nullAllowed=true;
            c=lexer.skipWhiteSpaceAndBegin();
            if (c=='(')
            {
                lexer.skipWhiteSpaceAndBegin();
                size=lexer.produceNumber(false);
                if (size.isError())
                {
//                    lexer.revert(size);
                    size=lexer.produceJavaIdentifier();
                }
                Lexeme lexeme=lexer.expectPunctuator(')');
                if (lexeme.isError())
                {
                    throw new ScanException("type size or max expected", lexeme);
                }
                c=lexer.skipWhiteSpaceAndBegin();
            }
            if ((c!=',')&&(c!=')'))
            {
                Lexeme lexeme=lexer.produceJavaIdentifier();
                if (lexeme.isError())
                {
                    throw new ScanException("type attribute expected", lexeme);
                }
                if (lexeme.isCaseInsenstiveWord("IDENTITY"))
                {
                    lexeme=lexer.expectPunctuator('(');
                    if (lexeme.isError())
                    {
                        throw new ScanException("( expected", lexeme);
                    }
                    lexer.skipWhiteSpace();
                    identityStart=lexer.produceNumber(false);
                    if (identityStart.isError())
                    {
                        throw new ScanException("number expected", identityStart);
                    }
                    lexeme=lexer.expectPunctuator(',');
                    if (identityStart.isError())
                    {
                        throw new ScanException(", expected", lexeme);
                    }
                    lexer.skipWhiteSpace();
                    identityIncrement=lexer.produceNumber(false);
                    if (identityIncrement.isError())
                    {
                        throw new ScanException("number expected", identityIncrement);
                    }
                    lexeme=lexer.expectPunctuator(')');
                    if (lexeme.isError())
                    {
                        throw new ScanException(") expected", lexeme);
                    }
                    lexer.skipWhiteSpace();
                    lexeme=lexer.produceJavaIdentifier();
                    if (lexeme.isError())
                    {
                        throw new ScanException("type attribute expected", lexeme);
                    }
                }
                if (lexeme.isCaseInsenstiveWord("NOT"))
                {
                    nullAllowed=false;
                    lexer.skipWhiteSpace();
                    lexeme=lexer.produceJavaIdentifier();
                }
                if (lexeme.isCaseInsenstiveWord("NULL")==false)
                {
                    throw new ScanException("NULL expected", lexeme);
                }
                c=lexer.skipWhiteSpaceAndBegin();
            }
//            System.out.println("Type="+type.getValue());
            Column column=new Column(name.getValue(), type.getValue(), size==null?null:size.getValue(), identity,identityStart==null?0:Long.parseLong(identityStart.getValue()), identityIncrement==null?0:Long.parseLong(identityIncrement.getValue()), nullAllowed);
            columns.add(column);
            if (c==',')
            {
                lexer.end(0);
                c=lexer.skipWhiteSpaceAndBegin();
                continue;
            }
          
            
        }
        lexer.end(0);
        Table table=new Table(block, owner==null?null:owner.getValue(),tableName.getValue(), columns.toArray(new Column[columns.size()]));
        this.tables.add(table);
    }
    
    static public SqlServerObjects parse(String text) throws Throwable
    {
        ScriptParser parser=new ScriptParser(text);
        return parser.process();
    }
}

