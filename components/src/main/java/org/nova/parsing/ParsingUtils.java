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
    static public void printError(Token token)
    {
        printError(System.out,token);
    }    
    static public void printError(PrintStream stream,Token token)
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
        stream.print("\u001B[31m"); //set color to red
        for (int i=0;i<token.getLiteral().length();i++)
        {
            stream.print('^');
        }
        stream.print("\u001B[0m"); //reset color
        stream.println();
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
    
    static public void printExpressionTree(PrintStream stream,ExpressionNode node,int level)
    {
        if (node==null)
        {
            return;
        }
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
    

    public static void printErrors(PrintStream stream,List<ErrorNode> errors)
    {
        for (var error:errors)
        {
            stream.println(error.getMessage());
            if (error.getToken()!=null)
            {
                printError(stream, error.getToken());
            }
            if (error.getSecondaryToken()!=null)
            {
                printError(stream, error.getSecondaryToken());
            }
        }
    }
    public static void printErrors(List<ErrorNode> errors)
    {
        printErrors(System.out,errors);
    }

    static public ExpressionNode[] toPostOrder(ExpressionNode node) throws Exception //post order, ArgumentNode arguments are added left to right and then node. 
    {
        ArrayList<ExpressionNode> list=new ArrayList<>();
        toPostOrder(node,list);
        return list.toArray(new ExpressionNode[list.size()]);
    }
    
    static public void toPostOrder(ExpressionNode node,List<ExpressionNode> list) throws Exception //post order, ArgumentNode arguments are added left to right and then node. 
    {
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode n=(BinaryOperatorNode)node;
            toPostOrder(n.getLeftOperand(),list);
            toPostOrder(n.getRightOperand(),list);
            list.add(node);
        }
        else if (node instanceof ConstantNode)
        {
            list.add(node);
        }
        else if (node instanceof KeywordNode)
        {
            KeywordNode n=(KeywordNode)node;
            ArgumentNode argumentNode=n.getArgumentNode();
            if (argumentNode!=null)
            {
                for (ExpressionNode argument:argumentNode.getArguments())
                {
                    toPostOrder(argument,list);
                }
            }
            list.add(node);
        }
        else if (node instanceof IdentifierNode)
        {
            IdentifierNode n=(IdentifierNode)node;
            ArgumentNode argumentNode=n.getArgumentNode();
            if (argumentNode!=null)
            {
                for (ExpressionNode argument:argumentNode.getArguments())
                {
                    toPostOrder(argument,list);
                }
            }
            list.add(node);
        }
        else if (node instanceof PrefixOperatorNode)
        {
            PrefixOperatorNode n=(PrefixOperatorNode)node;
            toPostOrder(n.getOperand(),list);
            list.add(node);
        }
        else if (node instanceof PostfixOperatorNode)
        {
            PostfixOperatorNode n=(PostfixOperatorNode)node;
            toPostOrder(n.getOperand(),list);
            list.add(node);
        }
        else
        {
            throw new Exception("Unhandled node:"+node.getClass().getSimpleName());
        }
               
    }
}
