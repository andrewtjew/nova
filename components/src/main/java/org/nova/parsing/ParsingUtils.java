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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.nova.lexing.dep.LexingUtils;
import org.nova.parsing.section.dep.Section;

public class ParsingUtils
{
//    private static void getTokens(ExpressionNode node,ArrayList<Token> list)
//    {
//        if (node==null)
//        {
//            return;
//        }
//        if (node instanceof BinaryOperatorNode)
//        {
//            BinaryOperatorNode n=(BinaryOperatorNode)node;
//            getTokens(n.getLeftOperand(), list);
//            list.add(n.getToken());
//            getTokens(n.getRightOperand(),list);
//            return;
//        }
//        else if (node instanceof PrefixOperatorNode)
//        {
//            PrefixOperatorNode n=(PrefixOperatorNode)node;
//            list.add(n.getToken());
//            getTokens(n.getOperand(),list);
//            return;
//        }
//        else if (node instanceof PostfixOperatorNode)
//        {
//            PostfixOperatorNode n=(PostfixOperatorNode)node;
//            list.add(n.getToken());
//            getTokens(n.getOperand(),list);
//            return;
//        }
//        else if (node instanceof ConstantNode)
//        {
//            ConstantNode n=(ConstantNode)node;
//            list.add(n.getToken());
//        }
//        else if (node instanceof KeywordNode)
//        {
//            KeywordNode n=(KeywordNode)node;
//            list.add(n.getToken());
//        }
//        else if (node instanceof IdentifierNode)
//        {
//            IdentifierNode n=(IdentifierNode)node;
//            list.add(n.getToken());
//        }
//        else if (node instanceof ArgumentNode)
//        {
//            ArgumentNode n=(ArgumentNode)node;
//            list.add(n.getOpenToken());
//            for (ExpressionNode argument:n.getArguments())
//            {
//                list.add(argument.getToken());
//            }
//            list.add(n.getCloseToken());
//        }
//        else if (node instanceof ErrorNode)
//        {
//        }
//        else
//        {
//           throw new RuntimeException();
//        }
//    }

    private static void collapseToListSeperatedByOperator(ExpressionNode node,String operator,ArrayList<ExpressionNode> list)
    {
        if (node==null)
        {
            return;
        }
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode n=(BinaryOperatorNode)node;
            if (n.isOperator(operator))
            {
                collapseToListSeperatedByOperator(n.getLeftOperand(), operator,list);
                collapseToListSeperatedByOperator(n.getRightOperand(), operator,list);
                return;
            }
        }
        list.add(node);
    }
    
//    public static Token[] getTokens(ExpressionNode node)
//    {
//        ArrayList<Token> list=new ArrayList<>();
//        getTokens(node,list);
//        return list.toArray(new Token[list.size()]);
//    }   
    
//    static public List<ExpressionNode> collapseToListSeperatedByOperator(ExpressionNode root,String operator)
//    {
//        ArrayList<ExpressionNode> list=new ArrayList<>();
//        collapseToListSeperatedByOperator(root, operator,list);
//        return list;
//    }
//
//    static public List<ExpressionNode> collapseToList(ExpressionNode root)
//    {
//        return collapseToListSeperatedByOperator(root, ",");
//    }
    static class LineAndColumn
    {
        final int line;
        final int column;
        final int targetLineStart;
        final int targetLineEnd;
        public LineAndColumn(String text,int position)
        {
            if (position>text.length())
            {
                position=text.length();
            }
            int line=0;
            int column=0;
            int targetLineStart=0;
            int targetLineEnd=text.length();
            for (int i=0;i<position;i++)
            {
                if (text.charAt(i)=='\n')
                {
                    line++;
                    column=0;
                    targetLineStart=i+1;
                }
                else
                {
                    column++;
                }
            }
            for (int i=position;i<text.length();i++)
            {
                if (text.charAt(i)=='\n')
                {
                    targetLineEnd=i+1;
                    break;
                }
            }
            this.line=line;
            this.column=column;
            this.targetLineStart=targetLineStart;
            this.targetLineEnd=targetLineEnd;
        }
    }
    
    static public void printTokenError(PrintStream stream,Token token)
    {
        var snippet=token.getSnippet();
        var source=snippet.getSource();
        String text=snippet.getBuffer();
        LineAndColumn lineAndColumn=new LineAndColumn(text, token.getSourceIndex());
        int line=lineAndColumn.line+1;
        int column=lineAndColumn.column+1;
        String position="("+line+","+column+"):";
        stream.print(position);
        stream.println(token.getLiteral());
        stream.println(text.substring(lineAndColumn.targetLineStart, lineAndColumn.targetLineEnd));
        for (int i=0;i<column-1;i++)
        {
            stream.print(' ');
        }
        for (int i=0;i<snippet.getTarget().length();i++)
        {
            stream.print('^');
        }
        stream.println();
    }
    
    static public void printParseException(PrintStream stream,ParseError exception)
    {
        stream.println(exception.getMessage());
        for (Token token:exception.getTokens())
        {
            printTokenError(stream, token);
        }
    }    
    
    static public void printParseException(ParseError exception)
    {
        printParseException(System.out,exception);
    }    

    static String INDENT="--";
    
    public static void setIndent(String indent)
    {
        INDENT=indent;
    }
    
    static void printIndent(PrintStream stream,int level)
    {
        for (int i=0;i<level;i++)
        {
            stream.print(INDENT);
        }
    }

    static void printExpressionNode(PrintStream stream,ExpressionNode node,Token token)
    {
        stream.print(node.toString());
        if (token.getNumericType()!=null)
        {
            stream.print(",numeric type="+token.getNumericType());
            if (token.getIntegerSize()!=null)
            {
                stream.print(",integer size="+token.getIntegerSize());
            }
        }
        stream.println();
    }

//    static void printLexeme(PrintStream stream,Token token)
//    {
//        stream.print("token="+token.getType()+",literal="+token.getLiteral());
//        if (token.getNumericType()!=null)
//        {
//            stream.print(",numeric type="+token.getNumericType());
//            if (token.getIntegerSize()!=null)
//            {
//                stream.print(",integer size="+token.getIntegerSize());
//            }
//        }
//        stream.println();
//    }
    
    static public void printExpressionTree(PrintStream stream,ExpressionNode node,int level)
    {
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode n=(BinaryOperatorNode)node;
            printIndent(stream,level);
            printExpressionNode(stream,n,n.getToken());
            printExpressionTree(stream,n.getLeftOperand(),level+1);
            printExpressionTree(stream,n.getRightOperand(),level+1);
        }
        else if (node instanceof ConstantNode)
        {
            ConstantNode n=(ConstantNode)node;
            printIndent(stream,level);
            printExpressionNode(stream,n,n.getToken());
        }
        else if (node instanceof KeywordNode)
        {
            KeywordNode n=(KeywordNode)node;
            printIndent(stream,level);
            printExpressionNode(stream,n,n.getToken());
            ArgumentNode argumentNode=n.getArgumentNode();
            if (argumentNode!=null)
            {
                printIndent(stream,level);
                stream.println(argumentNode.getOpenToken().getLiteral());
                for (ExpressionNode argument:argumentNode.getArguments())
                {
                    printExpressionTree(stream,argument,level+1);
                }
                printIndent(stream,level);
                stream.println(argumentNode.getCloseToken().getLiteral());
            }
        }
        else if (node instanceof IdentifierNode)
        {
            IdentifierNode n=(IdentifierNode)node;
            printIndent(stream,level);
            printExpressionNode(stream,n,n.getToken());
            ArgumentNode argumentNode=n.getArgumentNode();
            if (argumentNode!=null)
            {
                printIndent(stream,level);
                stream.println(argumentNode.getOpenToken().getLiteral());
                for (ExpressionNode argument:argumentNode.getArguments())
                {
                    printExpressionTree(stream,argument,level+1);
                }
                printIndent(stream,level);
                stream.println(argumentNode.getCloseToken().getLiteral());
            }
        }
        else if (node instanceof PrefixOperatorNode)
        {
            PrefixOperatorNode n=(PrefixOperatorNode)node;
            printIndent(stream,level);
            printExpressionNode(stream,n,n.getToken());
            printExpressionTree(stream,n.getOperand(),level+1);
        }
        else if (node instanceof PostfixOperatorNode)
        {
            PostfixOperatorNode n=(PostfixOperatorNode)node;
            printIndent(stream,level);
            printExpressionNode(stream,n,n.getToken());
            printExpressionTree(stream,n.getOperand(),level+1);
        }
        else
        {
            stream.println("Unhandled node:"+node.getClass().getSimpleName());
        }
               
    }
    
    static public void printExpressionTree(PrintStream stream,ExpressionNode root)
    {
        printExpressionTree(stream,root,0);
    }
    static public void printExpressionTree(ExpressionNode root)
    {
        printExpressionTree(System.out,root,0);
    }
    
//    public static void printSectionTokens(PrintStream stream,List<Section> sections,List<Token> tokens)
//    {
//        for (Section section:sections)
//        {
//            for (int i=0;i<section.getEnd()-section.getStart();i++)
//            {
//                printIndent(stream, i);
//                printLexeme(stream,tokens.get(i+section.getStart()));
//            }
//        }
//    }
    
//    public static void printSectionTokens(List<Section> sections,List<Token> tokens)
//    {
//        printSectionTokens(System.out,sections,tokens);
//    }

//    public static void printSectionLiterals(PrintStream stream,List<Section> sections,List<Token> tokens)
//    {
//        for (Section section:sections)
//        {
//            for (int i=0;i<section.getEnd()-section.getStart();i++)
//            {
//                stream.print(tokens.get(i+section.getStart()).getLiteral());
//                stream.print(' ');
//            }
//            stream.println();
//        }
//    }
//    
//    public static void printSectionLiterals(List<Section> sections,List<Token> tokens)
//    {
//        printSectionLiterals(System.out,sections,tokens);
//    }
    
    public static void printParseErrors(PrintStream stream,List<ParseError> errors)
    {
        for (ParseError error:errors)
        {
            stream.println(error.getMessage());
            if (error.getTokens()!=null)
            {
                for (Token token:error.getTokens())
                printTokenError(stream, token);
            }
        }
    }
    public static void printParseErrors(List<ParseError> errors)
    {
        printParseErrors(System.out,errors);
    }
}
