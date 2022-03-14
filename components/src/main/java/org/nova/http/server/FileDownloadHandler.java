package org.nova.http.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.collections.ContentCache;
import org.nova.collections.FileCacheConfiguration;
import org.nova.collections.ContentCache.ValueSize;
import org.nova.html.ExtensionToContentTypeMappings;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

import com.google.common.io.Files;
import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;
import com.nixxcode.jvmbrotli.enc.Encoder;

public abstract class FileDownloadHandler extends ServletHandler
{

    final private String rootDirectory;
    final private String cacheControl;
    final private long cacheControlMaxAge;
    final private FileCache cache;
//    final private String[] preferredEncodings=new String[] {"br","deflate","gzip","raw"};

    final private String[] preferredEncodings=new String[] {"deflate","gzip","raw"};
    private boolean active;
    
    public abstract DownloadResponse getDownloadResponse(Trace parent,HttpServletRequest request, HttpServletResponse response,String rootDirectory) throws Throwable;
    
    //cacheControlMaxAge in seconds, maxAge in ms
    public FileDownloadHandler(String rootDirectory,String cacheControl,long cacheControlMaxAge,long maxAge,long maxSize,long freeMemory,boolean active) throws Throwable
    {
        File file=new File(FileUtils.toNativePath(rootDirectory));
        this.rootDirectory=file.getCanonicalPath();
        this.cacheControlMaxAge=cacheControlMaxAge;
        this.cache=new FileCache(maxAge, maxSize, freeMemory);
        this.cacheControl=cacheControl;
        this.active=active;
    }
    
    public boolean isActive()
    {
        return this.active;
    }
    
    public void setActive(boolean active)
    {
        synchronized(this)
        {
            this.active=active;
        }
    }
    
    public void evictAll()
    {
        this.cache.evictAll();
    }
    
    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        if (this.active==false)
        {
            return false;
        }
        DownloadResponse downloadResponse=getDownloadResponse(parent, request, response,this.rootDirectory);
        if (downloadResponse.getHandled()!=null)
        {
            return downloadResponse.getHandled();
        }

        String rootFilePath=FileUtils.toNativePath(this.rootDirectory+downloadResponse.getFilePath());
        File file=new File(rootFilePath);
        if (file.isDirectory())
        {
            response.setStatus(HttpStatus.FORBIDDEN_403);
            return true;
        }
        if (file.exists()==false)
        {
            return false;
        }
        if (file.getCanonicalPath().contains(this.rootDirectory)==false)
        {
            response.setStatus(HttpStatus.FORBIDDEN_403);
            return true;
        }

        boolean browserCachingEnabled=this.cacheControl==null?false:downloadResponse.isAllowBrowserCaching();

        String cacheControlValue=request.getHeader("Cache-Control");
        boolean cacheControlSet=false;
        if (TypeUtils.containsIgnoreCase(cacheControlValue, "no-cache"))
        {
            browserCachingEnabled=false;
            cacheControlSet=true;
        }
        String pragmaValue=request.getHeader("Pragma");
        boolean pragmaSet=false;
        if (TypeUtils.containsIgnoreCase(pragmaValue, "no-cache"))
        {
            browserCachingEnabled=false;
            pragmaSet=true;
        }
        if (browserCachingEnabled)
        {
            if (this.cacheControlMaxAge>0)
            {
                response.setHeader("Cache-Control",this.cacheControl+", max-age="+this.cacheControlMaxAge);
                String expires=OffsetDateTime.now().plusSeconds(this.cacheControlMaxAge).format(DateTimeFormatter.RFC_1123_DATE_TIME);
                response.setHeader("Expires",expires);
            }
            else
            {
                response.setHeader("Cache-Control",this.cacheControl);
            }
        }
        else
        {
            if (cacheControlSet==false)
            {
                response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
            }
            if (pragmaSet==false)
            {
                response.setHeader("Pragma","no-cache");
            }
            response.setHeader("Expires","0");
        }

        response.setContentType(downloadResponse.getContentType());
        send(parent, request, response, rootFilePath,downloadResponse.isAllowCompression());
        response.setStatus(HttpStatus.OK_200);
        return true;
    }

    private void send(Trace parent, HttpServletRequest request, HttpServletResponse response,String localFile,boolean allowCompression) throws Throwable
    {
        HashSet<String> set=new HashSet<String>();
        set.add("none");
        if (allowCompression)
        {
            String accept=request.getHeader("Accept-Encoding");
            String[] accepts=Utils.split(accept.toLowerCase(), ',');
            for (String item:accepts)
            {
                set.add(item.trim());
            }
        }
        
        for (String encoding:this.preferredEncodings)
        {
            if (set.contains(encoding))
            {
                byte[] bytes=this.cache.get(parent,encoding+"|"+localFile);
                if (bytes!=null)
                {
                    if ("none".equals(encoding)==false)
                    {
                        response.setHeader("Content-Encoding", encoding);
                        response.setContentLength(bytes.length);
                    }
                    else
                    {
                        response.setContentLength(bytes.length);
                    }
                   response.getOutputStream().write(bytes);
                   System.out.println("Cache size:"+this.cache.getTotalContentSize()/1024L);
                   return;
               }
            }
        }
    }

}
