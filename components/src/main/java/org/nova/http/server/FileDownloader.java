package org.nova.http.server;

import java.io.File;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.html.ExtensionToContentTypeMappings;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;

import com.google.common.io.Files;
import com.nixxcode.jvmbrotli.common.BrotliLoader;

public class FileDownloader extends ServletHandler
{
    final private String root;
    final private String cacheControl;
    final private long maxAge;
    final private long expires;
    final private FileCache cache;
    final private boolean enableLocalCaching;
    final private HashSet<String> supportedEncodings;

    public FileDownloader(String rootDirectory, boolean enableLocalCaching, String cacheControl, long maxAge, long maxSize, long freeMemory) throws Throwable
    {
        File file = new File(FileUtils.toNativePath(rootDirectory));
        if (file.isDirectory()==false)
        {
            throw new Exception("Not a directory:"+file.getCanonicalPath());
        }
  //      file.mkdirs();
        this.root = file.getCanonicalPath();
        this.maxAge = maxAge;
        this.expires=maxAge>31536000 ?31536000 :maxAge;
        this.cache = new FileCache(this.root,maxSize, freeMemory);
        this.cacheControl = cacheControl;
        this.enableLocalCaching=enableLocalCaching;
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

    public FileDownloader(String rootDirectory, boolean enableLocalCaching, String cacheControl,long maxSize, long freeMemory) throws Throwable
    {
        this(rootDirectory,enableLocalCaching,cacheControl,2147483648L,maxSize,freeMemory);
    }

    public void clearCache()
    {
        this.cache.clear();
    }
    static public boolean DEBUG=false;
    static public String LOG_DEBUG_CATEGORY=FileDownloader.class.getSimpleName();

    final private HashSet<String> doNotCompressFileExtensions;
    final private ExtensionToContentTypeMappings mappings;

    public static HashSet<String> defaultDoNotCompressFileExtensions()
    {
        HashSet<String> set=new HashSet<String>();
        set.add("gif");
        set.add("jpg");
        set.add("png");
        set.add("mp3");
        set.add("mp4");
        return set;
    }
    private void write(Trace parent, byte[] bytes, HttpServletResponse response,Long rangeStart,Long rangeEnd) throws Throwable
    {
        try
        {
            if (rangeStart==null)
            {
                response.setStatus(HttpStatus.OK_200);
                response.setContentLength(bytes.length);
                response.getOutputStream().write(bytes);
            }
            else
            {
//                System.out.println("partial");
                
                if (rangeEnd==null)
                {
                    rangeEnd=rangeStart+1000000;
                }
                if (rangeEnd>=bytes.length)
                {
                    rangeEnd=(long)bytes.length-1;
                }
                int length=(int)(rangeEnd-rangeStart+1);
                {
                    response.setStatus(HttpStatus.PARTIAL_CONTENT_206);
                }
                String contentRange="bytes "+rangeStart+"-"+rangeEnd+"/"+bytes.length;
                if (Debug.ENABLE && DEBUG)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"Content-Range:"+contentRange);
                }
                
//                System.out.println("length:"+length);
//                System.out.println("contentLength:"+bytes.length);

                response.setContentLength(length);
                response.setHeader("Content-Range", contentRange);
                response.getOutputStream().write(bytes,(int)(long)rangeStart,length);


            }
        }
        catch (Throwable t)
        {
            if (Debug.ENABLE && DEBUG)
            {
                t.printStackTrace();
            }
        }
    }    
    public boolean download(Trace parent, String filePath, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        Long rangeStart=null;
        Long rangeEnd=null;
        String range=request.getHeader("Range");
        if (range!=null)
        {
            if (range.startsWith("bytes"))
            {
                int index=range.indexOf('=');
                if (index>0)
                {
                    String[] parts=range.substring(index+1).split("-");
                    if (parts.length>0)
                    {
                        rangeStart=Long.parseLong(parts[0]);
                    }
                    if (parts.length>1)
                    {
                        rangeEnd=Long.parseLong(parts[1]);
                    }
                        
                    
                }
            }
        }
         
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
            if (this.maxAge > 0)
            {
                response.setHeader("Cache-Control", this.cacheControl + ", max-age=" + this.maxAge);
                String expires = OffsetDateTime.now().plusSeconds(this.expires)
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME);
                response.setHeader("Expires", expires);
            } 
            else
            {
                response.setHeader("Cache-Control", this.cacheControl);
            }
        } 
        else
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
            byte[] bytes=this.cache.getValueFromCache(key);
            if (bytes!=null)
            {
                if (Debug.ENABLE && DEBUG)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"From cache:filePath="+filePath+", length="+bytes.length);
                }
                write(parent, bytes, response,rangeStart,rangeEnd);
                if (Debug.ENABLE && DEBUG)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"Write ended:filePath="+filePath+", length="+bytes.length);
                }
                return true;
            }
        }
        
        String rootFilePath = root+filePath;
        File file = new File(rootFilePath);
        if (file.isDirectory())
        {
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"File is directory:filePath="+filePath);
            }
            return false;
        }
        if (file.exists() == false)
        {
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"Not found:filePath="+filePath);
            }
            return false;
        }
        if (file.getCanonicalPath().startsWith(this.root) == false)
        {
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"Invalid file:filePath="+filePath);
            }
            return false;
        }
        {
            byte[] bytes = this.cache.get(parent, key);
            if (bytes != null)
            {
                if (this.enableLocalCaching==false)
                {
                    this.cache.remove(key);
                }            
                if (Debug.ENABLE && DEBUG)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"Load:filePath="+filePath+", lenght="+bytes.length);
                }
                write(parent, bytes, response,rangeStart,rangeEnd);
                if (Debug.ENABLE && DEBUG)
                {
                    Debugging.log(LOG_DEBUG_CATEGORY,"Write ended:filePath="+filePath+", length="+bytes.length);
                }
            }
            else if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"Not in cache:filePath="+filePath);
            }
                
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
