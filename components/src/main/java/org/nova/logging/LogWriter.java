package org.nova.logging;

import org.nova.tracing.Trace;

abstract public class LogWriter
{
    abstract LogEntry write(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items);
}