package org.nova.http.server;

import java.io.File;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.html.ExtensionToContentTypeMappings;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;

import com.google.common.io.Files;
import com.nixxcode.jvmbrotli.common.BrotliLoader;

public class FileDownloader extends ServletHandler
{
    //CR000054453
    final private String root;
    final private String cacheControl;
    final private long cacheControlMaxAge;
    final private FileCache cache;
    final private boolean localCaching;
    final private HashSet<String> supportedEncodings;

    public FileDownloader(String rootDirectory, boolean enableLocalCaching, String cacheControl, long cacheControlMaxAge, long maxAge,
            long maxSize, long freeMemory) throws Throwable
    {
        File file = new File(FileUtils.toNativePath(rootDirectory));
        if (file.isDirectory()==false)
        {
            throw new Exception("Not a directory:"+file.getCanonicalPath());
        }
  //      file.mkdirs();
        this.root = file.getCanonicalPath();
        this.cacheControlMaxAge = cacheControlMaxAge;
        this.cache = new FileCache(this.root,maxAge, maxSize, freeMemory);
        this.cacheControl = cacheControl;
        this.localCaching=enableLocalCaching;
        this.supportedEncodings = new HashSet<String>();
        this.supportedEncodings.add("deflate");
        this.supportedEncodings.add("gzip");
        if (BrotliLoader.isBrotliAvailable())
        {
            this.supportedEncodings.add("br");
        }
        this.doNotCompressFileExtensions=defaultDoNotCompressFileExtensions();
        this.mappings=ExtensionToContentTypeMappings.fromDefault();
    }

    public void clearCache()
    {
        this.cache.clear();
    }
    static public boolean DEBUG=false;

    final private HashSet<String> doNotCompressFileExtensions;
    final private ExtensionToContentTypeMappings mappings;

    public static HashSet<String> defaultDoNotCompressFileExtensions()
    {
        HashSet<String> set=new HashSet<String>();
        set.add("gif");
        set.add("jpg");
        set.add("png");
        set.add("mp3");
        return set;
    }
    
    public boolean download(Trace parent, String filePath, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        filePath=FileUtils.toNativePath(filePath);
        String contentType=this.mappings.getContentType(filePath);
        String extension=Files.getFileExtension(filePath).toLowerCase();
        boolean allowCompression=this.doNotCompressFileExtensions.contains(extension)==false;

        String encoding = "none";
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
    
                        if (this.supportedEncodings.contains(accept))
                        {
                            response.setHeader("Content-Encoding", value.value);
                            encoding = accept;
                            break;
                        }
                    }
                }
            }            
        }
        String key= encoding+"|"+filePath;
        
        boolean browserCachingEnabled = this.cacheControl != null;

        String cacheControlValue = request.getHeader("Cache-Control");
        boolean cacheControlSet = false;
        if (TypeUtils.containsIgnoreCase(cacheControlValue, "no-cache"))
        {
            browserCachingEnabled = false;
            cacheControlSet = true;
        }
        String pragmaValue = request.getHeader("Pragma");
        boolean pragmaSet = false;
        if (TypeUtils.containsIgnoreCase(pragmaValue, "no-cache"))
        {
            browserCachingEnabled = false;
            pragmaSet = true;
        }
        if (browserCachingEnabled)
        {
            if (this.cacheControlMaxAge > 0)
            {
                response.setHeader("Cache-Control", this.cacheControl + ", max-age=" + this.cacheControlMaxAge);
                String expires = OffsetDateTime.now().plusSeconds(this.cacheControlMaxAge)
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME);
                response.setHeader("Expires", expires);
            } else
            {
                response.setHeader("Cache-Control", this.cacheControl);
            }
        } else
        {
            if (cacheControlSet == false)
            {
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            }
            if (pragmaSet == false)
            {
                response.setHeader("Pragma", "no-cache");
            }
            response.setHeader("Expires", "0");
        }
        response.setContentType(contentType);
        {
            byte[] bytes=this.cache.getValueFromCache(parent, key);
            if (bytes!=null)
            {
                if (Debugging.ENABLE)
                {
                    if (DEBUG)
                    {
                        Debugging.log("FileDownloadHandler:cache="+filePath);
                    }
                }
                response.setContentLength(bytes.length);
                response.getOutputStream().write(bytes);
                return true;
            }
        }
        if (Debugging.ENABLE)
        {
            if (DEBUG)
            {
                Debugging.log("FileDownloadHandler:load="+filePath);
            }
        }
        
        
        String rootFilePath = root+filePath;
        File file = new File(rootFilePath);
        if (file.isDirectory())
        {
//            response.setStatus(HttpStatus.FORBIDDEN_403);
            return false;
        }
        if (file.exists() == false)
        {
//            response.setStatus(HttpStatus.NOT_FOUND_404);
            return false;
        }
        if (file.getCanonicalPath().startsWith(this.root) == false)
        {
//            response.setStatus(HttpStatus.FORBIDDEN_403);
            return false;
        }
        {
            if (this.localCaching==false)
            {
                this.cache.remove(key);
            }
            byte[] bytes = this.cache.get(parent, key);
            if (bytes != null)
            {
                response.setContentLength(bytes.length);
                response.getOutputStream().write(bytes);
            }
            response.setStatus(HttpStatus.OK_200);
        }
        return true;
    }

    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        String URI=request.getRequestURI();
        return this.download(parent, URI, request, response);
    }


}
