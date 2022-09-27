package org.nova.http.server;

import java.util.HashSet;
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

    public static HashSet<String> defaultDoNotCompressFileExtensions()
    {
        HashSet<String> set=new HashSet<String>();
        set.add("gif");
        set.add("jpg");
        set.add("png");
        return set;
    }

    public static HashSet<String> defaultNoBrowserCachingPaths()
    {
        HashSet<String> set=new HashSet<String>();
        set.add("/index.html");
        return set;
    }
    
    //cacheControlMaxAge in seconds, maxAge in ms
    public IndexFileDownloadHandler(String rootDirectory,HashSet<String> noBrowserCachingPaths,String cacheControl,long cacheControlMaxAge,long maxAge,long maxSize,long freeMemory,String indexFile,ExtensionToContentTypeMappings mappings,HashSet<String> doNotCompressExtensions,boolean active) throws Throwable
    {
        super(rootDirectory,cacheControl,cacheControlMaxAge,maxAge,maxSize,freeMemory,active);
        this.mappings=mappings;
        this.doNotCompressFileExtensions=doNotCompressExtensions;
        this.noBrowserCachingPaths=noBrowserCachingPaths;
        this.indexFile=indexFile;
    }

    public IndexFileDownloadHandler(String rootDirectory,HashSet<String> noBrowserCachingPaths,String cacheControl,long cacheControlMaxAge,long maxAge,long maxSize,long freeMemory) throws Throwable
    {
        this(rootDirectory,noBrowserCachingPaths,cacheControl,cacheControlMaxAge,maxAge,maxSize,freeMemory,"index.html",ExtensionToContentTypeMappings.fromDefault(),defaultDoNotCompressFileExtensions(),true);
    }
    public IndexFileDownloadHandler(String rootDirectory,String cacheControl) throws Throwable
    {
      this(rootDirectory,defaultNoBrowserCachingPaths(),cacheControl,CACHE_CONTROL_MAX_AGE,MAX_AGE,(long)(0.5*Runtime.getRuntime().maxMemory()),(long)(0.9*Runtime.getRuntime().maxMemory()));
    }
    public IndexFileDownloadHandler(String rootDirectory) throws Throwable
    {
        this(rootDirectory,null);
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
        
        return new DownloadResponse(filePath, contentType, allowBrowserCaching, allowCompression);
    }
}
