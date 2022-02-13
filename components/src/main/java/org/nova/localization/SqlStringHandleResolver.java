package org.nova.localization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.nova.html.ext.Locale_ISO_639_1;
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
                ,"SELECT format FROM HandleFormats JOIN HandleEnums ON HandleEnums.ID=EnumID JOIN HandleLanguages ON HandleLanguages.ID=LanguageID WHERE Language=? AND Enum=? AND Handle=?"
                );
    }
    public void evict(LanguageCode languageCode,Class<?> enum_,String handle)
    {
        String language=languageCode.name();
        String key=language+":"+enum_+":"+handle;
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
    public String resolve(LanguageCode languageCode,Class<?> enum_,String handle,Object...parameters) throws Throwable
    {
        String language=languageCode.name();
        String enumName=enum_!=null?enum_.getName():"";
        String key=language+":"+enum_+":"+handle;
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
                    row=SqlUtils.executeQueryOne(trace, null, accessor, this.selectFormat, language,enumName,handle);
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
            return String.format(this.notFoundFormat, language,enumName,handle);
        }
        return String.format(languageCode.getValue().locale,format,parameters);
    }
}
