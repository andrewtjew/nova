package org.nova.operations;

import org.nova.tracing.Trace;

public abstract class OperatorVariableStore
{
    static public record OperatorVariableStoreValue(String value) {};
    
    public abstract void save(Trace parent,String category,VariableInstance instance, String value) throws Throwable;
    public abstract OperatorVariableStoreValue load(Trace parent,String category,VariableInstance instance) throws Throwable;
}
