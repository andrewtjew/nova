package org.nova.concurrent;

import org.nova.tracing.Trace;

public interface TraceCombiner<RESULT,INPUT>
{
    public RESULT combine(Trace parent,INPUT input) throws Throwable;
}
