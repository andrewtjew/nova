package org.nova.localization;

import java.util.HashMap;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Row;
import org.nova.sqldb.SqlUtils;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

public class SqlStringHandleResolver extends StringHandleResolver
{
    final private HashMap<String,String> formatMap;
    final private Connector connector;
    final private String notFoundFormat;
    final private String selectFormat;
    final private TraceManager traceManager;
    public SqlStringHandleResolver(TraceManager traceManager,Connector connector,String notFoundFormat,String selectFormat)
    {
        this.formatMap=new HashMap<String, String>();
        this.connector=connector;
        this.notFoundFormat=notFoundFormat;
        this.selectFormat=selectFormat;
        this.traceManager=traceManager;
    }

    public SqlStringHandleResolver(TraceManager traceManager,Connector connector)
    {
        this(traceManager,connector,"{{%s:%s:%s}}"
                ,"SELECT format FROM HandleFormats JOIN HandleEnums ON HandleEnums.ID=EnumID JOIN HandleLocales ON HandleLocales.ID=LocaleID WHERE Locale=? AND Enum=? AND Handle=?"
                );
    }
    public void evict(String locale,Class<?> enum_,String handle)
    {
        String key=locale+":"+enum_+":"+handle;
        synchronized(this)
        {
            this.formatMap.remove(key);
        }
        
    }
    public void evictAll()
    {
        synchronized(this)
        {
            this.formatMap.clear();
        }
    }
    
    @Override
    public String resolve(String locale,Class<?> enum_,String handle,Object...parameters) throws Throwable
    {
        String enumName=enum_!=null?enum_.getName():"";
        String key=locale+":"+enum_+":"+handle;
        String format;
        synchronized(this)
        {
            format=this.formatMap.get(key);
        }
        if (format==null)
        {
            try (Trace trace=new Trace(this.traceManager, "StringHandleFormat"))
            {
                Row row;
                try (Accessor accessor=this.connector.openAccessor(trace))
                {
                    row=SqlUtils.executeQueryOne(trace, null, accessor, this.selectFormat, locale,enumName,handle);
                }
                if (row!=null)
                {
                    format=row.getVARCHAR(0);
                    synchronized(this)
                    {
                        this.formatMap.put(key, format);
                    }
                }
            }
        }
        if (format==null)
        {
            System.err.println(String.format(this.notFoundFormat, locale,enumName,handle));
//            return String.format(this.notFoundFormat, locale,enumName,handle);
            return handle;
        }
        return String.format(format,parameters);
    }
}
