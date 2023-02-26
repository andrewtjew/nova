package org.nova.http.server;

public class DownloadResponse
{
    final private String filePath;
    final private Boolean handled;
    final private boolean allowBrowserCaching;
    final private boolean allowCompression;
    final private String contentType;
    final private String preCompressionExtension;
    final private String preCompressionEncoding;
    DownloadResponse(String filePath,String contentType,boolean allowBrowserCaching,boolean allowCompression,String preCompressionExtension,String preCompressionEncoding)
    {
        this.filePath=filePath;
        this.contentType=contentType;
        this.allowBrowserCaching=allowBrowserCaching;
        this.allowCompression=allowCompression;
        this.handled=null;
        this.preCompressionEncoding=preCompressionEncoding;
        this.preCompressionExtension=preCompressionExtension;
    }
    DownloadResponse(boolean handled)
    {
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

    public String getFilePath()
    {
        return this.filePath;
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
