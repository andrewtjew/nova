package org.nova.http.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.nova.tracing.Trace;

public class FileDownloadHandler extends ServletHandler
{
    FileDownloader downloader;
    public FileDownloadHandler(String rootDirectory, boolean enableLocalCaching, String cacheControl, long maxAge, long maxSize, long freeMemory) throws Throwable
    {
        this.downloader=new FileDownloader(rootDirectory, enableLocalCaching, cacheControl, maxSize, freeMemory);
    }

    public FileDownloadHandler(String rootDirectory, boolean enableLocalCaching, String cacheControl,long maxSize, long freeMemory) throws Throwable
    {
        this(rootDirectory,enableLocalCaching,cacheControl,2147483648L,maxSize,freeMemory);
    }
    public FileDownloadHandler(String rootDirectory, long maxSize, long freeMemory) throws Throwable
    {
        this(rootDirectory,false,"public",2147483648L,maxSize,freeMemory);
    }

    @Override
    public boolean handle(Trace parent, HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        if (request.getMethod()!="GET")
        {
            return false;
        }
        String URI=request.getRequestURI();
        return this.downloader.download(parent, URI, null, request, response);
    }


}
