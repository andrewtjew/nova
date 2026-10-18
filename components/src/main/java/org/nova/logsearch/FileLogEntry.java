package org.nova.logsearch;

import java.io.File;

public record FileLogEntry(LogRecord logRecord,File file)
{
}