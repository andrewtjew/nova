package org.nova.http.server;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.collections.ContentCache;
import org.nova.debug.Debug;
import org.nova.debug.Debugging;
import org.nova.html.ExtensionToContentTypeMappings;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;
import org.nova.utils.TypeUtils;

import com.github.luben.zstd.Zstd;
import com.nixxcode.jvmbrotli.common.BrotliLoader;
import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;
import com.nixxcode.jvmbrotli.enc.Encoder;

public abstract class CacheDownloader<KEY> 
{
    public abstract CacheValue load(Trace parent,KEY key) throws Throwable;
    
    static public record CacheValue(String fileName,byte[] bytes)
    {
    }
    static public record CacheKey<KEY>(Encoding encoding,KEY key)
    {
    }
    static class Cache<KEY> extends ContentCache<CacheKey<KEY>,CacheValue>
    {
        public Cache(long contentCapacity,long freeMemoryCapacity)
        {
            super(0,-1,contentCapacity,freeMemoryCapacity);
        }
        @Override
        protected ValueSize<CacheValue> load(Trace parent, CacheKey<KEY> key) throws Throwable
        {
            return null;
        }
        @Override
        protected void onEvict(Trace parent, CacheKey<KEY> key, CacheValue value) throws Throwable
        {
        }
    }
    static enum Encoding
    {
        gzip,
        deflate,
        br,
        zstd,
        ;
        static public Encoding tryValueOf(String contentEncoding)
        {
            try
            {
                return Encoding.valueOf(contentEncoding.toLowerCase());
            }
            catch (Throwable t)
            {
            }            
            return null;
        }
    }
    final private String cacheControl;
    final private long maxAge;
    final private long expires;
    final private Cache<KEY> cache;
    final private boolean enableLocalCaching;
    final private HashSet<String> supportedEncodings;
    final private HashSet<String> doNotCompressFileExtensions;
    final private ExtensionToContentTypeMappings mappings;

    static public boolean DEBUG=false;
    static public String LOG_DEBUG_CATEGORY=CacheDownloader.class.getSimpleName();

    public CacheDownloader(boolean enableLocalCaching, String cacheControl, long maxAge, long maxSize, long freeMemory) throws Throwable
    {
        this.cache = new Cache<KEY>(maxSize, freeMemory);
        this.maxAge = maxAge;
        this.expires=maxAge>31536000 ?31536000 :maxAge;
        this.cacheControl=cacheControl;
        this.enableLocalCaching=enableLocalCaching;
        this.supportedEncodings = new HashSet<String>();
        this.supportedEncodings.add(Encoding.deflate.toString());
        this.supportedEncodings.add(Encoding.gzip.toString());
        this.supportedEncodings.add(Encoding.zstd.toString());
        if (BrotliLoader.isBrotliAvailable())
        {
            this.supportedEncodings.add(Encoding.br.toString());
        }
        this.doNotCompressFileExtensions=defaultDoNotCompressFileExtensions();
        this.mappings=ExtensionToContentTypeMappings.fromDefault();
    }

    public void clearCache()
    {
        this.cache.clear();
    }
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
    private CacheValue encode(Encoding encoding,CacheValue value) throws Throwable
    {
        byte[] encoded=null;
        switch (encoding)
        {
            case gzip:
            try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(value.bytes.length))
            {
                try (GZIPOutputStream encodingOutputStream=new GZIPOutputStream(byteArrayOutputStream))
                {
                   encodingOutputStream.write(value.bytes);
                   encodingOutputStream.close();
                }
                byteArrayOutputStream.close();
                encoded= byteArrayOutputStream.toByteArray();
            }
            break;
            
            case deflate:
            try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(value.bytes.length))
            {
                try (DeflaterOutputStream encodingOutputStream=new DeflaterOutputStream(byteArrayOutputStream))
                {
                   encodingOutputStream.write(value.bytes);
                   encodingOutputStream.close();
                }
                byteArrayOutputStream.close();
                encoded= byteArrayOutputStream.toByteArray();
            }
            break;
            
            case br:
            Encoder.Parameters params = new Encoder.Parameters().setQuality(4);
            try (ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(value.bytes.length))
            {
                try (BrotliOutputStream  encodingOutputStream=new BrotliOutputStream(byteArrayOutputStream))
                {
                   encodingOutputStream.write(value.bytes);
                   encodingOutputStream.close();
                }
                byteArrayOutputStream.close();
                encoded= byteArrayOutputStream.toByteArray();
            }
            break;
            
            case zstd:
            encoded = Zstd.compress(value.bytes);
            break;
    
                
            default:
            return null;
        }
        return new CacheValue(value.fileName,encoded);
    }

    static record CacheResult(String contentEncoding,CacheValue cacheValue)
    {
        
    }
    
    CacheResult getCacheValue(Trace parent,KEY key,HttpServletRequest request) throws Throwable
    {
        String acceptableEncoding=null;
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
                        Encoding encoding=Encoding.tryValueOf(accept);
                        if (encoding!=null)
                        {
                            if (acceptableEncoding==null)
                            {
                                acceptableEncoding=accept;
                            }
                            CacheKey<KEY> ck=new CacheKey<KEY>(encoding,key);
                            CacheValue cv=this.cache.getValueFromCache(ck);
                            if (cv!=null)
                            {
                                return new CacheResult(accept, cv);
                            }
                        }
                    }
                }
            }
        }
        CacheValue cv=this.cache.getValueFromCache(new CacheKey<KEY>(null,key));
        if (cv==null)
        {
            cv=load(parent,key);
        }
        if (cv==null)
        {
            return null;
        }

        //cv is now uncompressed and not in cache.
        
        String extension=FileUtils.getFileExtension(cv.fileName);
        boolean allowCompression=this.doNotCompressFileExtensions.contains(extension.toLowerCase())==false;
        Encoding encoding=null;
        if (allowCompression==false)
        {
            acceptableEncoding=null;
        }
        else
        {
            encoding=Encoding.tryValueOf(acceptableEncoding);
            if (encoding!=null)
            {
                cv=encode(encoding,cv);
            }
            else
            {
                acceptableEncoding=null;
            }
        }
        this.cache.put(parent, new CacheKey<KEY>(encoding,key), cv);
        return new CacheResult(acceptableEncoding,cv);
    }
    
    
    public boolean download(Trace parent, KEY key, HttpServletRequest request, HttpServletResponse response) throws Throwable
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
        
        if (this.enableLocalCaching==false)
        {
            //Do this only in test mode? 
            this.cache.remove(new CacheKey<KEY>(null,key));
            for (Encoding encoding:Encoding.values())
            {
                this.cache.remove(new CacheKey<KEY>(encoding,key));
            }
        }

        CacheResult cacheResult=getCacheValue(parent, key, request);
        
        if (cacheResult==null)
        {
            response.setStatus(HttpStatus.NOT_FOUND_404);
            return false;
        }
        if (cacheResult.contentEncoding!=null)
        {
            response.setHeader("Content-Encoding", cacheResult.contentEncoding);
        }

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
        CacheValue cacheValue=cacheResult.cacheValue;
        String contentType=this.mappings.getContentType(cacheValue.fileName);
        response.setContentType(contentType);
        if (Debug.ENABLE && DEBUG)
        {
            Debugging.log(LOG_DEBUG_CATEGORY,"Write start:key="+key+", length="+cacheValue.bytes.length);
        }
        write(parent, cacheValue.bytes, response,rangeStart,rangeEnd);
        if (Debug.ENABLE && DEBUG)
        {
            Debugging.log(LOG_DEBUG_CATEGORY,"Write end:key="+key+", length="+cacheValue.bytes.length);
        }
        return true;
    }

}
