package org.nova.logging;

import java.io.OutputStream;

public abstract class FormatWriter
{
    abstract public void write(LogEntry entry) throws Throwable;
    abstract public void writeBeginDocument() throws Throwable;
    abstract public void writeEndDocument() throws Throwable;
    abstract public void writeSeparator() throws Throwable;
}
