package org.nova.http.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import org.nova.collections.ContentCache;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;
import com.nixxcode.jvmbrotli.enc.Encoder;

public class FileCache extends ContentCache<String,byte[]>
{
    final private String root;
    public FileCache(String root,long maxSize,long freeMemoryCapacity) throws Exception
    {
        super(0,-1,maxSize,freeMemoryCapacity);
        this.root=root;
    }
    public FileCache(long maxSize,long freeMemoryCapacity) throws Exception
    {
        this(null,maxSize,freeMemoryCapacity);
    }
     
    @Override
    protected ValueSize<byte[]> load(Trace trace, String fileKey) throws Throwable
    {
        int index=fileKey.indexOf('|');
        String compression=fileKey.substring(0,index).toLowerCase();
        String localFile=this.root!=null?this.root+File.separator+fileKey.substring(index+1):fileKey.substring(index+1);
        byte[] bytes=null;
        if ("gzip".equals(compression))
        {
            bytes=getValueFromCache("raw|"+localFile);
            if (bytes==null)
            {
                bytes=FileUtils.readFile(localFile);
            }
            try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(bytes.length))
            {
                try (GZIPOutputStream encodingOutputStream=new GZIPOutputStream(byteArrayOutputStream))
                {
                   encodingOutputStream.write(bytes);
                   encodingOutputStream.close();
                }
                byteArrayOutputStream.close();
                bytes= byteArrayOutputStream.toByteArray();
            }
        }
        else if ("deflate".equals(compression))
        {
            bytes=getValueFromCache("raw|"+localFile);
            if (bytes==null)
            {
                bytes=FileUtils.readFile(localFile);
            }
            try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(bytes.length))
            {
                try (DeflaterOutputStream encodingOutputStream=new DeflaterOutputStream(byteArrayOutputStream))
                {
                   encodingOutputStream.write(bytes);
                   encodingOutputStream.close();
                }
                byteArrayOutputStream.close();
                bytes= byteArrayOutputStream.toByteArray();
            }
        }
        else if ("br".equals(compression))
        {
            bytes=getValueFromCache("raw|"+localFile);
            if (bytes==null)
            {
                bytes=FileUtils.readFile(localFile);
            }
            Encoder.Parameters params = new Encoder.Parameters().setQuality(4);
            try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(bytes.length))
            {
                try (BrotliOutputStream  encodingOutputStream=new BrotliOutputStream(byteArrayOutputStream))
                {
                   encodingOutputStream.write(bytes);
                   encodingOutputStream.close();
                }
                byteArrayOutputStream.close();
                bytes= byteArrayOutputStream.toByteArray();
            }
        }
        else
        {
            bytes=FileUtils.readFile(localFile);
        }
        return new ValueSize<byte[]>(bytes,bytes.length);
    }

    @Override
    protected void onEvict(Trace parent, String key, byte[] value) throws Throwable
    {
        // TODO Auto-generated method stub
        
    }
}