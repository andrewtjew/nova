package org.nova.logsearch;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.logging.MultiThreadLogWriter;
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

public class LogSearcher
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
        ADD_INTEGER_NUMBER,
        ADD_INTEGER_STRING,

        ADD_NUMBER_INTEGER,
        ADD_NUMBER_NUMBER,
        ADD_NUMBER_STRING,

        ADD_STRING_INTEGER,
        ADD_STRING_NUMBER,
        ADD_STRING_STRING,
        
        SUBTRACT_INTEGER_INTEGER,
        SUBTRACT_INTEGER_NUMBER,
        
        SUBTRACT_NUMBER_INTEGER,
        SUBTRACT_NUMBER_NUMBER,
        
        MULTIPLY_INTEGER_INTEGER,
        MULTIPLY_INTEGER_NUMBER,
        MULTIPLY_NUMBER_INTEGER,
        MULTIPLY_NUMBER_NUMBER,
        
        DIVIDE_INTEGER_INTEGER,
        DIVIDE_INTEGER_NUMBER,
        DIVIDE_NUMBER_INTEGER,
        DIVIDE_NUMBER_NUMBER,
        
        EQUAL_INTEGER_INTEGER,
        EQUAL_INTEGER_NUMBER,
        EQUAL_NUMBER_INTEGER,
        EQUAL_NUMBER_NUMBER,
        EQUAL_STRING_STRING,
        EQUAL_BOOLEAN_BOOLEAN,
        
        GREATER_INTEGER_INTEGER,
        GREATER_INTEGER_NUMBER,
        GREATER_NUMBER_INTEGER,
        GREATER_NUMBER_NUMBER,
        GREATER_STRING_STRING,
                
        LESS_INTEGER_INTEGER,
        LESS_INTEGER_NUMBER,
        LESS_NUMBER_INTEGER,
        LESS_NUMBER_NUMBER,
        LESS_STRING_STRING,
        
        GREATER_EQUAL_INTEGER_INTEGER,
        GREATER_EQUAL_INTEGER_NUMBER,
        GREATER_EQUAL_NUMBER_INTEGER,
        GREATER_EQUAL_NUMBER_NUMBER,
        GREATER_EQUAL_STRING_STRING,
        
        LESS_EQUAL_INTEGER_INTEGER,
        LESS_EQUAL_INTEGER_NUMBER,
        LESS_EQUAL_NUMBER_INTEGER,
        LESS_EQUAL_NUMBER_NUMBER,
        LESS_EQUAL_STRING_STRING,
        
        NOT_EQUAL_INTEGER_INTEGER,
        NOT_EQUAL_INTEGER_NUMBER,
        NOT_EQUAL_NUMBER_INTEGER,
        NOT_EQUAL_NUMBER_NUMBER,
        NOT_EQUAL_STRING_STRING,
        NOT_EQUAL_BOOLEAN_BOOLEAN,
        
        AND,
        OR,
        NOT,
        CONTAINS,
        
        PUSH_NUMBER,
        PUSH_LOG_LEVEL,
        PUSH_CATEGORY,
        PUSH_CREATED,
        PUSH_MESSAGE,
        PUSH_EXCEPTION,
        PUSH_STACK_TRACE,
        PUSH_TRACE_NUMBER,
        PUSH_TRACE_CREATED,
        PUSH_TRACE_CATEGORY,
        PUSH_DURATION,
        PUSH_WAIT,
        PUSH_FROM_LINK,
        PUSH_TO_LINK,
        PUSH_TRACE_EXCEPTION,
        PUSH_TRACE_STACK_TRACE,
        
        PUSH_CONSTANT,
    }
    final protected static boolean DEBUG=true;
    final protected static boolean DEBUG_PRINT_STACK=true;
    final protected static boolean DEBUG_PRINT_INSTRUCTIONS=true;
    static final String DEBUG_CATEGORY=LogSearcher.class.getSimpleName();
    
    
    private List<Instruction> instructions=new ArrayList<>();
    
    static record ValueTypeInstruction(ValueType valueType,Instruction instruction)
    {
    }
    
    final HashMap<String,ValueTypeInstruction> keyInstructions;
    public LogSearcher(String expression) throws Throwable
    {
        SearchExpressionParser parser=new SearchExpressionParser();
        this.keyInstructions=new HashMap<>();
        this.keyInstructions.put("number",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_NUMBER)));
        this.keyInstructions.put("logLevel",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_LOG_LEVEL)));
        this.keyInstructions.put("category",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_CATEGORY)));
        this.keyInstructions.put("created",new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.PUSH_CREATED)));
        this.keyInstructions.put("message",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_MESSAGE)));
        this.keyInstructions.put("exception",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_EXCEPTION)));
        this.keyInstructions.put("stackTrace",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_STACK_TRACE)));
        this.keyInstructions.put("traceNumber",new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.PUSH_TRACE_NUMBER)));
        this.keyInstructions.put("traceCreated",new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.PUSH_TRACE_CREATED)));
        this.keyInstructions.put("traceCategory",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_TRACE_CATEGORY)));
        this.keyInstructions.put("duration",new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.PUSH_DURATION)));
        this.keyInstructions.put("wait",new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.PUSH_WAIT)));
        this.keyInstructions.put("fromLink",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_FROM_LINK)));
        this.keyInstructions.put("toLink",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_TO_LINK)));
        this.keyInstructions.put("traceException",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_TRACE_EXCEPTION)));
        var node=parser.parse(expression);
        generateInstruction(node);
        if (Debug.ENABLE && DEBUG && DEBUG_PRINT_INSTRUCTIONS)
        {
            Debugging.log(DEBUG_CATEGORY,"Start instructions ------------------------");
            for (Instruction instruction:this.instructions)
            {
                Debugging.log(DEBUG_CATEGORY,instruction.code+(instruction.value!=null?(":value="+instruction.value):""));
            }
            Debugging.log(DEBUG_CATEGORY,"End instructions ------------------------");
        }

    }
    
    public void reportError(ExpressionNode node,String message)
    {
        System.out.println("Error: message="+message+", node="+node.toString());
    }

    ValueTypeInstruction getBinaryInstruction(BinaryOperatorNode node,ValueType leftType,ValueType rightType) throws Exception
    {
        String operator=node.getOperator();
        switch (operator)
        {
            case "+":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.ADD_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.ADD_INTEGER_NUMBER));
                            case STRING: return new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.ADD_INTEGER_STRING));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.ADD_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.ADD_NUMBER_NUMBER));
                            case STRING: return new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.ADD_NUMBER_STRING));
                            default: break;
                        }
                        break;
                    case STRING:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.ADD_STRING_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.ADD_STRING_NUMBER));
                            case STRING: return new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.ADD_STRING_STRING));
                            default: break;
                        }
                        break;
                    default: break;
                }
                break;

            case "-":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.SUBTRACT_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.SUBTRACT_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.SUBTRACT_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.SUBTRACT_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    default: break;
                }
                break;

            case "*":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.MULTIPLY_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.MULTIPLY_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.MULTIPLY_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.MULTIPLY_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    default: break;
                }
                break;

            case "/":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.DIVIDE_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.DIVIDE_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.DIVIDE_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.NUMBER,new Instruction(Code.DIVIDE_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    default: break;
                }
                break;

            case "==":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.EQUAL_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.EQUAL_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.EQUAL_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.EQUAL_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    case STRING:
                        if (rightType==ValueType.STRING)
                        {
                            return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.EQUAL_STRING_STRING));
                        }
                        break;
                    case BOOLEAN:
                        if (rightType==ValueType.BOOLEAN)
                        {
                            return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.EQUAL_BOOLEAN_BOOLEAN));
                        }
                        break;
                    default: break;
                }
                break;

            case ">":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    case STRING:
                        if (rightType==ValueType.STRING)
                        {
                            return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_STRING_STRING));
                        }
                        break;
                    default: break;
                }
                break;

            case "<":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    case STRING:
                        if (rightType==ValueType.STRING)
                        {
                            return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_STRING_STRING));
                        }
                        break;
                    default: break;
                }
                break;

            case ">=":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_EQUAL_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_EQUAL_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_EQUAL_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_EQUAL_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    case STRING:
                        if (rightType==ValueType.STRING)
                        {
                            return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.GREATER_EQUAL_STRING_STRING));
                        }
                        break;
                    default: break;
                }
                break;

            case "<=":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_EQUAL_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_EQUAL_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_EQUAL_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_EQUAL_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    case STRING:
                        if (rightType==ValueType.STRING)
                        {
                            return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.LESS_EQUAL_STRING_STRING));
                        }
                        break;
                    default: break;
                }
                break;

            case "!=":
                switch (leftType)
                {
                    case INTEGER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.NOT_EQUAL_INTEGER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.NOT_EQUAL_INTEGER_NUMBER));
                            default: break;
                        }
                        break;
                    case NUMBER:
                        switch (rightType)
                        {
                            case INTEGER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.NOT_EQUAL_NUMBER_INTEGER));
                            case NUMBER: return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.NOT_EQUAL_NUMBER_NUMBER));
                            default: break;
                        }
                        break;
                    case STRING:
                        if (rightType==ValueType.STRING)
                        {
                            return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.NOT_EQUAL_STRING_STRING));
                        }
                        break;
                    case BOOLEAN:
                        if (rightType==ValueType.BOOLEAN)
                        {
                            return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.NOT_EQUAL_BOOLEAN_BOOLEAN));
                        }
                        break;
                    default: break;
                }
                break;

            case "and":
                return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.AND));

            case "or":
                return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.OR));

            case "not":
                return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.NOT));

            case "contains":
                return new ValueTypeInstruction(ValueType.BOOLEAN,new Instruction(Code.CONTAINS));

            default:
                break;
        }
        
        throw new Exception("Unhandled binary operator:"+operator+" for types "+leftType+" and "+rightType);
    }
    
    public ValueType generateInstruction(ExpressionNode node) throws Exception //post order, ArgumentNode arguments are added left to right and then node. 
    {
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode n=(BinaryOperatorNode)node;
            var rightType=generateInstruction(n.getRightOperand());
            var leftType=generateInstruction(n.getLeftOperand());
            var result=getBinaryInstruction(n,leftType,rightType);
            this.instructions.add(result.instruction());
            return result.valueType();
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
                String inner=value.substring(1,value.length()-1);
                this.instructions.add(new Instruction(Code.PUSH_CONSTANT,inner));
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
            var keyInstruction=this.keyInstructions.get(value);
            if (keyInstruction!=null)
            {
                this.instructions.add(keyInstruction.instruction);
                return keyInstruction.valueType;
            }
            reportError(node,"Unhandled keyword:"+value);
            return null;
        }
        else if (node instanceof IdentifierNode)
        {
            reportError(node,"Not valid search key.");
            return null;
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
    }
    
    public boolean evaluate(LogRecord logRecord) throws Exception
    {
        if (logRecord==null)
        {
            return false;
        }
        Stack<Object> stack=new Stack<>();
        TraceRecord traceRecord=logRecord.trace;
        for (Instruction instruction:this.instructions)
        {
            switch (instruction.code)
            {
                case PUSH_NUMBER:
                    stack.push(logRecord.number);
                    break;

                case PUSH_LOG_LEVEL:
                    stack.push(logRecord.logLevel);
                    break;

                case PUSH_CATEGORY:
                    stack.push(logRecord.category);
                    break;

                case PUSH_CREATED:
                    stack.push(logRecord.created!=null?logRecord.created.toInstant(ZoneOffset.UTC).toEpochMilli():null);
                    break;

                case PUSH_MESSAGE:
                    stack.push(logRecord.message);
                    break;

                case PUSH_EXCEPTION:
                    stack.push(logRecord.exception);
                    break;

                case PUSH_STACK_TRACE:
                    stack.push(logRecord.stackTrace);
                    break;

                case PUSH_TRACE_NUMBER:
                    stack.push(traceRecord!=null?traceRecord.number:null);
                    break;

                case PUSH_TRACE_CREATED:
                    stack.push(traceRecord!=null?(traceRecord.created!=null?traceRecord.created.toInstant(ZoneOffset.UTC).toEpochMilli():null):null);
                    break;

                case PUSH_TRACE_CATEGORY:
                    stack.push(traceRecord!=null?traceRecord.category:null);
                    break;

                case PUSH_DURATION:
                    stack.push(traceRecord!=null?traceRecord.duration:null);
                    break;

                case PUSH_WAIT:
                    stack.push(traceRecord!=null?traceRecord.wait:null);
                    break;

                case PUSH_FROM_LINK:
                    stack.push(traceRecord!=null?traceRecord.fromLink:null);
                    break;

                case PUSH_TO_LINK:
                    stack.push(traceRecord!=null?traceRecord.toLink:null);
                    break;

                case PUSH_TRACE_EXCEPTION:
                    stack.push(traceRecord!=null?traceRecord.exception:null);
                    break;
                // end of keyword instructions -----------------
                    
                case PUSH_CONSTANT:
                    stack.push(instruction.value);
                    break; 

                case ADD_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left+right);
                }
                    break;
                    
                case ADD_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left+right);
                }
                    break;
                    
                case ADD_INTEGER_STRING:
                {
                    long left=(long)stack.pop();
                    String right=(String)stack.pop();
                    if (right==null)
                    {
                        stack.push(Long.toString(left));
                    }
                    else
                    {
                        stack.push(left+right);
                    }
                }
                    break;
                case ADD_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left+right);
                }
                    break;
                case ADD_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left+right);
                }
                    break;
                case ADD_NUMBER_STRING:
                {
                    double left=(double)stack.pop();
                    String right=(String)stack.pop();
                    if (right==null)
                    {
                        stack.push(Double.toString(left));
                    }
                    else
                    {
                        stack.push(left+right);
                    }
                }
                    break;
                case ADD_STRING_INTEGER:
                {
                    String left=(String)stack.pop();
                    long right=(long)stack.pop();
                    if (left==null)
                    {
                        stack.push(Long.toString(right));
                    }
                    else
                    {
                        stack.push(left+right);
                    }
                }
                    break;
                case ADD_STRING_NUMBER:
                {
                    String left=(String)stack.pop();
                    double right=(double)stack.pop();
                    if (left==null)
                    {
                        stack.push(Double.toString(right));
                    }
                    else
                    {
                        stack.push(left+right);
                    }
                }
                    break;
                case ADD_STRING_STRING:
                {
                    String left=(String)stack.pop();
                    String right=(String)stack.pop();
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack.push(null);
                        }
                        else if (left==null)
                        {
                            stack.push(right);
                        }
                        else
                        {
                            stack.push(left);
                        }
                    }
                    else
                    {
                        stack.push(left+right);
                    }
                }
                    break;

                case AND:
                {
                    boolean left=(boolean)stack.pop();
                    boolean right=(boolean)stack.pop();
                    stack.push(left&&right);
                }
                    break;
                case CONTAINS:
                {
                    String left=(String)stack.pop();
                    String right=(String)stack.pop();
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack.push(true);
                        }
                        else if (left==null)
                        {
                            stack.push(false);
                        }
                        else
                        {
                            stack.push(true);
                        }
                        stack.push(null);
                    }
                    else
                    {
                        stack.push(left.contains(right));
                    }
                }
                    break;
                case DIVIDE_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left/right);
                }
                    break;
                case DIVIDE_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left/right);
                }
                    break;
                    
                case DIVIDE_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left/right);
                }
                    break;
                    
                case DIVIDE_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left/right);
                }
                    break;
                case EQUAL_BOOLEAN_BOOLEAN:
                {
                    boolean left=(boolean)stack.pop();
                    boolean right=(boolean)stack.pop();
                    stack.push(left==right);
                }
                    break;
                case EQUAL_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left==right);
                }
                    break;
                case EQUAL_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push((double)left==right);
                }
                    break;
                case EQUAL_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left==(double)right);
                }
                    break;
                case EQUAL_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left==right);
                }
                    break;
                case EQUAL_STRING_STRING:
                {
                    String left=(String)stack.pop();
                    String right=(String)stack.pop();
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack.push(true);
                        }
                        else
                        {
                            stack.push(false);
                        }
                    }
                    else
                    {
                        stack.push(left.equals(right));
                    }
                }
                    break;
                case GREATER_EQUAL_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left>=right);
                }
                    break;
                case GREATER_EQUAL_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push((double)left>=right);
                }
                    break;
                case GREATER_EQUAL_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left>=(double)right);
                }
                    break;
                case GREATER_EQUAL_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left>=right);
                }
                    break;
                case GREATER_EQUAL_STRING_STRING:
                {
                    String left=(String)stack.pop();
                    String right=(String)stack.pop();
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack.push(true);
                        }
                        else if (left==null)
                        {
                            stack.push(false);
                        }
                        else
                        {
                            stack.push(true);
                        }
                    }
                    else
                    {
                        stack.push(left.compareTo(right)>=0);
                    }
                }
                    break;
                case GREATER_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left>right);
                }
                    break;
                case GREATER_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push((double)left>right);
                }
                    break;
                case GREATER_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left>(double)right);
                }
                    break;
                case GREATER_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left>right);
                }
                    break;
                case GREATER_STRING_STRING:
                {
                    String left=(String)stack.pop();
                    String right=(String)stack.pop();
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack.push(false);
                        }
                        else if (left==null)
                        {
                            stack.push(false);
                        }
                        else
                        {
                            stack.push(true);
                        }
                    }
                    else
                    {
                        stack.push(left.compareTo(right)>0);
                    }
                }
                    break;
                case LESS_EQUAL_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left<=right);
                }
                    break;
                case LESS_EQUAL_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push((double)left<=right);
                }
                    break;
                case LESS_EQUAL_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left<=(double)right);
                }
                    break;
                case LESS_EQUAL_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left<=right);
                }
                    break;
                case LESS_EQUAL_STRING_STRING:
                {
                    String left=(String)stack.pop();
                    String right=(String)stack.pop();
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack.push(true);
                        }
                        else if (left==null)
                        {
                            stack.push(true);
                        }
                        else
                        {
                            stack.push(false);
                        }
                    }
                    else
                    {
                        stack.push(left.equalsIgnoreCase(right));
                    }
                }
                    break;
                case LESS_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left<right);
                }
                    break;
                case LESS_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push((double)left<right);
                }
                    break;
                case LESS_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left<(double)right);
                }
                    break;
                case LESS_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left<right);
                }
                    break;
                case LESS_STRING_STRING:
                {
                    String left=(String)stack.pop();
                    String right=(String)stack.pop();
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack.push(false);
                        }
                        else if (left==null)
                        {
                            stack.push(true);
                        }
                        else
                        {
                            stack.push(false);
                        }
                    }
                    else
                    {
                        stack.push(left.compareTo(right)<0);
                    }
                }
                    break;
                case MULTIPLY_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left*right);
                }
                    break;
                case MULTIPLY_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left*right);
                }
                    break;
                case MULTIPLY_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left*right);
                }
                    break;
                case MULTIPLY_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left*right);
                }
                    break;
                case NOT:
                {
                    boolean value=(boolean)stack.pop();
                    stack.push(!value);
                }
                    break;
                case NOT_EQUAL_BOOLEAN_BOOLEAN:
                {
                    boolean left=(boolean)stack.pop();
                    boolean right=(boolean)stack.pop();
                    stack.push(left!=right);
                }
                    break;
                case NOT_EQUAL_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left!=right);
                }
                    break;
                case NOT_EQUAL_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push((double)left!=right);
                }
                    break;
                case NOT_EQUAL_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left!=(double)right);
                }
                    break;
                case NOT_EQUAL_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left!=right);
                }
                    break;
                case NOT_EQUAL_STRING_STRING:
                {
                    String left=(String)stack.pop();
                    String right=(String)stack.pop();
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack.push(false);
                        }
                        else
                        {
                            stack.push(true);
                        }
                    }
                    else
                    {
                        var result=left.equalsIgnoreCase(right);
                        stack.push(!result);
                    }
                }
                    break;
                case OR:
                {
                    boolean left=(boolean)stack.pop();
                    boolean right=(boolean)stack.pop();
                    stack.push(left||right);
                }
                    break;
                case PUSH_TRACE_STACK_TRACE:
                {
                    stack.push(traceRecord!=null?traceRecord.stackTrace:null);
                }
                    break;
                case SUBTRACT_INTEGER_INTEGER:
                {
                    long left=(long)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left-right);
                }
                    break;
                case SUBTRACT_INTEGER_NUMBER:
                {
                    long left=(long)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left-right);
                }
                    break;
                case SUBTRACT_NUMBER_INTEGER:
                {
                    double left=(double)stack.pop();
                    long right=(long)stack.pop();
                    stack.push(left-right);
                }
                    break;
                case SUBTRACT_NUMBER_NUMBER:
                {
                    double left=(double)stack.pop();
                    double right=(double)stack.pop();
                    stack.push(left-right);
                }
                    break;
                default:
                    break;
                 
                    
            }
            if (Debug.ENABLE && DEBUG && DEBUG_PRINT_STACK)
            {
                Debugging.log(DEBUG_CATEGORY,"PC:"+instruction.code+(instruction.value!=null?(":value="+instruction.value):""));
                int i=0;
                for (var element:stack)
                {
                    Debugging.log(DEBUG_CATEGORY,i+":="+element);
                    i++;
                }
            }
        }
        if (stack.size()!=1)
        {
            throw new Exception("Evaluator bug: stack size="+stack.size());
        }
        var result=stack.pop();
        if (result==null)
        {
            return false;
        }
        if (!(result instanceof Boolean))
        {
            return false;
        }
        return (boolean)result;
    }
}
