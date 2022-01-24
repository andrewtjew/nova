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

public class FileDownloadHandler extends ServletHandler
{
    static public class Cache extends ContentCache<String,byte[]>
    {
        public Cache(long maxAgeMs,long maxSize,long freeMemoryCapacity) throws Exception
        {
            super(0,maxAgeMs,maxSize,freeMemoryCapacity);
        }
        
        @Override
        protected ValueSize<byte[]> load(Trace trace, String fileKey) throws Throwable
        {
            int index=fileKey.indexOf('|');
            String compression=fileKey.substring(0,index).toLowerCase();
            String localFile=fileKey.substring(index+1);
            byte[] bytes=null;
            if ("gzip".equals(compression))
            {
                bytes=getValueFromCache(trace,"raw|"+localFile);
                if (bytes==null)
                {
                    bytes=FileUtils.readFile(localFile);
                }
                try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(bytes.length))
                {
                    try (GZIPOutputStream encodingOutputStream=new GZIPOutputStream(byteArrayOutputStream))
                    {
                       encodingOutputStream.write(bytes);
                       encodingOutputStream.close();
                    }
                    byteArrayOutputStream.close();
                    bytes= byteArrayOutputStream.toByteArray();
                }
            }
            else if ("deflate".equals(compression))
            {
                bytes=getValueFromCache(trace,"raw|"+localFile);
                if (bytes==null)
                {
                    bytes=FileUtils.readFile(localFile);
                }
                try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(bytes.length))
                {
                    try (DeflaterOutputStream encodingOutputStream=new DeflaterOutputStream(byteArrayOutputStream))
                    {
                       encodingOutputStream.write(bytes);
                       encodingOutputStream.close();
                    }
                    byteArrayOutputStream.close();
                    bytes= byteArrayOutputStream.toByteArray();
                }
            }
            else if ("br".equals(compression))
            {
                bytes=getValueFromCache(trace,"raw|"+localFile);
                if (bytes==null)
                {
                    bytes=FileUtils.readFile(localFile);
                }
                Encoder.Parameters params = new Encoder.Parameters().setQuality(4);
                try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(bytes.length))
                {
                    try (BrotliOutputStream  encodingOutputStream=new BrotliOutputStream(byteArrayOutputStream))
                    {
                       encodingOutputStream.write(bytes);
                       encodingOutputStream.close();
                    }
                    byteArrayOutputStream.close();
                    bytes= byteArrayOutputStream.toByteArray();
                }
            }
            else
            {
                bytes=FileUtils.readFile(localFile);
            }
            return new ValueSize<byte[]>(bytes,bytes.length);
        }
        
        
    }
    
    
    final private String rootDirectory;
    final private String cacheControl;
    final private long cacheControlMaxAge;
    final private ExtensionToContentTypeMappings mappings;
    final private HashSet<String> doNotCompressFileExtensions;
    final private String indexFile="index.html";
    final private Cache cache;
//    final private String[] preferredEncodings=new String[] {"br","deflate","gzip","raw"};
    final private String[] preferredEncodings=new String[] {"deflate","gzip","raw"};
    
    
    static HashSet<String> defaultDoNotCompressFileExtensions()
    {
        HashSet<String> set=new HashSet<String>();
        set.add("gif");
        set.add("jpg");
        set.add("png");
        return set;
    }
    
    //cacheControlMaxAge in seconds, maxAge in ms
    public FileDownloadHandler(String rootDirectory,ExtensionToContentTypeMappings mappings,HashSet<String> doNotCompressExtensions,String cacheControl,long cacheControlMaxAge,long maxAge,long maxSize,long freeMemory) throws Throwable
    {
        this.mappings=mappings;
        this.doNotCompressFileExtensions=doNotCompressExtensions;
        
        File file=new File(rootDirectory);
        this.rootDirectory=file.getCanonicalPath();
        this.cacheControlMaxAge=cacheControlMaxAge;
        this.cache=new Cache(maxAge, maxSize, freeMemory);
        this.cacheControl=cacheControl;
    }
    public FileDownloadHandler(String rootDirectory) throws Throwable
    {
        this(rootDirectory,ExtensionToContentTypeMappings.fromDefault(),defaultDoNotCompressFileExtensions(),"public",100L*24L*3600L,0,(long)(0.5*Runtime.getRuntime().maxMemory()),(long)(0.9*Runtime.getRuntime().maxMemory()));
    }

    @Override
    public void handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        String method=request.getMethod();
        if ("GET".equalsIgnoreCase(method)==false)
        {
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
            return;
        }
        String URI = request.getRequestURI();
        String remoteFile;
        if (URI.endsWith("/"))
        {
            remoteFile=URI+this.indexFile;
        }
        else
        {
            remoteFile=URI;
        }
        String localFile=FileUtils.toNativePath(this.rootDirectory+remoteFile);
        File file=new File(localFile);
        String path=file.getCanonicalPath();
        if (path.contains(this.rootDirectory)==false)
        {
            response.setStatus(HttpStatus.FORBIDDEN_403);
            return;
        }
        if (file.isDirectory())
        {
            String location=request.getRequestURL().toString()+"/";
            response.setHeader("Location", location);
            response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
            return;
        }
        if (file.exists()==false)
        {
            response.setStatus(HttpStatus.NOT_FOUND_404);
            return;
        }

        boolean browserCachingEnabled=this.cacheControl!=null;

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
                response.setHeader("Cache-Control",this.cacheControl+" max-age="+this.cacheControlMaxAge);
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
        response.setContentType(this.mappings.getContentType(remoteFile));
        String extension=Files.getFileExtension(remoteFile).toLowerCase();
        boolean doNotCompress=this.doNotCompressFileExtensions.contains(extension);
        send(parent, request, response, localFile,doNotCompress);
        response.setStatus(HttpStatus.OK_200);
    }

    private void send(Trace parent, HttpServletRequest request, HttpServletResponse response,String localFile,boolean doNotCompress) throws Throwable
    {
        HashSet<String> set=new HashSet<String>();
        set.add("raw");
        if (doNotCompress==false)
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
                    if ("raw".equals(encoding)==false)
                    {
                        response.setHeader("Content-Encoding", encoding);
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
