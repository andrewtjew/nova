package org.nova.logging;

import org.nova.metrics.CountMeter;
import org.nova.metrics.LevelMeter;
import org.nova.metrics.RateMeter;
import org.nova.tracing.Trace;

abstract public class LogWriter
{
    final protected CountMeter droppedMeter;
    final protected CountMeter stalledMeter;
    final protected LevelMeter busyMeter;
    final protected RateMeter writeMeter;
    
    public LogWriter()
    {
        this.droppedMeter=new CountMeter();
        this.stalledMeter=new CountMeter();
        this.busyMeter=new LevelMeter();
        this.writeMeter=new RateMeter();
    }
    public CountMeter getDroppedMeter()
    {
        return droppedMeter;
    }

    public CountMeter getStalledMeter()
    {
        return stalledMeter;
    }

    public LevelMeter getBusyMeter()
    {
        return busyMeter;
    }
    public RateMeter getWriteMeter()
    {
        return this.writeMeter;
    }
    abstract LogEntry write(Trace trace,Level logLevel,String category,Throwable throwable,String message,Item[] items);
}