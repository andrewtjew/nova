package org.nova.operations;

import org.nova.tracing.Trace;

public abstract class OperatorVariableStore
{
    public abstract void save(Trace parent,String category,VariableInstance instance, String value) throws Throwable;
    public abstract String load(Trace parent,String category,VariableInstance instance) throws Throwable;
}
