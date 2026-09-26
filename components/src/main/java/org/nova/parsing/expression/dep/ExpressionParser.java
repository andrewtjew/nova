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
package org.nova.parsing.expression.dep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.nova.lexing.dep.Lexeme;
import org.nova.lexing.dep.Token;

public class ExpressionParser
{
    final private Map<String,Integer> precedenceLevels;
    final private Set<String> prefixOperators;
    final private Set<String> postfixOperators;
    final int maximumPrecedence;
    final int maximumOperatorPrecendence;
    final int constantPrecedence;
    final int identifierPrecedence;
    final ArrayList<ParseError> parseErrors;

    public ExpressionParser(Map<String,Integer> precedenceLevels,Set<String> prefixOperators,Set<String> postfixOperators)
    {
        this.precedenceLevels=precedenceLevels;
        this.prefixOperators=prefixOperators!=null?prefixOperators:new HashSet<String>();
        this.postfixOperators=postfixOperators!=null?postfixOperators:new HashSet<String>();
        this.maximumOperatorPrecendence=precedenceLevels.size()-1;
        int level=precedenceLevels.size();
        this.identifierPrecedence=level++;
        this.constantPrecedence=level++;
        this.maximumPrecedence=level;
        this.parseErrors=new ArrayList<>();
    }
    
    private int getPrecedenceLevel(Lexeme lexeme,int level)
    {
        String operator=(String)lexeme.getValue();
        Integer value=this.precedenceLevels.get(operator);
        if (value==null)
        {
            addParseError("Unknown operator",lexeme);
            return Integer.MAX_VALUE;
        }
        return value + level * this.precedenceLevels.size();
    }
    private int getPrecedenceLevel(int precedence,int level)
    {
        return precedence + level * this.precedenceLevels.size();
    }
    private int getMaximumPrecedence()
    {
        return this.maximumPrecedence;
    }
    
    public void addParseError(String message,int start,int end,List<Lexeme> lexemes)
    {
        Lexeme[] errorLexemes=null;
        if (end>start)
        {
            errorLexemes=new Lexeme[end-start];
            for (int i=0;i<errorLexemes.length;i++)
            {
                errorLexemes[i]=lexemes.get(start+i);
            }
        }
        addParseError(message,errorLexemes);
    }
    public void addParseError(String message,Lexeme...lexemes)
    {
        ParseError parseError=new ParseError(message, lexemes);
        this.parseErrors.add(parseError);
    }
    public void addParseError(String message,ExpressionNode node)
    {
        ParseError parseError=new ParseError(message, ParsingUtils.getLexemes(node));
        this.parseErrors.add(parseError);
    }
    public List<ParseError> getParseErrors()
    {
        return this.parseErrors;
    }
    
    public ExpressionNode parse(int start,int end,List<Lexeme> lexemes)
    {
        if (start>=end)
        {
            return null;
        }
        int lowestPrecedenceLevel = Integer.MAX_VALUE;
        int lowestIndex = start;

        Stack<Lexeme> stack=new Stack<>();
        for (int i = end - 1; i >= start; i--)
        {
            Lexeme lexeme = lexemes.get(i);
            Token token = lexeme.getToken();
            int precedenceLevel=Integer.MAX_VALUE;

            switch (token)
            {
            case IDENTIFIER:
                precedenceLevel=getPrecedenceLevel(this.identifierPrecedence,stack.size());
            break;

            case TEXT:
            case CHARACTER:
            case INTEGER:
            case FLOAT:
            case STRING:
            case DATETIME:
                precedenceLevel=getPrecedenceLevel(this.constantPrecedence,stack.size());
                break;

            case OPERATOR:
                while (i > start)
                {
                    // To handle prefix operators
                    // We want to find left most operator as prefix operators
                    // associate strictly right to left
                    Lexeme before = lexemes.get(i - 1);
                    if (before.getToken()!=Token.OPERATOR)
                    {
                        break;
                    }
                    if (this.prefixOperators.contains((String)before.getValue())==false)
                    {
                        break;
                    }
                    lexeme = before;
                    i--;
                }
                precedenceLevel = getPrecedenceLevel(lexeme,stack.size());
                break;

            case PUNCTUATOR:
                char punctuator=(char)lexeme.getValue();
                switch (punctuator)
                {
                    case '(':
                        if (stack.isEmpty())
                        {
                            addParseError("Missing right match",lexeme);
                        }
                        else
                        {
                            Lexeme right=stack.pop();
                            if ((char)(right.getValue())!=')')
                            {
                                addParseError("Parenthesis mismmatch",lexeme,right);
                            }
                        }
                        break;
                        
                    case '[':
                        if (stack.isEmpty())
                        {
                            addParseError("Missing right match",lexeme);
                        }
                        else
                        {
                            Lexeme right=stack.pop();
                            if ((char)(right.getValue())!=']')
                            {
                                addParseError("Incorrect right match",lexeme,right);
                            }
                        }
                        break;
                        
                    case ')':
                    case ']':
                        stack.push(lexeme);
                        break;
                }
                case COMMENT:
                case EXTRA:
                    break;
                
                case KEYWORD:
                    addParseError("Unexpected keyword in expression",lexeme);

                default:
                    break;

            }
            if (precedenceLevel < lowestPrecedenceLevel)
            {
                lowestPrecedenceLevel = precedenceLevel;
                lowestIndex = i;
            }
        }
        if (stack.size()>0)
        {
            addParseError("Missing left matches", stack.toArray(new Lexeme[stack.size()]));
        }
        Lexeme startLexeme=lexemes.get(start);
        Lexeme endLexeme=lexemes.get(end-1);
        if (lowestPrecedenceLevel>=getMaximumPrecedence())
        {
            if (startLexeme.isPunctuator('(') && endLexeme.isPunctuator(')')) 
            {
                return parse(start + 1, end - 1, lexemes);
            }
            addParseError("No object for lookup", startLexeme,endLexeme);
            return new ErrorNode();
        }
        Lexeme lowest = lexemes.get(lowestIndex);
        if (lowestPrecedenceLevel<=this.maximumOperatorPrecendence)
        {
            if (lowestIndex==start)
            {
                if (this.prefixOperators.contains((String)lowest.getValue()))
                {
                    ExpressionNode right = parse(lowestIndex + 1, end, lexemes);
                    if (right==null)
                    {
                        addParseError("Right operand missing", lowest);
                        return new ErrorNode();
                    }
                    return new PrefixOperatorNode(lowest,right);
                }
                addParseError("Not a prefix operator.", lowest);
                return new ErrorNode();
            }
            else if (lowestIndex==end-1)
            {
                if (this.postfixOperators.contains((String)lowest.getValue()))
                {
                    ExpressionNode left = parse(start,lowestIndex,lexemes);
                    if (left==null)
                    {
                        addParseError("Left operand missing", lowest);
                        return new ErrorNode();
                    }
                    return new PostfixOperatorNode(lowest,left);
                }
                addParseError("Not a postfix operator.", lowest);
                return new ErrorNode();
            }
            ExpressionNode left = parse(start, lowestIndex, lexemes);
            ExpressionNode right = parse(lowestIndex + 1, end, lexemes);
            if (left==null)
            {
                addParseError("Left operand missing", lowest);
                return new ErrorNode();
            }
            if (right==null)
            {
                addParseError("Right operand missing", lowest);
                return new ErrorNode();
            }
            return new BinaryOperatorNode(lowest,left,right);
        }
        if (lowestPrecedenceLevel==this.constantPrecedence)
        {
            if (lowestIndex>start)
            {
                addParseError("Unexpected extra token", lowest,lexemes.get(lowestIndex-1));
                return new ErrorNode();
            }
            if (lowestIndex<end-1)
            {
                addParseError("Unexpected extra token", lowest,lexemes.get(lowestIndex+1));
                return new ErrorNode();
            }
            if (lowest.getToken()==Token.OPERATOR)
            {
                return new KeywordNode(lowest);
            }
            return new ConstantNode(lowest);
        }
        if (lowestPrecedenceLevel==this.identifierPrecedence)
        {
            if (lowestIndex>start)
            {
                addParseError("Unexepected tokens before this identifier.", start,lowestIndex+1,lexemes);
                return new ErrorNode();
            }
            if (lowestIndex==end-1)
            {
                if (lowest.getToken()==Token.KEYWORD)
                {
                    return new KeywordNode(lowest);
                }
                return new IdentifierNode(lowest,null);
            }
            
            Lexeme openLexeme=lexemes.get(lowestIndex+1);
            if (openLexeme.isPunctuator('(')&&endLexeme.isPunctuator(')'))
            {
                if (lowest.getToken()==Token.KEYWORD)
                {
                    addParseError("Keyword is used as function name.", lowestIndex,end,lexemes);
                    return new ErrorNode();
                }
                ExpressionNode operand=parse(lowestIndex+2,end-1,lexemes);
                ArgumentNode argumentNode=new ArgumentNode(openLexeme, endLexeme, operand);
                return new IdentifierNode(lowest,argumentNode);
            }
            if (openLexeme.isPunctuator('[')&&endLexeme.isPunctuator(']'))
            {
                if (lowest.getToken()==Token.KEYWORD)
                {
                    addParseError("Keyword is used as function name.", lowestIndex,end,lexemes);
                    return new ErrorNode();
                }
                ExpressionNode operand=parse(lowestIndex+2,end-1,lexemes);
                ArgumentNode argumentNode=new ArgumentNode(openLexeme, endLexeme, operand);
                return new IdentifierNode(lowest,argumentNode);
            }
        }
        addParseError("Syntax error.", start,end,lexemes);
        return new ErrorNode();
    }
}
