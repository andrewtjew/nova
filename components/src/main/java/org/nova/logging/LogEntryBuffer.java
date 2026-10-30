package org.nova.logging;

import java.util.Arrays;

class LogEntryBuffer
{
    public LogEntry[] entries;
    public int index;
    public long started;
    public long number;
    public LogEntryBuffer(int length)
    {
        this.entries=new LogEntry[length];
        this.index=0;
    }
    public void start(long number)
    {
        this.number=number;
        this.started=System.currentTimeMillis();
        Arrays.fill(this.entries, 0,this.index, null);
        this.index=0;
    }
    public boolean addAndCheckFull(LogEntry entry)
    {
        this.entries[this.index++]=entry;
        return this.index==this.entries.length;
    }
}