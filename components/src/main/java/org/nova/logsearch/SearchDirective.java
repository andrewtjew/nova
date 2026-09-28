package org.nova.logsearch;

import org.nova.parsing.Directive;
import org.nova.parsing.NumericType;

public class SearchDirective extends Directive
{
//    FLOAT,
//    DOUBLE,
//
//    INTEGER,
//    LONG,
    
    static public enum ValueType
    {
        INTEGER,
        FLOAT,
        STRING,
    }
    
    
    static record Instruction(Code code)
    {
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
    }

    public ValueType valueType;
    public Code instruction;
    public Object value;
    
    public SearchDirective(ValueType valueType,Object value)
    {
        this.value=value;
        this.valueType=valueType;
    }
}
