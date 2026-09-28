package org.nova.logsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nova.logsearch.SearchDirective.Code;
import org.nova.logsearch.SearchDirective.ValueType;
import org.nova.parsing.ArgumentNode;
import org.nova.parsing.BinaryOperatorNode;
import org.nova.parsing.ConstantNode;
import org.nova.parsing.ErrorNode;
import org.nova.parsing.ExpressionNode;
import org.nova.parsing.IdentifierNode;
import org.nova.parsing.KeywordNode;
import org.nova.parsing.NumericType;
import org.nova.parsing.ParsingUtils;
import org.nova.parsing.PostfixOperatorNode;
import org.nova.parsing.PrefixOperatorNode;
import org.nova.parsing.Token;
import org.nova.parsing.TokenType;

public class SearchTreeGenerator
{
    static long fromBinary(String value)
    {
        return Long.parseLong(value.replace("0b","").replace("0B",""),2);
    } 

    static long fromHexadecimal(String value)
    {
        return Long.parseLong(value.replace("0x","").replace("0X",""),16);
    } 

    static long fromDecimal(String value)
    {
        return Long.parseLong(value);
    } 

    static long fromOctal(String value)
    {
        return Long.parseLong(value.substring(1),8);
    } 
    

    static record Instruction(Code code,Object value)
    {
        public Instruction(Code code)
        {
            this(code,null);
        }
    }
    static public enum ValueType
    {
        INTEGER,
        NUMBER,
        STRING,
        BOOLEAN,
        UNDEFINED,
    }
    
    static public enum Code
    {
        ADD_INTEGER_INTEGER,
        ADD_INTEGER_FLOAT,
        ADD_INTEGER_STRING,

        ADD_FLOAT_INTEGER,
        ADD_FLOAT_FLOAT,
        ADD_FLOAT_STRING,

        ADD_STRING_INTEGER,
        ADD_STRING_FLOAT,
        ADD_STRING_STRING,
        
        SUBTRACT_INTEGER_INTEGER,
        SUBTRACT_INTEGER_FLOAT,
        
        SUBTRACT_FLOAT_INTEGER,
        SUBTRACT_FLOAT_FLOAT,
        
        MULTIPLY_INTEGER_INTEGER,
        MULTIPLY_INTEGER_FLOAT,
        MULTIPLY_FLOAT_INTEGER,
        MULTIPLY_FLOAT_FLOAT,
        
        DIVIDE_INTEGER_INTEGER,
        DIVIDE_INTEGER_FLOAT,
        DIVIDE_FLOAT_INTEGER,
        DIVIDE_FLOAT_FLOAT,
        
        EQUAL_INTEGER_INTEGER,
        EQUAL_INTEGER_FLOAT,
        EQUAL_FLOAT_INTEGER,
        EQUAL_FLOAT_FLOAT,
        EQUAL_STRING_STRING,
        EQUAL_BOOLEAN_BOOLEAN,
        
        GREATER_INTEGER_INTEGER,
        GREATER_INTEGER_FLOAT,
        GREATER_FLOAT_INTEGER,
        GREATER_FLOAT_FLOAT,
        GREATER_STRING_STRING,
                
        LESS_INTEGER_INTEGER,
        LESS_INTEGER_FLOAT,
        LESS_FLOAT_INTEGER,
        LESS_FLOAT_FLOAT,
        LESS_STRING_STRING,
        
        GREATER_EQUAL_INTEGER_INTEGER,
        GREATER_EQUAL_INTEGER_FLOAT,
        GREATER_EQUAL_FLOAT_INTEGER,
        GREATER_EQUAL_FLOAT_FLOAT,
        GREATER_EQUAL_STRING_STRING,
        
        LESS_EQUAL_INTEGER_INTEGER,
        LESS_EQUAL_INTEGER_FLOAT,
        LESS_EQUAL_FLOAT_INTEGER,
        LESS_EQUAL_FLOAT_FLOAT,
        LESS_EQUAL_STRING_STRING,
        
        NOT_EQUAL_INTEGER_INTEGER,
        NOT_EQUAL_INTEGER_FLOAT,
        NOT_EQUAL_FLOAT_INTEGER,
        NOT_EQUAL_FLOAT_FLOAT,
        NOT_EQUAL_STRING_STRING,
        NOT_EQUAL_BOOLEAN_BOOLEAN,
        
        AND,
        OR,
        NOT,
        CONTAINS,
        
        PUSH_CONSTANT,
        PUSH_NAME,
    }
    
    List<Instruction> instructions=new ArrayList<>();
    
    public SearchTreeGenerator(String expression) throws Throwable
    {
        SearchExpressionParser parser=new SearchExpressionParser();
        var node=parser.parse(expression);
        var nodes=ParsingUtils.toPostOrder(node);
        generateInstruction(nodes);
    }
    
    public void reportError(ExpressionNode node,String message)
    {
        System.out.println("Error:"+message);
    }

    Instruction getBinaryInstruction(BinaryOperatorNode node,ValueType leftType,ValueType rightType) throws Exception
    {
        String operator=node.getOperator();
        if ("+".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.ADD_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.ADD_INTEGER_FLOAT);
                }
                else if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.ADD_INTEGER_STRING);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.ADD_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.ADD_FLOAT_FLOAT);
                }
                else if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.ADD_FLOAT_STRING);
                }
            }
            else if (leftType==ValueType.STRING)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.ADD_STRING_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.ADD_STRING_FLOAT);
                }
                else if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.ADD_STRING_STRING);
                }
            }
        }
        else if ("-".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.SUBTRACT_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.SUBTRACT_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.SUBTRACT_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.SUBTRACT_FLOAT_FLOAT);
                }
            }
        }
        else if ("*".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.MULTIPLY_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.MULTIPLY_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.MULTIPLY_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.MULTIPLY_FLOAT_FLOAT);
                }
            }
        }
        else if ("/".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.DIVIDE_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.DIVIDE_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.DIVIDE_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.DIVIDE_FLOAT_FLOAT);
                }
            }
        }
        else if ("==".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.EQUAL_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.EQUAL_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.EQUAL_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.EQUAL_FLOAT_FLOAT);
                }
            }
            else if (leftType==ValueType.STRING)
            {
                if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.EQUAL_STRING_STRING);
                }
            }
            else if (leftType==ValueType.BOOLEAN)
            {
                if (rightType==ValueType.BOOLEAN)
                {
                    return new Instruction(Code.EQUAL_BOOLEAN_BOOLEAN);
                }
            }
        }
        else if (">".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.GREATER_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.GREATER_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.GREATER_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.GREATER_FLOAT_FLOAT);
                }
            }
            else if (leftType==ValueType.STRING)
            {
                if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.GREATER_STRING_STRING);
                }
            }
        }
        else if ("<".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.LESS_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.LESS_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.LESS_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.LESS_FLOAT_FLOAT);
                }
            }
            else if (leftType==ValueType.STRING)
            {
                if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.LESS_STRING_STRING);
                }
            }
        }
        else if (">=".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.GREATER_EQUAL_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.GREATER_EQUAL_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.GREATER_EQUAL_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.GREATER_EQUAL_FLOAT_FLOAT);
                }
            }
            else if (leftType==ValueType.STRING)
            {
                if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.GREATER_EQUAL_STRING_STRING);
                }
            }
        }
        else if ("<=".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.LESS_EQUAL_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.LESS_EQUAL_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.LESS_EQUAL_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.LESS_EQUAL_FLOAT_FLOAT);
                }
            }
            else if (leftType==ValueType.STRING)
            {
                if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.LESS_EQUAL_STRING_STRING);
                }
            }
        }
        else if ("!=".equals(operator))
        {
            if (leftType==ValueType.INTEGER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.NOT_EQUAL_INTEGER_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.NOT_EQUAL_INTEGER_FLOAT);
                }
            }
            else if (leftType==ValueType.NUMBER)
            {
                if (rightType==ValueType.INTEGER)
                {
                    return new Instruction(Code.NOT_EQUAL_FLOAT_INTEGER);
                }
                else if (rightType==ValueType.NUMBER)
                {
                    return new Instruction(Code.NOT_EQUAL_FLOAT_FLOAT);
                }
            }
            else if (leftType==ValueType.STRING)
            {
                if (rightType==ValueType.STRING)
                {
                    return new Instruction(Code.NOT_EQUAL_STRING_STRING);
                }
            }
            else if (leftType==ValueType.BOOLEAN)
            {
                if (rightType==ValueType.BOOLEAN)
                {
                    return new Instruction(Code.NOT_EQUAL_BOOLEAN_BOOLEAN);
                }
            }
        }
        else if ("and".equals(operator))
        {
            return new Instruction(Code.AND);
        }
        else if ("or".equals(operator))
        {
            return new Instruction(Code.OR);
        }
        else if ("not".equals(operator))
        {
            return new Instruction(Code.NOT);
        }
        else if ("contains".equals(operator))
        {
            return new Instruction(Code.CONTAINS);
        }
        
        throw new Exception("Unhandled binary operator:"+operator+" for types "+leftType+" and "+rightType);
    }
    
    public ValueType generateInstruction(ExpressionNode node) throws Exception //post order, ArgumentNode arguments are added left to right and then node. 
    {
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode n=(BinaryOperatorNode)node;
            var leftType=generateInstruction(n.getLeftOperand());
            var rightType=generateInstruction(n.getRightOperand());
            this.instructions.add(getBinaryInstruction(n,leftType,rightType));
            return ValueType.BOOLEAN; 
        }
        else if (node instanceof ConstantNode)
        {
            ConstantNode n=(ConstantNode)node;
            Token token=n.getToken();
            String value=token.getLiteral();
            if (token.getType()==TokenType.NUMBER)
            {
                NumericType numericType=token.getNumericType();
                Object constant=null;
                if (numericType==NumericType.INTEGER)
                {
                    constant=fromDecimal(value);
                }
                else if (numericType==NumericType.BINARY_INTEGER)
                {
                    constant=fromBinary(value);
                }
                else if (numericType==NumericType.HEXADECIMAL_INTEGER)
                {
                    constant=fromHexadecimal(value);
                }
                else if (numericType==NumericType.OCTAL_INTEGER)
                {
                    constant=fromOctal(value);
                }
                else
                {
                    reportError(node,"Unhandled numeric type:"+numericType);
                    return null;
                }
                this.instructions.add(new Instruction(Code.PUSH_CONSTANT,constant));
                return ValueType.INTEGER;
            }
            else if (token.getType()==TokenType.STRING)
            {
                this.instructions.add(new Instruction(Code.PUSH_CONSTANT,value.translateEscapes()));
                return ValueType.STRING;
            }
            else if (token.getType()==TokenType.NUMBER)
            {
                this.instructions.add(new Instruction(Code.PUSH_CONSTANT,Double.parseDouble(value)));
                return ValueType.NUMBER;
            }
            else
            {
                reportError(node,"Unhandled constant type:"+token.getType());
                return null;
            }
        }
        else if (node instanceof KeywordNode)
        {
            KeywordNode n=(KeywordNode)node;
            Token token=n.getToken();
            String value=token.getLiteral();
            if ("number".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"number"));
                return ValueType.INTEGER;
            }
            else if ("category".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"category"));
                return ValueType.STRING;
            }
            else if ("created".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"created"));
                return ValueType.NUMBER;
            }
            else if ("message".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"message"));
                return ValueType.STRING;
            }
            else if ("exception".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"exception"));
                return ValueType.STRING;
            }
            else if ("traceNumber".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"trace"));
                return ValueType.NUMBER;
            }
            else if ("traceCreated".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"trace"));
                return ValueType.NUMBER;
            }
            else if ("traceCategory".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"trace"));
                return ValueType.STRING;
            }
            else if ("duration".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"duration"));
                return ValueType.NUMBER;
            }
            else if ("wait".equals(value))
            {
                this.instructions.add(new Instruction(Code.PUSH_NAME,"wait"));
                return ValueType.NUMBER;
            }
            else
            {
                reportError(node,"Unhandled keyword:"+value);
                return null;
            }
        }
        else if (node instanceof IdentifierNode)
        {
            reportError(node,"Not valid search key.");
        }
        else if (node instanceof PrefixOperatorNode)
        {
            PrefixOperatorNode n=(PrefixOperatorNode)node;
            if (generateInstruction(n.getOperand())!=ValueType.BOOLEAN)
            {
                reportError(node,"Operand must be boolean.");
                return null;
            }
            this.instructions.add(new Instruction(Code.NOT));
            return ValueType.BOOLEAN;
        }
        else
        {
            reportError(node,"Invalid expression.");
            return null;
        }
        reportError(node,"Invalid expression.");
        return null;
    }
    
    public boolean isMatch(LogRecord record)
    {
        Stack<Object> stack=new Stack<>();
        for (Instruction instruction:this.instructions)
        {
            if (instruction.code==Code.PUSH_CONSTANT)
            {
                stack.push(instruction.value);
            }
            else if (instruction.code==Code.PUSH_NAME)
            {
                
            }
        }
        return true;
    }
}
