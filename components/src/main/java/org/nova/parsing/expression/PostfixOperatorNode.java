package org.nova.parsing.expression;

import org.nova.lexing.Lexeme;

public class PostfixOperatorNode extends ExpressionNode
{
    final private ExpressionNode operand;
    final private Lexeme lexeme;
    public PostfixOperatorNode(Lexeme lexeme,ExpressionNode operand)
    {
        this.lexeme=lexeme;
        this.operand=operand;
    }
    public ExpressionNode getOperand()
    {
        return operand;
    }
    public Lexeme getLexeme()
    {
        return this.lexeme;
    }
    public boolean isOperator(String operator)
    {
        return this.lexeme.isOperator(operator);
    }
}