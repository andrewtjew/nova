package org.nova.http.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nova.utils.FileUtils;

public class DownloadResponse
{
    final private String filePath;
    final private Boolean handled;
    final private boolean allowBrowserCaching;
    final private boolean allowCompression;
    final private String contentType;
    final private String preCompressionExtension;
    final private String preCompressionEncoding;
    final private String localFilePath;
    final private String key;
    
    DownloadResponse(String encoding, String rootDirectory, String filePath,String contentType,boolean allowBrowserCaching,boolean allowCompression,String preCompressionExtension,String preCompressionEncoding)
    {
        this.filePath=filePath;
        this.contentType=contentType;
        this.allowBrowserCaching=allowBrowserCaching;
        this.allowCompression=allowCompression;
        this.handled=null;
        this.preCompressionEncoding=preCompressionEncoding;
        this.preCompressionExtension=preCompressionExtension;
        this.localFilePath=FileUtils.toNativePath(rootDirectory+filePath);
        if ((allowCompression)&&(encoding!=null))
        {
            this.key=encoding+"|"+localFilePath;
        }
        else 
        {
            this.key="none|"+localFilePath;
        }
    }
    DownloadResponse(boolean handled)
    {
        this.key=null;
        this.localFilePath=null;
        this.filePath=null;
        this.contentType=null;
        this.handled=handled;
        this.allowBrowserCaching=false;
        this.allowCompression=false;
        this.preCompressionEncoding=null;
        this.preCompressionExtension=null;
    }
    public String getContentType()
    {
        return this.contentType;
    }

    public String getKey()
    {
        return this.key;
    }
    
    public String getFilePath()
    {
        return this.filePath;
    }
    public String getLocalFilePath()
    {
        return this.localFilePath;
    }
    public Boolean getHandled()
    {
        return this.handled;
    }
    public boolean isAllowBrowserCaching()
    {
        return this.allowBrowserCaching;
    }
    public boolean isAllowCompression()
    {
        return this.allowCompression;
    }
    public String getPreCompressionExtension()
    {
        return this.preCompressionExtension;
    }
    public String getPreCompressionEncoding()
    {
        return this.preCompressionEncoding;
    }
}
