package org.nova.html.ext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Row;
import org.nova.sqldb.SqlUtils;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;

public class StringHandleSqlResolver extends StringHandleResolver
{
    final private HashMap<String,String> formatMap;
    final private HashMap<String,Locale> localeMap;
    final private Connector connector;
    final private String notFoundFormat;
    final private String selectFormat;
    final private String selectLocale;
    final private TraceManager traceManager;
    final private HashSet<String> notFoundNamespaces;
    final private HashSet<String> notFoundHandles;
    public StringHandleSqlResolver(TraceManager traceManager,Connector connector,String notFoundFormat,String selectFormat,String selectLocale)
    {
        this.notFoundHandles=new HashSet<String>();
        this.notFoundNamespaces=new HashSet<String>();
        this.formatMap=new HashMap<String, String>();
        this.localeMap=new HashMap<String, Locale>(); 
        this.connector=connector;
        this.notFoundFormat=notFoundFormat;
        this.selectFormat=selectFormat;
        this.selectLocale=selectLocale;
//        this.selectFormat=;
//        this.selectLocale=;
        this.traceManager=traceManager;
    }

    public StringHandleSqlResolver(TraceManager traceManager,Connector connector)
    {
        this(traceManager,connector,"[|%s:%s|]"
                ,"SELECT format FROM HandleFormats WHERE namespace=? and handle=?"
                ,"SELECT locale FROM HandleLocales WHERE namespace=?");
    }
    @Override
    public String resolve(Enum<?> namespace,Enum<?> handle,Object...parameters) throws Throwable
    {
        String ns=namespace.toString();
        String h=handle.toString();
        String key=ns+"."+h;
        Locale locale;
        String format;
        synchronized(this)
        {
            locale=this.localeMap.get(ns);
            format=this.formatMap.get(key);
        }
        if (locale==null)
        {
            try (Trace trace=new Trace(this.traceManager, "StringHandleLocale"))
            {
                Row row;
                try (Accessor accessor=this.connector.openAccessor(trace))
                {
                    row=SqlUtils.executeQueryOne(trace, null, accessor, this.selectLocale, ns);
                }
                if (row!=null)
                {
                    String l=row.getVARCHAR(0);
                    locale=LocaleUtils.toLocale(l);
                    synchronized(this)
                    {
                        this.localeMap.put(ns, locale);
                    }
                }
            }
        }
        if (locale==null)
        {
            synchronized(this)
            {
                this.notFoundNamespaces.add(ns);
            }
            return String.format(this.notFoundFormat, "namespace",ns);
        }
        if (format==null)
        {
            try (Trace trace=new Trace(this.traceManager, "StringHandleFormat"))
            {
                Row row;
                try (Accessor accessor=this.connector.openAccessor(trace))
                {
                    row=SqlUtils.executeQueryOne(trace, null, accessor, this.selectFormat, ns,h);
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
            synchronized(this)
            {
                this.notFoundHandles.add(h);
            }
            return String.format(this.notFoundFormat, "handle",h);
        }
        return String.format(locale,format,parameters);
    }

    synchronized public String[] getNotFoundNamespaces()
    {
        return this.notFoundNamespaces.toArray(new String[this.notFoundNamespaces.size()]);
    }
    synchronized public String[] getNotFoundHandles()
    {
        return this.notFoundHandles.toArray(new String[this.notFoundHandles.size()]);
    }
}
