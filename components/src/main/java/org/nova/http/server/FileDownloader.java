package org.nova.http.server;

import java.io.File;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

public class FileDownloader extends CacheDownloader<String>
{
    final private String root;

    public FileDownloader(String rootDirectory, boolean enableLocalCaching, String cacheControl, long maxAge, long maxSize, long freeMemory) throws Throwable
    {
        super(enableLocalCaching,cacheControl,maxAge,maxSize,freeMemory);
        File file = new File(FileUtils.toNativePath(rootDirectory));
        if (file.isDirectory()==false)
        {
            throw new Exception("Not a directory:"+file.getCanonicalPath());
        }
        this.root = file.getCanonicalPath();        
    }

    public FileDownloader(String rootDirectory, boolean enableLocalCaching, String cacheControl,long maxSize, long freeMemory) throws Throwable
    {
        this(rootDirectory,enableLocalCaching,cacheControl,2147483648L,maxSize,freeMemory);
    }

    @Override
    public CacheValue load(Trace parent, String filePath) throws Throwable
    {
        filePath=FileUtils.toNativePath(filePath);
        String rootFilePath = root+filePath;
        File file = new File(rootFilePath);
        if (file.isDirectory())
        {
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"File is directory:filePath="+filePath);
            }
            return null;
        }
        if (file.exists() == false)
        {
            if (Debug.ENABLE && DEBUG)
            {
                Debugging.log(LOG_DEBUG_CATEGORY,"Not found:filePath="+filePath);
            }
            return null;
        }
        byte[] bytes=FileUtils.readFile(rootFilePath);
        return new CacheValue(filePath,bytes);
    }
}
