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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.nova.lexing.dep.Lexeme;
import org.nova.lexing.dep.LexingUtils;
import org.nova.parsing.section.dep.Section;

public class ParsingUtils
{
    private static void getLexemes(ExpressionNode node,ArrayList<Lexeme> list)
    {
        if (node==null)
        {
            return;
        }
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode n=(BinaryOperatorNode)node;
            getLexemes(n.getLeftOperand(), list);
            list.add(n.getLexeme());
            getLexemes(n.getRightOperand(),list);
            return;
        }
        else if (node instanceof PrefixOperatorNode)
        {
            PrefixOperatorNode n=(PrefixOperatorNode)node;
            list.add(n.getLexeme());
            getLexemes(n.getOperand(),list);
            return;
        }
        else if (node instanceof PostfixOperatorNode)
        {
            PostfixOperatorNode n=(PostfixOperatorNode)node;
            list.add(n.getLexeme());
            getLexemes(n.getOperand(),list);
            return;
        }
        else if (node instanceof ConstantNode)
        {
            ConstantNode n=(ConstantNode)node;
            list.add(n.getLexeme());
        }
        else if (node instanceof KeywordNode)
        {
            KeywordNode n=(KeywordNode)node;
            list.add(n.getLexeme());
        }
        else if (node instanceof IdentifierNode)
        {
            IdentifierNode n=(IdentifierNode)node;
            list.add(n.getLexeme());
        }
        else if (node instanceof ArgumentNode)
        {
            ArgumentNode n=(ArgumentNode)node;
            list.add(n.getOpenLexeme());
            getLexemes(n.getExpressionNode(),list);
            list.add(n.getCloseLexeme());
        }
        else if (node instanceof ErrorNode)
        {
        }
        else
        {
           throw new RuntimeException();
        }
    }

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
    
    public static Lexeme[] getLexemes(ExpressionNode node)
    {
        ArrayList<Lexeme> list=new ArrayList<>();
        getLexemes(node,list);
        return list.toArray(new Lexeme[list.size()]);
    }   
    
    static public List<ExpressionNode> collapseToListSeperatedByOperator(ExpressionNode root,String operator)
    {
        ArrayList<ExpressionNode> list=new ArrayList<>();
        collapseToListSeperatedByOperator(root, operator,list);
        return list;
    }

    static public List<ExpressionNode> collapseToList(ExpressionNode root)
    {
        return collapseToListSeperatedByOperator(root, ",");
    }
    
    static public void printParseException(PrintStream stream,ParseError exception)
    {
        stream.println(exception.getMessage());
        for (Lexeme lexeme:exception.getLexemes())
        {
            LexingUtils.printLexemeError(stream, lexeme);
        }
    }    
    
    static public void printParseException(ParseError exception)
    {
        printParseException(System.out,exception);
    }    

    static String INDENT="  ";
    
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

    static void printLexeme(PrintStream stream,ExpressionNode node,Lexeme lexeme)
    {
        stream.print(node.getClass().getSimpleName()+":token="+lexeme.getToken()+",literal="+lexeme.getLiteral()+",value="+lexeme.getValue());
        if (lexeme.getModifier()!=null)
        {
            stream.print(",modifier="+lexeme.getModifier());
        }
        stream.println();
    }

    static void printLexeme(PrintStream stream,Lexeme lexeme)
    {
        stream.print("token="+lexeme.getToken()+",literal="+lexeme.getLiteral()+",value="+lexeme.getValue());
        if (lexeme.getModifier()!=null)
        {
            stream.print(",modifier="+lexeme.getModifier());
        }
        stream.println();
    }
    
    static public void printExpressionTree(PrintStream stream,ExpressionNode node,int level)
    {
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode n=(BinaryOperatorNode)node;
            printIndent(stream,level);
            printLexeme(stream,n,n.getLexeme());
            printExpressionTree(stream,n.getLeftOperand(),level+1);
            printExpressionTree(stream,n.getRightOperand(),level+1);
        }
        else if (node instanceof ConstantNode)
        {
            ConstantNode n=(ConstantNode)node;
            printIndent(stream,level);
            printLexeme(stream,n,n.getLexeme());
        }
        else if (node instanceof KeywordNode)
        {
            KeywordNode n=(KeywordNode)node;
            printIndent(stream,level);
            printLexeme(stream,n,n.getLexeme());
        }
        else if (node instanceof IdentifierNode)
        {
            IdentifierNode n=(IdentifierNode)node;
            printIndent(stream,level);
            printLexeme(stream,n,n.getLexeme());
            ArgumentNode argumentNode=n.getArgumentNode();
            if (argumentNode!=null)
            {
                printIndent(stream,level);
                stream.println(argumentNode.getOpenLexeme().getValue());
                printExpressionTree(stream,argumentNode.getExpressionNode(),level+1);
                printIndent(stream,level);
                stream.println(argumentNode.getCloseLexeme().getValue());
            }
        }
        else if (node instanceof PrefixOperatorNode)
        {
            PrefixOperatorNode n=(PrefixOperatorNode)node;
            printIndent(stream,level);
            printLexeme(stream,n,n.getLexeme());
            printExpressionTree(stream,n.getOperand(),level+1);
        }
        else if (node instanceof PostfixOperatorNode)
        {
            PostfixOperatorNode n=(PostfixOperatorNode)node;
            printIndent(stream,level);
            printLexeme(stream,n,n.getLexeme());
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
    
    public static void printSectionLexemes(PrintStream stream,List<Section> sections,List<Lexeme> lexemes)
    {
        for (Section section:sections)
        {
            for (int i=0;i<section.getEnd()-section.getStart();i++)
            {
                printIndent(stream, i);
                printLexeme(stream,lexemes.get(i+section.getStart()));
            }
        }
    }
    
    public static void printSectionLexemes(List<Section> sections,List<Lexeme> lexemes)
    {
        printSectionLexemes(System.out,sections,lexemes);
    }

    public static void printSectionLiterals(PrintStream stream,List<Section> sections,List<Lexeme> lexemes)
    {
        for (Section section:sections)
        {
            for (int i=0;i<section.getEnd()-section.getStart();i++)
            {
                stream.print(lexemes.get(i+section.getStart()).getLiteral());
                stream.print(' ');
            }
            stream.println();
        }
    }
    
    public static void printSectionLiterals(List<Section> sections,List<Lexeme> lexemes)
    {
        printSectionLiterals(System.out,sections,lexemes);
    }
    
    public static void printParseErrors(PrintStream stream,List<ParseError> errors)
    {
        for (ParseError error:errors)
        {
            stream.println(error.getMessage());
            if (error.getLexemes()!=null)
            {
                for (Lexeme lexeme:error.getLexemes())
                LexingUtils.printLexemeError(stream, lexeme);
            }
        }
    }
    public static void printParseErrors(List<ParseError> errors)
    {
        printParseErrors(System.out,errors);
    }
}
