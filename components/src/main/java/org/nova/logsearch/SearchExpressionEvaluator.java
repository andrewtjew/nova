package org.nova.logsearch;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.logging.Item;
import org.nova.logsearch.SearchExpressionCompiler.Instruction;

public class SearchExpressionEvaluator
{
    final protected static boolean DEBUG=false;
    final protected static boolean DEBUG_PRINT_STACK=true;
    final protected static boolean DEBUG_PRINT_INSTRUCTIONS=true;
    static final String DEBUG_CATEGORY=SearchExpressionEvaluator.class.getSimpleName();

    private Instruction[] instructions;
    public SearchExpressionEvaluator(Instruction[] instructions)
    {
        this.instructions=instructions;
    }
    public boolean evaluate(LogRecord logRecord) throws Exception
    {
        if (logRecord==null)
        {
            return false;
        }
        Stack<Object> stack=new Stack<>();
        TraceRecord traceRecord=logRecord.traceRecord;
        for (Instruction instruction:this.instructions)
        {
            switch (instruction.code())
            {
                case PUSH_NUMBER:
                    stack.push(logRecord.number);
                    break;

                case PUSH_LOG_LEVEL:
                    stack.push(logRecord.level);
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

                case PUSH_TRACE_STACK_TRACE:
                    stack.push(traceRecord!=null?traceRecord.stackTrace:null);
                    break;
                    
                // start of constant instructions -----------------
                case PUSH_CONSTANT:
                    stack.push(instruction.value());
                    break; 
                    
                case PUSH_ITEM_KEY:
                {
                    String key=(String)instruction.value();
                    String value=null;
                    if (logRecord.items!=null)
                    {
                        for (Item item:logRecord.items)
                        {
                            if (key.equals(item.getName()))
                            {
                                value=item.getValue();
                                break;
                            }
                        }
                    }
                    stack.push(value);
                }
                break;
                    
                // start binary operator instructions -----------------    
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
                Debugging.log(DEBUG_CATEGORY,"PC:"+instruction.code()+(instruction.value()!=null?(":value="+instruction.value()):""));
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
            return false;
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
