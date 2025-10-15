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
        String URI=request.getRequestURI();
        return this.downloader.download(parent, URI, request, response);
    }


}
