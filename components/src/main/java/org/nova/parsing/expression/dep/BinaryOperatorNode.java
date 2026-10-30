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

import org.nova.lexing.dep.Lexeme;

public class BinaryOperatorNode extends ExpressionNode
{
    final private ExpressionNode leftOperand;
    final private ExpressionNode rightOperand;
    final private Lexeme lexeme;
    public BinaryOperatorNode(Lexeme lexeme,ExpressionNode leftOperand,ExpressionNode rightOperand)
    {
        this.lexeme=lexeme;
        this.leftOperand=leftOperand;
        this.rightOperand=rightOperand;
    }
    public ExpressionNode getLeftOperand()
    {
        return leftOperand;
    }
    public ExpressionNode getRightOperand()
    {
        return rightOperand;
    }
    public Lexeme getLexeme()
    {
        return this.lexeme;
    }
    
    public boolean isOperator(String operator)
    {
        return this.lexeme.isOperator(operator);
    }
    public String getOperator()
    {
        return (String)this.lexeme.getValue();
    }
}
