package org.nova.http.server;

import java.io.File;

public class DownloadResponse
{
    final private String filePath;
    final private Boolean handled;
    final private boolean allowBrowserCaching;
    final private boolean allowCompression;
    final private String contentType;
    DownloadResponse(String filePath,String contentType,boolean allowBrowserCaching,boolean allowCompression)
    {
        this.filePath=filePath;
        this.contentType=contentType;
        this.allowBrowserCaching=allowBrowserCaching;
        this.allowCompression=allowCompression;
        this.handled=null;
    }
    DownloadResponse(boolean handled)
    {
        this.filePath=null;
        this.contentType=null;
        this.handled=handled;
        this.allowBrowserCaching=false;
        this.allowCompression=false;
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
}
