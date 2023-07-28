package org.nova.http.server;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.html.ExtensionToContentTypeMappings;
import org.nova.tracing.Trace;
import com.google.common.io.Files;

public class IndexFileDownloadHandler extends FileDownloadHandler
{
    static public String CACHE_CONTROL="public";
    static public long CACHE_CONTROL_MAX_AGE=100L*24L*3600L;
    static public long MAX_AGE=0;

    final private ExtensionToContentTypeMappings mappings;
    final private HashSet<String> doNotCompressFileExtensions;
    final private HashSet<String> noBrowserCachingPaths;
    final private String indexFile;
    final private String preCompressionExtension;
    final private String preCompressionEncoding;

    public static HashSet<String> defaultDoNotCompressFileExtensions()
    {
        HashSet<String> set=new HashSet<String>();
        set.add("gif");
        set.add("jpg");
        set.add("png");
        return set;
    }

    public static HashSet<String> DEFAULT_NO_BROWSER_CACHING_PATHS()
    {
        HashSet<String> set=new HashSet<String>();
        set.add("/index.html");
        return set;
    }
    
    //cacheControlMaxAge in seconds, maxAge in ms
    public IndexFileDownloadHandler(String rootDirectory,boolean enableLocalCaching,HashSet<String> noBrowserCachingPaths,String cacheControl,long cacheControlMaxAge,long maxAge,long maxSize,long freeMemory,String indexFile,ExtensionToContentTypeMappings mappings,HashSet<String> doNotCompressExtensions,boolean active,String preCompressionExtension,String preCompressionEncoding) throws Throwable
    {
        super(rootDirectory,enableLocalCaching,cacheControl,cacheControlMaxAge,maxAge,maxSize,freeMemory,active);
        this.mappings=mappings;
        this.doNotCompressFileExtensions=doNotCompressExtensions;
        this.noBrowserCachingPaths=noBrowserCachingPaths;
        this.preCompressionExtension=preCompressionExtension;
        this.preCompressionEncoding=preCompressionEncoding;
        this.indexFile=indexFile;
    }
    public IndexFileDownloadHandler(String rootDirectory,boolean enableLocalCaching,HashSet<String> noBrowserCachingPaths
            ,String cacheControl,long cacheControlMaxAge,long maxAge,long maxSize,long freeMemory,String indexFile,ExtensionToContentTypeMappings mappings,HashSet<String> doNotCompressExtensions,boolean active) throws Throwable
    {
        this(rootDirectory,enableLocalCaching,noBrowserCachingPaths,cacheControl,cacheControlMaxAge,maxAge,maxSize,freeMemory,indexFile,mappings,doNotCompressExtensions,active,null,null);
    }

    public IndexFileDownloadHandler(String rootDirectory,boolean enableLocalCaching,HashSet<String> noBrowserCachingPaths,String cacheControl,long cacheControlMaxAge,long maxAge,long maxSize,long freeMemory) throws Throwable
    {
        this(rootDirectory,enableLocalCaching,noBrowserCachingPaths,cacheControl,cacheControlMaxAge,maxAge,maxSize,freeMemory,"index.html",ExtensionToContentTypeMappings.fromDefault(),defaultDoNotCompressFileExtensions(),true,"gz","gzip");
    }
    public IndexFileDownloadHandler(String rootDirectory,boolean enableLocalCaching,String cacheControl) throws Throwable
    {
      this(rootDirectory,enableLocalCaching,DEFAULT_NO_BROWSER_CACHING_PATHS(),cacheControl,CACHE_CONTROL_MAX_AGE,MAX_AGE,0,1024L*1024L*200);
    }
    public IndexFileDownloadHandler(String rootDirectory,boolean caching) throws Throwable
    {
        this(rootDirectory,caching,caching?"public":null);
    }
    
    @Override
    public DownloadResponse getDownloadResponse(Trace parent, HttpServletRequest request, HttpServletResponse response, String rootDirectory) throws Throwable
    {
        String method=request.getMethod();
        if ("GET".equalsIgnoreCase(method)==false)
        {
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
            return new DownloadResponse(true);
        }
        String URI = request.getRequestURI();
        String filePath=null;
        if (URI.endsWith("/"))
        {
            filePath=URI+this.indexFile;
        }
        else
        {
            filePath=URI;
        }
        boolean allowBrowserCaching=this.noBrowserCachingPaths==null?true:this.noBrowserCachingPaths.contains(filePath)==false;
        String contentType=this.mappings.getContentType(filePath);
        String extension=Files.getFileExtension(filePath).toLowerCase();
        boolean allowCompression=this.doNotCompressFileExtensions==null?true:this.doNotCompressFileExtensions.contains(extension)==false;

        String encoding = null;
        if (allowCompression)
        {
            String accepts = request.getHeader("Accept-Encoding");
            if (accepts!=null)
            {
                List<ValueQ> values = ValueQ.sortDescending(accepts);
                for (ValueQ value : values)
                {
                    if (value.value != null)
                    {
                        String accept = value.value.toLowerCase();
    
                        if (this.getSupportedEncodings().contains(accept))
                        {
                            response.setHeader("Content-Encoding", value.value);
                            encoding = accept;
                            break;
                        }
                    }
                }
            }            
        }
        
        return new DownloadResponse(encoding,this.getRootDirectory(),filePath, contentType, allowBrowserCaching, allowCompression,this.preCompressionExtension,this.preCompressionEncoding);
    }
}
