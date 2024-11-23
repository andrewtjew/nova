package xp.nova.sqldb.graph;

import org.nova.metrics.LongValueMeter;

public record QueryPerformance(StackTraceElement[] stackTraceElements,LongValueMeter meter,QueryKey queryKey)
{
    
}
