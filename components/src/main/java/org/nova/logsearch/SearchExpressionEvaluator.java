package org.nova.logsearch;

import java.time.ZoneOffset;

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
        Object[] stack=new Object[this.instructions.length+1];
        int sp=0;
        TraceRecord traceRecord=logRecord.traceRecord;
        for (Instruction instruction:this.instructions)
        {
            switch (instruction.code())
            {
                case PUSH_NUMBER:
                    stack[sp++]=logRecord.number;
                    break;

                case PUSH_LEVEL:
                    stack[sp++]=logRecord.level.toString();
                    break;

                case PUSH_CATEGORY:
                    stack[sp++]=logRecord.category;
                    break;

                case PUSH_CREATED:
                    stack[sp++]=logRecord.created!=null?logRecord.created.toInstant(ZoneOffset.UTC).toEpochMilli():null;
                    break;

                case PUSH_MESSAGE:
                    stack[sp++]=logRecord.message;
                    break;

                case PUSH_EXCEPTION:
                    stack[sp++]=logRecord.exception;
                    break;

                case PUSH_STACK_TRACE:
                    stack[sp++]=logRecord.stackTrace;
                    break;

                case PUSH_TRACE_NUMBER:
                    stack[sp++]=traceRecord!=null?traceRecord.number:null;
                    break;

                case PUSH_TRACE_CREATED:
                    stack[sp++]=traceRecord!=null?(traceRecord.created!=null?traceRecord.created.toInstant(ZoneOffset.UTC).toEpochMilli():null):null;
                    break;

                case PUSH_TRACE_CATEGORY:
                    stack[sp++]=traceRecord!=null?traceRecord.category:null;
                    break;

                case PUSH_DURATION:
                    stack[sp++]=traceRecord!=null?traceRecord.duration:null;
                    break;

                case PUSH_WAIT:
                    stack[sp++]=traceRecord!=null?traceRecord.wait:null;
                    break;

                case PUSH_FROM_LINK:
                    stack[sp++]=traceRecord!=null?traceRecord.fromLink:null;
                    break;

                case PUSH_TO_LINK:
                    stack[sp++]=traceRecord!=null?traceRecord.toLink:null;
                    break;

                case PUSH_TRACE_EXCEPTION:
                    stack[sp++]=traceRecord!=null?traceRecord.exception:null;
                    break;

                case PUSH_TRACE_STACK_TRACE:
                    stack[sp++]=traceRecord!=null?traceRecord.stackTrace:null;
                    break;
                    
                case PUSH_DETAILS:
                    stack[sp++]=traceRecord!=null?traceRecord.details:null;
                    break;
                    
                // start of constant instructions -----------------
                case PUSH_CONSTANT:
                    stack[sp++]=instruction.value();
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
                    stack[sp++]=value;
                }
                break;
                    
                // start binary operator instructions -----------------    
                case ADD_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left+right;
                }
                    break;
                    
                case ADD_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left+right;
                }
                    break;
                    
                case ADD_INTEGER_STRING:
                {
                    long left=(long)stack[--sp];
                    String right=(String)stack[--sp];
                    if (right==null)
                    {
                        stack[sp++]=Long.toString(left);
                    }
                    else
                    {
                        stack[sp++]=left+right;
                    }
                }
                    break;
                case ADD_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left+right;
                }
                    break;
                case ADD_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left+right;
                }
                    break;
                case ADD_NUMBER_STRING:
                {
                    double left=(double)stack[--sp];
                    String right=(String)stack[--sp];
                    if (right==null)
                    {
                        stack[sp++]=Double.toString(left);
                    }
                    else
                    {
                        stack[sp++]=left+right;
                    }
                }
                    break;
                case ADD_STRING_INTEGER:
                {
                    String left=(String)stack[--sp];
                    long right=(long)stack[--sp];
                    if (left==null)
                    {
                        stack[sp++]=Long.toString(right);
                    }
                    else
                    {
                        stack[sp++]=left+right;
                    }
                }
                    break;
                case ADD_STRING_NUMBER:
                {
                    String left=(String)stack[--sp];
                    double right=(double)stack[--sp];
                    if (left==null)
                    {
                        stack[sp++]=Double.toString(right);
                    }
                    else
                    {
                        stack[sp++]=left+right;
                    }
                }
                    break;
                case ADD_STRING_STRING:
                {
                    String left=(String)stack[--sp];
                    String right=(String)stack[--sp];
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack[sp++]=null;
                        }
                        else if (left==null)
                        {
                            stack[sp++]=right;
                        }
                        else
                        {
                            stack[sp++]=left;
                        }
                    }
                    else
                    {
                        stack[sp++]=left+right;
                    }
                }
                    break;

                case AND:
                {
                    boolean left=(boolean)stack[--sp];
                    boolean right=(boolean)stack[--sp];
                    stack[sp++]=left&&right;
                }
                    break;
                case CONTAINS:
                {
                    String left=(String)stack[--sp];
                    String right=(String)stack[--sp];
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack[sp++]=true;
                        }
                        else if (left==null)
                        {
                            stack[sp++]=false;
                        }
                        else
                        {
                            stack[sp++]=true;
                        }
                        stack[sp++]=null;
                    }
                    else
                    {
                        stack[sp++]=left.contains(right);
                    }
                }
                    break;
                case DIVIDE_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left/right;
                }
                    break;
                case DIVIDE_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left/right;
                }
                    break;
                    
                case DIVIDE_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left/right;
                }
                    break;
                    
                case DIVIDE_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left/right;
                }
                    break;
                case EQUAL_BOOLEAN_BOOLEAN:
                {
                    boolean left=(boolean)stack[--sp];
                    boolean right=(boolean)stack[--sp];
                    stack[sp++]=left==right;
                }
                    break;
                case EQUAL_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left==right;
                }
                    break;
                case EQUAL_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=(double)left==right;
                }
                    break;
                case EQUAL_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left==(double)right;
                }
                    break;
                case EQUAL_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left==right;
                }
                    break;
                case EQUAL_STRING_STRING:
                {
                    String left=(String)stack[--sp];
                    String right=(String)stack[--sp];
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack[sp++]=true;
                        }
                        else
                        {
                            stack[sp++]=false;
                        }
                    }
                    else
                    {
                        stack[sp++]=left.equals(right);
                    }
                }
                    break;
                case GREATER_EQUAL_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left>=right;
                }
                    break;
                case GREATER_EQUAL_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=(double)left>=right;
                }
                    break;
                case GREATER_EQUAL_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left>=(double)right;
                }
                    break;
                case GREATER_EQUAL_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left>=right;
                }
                    break;
                case GREATER_EQUAL_STRING_STRING:
                {
                    String left=(String)stack[--sp];
                    String right=(String)stack[--sp];
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack[sp++]=true;
                        }
                        else if (left==null)
                        {
                            stack[sp++]=false;
                        }
                        else
                        {
                            stack[sp++]=true;
                        }
                    }
                    else
                    {
                        stack[sp++]=left.compareTo(right)>=0;
                    }
                }
                    break;
                case GREATER_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left>right;
                }
                    break;
                case GREATER_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=(double)left>right;
                }
                    break;
                case GREATER_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left>(double)right;
                }
                    break;
                case GREATER_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left>right;
                }
                    break;
                case GREATER_STRING_STRING:
                {
                    String left=(String)stack[--sp];
                    String right=(String)stack[--sp];
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack[sp++]=false;
                        }
                        else if (left==null)
                        {
                            stack[sp++]=false;
                        }
                        else
                        {
                            stack[sp++]=true;
                        }
                    }
                    else
                    {
                        stack[sp++]=left.compareTo(right)>0;
                    }
                }
                    break;
                case LESS_EQUAL_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left<=right;
                }
                    break;
                case LESS_EQUAL_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=(double)left<=right;
                }
                    break;
                case LESS_EQUAL_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left<=(double)right;
                }
                    break;
                case LESS_EQUAL_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left<=right;
                }
                    break;
                case LESS_EQUAL_STRING_STRING:
                {
                    String left=(String)stack[--sp];
                    String right=(String)stack[--sp];
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack[sp++]=true;
                        }
                        else if (left==null)
                        {
                            stack[sp++]=true;
                        }
                        else
                        {
                            stack[sp++]=false;
                        }
                    }
                    else
                    {
                        stack[sp++]=left.equalsIgnoreCase(right);
                    }
                }
                    break;
                case LESS_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left<right;
                }
                    break;
                case LESS_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=(double)left<right;
                }
                    break;
                case LESS_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left<(double)right;
                }
                    break;
                case LESS_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left<right;
                }
                    break;
                case LESS_STRING_STRING:
                {
                    String left=(String)stack[--sp];
                    String right=(String)stack[--sp];
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack[sp++]=false;
                        }
                        else if (left==null)
                        {
                            stack[sp++]=true;
                        }
                        else
                        {
                            stack[sp++]=false;
                        }
                    }
                    else
                    {
                        stack[sp++]=left.compareTo(right)<0;
                    }
                }
                    break;
                case MULTIPLY_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left*right;
                }
                    break;
                case MULTIPLY_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left*right;
                }
                    break;
                case MULTIPLY_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left*right;
                }
                    break;
                case MULTIPLY_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left*right;
                }
                    break;
                case NOT:
                {
                    boolean value=(boolean)stack[--sp];
                    stack[sp++]=!value;
                }
                    break;
                case NOT_EQUAL_BOOLEAN_BOOLEAN:
                {
                    boolean left=(boolean)stack[--sp];
                    boolean right=(boolean)stack[--sp];
                    stack[sp++]=left!=right;
                }
                    break;
                case NOT_EQUAL_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left!=right;
                }
                    break;
                case NOT_EQUAL_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=(double)left!=right;
                }
                    break;
                case NOT_EQUAL_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left!=(double)right;
                }
                    break;
                case NOT_EQUAL_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left!=right;
                }
                    break;
                case NOT_EQUAL_STRING_STRING:
                {
                    String left=(String)stack[--sp];
                    String right=(String)stack[--sp];
                    if (left==null||right==null)
                    {
                        if (left==null&&right==null)
                        {
                            stack[sp++]=false;
                        }
                        else
                        {
                            stack[sp++]=true;
                        }
                    }
                    else
                    {
                        var result=left.equalsIgnoreCase(right);
                        stack[sp++]=!result;
                    }
                }
                    break;
                case OR:
                {
                    boolean left=(boolean)stack[--sp];
                    boolean right=(boolean)stack[--sp];
                    stack[sp++]=left||right;
                }

                break;
                case SUBTRACT_INTEGER_INTEGER:
                {
                    long left=(long)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left-right;
                }
                    break;
                case SUBTRACT_INTEGER_NUMBER:
                {
                    long left=(long)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left-right;
                }
                    break;
                case SUBTRACT_NUMBER_INTEGER:
                {
                    double left=(double)stack[--sp];
                    long right=(long)stack[--sp];
                    stack[sp++]=left-right;
                }
                    break;
                case SUBTRACT_NUMBER_NUMBER:
                {
                    double left=(double)stack[--sp];
                    double right=(double)stack[--sp];
                    stack[sp++]=left-right;
                }
                    break;
                default:
                    break;
                 
                    
            }
            if (Debug.ENABLE && DEBUG && DEBUG_PRINT_STACK)
            {
                Debugging.log(DEBUG_CATEGORY,"PC:"+instruction.code()+(instruction.value()!=null?(":value="+instruction.value()):""));
                for (int i=0;i<sp;i++)
                {
                    Debugging.log(DEBUG_CATEGORY,i+":="+stack[i]);
                }
            }
        }
        if (sp!=1)
        {
            return false;
        }
        var result=stack[--sp];
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