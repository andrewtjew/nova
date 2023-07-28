package org.nova.http.server;

import java.io.File;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.testing.Testing;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;

import com.nixxcode.jvmbrotli.common.BrotliLoader;

public abstract class FileDownloadHandler extends ServletHandler
{

    final private String rootDirectory;
    final private String cacheControl;
    final private long cacheControlMaxAge;
    final private FileCache cache;
    final private boolean enableLocalCaching;

    final private HashSet<String> supportedEncodings;
    private boolean active;

    public abstract DownloadResponse getDownloadResponse(Trace parent, HttpServletRequest request,
            HttpServletResponse response, String rootDirectory) throws Throwable;

    public FileDownloadHandler(String rootDirectory, boolean enableLocalCaching, String cacheControl, long cacheControlMaxAge, long maxAge,
            long maxSize, long freeMemory, boolean active) throws Throwable
    {
        File file = new File(FileUtils.toNativePath(rootDirectory));
        this.rootDirectory = file.getCanonicalPath();
        this.cacheControlMaxAge = cacheControlMaxAge;
        this.cache = new FileCache(maxAge, maxSize, freeMemory);
        this.cacheControl = cacheControl;
        this.active = active;
        this.enableLocalCaching=enableLocalCaching;
        this.supportedEncodings = new HashSet<String>();
        this.getSupportedEncodings().add("deflate");
        this.getSupportedEncodings().add("gzip");
        if (BrotliLoader.isBrotliAvailable())
        {
            this.getSupportedEncodings().add("br");
        }
    }

    public boolean isActive()
    {
        return this.active;
    }

    public void setActive(boolean active)
    {
        synchronized (this)
        {
            this.active = active;
        }
    }

    public void evictAll()
    {
        this.cache.removeAll();
    }
    final private boolean TESTING=true;
    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        if (this.active == false)
        {
            return false;
        }
        DownloadResponse downloadResponse = getDownloadResponse(parent, request, response, this.getRootDirectory());
        if (downloadResponse.getHandled() != null)
        {
            return downloadResponse.getHandled();
        }
        String filePath=downloadResponse.getFilePath();
        boolean browserCachingEnabled = this.cacheControl == null ? false : downloadResponse.isAllowBrowserCaching();

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
        response.setContentType(downloadResponse.getContentType());
        byte[] bytes=this.cache.getValueFromCache(parent, downloadResponse.getKey());
        if (bytes!=null)
        {
            if (TESTING)
            {
                Testing.log("download using cache:"+filePath);
            }
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            return true;
        }
        if (TESTING)
        {
            Testing.log("download:"+filePath);
        }
        
        
        String rootFilePath = downloadResponse.getLocalFilePath();
        File file = new File(rootFilePath);
        if (file.isDirectory())
        {
            response.setStatus(HttpStatus.FORBIDDEN_403);
            return true;
        }
        boolean preCompressed=false;
        if (file.exists() == false)
        {
            String preCompressionExtension=downloadResponse.getPreCompressionExtension();
            if (preCompressionExtension!=null)
            {
                rootFilePath=FileUtils.toNativePath(this.getRootDirectory() + filePath)+"."+preCompressionExtension;
                file=new File(rootFilePath);
                if (file.exists()==false)
                {
                    if (TESTING)
                    {
                        Testing.log("FileDownload: not found: "+rootFilePath);
                    }
                    return false;
                }
                preCompressed=true;
            }
        }
        if (file.getCanonicalPath().contains(this.getRootDirectory()) == false)
        {
            response.setStatus(HttpStatus.FORBIDDEN_403);
            return true;
        }

        send(parent, request, response, rootFilePath, preCompressed, downloadResponse);
        response.setStatus(HttpStatus.OK_200);
        return true;
    }

        
    private void send(Trace parent, HttpServletRequest request, HttpServletResponse response, String localFile,boolean preCompressed
            ,DownloadResponse downloadResponse) throws Throwable
    {
        if (preCompressed)
        {
            response.setHeader("Content-Encoding", downloadResponse.getPreCompressionEncoding());
        }
        String key=downloadResponse.getKey();
        byte[] bytes = this.cache.get(parent, key);
        if (this.enableLocalCaching==false)
        {
            this.cache.remove(key);
        }
        if (bytes != null)
        {
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
        }
    }

    public HashSet<String> getSupportedEncodings()
    {
        return supportedEncodings;
    }

    public String getRootDirectory()
    {
        return rootDirectory;
    }

}
