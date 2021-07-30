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
package org.nova.collections;

import java.io.File;
import java.io.FileInputStream;

import org.nova.collections.ContentCache;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

public class FileCache extends ContentCache<String,byte[]>
{
    final public static boolean TEST_PRINT=false;
    
    final private String sharedDirectory;
    final private String localDirectory;

	public FileCache(FileCacheConfiguration configuration) throws Exception
	{
		super(configuration.capacity,configuration.maxAgeMs,configuration.maxSize,configuration.freeMemoryCapacity);
        this.sharedDirectory=new File(FileUtils.toNativePath(configuration.sharedDirectory)).getCanonicalPath();
        this.localDirectory=new File(FileUtils.toNativePath(configuration.localDirectory)).getCanonicalPath();
	}
	
	public void preload(Trace trace) throws Throwable
	{
		File directory=new File(sharedDirectory);
		for (String file:directory.list())
		{
			get(trace,file);
		}
	}
    
    public String getLocalDirectoy()
    {
        return this.localDirectory;
    }
    public String getSharedDirectoy()
    {
        return this.sharedDirectory;
    }
	
	static byte[] readFile(File file) throws Exception
    {
        if (file.exists()==false)
        {
            return null;
        }
        if (file.isDirectory()==true)
        {
            return null;
        }
        long length=file.length();
        if (length>Integer.MAX_VALUE)
        {
            throw new Exception("File too big. Filename="+file.getCanonicalPath());
        }
        try (FileInputStream stream=new FileInputStream(file))
        {
            byte[] bytes=new byte[(int)file.length()];
            stream.read(bytes);
            return bytes;
        }
    }
    	
	@Override
	protected ValueSize<byte[]> load(Trace trace, String filePath) throws Throwable
	{
//	    if (filePath.startsWith("ex))
	    
	    String fullPath=this.localDirectory+File.separator+filePath;
	    if (TEST_PRINT)
	    {
	        System.out.println("FileCache.local:"+fullPath);
	    }
        File file=new File(FileUtils.toNativePath(fullPath));
        if (file.getCanonicalPath().startsWith(this.localDirectory))
        {
            byte[] bytes=readFile(file);
            if (bytes!=null)
            {
                return new ValueSize<byte[]>(bytes,bytes.length);
            }
        }
        fullPath=this.sharedDirectory+File.separator+filePath;
        file=new File(FileUtils.toNativePath(fullPath));
        if (TEST_PRINT)
        {
            System.out.println("FileCache.shared:"+fullPath);
        }
        if (file.getCanonicalPath().startsWith(this.sharedDirectory))
        {
            byte[] bytes=readFile(file);
            if (bytes==null)
            {
                throw new Exception("File not found: "+filePath);
            }
            return new ValueSize<byte[]>(bytes,bytes.length);
        }
        throw new Exception("Invalid file: "+filePath);
	}
	
	public String getSharedDirectory()
	{
		return this.sharedDirectory;
	}
	
	public String getLocalDirectory()
	{
		return this.localDirectory;
	}
}
