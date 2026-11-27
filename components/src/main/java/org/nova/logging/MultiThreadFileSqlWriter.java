package org.nova.logging;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;

public class MultiThreadFileSqlWriter extends MultiThreadLogWriter
{
    static public class Configuration extends MultiThreadLogWriter.Configuration
    {
        public String categoryOverride=MultiThreadFileSqlWriter.class.getName();
        public String table="LogEntries";
    }
    
    final private Connector connector;
    final private Configuration configuration;
    final private String insert;
    
    public MultiThreadFileSqlWriter(Connector connector,Configuration configuration)
    {
        super(configuration);
        this.connector=connector;
        this.configuration=configuration;
        String table=configuration.table;
        this.insert="INSERT INTO "+table+" (Number,Created,LogLevel,Category,Data) VALUES(?,?,?,?,?,?)";
    }
    @Override
    protected void write(int threadIndex, LogEntryBuffer buffer) throws Throwable
    {
        int count=buffer.index;
        var entries=buffer.entries;
        Object[][] batchParameters=new Object[count][];
        for (int i=0;i<count;i++)
        {
            try (ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream())
            {
                var writer=new JSONFormatWriter(byteArrayOutputStream);
                writer.write(entries[i]);
                batchParameters[i]=new Object[5];
                int index=0;
                LogEntry entry=entries[i];
                batchParameters[i][index++]=entry.getNumber();
                batchParameters[i][index++]=new Timestamp(entry.getCreated());
                batchParameters[i][index++]=entry.getLogLevel().toString();
                batchParameters[i][index++]=entry.getCategory();
                batchParameters[i][index++]=byteArrayOutputStream.toString(StandardCharsets.UTF_8);
            }
        }
        returnBuffer(buffer);
        try (Accessor accessor=this.connector.openAccessor(null, this.configuration.categoryOverride))
        {
            accessor.executeBatchUpdate(null, this.configuration.categoryOverride, batchParameters, insert);
        }
    }
    @Override
    protected void signalStop() throws Throwable
    {
        // TODO Auto-generated method stub
        
    }
    

}
