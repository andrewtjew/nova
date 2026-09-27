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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class ExpressionParser
{
    final private Map<String,Integer> precedenceLevels;
    final private Set<String> prefixOperators;
    final private Set<String> postfixOperators;
    final int operatorPrecedence; 
    final int maximumPrecedence;
    final int maximumOperatorPrecendence;
    final int constantPrecedence;
    final int identifierPrecedence;
    final int keywordPrecedence;
    final int punctuatorPrecedence;
    final public String argumentSeperator;
    final ArrayList<ParseError> parseErrors;
    final private Map<String,Enclosure> openEnclosures;
    final private Map<String,Enclosure> closeEnclosures;
    public ExpressionParser(String argumentSeperator,Map<String,Integer> precedenceLevels,String[] prefixOperators,String[] postfixOperators,Enclosure[] subExpressionEnclosures)
    {
        this.precedenceLevels=precedenceLevels;
        this.prefixOperators=new HashSet<String>();
        if (prefixOperators!=null)
        {
            for (String operator:prefixOperators)
            {
                this.prefixOperators.add(operator);
            }
        }
        this.postfixOperators=new HashSet<String>();
        if (postfixOperators!=null)
        {
            for (String operator:postfixOperators)
            {
                this.postfixOperators.add(operator);
            }
        }
        this.openEnclosures=new java.util.HashMap<>();
        this.closeEnclosures=new java.util.HashMap<>();
        if (subExpressionEnclosures!=null)
        {
            for (Enclosure enclosure:subExpressionEnclosures)
            {
                this.openEnclosures.put(enclosure.open(),enclosure);
            }
            for (Enclosure enclosure:subExpressionEnclosures)
            {
                this.closeEnclosures.put(enclosure.close(),enclosure);
            }
        }
        
        int maximumOperatorPrecendence=0;
        int operatorPrecedence=Integer.MAX_VALUE;
        for (var entry:precedenceLevels.entrySet())
        {
            var precedenceLevel=entry.getValue();
            if (entry.getKey().equals(argumentSeperator))
            {
                operatorPrecedence=precedenceLevel;
            }
            if (precedenceLevel>maximumOperatorPrecendence)
            {
                maximumOperatorPrecendence=precedenceLevel;
            }
        }
        this.argumentSeperator=argumentSeperator;
        this.maximumOperatorPrecendence=maximumOperatorPrecendence;
        this.operatorPrecedence=operatorPrecedence;
        
        int level=this.maximumOperatorPrecendence+1;
        this.identifierPrecedence=level++;
        this.keywordPrecedence=level++;
        this.constantPrecedence=level++;
        this.punctuatorPrecedence=level++;
        this.maximumPrecedence=level;
        this.parseErrors=new ArrayList<>();
    }
    
    public void addParseError(String message,Token...tokens)
    {
        ParseError parseError=new ParseError(message, tokens);
        this.parseErrors.add(parseError);
    }
    public List<ParseError> getParseErrors()
    {
        return this.parseErrors;
    }
    
    public ExpressionNode parse(List<Token> tokens)
    {
        return parse(0,tokens.size(),tokens);
    }
    
    private int getPrecedenceLevel(Token token,int level)
    {
        TokenType tokenType = token.getType();
        int precedenceLevel=Integer.MAX_VALUE;
        switch (tokenType)
        {
            case TokenType.IDENTIFIER:
            precedenceLevel=this.identifierPrecedence;
            break;

            case TokenType.STRING:
            case TokenType.CHARACTER:
            case TokenType.NUMBER:
            precedenceLevel=this.constantPrecedence;
            break;

            case TokenType.OPERATOR:
            var operatorPrecedenceLevel=this.precedenceLevels.get(token.getLiteral());
            if (operatorPrecedenceLevel==null)
            {
                precedenceLevel=this.maximumPrecedence;
            }
            else
            {
                precedenceLevel = operatorPrecedenceLevel;
            }
            break;

            case PUNCTUATOR:
            precedenceLevel=this.punctuatorPrecedence;   
            break;
            
            case KEYWORD:
            precedenceLevel=this.keywordPrecedence;
            break;

            default:
            precedenceLevel=this.maximumPrecedence;
            break;
        }
        return precedenceLevel+this.maximumPrecedence*level;
    }

    public ExpressionNode parse(final int start,final int end,List<Token> tokens)
    {
        if (start>=end)
        {
            return null;
        }
        int lowestPrecedenceLevel = Integer.MAX_VALUE;
        int lowestIndex = start;

        Stack<Token> stack=new Stack<>();
        for (int i = end - 1; i >= start; i--)
        {
            Token token = tokens.get(i);
            TokenType tokenType = token.getType();
            switch (tokenType)
            {
                case TokenType.OPERATOR:
                while (i > start)
                {
                    // To handle prefix operators cases like +-1 or not not true
                    // We want to find left most operator as prefix operators
                    // associate strictly right to left
                    
                    Token before = tokens.get(i - 1);
                    if (before.getType()!=TokenType.OPERATOR)
                    {
                        break;
                    }
                    if (this.prefixOperators.contains(before.getLiteral())==false)
                    {
                        break;
                    }
                    token = before;
                    i--;
                }
                break;
                
                case PUNCTUATOR:
                String punctuator=token.getLiteral();
                var openingEnclosure=this.openEnclosures.get(punctuator);
                if (openingEnclosure!=null)
                {
                    if (stack.isEmpty())
                    {
                        addParseError("Missing right match",token);
                    }
                    else
                    {
                        Token close=stack.pop();
                        if (close.getLiteral().equals(openingEnclosure.close())==false)
                        {
                            addParseError("Parenthesis mismmatch",token,close);
                        }
                    }
                }
                else
                {
                    var closingEnclosure=this.closeEnclosures.get(punctuator);
                    if (closingEnclosure!=null)
                    {
                        stack.push(token);
                    }
                }
                break;
                
                default:
                break;
            }
            var precedenceLevel = getPrecedenceLevel(token,stack.size());
            if (precedenceLevel < lowestPrecedenceLevel)
            {
                lowestPrecedenceLevel = precedenceLevel;
                lowestIndex = i;
            }
        }
        if (stack.size()>0)
        {
            addParseError("Missing left matches", stack.toArray(new Token[stack.size()]));
        }
        Token startToken=tokens.get(start);
        Token endToken=tokens.get(end-1);
        if (lowestPrecedenceLevel==this.punctuatorPrecedence)
        {
            Enclosure openClosure=this.openEnclosures.get(startToken.getLiteral());
            Enclosure closeClosure=this.closeEnclosures.get(endToken.getLiteral());
            
            if (openClosure!=null && openClosure==closeClosure)
            {
                //"((a+b)+c+d)" becomes "(a+b)+c+d"
                return parse(start + 1, end - 1, tokens);
            }
            addParseError("No object for lookup", startToken,endToken);
            return new ErrorNode();
        }
        Token lowestToken = tokens.get(lowestIndex);
        if (lowestPrecedenceLevel<=this.maximumOperatorPrecendence)
        {
            // operator
            if (lowestIndex==start)
            {
                if (this.prefixOperators.contains(lowestToken.getLiteral()))
                {
                    // "not(a+b)" becomes "(a+b)"
                    ExpressionNode right = parse(lowestIndex + 1, end, tokens);
                    if (right==null)
                    {
                        addParseError("Right operand missing", lowestToken);
                        return new ErrorNode();
                    }
                    return new PrefixOperatorNode(lowestToken,right);
                }
                addParseError("Not a prefix operator.", lowestToken);
                return new ErrorNode();
            }
            else if (lowestIndex==end-1)
            {
                if (this.postfixOperators.contains(lowestToken.getLiteral()))
                {
                    // "a++" becomes "a"
                    ExpressionNode left = parse(start,lowestIndex,tokens);
                    if (left==null)
                    {
                        addParseError("Left operand missing", lowestToken);
                        return new ErrorNode();
                    }
                    return new PostfixOperatorNode(lowestToken,left);
                }
                addParseError("Not a postfix operator.", lowestToken);
                return new ErrorNode();
            }
            
            //"a*b+c" becomes "a*b" and "c"
            ExpressionNode left = parse(start, lowestIndex, tokens);
            ExpressionNode right = parse(lowestIndex + 1, end, tokens);
            if (left==null)
            {
                addParseError("Left operand missing", lowestToken);
                return new ErrorNode();
            }
            if (right==null)
            {
                addParseError("Right operand missing", lowestToken);
                return new ErrorNode();
            }
            return new BinaryOperatorNode(lowestToken,left,right);
        }
        if (lowestPrecedenceLevel==this.constantPrecedence)
        {
            if (lowestIndex>start)
            {
                addParseError("Unexpected extra token before constant", lowestToken,tokens.get(lowestIndex-1));
                return new ErrorNode();
            }
            if (lowestIndex<end-1)
            {
                addParseError("Unexpected extra token before constant", lowestToken,tokens.get(lowestIndex+1));
                return new ErrorNode();
            }
            return new ConstantNode(lowestToken);
        }
        if (lowestPrecedenceLevel==this.identifierPrecedence)
        {
            if (lowestIndex>start)
            {
                addParseError("Unexepected tokens before identifier.", tokens.get(lowestIndex-1));
                return new ErrorNode();
            }
            if (lowestIndex==end-1)
            {
                return new IdentifierNode(lowestToken);
            }
            
            //check if the next token is an open enclosure, like "(" or "[" eg foo(1,2) or bar[1]
            Token openToken=tokens.get(lowestIndex+1);
            if (openToken.getType()==TokenType.PUNCTUATOR)
            {
                Enclosure enclosure=this.openEnclosures.get(openToken.getLiteral());
                if ((enclosure!=null)&&endToken.getType()==TokenType.PUNCTUATOR&&endToken.getLiteral().equals(enclosure.close()))
                {
                    ExpressionNode operand=parse(lowestIndex+2,end-1,tokens);
                    var list=flattenArguments(operand,new ArrayList<ExpressionNode>()).reversed();
                    var arguments=list.toArray(new ExpressionNode[list.size()]);
                    ArgumentNode argumentNode=new ArgumentNode(openToken, endToken, arguments);
                    return new IdentifierNode(lowestToken,argumentNode);
                }
            }
        }
        if (lowestPrecedenceLevel==this.keywordPrecedence)
        {
            if (lowestIndex>start)
            {
                addParseError("Unexepected tokens before keyword.", tokens.get(lowestIndex-1));
                return new ErrorNode();
            }
            if (lowestIndex==end-1)
            {
                //just a keyword, like "do"
                return new KeywordNode(lowestToken);
            }
            
            Token openToken=tokens.get(lowestIndex+1);
            if (openToken.getType()==TokenType.PUNCTUATOR)
            {
                Enclosure enclosure=this.openEnclosures.get(openToken.getLiteral());
                if ((enclosure!=null)&&endToken.getType()==TokenType.PUNCTUATOR&&endToken.getLiteral().equals(enclosure.close()))
                {
                    // "if (a==b)" becomes "a==b"   
                    ExpressionNode operand=parse(lowestIndex+2,end-1,tokens);
                    var list=flattenArguments(operand,new ArrayList<ExpressionNode>()).reversed();
                    var arguments=list.toArray(new ExpressionNode[list.size()]);
                    ArgumentNode argumentNode=new ArgumentNode(openToken, endToken, arguments);
                    return new KeywordNode(lowestToken,argumentNode);
                }
            }
        }
        addParseError("Unexpected token.", lowestToken);
        return new ErrorNode();
    }
    private boolean isArgumentSeperator(ExpressionNode node)
    {
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode binaryOperatorNode=(BinaryOperatorNode)node;
            if (binaryOperatorNode.getToken().getLiteral().equals(this.argumentSeperator))
            {
                return true;
            }
        }
        return false;
    }
    
    private List<ExpressionNode> flattenArguments(ExpressionNode node,List<ExpressionNode> arguments)
    {
        if (isArgumentSeperator(node))
        {
            BinaryOperatorNode binaryOperatorNode=(BinaryOperatorNode)node;
            if (binaryOperatorNode.getToken().getLiteral().equals(this.argumentSeperator))
            {
                var leftOperand=binaryOperatorNode.getLeftOperand();
                var rightOperand=binaryOperatorNode.getRightOperand();
                if (isArgumentSeperator(leftOperand))
                {
                    flattenArguments(leftOperand,arguments);
                }
                else
                {
                    arguments.add(leftOperand);
                }
                if (isArgumentSeperator(rightOperand)) 
                {
                    //This should never execute because the parser current parses right to left.
                    flattenArguments(rightOperand,arguments);
                }
                else
                {
                    arguments.add(rightOperand);
                }
            }
        }
        return arguments;
    }
}
