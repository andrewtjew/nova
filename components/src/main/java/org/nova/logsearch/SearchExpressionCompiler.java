package org.nova.logsearch;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.logging.Item;
import org.nova.logging.MultiThreadLogWriter;
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

public class SearchExpressionCompiler
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
    

    static public record Instruction(Code code,Object value)
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
    }

    static record ValueTypeInstruction(ValueType valueType,Instruction instruction)
    {
    }
    
    static public record CompilerError(String message,ExpressionNode node)
    {
    }
    
    final protected static boolean DEBUG=true;
    final protected static boolean DEBUG_PRINT_INSTRUCTIONS=true;
    static final String DEBUG_CATEGORY=SearchExpressionCompiler.class.getSimpleName();
    
    
    private List<Instruction> instructions=new ArrayList<>();
    private List<CompilerError> errors=new ArrayList<>();
    final HashMap<String,ValueTypeInstruction> keyInstructions;

    public SearchExpressionCompiler() throws Throwable
    {
        this.keyInstructions=new HashMap<>();
        this.keyInstructions.put("number",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_NUMBER)));
        this.keyInstructions.put("logLevel",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_LOG_LEVEL)));
        this.keyInstructions.put("category",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_CATEGORY)));
        this.keyInstructions.put("created",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_CREATED)));
        this.keyInstructions.put("message",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_MESSAGE)));
        this.keyInstructions.put("exception",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_EXCEPTION)));
        this.keyInstructions.put("stackTrace",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_STACK_TRACE)));
        this.keyInstructions.put("traceNumber",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_TRACE_NUMBER)));
        this.keyInstructions.put("traceCreated",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_TRACE_CREATED)));
        this.keyInstructions.put("traceCategory",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_TRACE_CATEGORY)));
        this.keyInstructions.put("duration",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_DURATION)));
        this.keyInstructions.put("wait",new ValueTypeInstruction(ValueType.INTEGER,new Instruction(Code.PUSH_WAIT)));
        this.keyInstructions.put("fromLink",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_FROM_LINK)));
        this.keyInstructions.put("toLink",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_TO_LINK)));
        this.keyInstructions.put("traceException",new ValueTypeInstruction(ValueType.STRING,new Instruction(Code.PUSH_TRACE_EXCEPTION)));
    }
    
    public SearchExpressionEvaluator compile(String expression) throws Throwable
    {
        this.instructions=new ArrayList<>();
        this.errors=new ArrayList<>();
        SearchExpressionParser parser=new SearchExpressionParser();
        var node=parser.parse(expression);
        compile(node);
        if (Debug.ENABLE && DEBUG && DEBUG_PRINT_INSTRUCTIONS)
        {
            Debugging.log(DEBUG_CATEGORY,"Start instructions ------------------------");
            for (Instruction instruction:this.instructions)
            {
                Debugging.log(DEBUG_CATEGORY,instruction.code+(instruction.value!=null?(":value="+instruction.value):""));
            }
            Debugging.log(DEBUG_CATEGORY,"End instructions ------------------------");
        }
        if (this.errors.size()>0)
        {
            return null;
        }
        return new SearchExpressionEvaluator(this.instructions.toArray(new Instruction[this.instructions.size()]));
    }
    
    public void reportError(ExpressionNode node,String message)
    {
        this.errors.add(new CompilerError(message,node));
        System.out.println("Error: "+message);
        if (node!=null)
        {
            for (Token token:node.getTokens())
            {
                ParsingUtils.printError(token);
            }
        }
    }
    
    public List<CompilerError> getErrors()
    {
        return this.errors;
    }

    ValueTypeInstruction generateBinaryInstruction(BinaryOperatorNode node,ValueType leftType,ValueType rightType) throws Exception
    {
        String operator=node.getOperator();
        if (leftType==null)
        {
            reportError(node,"Invalid left operand type for operator "+operator+".");
            return null;
        }
        if (rightType==null)
        {
            reportError(node,"Invalid right operand type for operator "+operator+".");
            return null;
        }
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
    
    private String getOneStringArgument(KeywordNode node) throws Exception
    {
        var argumentNode=node.getArgumentNode();
        if (argumentNode==null)
        {
            reportError(node,"Item must have exactly one argument.");
            return null;
        }
        if ("[".equals(argumentNode.getOpenToken().getLiteral())==false)
        {
            reportError(node,"Invalid argument specification. Valid example is ['hello'].");
            return null;
        }
        var arguments=argumentNode.getArguments();
        if (arguments.length!=1)
        {
            reportError(node,"Item must have exactly one argument.");
            return null;
        }
        var argument=arguments[0];
        if (argument instanceof ConstantNode==false)
        {
            reportError(node,"Item argument must be a string.");
            return null;
        }
        ConstantNode cn=(ConstantNode)argument;
        Token t=cn.getToken();
        if (t.getType()!=TokenType.STRING)
        {
            reportError(node,"Item argument must be a string.");
            return null;
        }
        String inner=t.getLiteral().substring(1,t.getLiteral().length()-1);
        if (inner.length()==0)
        {
            reportError(node,"Item argument must be a non-empty string.");
            return null;
        }
        return inner;
    }

    private ValueType compile(ExpressionNode node) throws Exception //post order, ArgumentNode arguments are added left to right and then node. 
    {
        if (node instanceof BinaryOperatorNode)
        {
            BinaryOperatorNode n=(BinaryOperatorNode)node;
            var rightType=compile(n.getRightOperand());
            var leftType=compile(n.getLeftOperand());
            var result=generateBinaryInstruction(n,leftType,rightType);
            if (result==null)
            {
                return null;
            }
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
            switch (value)
            {
                case "utc":
                {
                    String utc=getOneStringArgument(n);
                    try
                    {
                        var localDateTime=LocalDateTime.parse(utc);
                        long epochMilli=localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                        this.instructions.add(new Instruction(Code.PUSH_CONSTANT,epochMilli));
                        return ValueType.INTEGER;
                    }
                    catch (Exception e)
                    {
                        reportError(node,"Invalid utc argument. Must be in format yyyy-MM-ddTHH:mm:ss.SSS");
                        return null;
                    }
                }
                case "item":
                {
                    String key=getOneStringArgument(n);
                    if (key==null)
                    {
                        return null;
                    }
                    this.instructions.add(new Instruction(Code.PUSH_ITEM_KEY,key));
                    return ValueType.STRING;
                }
            }
            reportError(node,"Unhandled keyword:"+value+".");
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
            if (compile(n.getOperand())!=ValueType.BOOLEAN)
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
}
