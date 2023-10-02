/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.services;
import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.nova.annotations.Description;
import org.nova.collections.FileCache;
import org.nova.frameworks.ServerApplication;
import org.nova.html.elements.Element;
import org.nova.http.client.BinaryResponse;
import org.nova.http.client.JSONClient;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentDecoder;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONPatchContentReader;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.Log;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.PathParam;
import org.nova.testing.Debugging;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

@Description("Handlers for server operator pages")
@ContentDecoders({GzipContentDecoder.class,DeflaterContentDecoder.class})
//@ContentEncoders({BrotliContentEncoder.class,GzipContentEncoder.class})
@ContentReaders({ JSONContentReader.class, JSONPatchContentReader.class })

@Deprecated
//Use FileDownloadHandler instead
public class ResourceController
{
    private final ServerApplication serverApplication;
    private final FileCache cache; 
    private int cacheControlMaxAge = 300;
    private String cacheControlValue = "public";
    private final boolean cacheControl;
    private final HashMap<String,String> hashMap;
    private final HashSet<String> doNotCacheFiles;  
    private final MessageDigest digest; 
    private final String pathPrefixFind;
    private final String pathPrefixReplace;
    
    public final static boolean TESTING=true;

    public ResourceController(ServerApplication serverApplication) throws Throwable
    {
        this.cache=serverApplication.getFileCache();
        this.cacheControl= serverApplication.getConfiguration().getBooleanValue("ResourceController.cacheControl", true);
        this.cacheControlMaxAge = serverApplication.getConfiguration().getIntegerValue("ResourceController.cacheControl.maxAgeS", 3600*24);
        this.cacheControlValue = serverApplication.getConfiguration().getValue("ResourceController.cacheControl.controlValue", "public");
        this.pathPrefixFind= serverApplication.getConfiguration().getValue("ResourceController.pathPrefixFind", null);
        this.pathPrefixReplace = serverApplication.getConfiguration().getValue("ResourceController.pathPrefixReplace", null);
        this.serverApplication = serverApplication;
        this.hashMap=new HashMap<>();
        this.doNotCacheFiles=new HashSet<>();
        this.digest = MessageDigest.getInstance("SHA-256");
        
    }

    public void clearCache()
    {
        this.cache.removeAll();
        synchronized(this)
        {
            this.hashMap.clear();
        }
    }
    
    public void doNotCache(String...files)
    {
        for (String file:files)
        {
            this.doNotCacheFiles.add(file);
        }
    }
    
    public String getHash(String file)
    {
        try
        {
            String fileName=this.cache.getLocalDirectory()+File.separator+file;
            synchronized(this)
            {
                String hash=this.hashMap.get(file);
                if (hash!=null)
                {
                    return hash;
                }
            }
            String hash=FileUtils.computeHash(this.digest,new File(fileName),65536);
            synchronized(this)
            {
                this.hashMap.put(file, hash);
            }
            return hash;
        }
        catch (Throwable e)
        {
            return null;
        }
        
    }
    
    @GET
    @Path("/resources/{+}")
    @Log(responseContent=false)
    public void resource(@PathParam(PathParam.AT_LEAST_ONE_SEGMENT) String file, Context context, Trace parent) throws Throwable
    {
        if (this.pathPrefixFind!=null)
        {
            if (file.startsWith(this.pathPrefixFind))
            {
                file=file.replaceFirst(pathPrefixFind, pathPrefixReplace);
            }
        }
        HttpServletResponse response = context.getHttpServletResponse();
        if (file!=null)
        {
            if (file.contains(".."))
            {
                response.setStatus(HttpStatus.NOT_FOUND_404);
                return;
            }
        }
//        System.out.println(file);
        try (Trace trace=new Trace(parent,"ResourceController.resource"))
        {
            trace.setDetails(file);
            context.setCaptured(true);

            String external=null;
            if (Element.HREF_LOCAL_DIRECTORY!=null)
            {
                int colon=file.indexOf(":");
                if (colon>0)
                {
                    int slash=file.lastIndexOf('/',colon);
                    external=file.substring(slash+1);
                    String root=file.substring(0,slash+1);
                    file=root+external.substring(colon-slash+2);
                    
                }
            }
            file=FileUtils.toNativePath(file);
            byte[] bytes;
            try
            {
                if (this.cacheControl==false)
                {
                    this.cache.remove(file);
                }
                bytes = this.cache.get(parent, file);
                if (TESTING)
                {
//                    Testing.printf("Resource:"+file);
                }
            }
            catch (Throwable t)
            {
                if (external!=null)
                {
                    int colon=external.indexOf(':');
                    int firstSlash=external.indexOf('/',colon+3);
                    String endPoint=external.substring(0,firstSlash);
                    String pathAndQuery=external.substring(firstSlash);
                    
                    JSONClient client=new JSONClient(this.serverApplication.getTraceManager(),this.serverApplication.getLogger(),endPoint);
                    BinaryResponse binaryResponse=client.getBinary(parent, null, pathAndQuery);
                    
                    String localDirectory=this.cache.getLocalDirectory();
                    String fileName=localDirectory+File.separator+file;
                    String dirs=FileUtils.toNativePath(localDirectory+File.separator+file.substring(0,file.lastIndexOf(File.separator)));
                    new File(dirs).mkdirs();
                    bytes=binaryResponse.get();
                    FileUtils.writeBinaryFile(fileName, bytes);
                    
                }
                trace.close(t);
                if (TESTING)
                {
                    Debugging.log("Resource: "+file + " not found\r\n");
                }
                response.setHeader("Cache-Control","no-store, no-cache, must-revalidate, max-age=0");
                response.setStatus(HttpStatus.NOT_FOUND_404);
                return;
            }
            String contentType = this.serverApplication.getContentTypeMappings().getContentType(file);
            if (contentType != null)
            {
                response.setContentType(contentType);
            }
            if (file.endsWith(".gz"))
            {
                response.setHeader("Content-Encoding", "gzip");
            }
//            response.setContentLength(bytes.length);
            
            boolean cacheControl=this.cacheControl;
            if (this.doNotCacheFiles.contains(file))
            {
                cacheControl=false;
            }
            else
            {
                int lastIndex=file.lastIndexOf('.');
                if (lastIndex>=0)
                {
                    String pattern="*"+file.substring(lastIndex);
                    if (this.doNotCacheFiles.contains(pattern))
                    {
                        cacheControl=false;
                    }
                }
            }
                    
            if (cacheControl)
            {
                response.setHeader("Cache-Control",(this.cacheControlValue == null || this.cacheControlValue.length() == 0) ? "max-age=" + this.cacheControlMaxAge : this.cacheControlValue + ",max-age=" + this.cacheControlMaxAge);
            }
            else
            {
                response.setHeader("Cache-Control","no-store, no-cache, must-revalidate, max-age=0");
            }
            
            response.setStatus(HttpStatus.OK_200);
            context.writeContent(bytes);
        }
    }
}
