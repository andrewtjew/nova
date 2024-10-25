package xp.nova.sqldb.graph;

import org.nova.metrics.LongValueMeter;

public record StackAndMeter(StackTraceElement[] stackTraceElements,LongValueMeter meter)
{
    
}
