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
package org.nova.logging;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map.Entry;

import org.nova.annotations.Description;
import org.nova.metrics.CountMeter;
import org.nova.utils.Utils;

import java.util.TreeMap;

public class LogDirectoryManager
{
    final private Object writeLock=new Object();
    final private File directory;
    final private String fullDirectoryPath;
    final private long reserveSpace;
    final private long maximumDirectorySize;
    final private long maximumFiles;
    private Throwable throwable;
    private TreeMap<String,File> map;
    private long directorySize;
    final private int maximumMakeSpaceAttempts;

    @Description("Make space failures")
    final CountMeter makeSpaceFailedMeter; 
    @Description("File delete failures")
    final CountMeter fileDeleteFailedMeter; 

    @Description("File delete attempts due to maximum files exceeded")
    final CountMeter maximumFilesDeleteMeter; 
    @Description("File delete attempts due to directory size exceeded")
    final CountMeter directorySizeDeleteMeter; 
    @Description("File delete attempts due to reserve space exceeded")
    final CountMeter reserveSpaceDeleteMeter; 
    
    public LogDirectoryManager(String directory,int maximumMakeSpaceAttemps,long maximumFiles,long maximumSize,long reserveSpace) throws Exception
    {
        this.reserveSpace=reserveSpace;
        this.maximumFiles=maximumFiles;
        this.maximumDirectorySize=maximumSize;
        this.directory=new File(directory);
        this.maximumMakeSpaceAttempts=maximumMakeSpaceAttemps;
        
        if (this.directory.exists()==false)
        {
            this.directory.mkdirs();
        }
        else if (this.directory.isDirectory()==false)
        {
            throw new Exception("Not a directory: "+this.directory.getCanonicalPath());
        }
        this.map=new TreeMap<>();
        String[] fileNames=this.directory.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                int index=name.indexOf(".");
                if (index>=0)
                {
                    name=name.substring(0,index);
                }
                String zoneDateTime=name;
                index=name.indexOf("X");
                if (index>=0)
                {
                    zoneDateTime=name.substring(0,index);
                }
                try
                {
                    LocalDateTime dateTime=LocalDateTime.parse(zoneDateTime,Utils.LOCALDATETIME_FILENAME_FORMATTER);
                    return true;
                }
                catch(Throwable t)
                {
                    return false;
                }
            }
        });
        for (String fileName:fileNames)
        {
            File file=new File(this.directory,fileName);
            this.directorySize+=file.length();
            this.map.put(fileName, file);
        }

        this.makeSpaceFailedMeter=new CountMeter();
        this.reserveSpaceDeleteMeter=new CountMeter();
        this.directorySizeDeleteMeter=new CountMeter();
        this.maximumFilesDeleteMeter=new CountMeter();
        this.fileDeleteFailedMeter=new CountMeter();
        this.fullDirectoryPath=this.directory.getCanonicalPath();
    }
    
    private void deleteFirst()
    {
        try
        {
            Entry<String, File> entry=this.map.firstEntry();
            File file=entry.getValue();
            this.map.remove(entry.getKey());
            long fileSize=file.length();
            if (file.delete()==false)
            {
                if (file.exists()==false)
                {
                    this.directorySize-=fileSize;
                }
                else
                {
                    throw new Exception("Unable to delete file "+file.getName());
                }
            }
            else
            {
                this.directorySize-=fileSize;
            }
        }
        catch(Throwable t)
        {
            this.throwable=t;
            this.fileDeleteFailedMeter.increment();
        }
    }

    private boolean makeSpace()
    {
        try
        {
            if (this.maximumFiles>0)
            {
                int attempts=0;
                while ((this.map.size()>this.maximumFiles))
                {
                    this.maximumFilesDeleteMeter.increment();
                    deleteFirst();
                    if (++attempts>=this.maximumMakeSpaceAttempts)
                    {
                        this.makeSpaceFailedMeter.increment();
                        return false;
                    }
                }
            }
            if (this.maximumDirectorySize>0)
            {
                int attempts=0;
                while ((this.directorySize>this.maximumDirectorySize))
                {
                    this.directorySizeDeleteMeter.increment();
                    deleteFirst();
                    if (++attempts>=this.maximumMakeSpaceAttempts)
                    {
                        this.makeSpaceFailedMeter.increment();
                        return false;
                    }
                }
            }
            if (this.reserveSpace>0)
            {
                int attempts=0;
                while (this.directory.getFreeSpace()<this.reserveSpace)
                {
                    this.reserveSpaceDeleteMeter.increment();
                    deleteFirst();
                    if (++attempts>=this.maximumMakeSpaceAttempts)
                    {
                        this.makeSpaceFailedMeter.increment();
                        return false;
                    }
                }
            }
            return true;
        }
        catch (Throwable t)
        {
            this.makeSpaceFailedMeter.increment();
            synchronized (this)
            {
                this.throwable=t;
            }
            return false;
        }
    }
    
    public LogDirectoryInfo getLogDirectoryInfo()
    {
        LogDirectoryInfo info=new LogDirectoryInfo();
        info.totalSpace=this.directory.getTotalSpace();
        info.totalSpace=this.directory.getTotalSpace();
        info.freeSpace=this.directory.getFreeSpace();
        info.usableSpace=this.directory.getUsableSpace();
        File oldest=null;
        File newest=null;
        synchronized(this)
        {
            info.fileCount=this.map.size();
            info.directorySize=this.directorySize;
            if (info.fileCount>0)
            {
                oldest=this.map.firstEntry().getValue();
                newest=this.map.lastEntry().getValue();
            }
        }
        if (info.fileCount>0)
        {
            info.oldestFileDate=oldest.lastModified();
            info.oldestFileName=oldest.getName();
            info.oldestFileSize=oldest.length();

            info.newestFileDate=newest.lastModified();
            info.newestFileName=newest.getName();
            info.newestFileSize=newest.length();
        }
        return info;
    }
    
//  public File createFile(String extension) throws FileNotFoundException
//  {
//      makeSpace();
//      long now=System.currentTimeMillis();
//      String dateTime=Utils.millisToLocalDateTimeFileName(now);
//      String name=dateTime;
//        int counter=0;
//      while (this.map.containsKey(name))
//      {
//          synchronized (this)
//          {
//              counter++;
//          }
//            name=dateTime+"X"+counter;
//      }
//      return new File(this.fullDirectoryPath+File.separator+name+extension);
//  }
//    public File createFile(long millis,String extension) throws FileNotFoundException
//    {
//        makeSpace();
//        String dateTime=Utils.millisToLocalDateTimeFileName(millis);
//        String name=dateTime;
//        int counter=0;
//        while (this.map.containsKey(name))
//        {
//            synchronized (this)
//            {
//                counter++;
//            }
//            name=dateTime+"_"+counter;
//        }
//        return new File(this.fullDirectoryPath+File.separator+name+extension);
//    }

    public String getFullDirectoryPath()
    {
        return fullDirectoryPath;
    }

    public CountMeter getMakeSpaceFailedMeter()
    {
        return makeSpaceFailedMeter;
    }

    public CountMeter getFileDeleteFailedMeter()
    {
        return fileDeleteFailedMeter;
    }

    public CountMeter getMaximumFilesDeleteMeter()
    {
        return maximumFilesDeleteMeter;
    }

    public CountMeter getDirectorySizeDeleteMeter()
    {
        return directorySizeDeleteMeter;
    }

    public CountMeter getReserveSpaceDeleteMeter()
    {
        return reserveSpaceDeleteMeter;
    }
    
    public void write(ByteArrayOutputStream outputStream,long instanceMillis,String extension) throws FileNotFoundException, IOException
    {
        synchronized(this.writeLock)
        {
            makeSpace();
            String dateTime=Utils.millisToLocalDateTimeFileName(instanceMillis);
            String name=dateTime;
            int counter=0;
            while (this.map.containsKey(name))
            {
                synchronized (this)
                {
                    counter++;
                }
                name=dateTime+"X"+counter;
            }
            File file=new File(this.fullDirectoryPath+File.separator+name+extension);
            try (FileOutputStream fileOutputStream=new FileOutputStream(file))
            {
                this.map.put(name, file);
                outputStream.writeTo(fileOutputStream);
            }
        }
    }

    public FileOutputStream openFileOutputStream(long instanceMillis,String extension) throws FileNotFoundException, IOException
    {
        synchronized(this.writeLock)
        {
            makeSpace();
            String dateTime=Utils.millisToLocalDateTimeFileName(instanceMillis);
            String name=dateTime;
            int counter=0;
            while (this.map.containsKey(name))
            {
                synchronized (this)
                {
                    counter++;
                }
                name=dateTime+"X"+counter;
            }
            File file=new File(this.fullDirectoryPath+File.separator+name+extension);
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            this.map.put(name, file);
            return fileOutputStream;
        }
    }
    public File[] getSnapshotOfFiles()
    {
        synchronized(this.writeLock)
        {
            return this.map.values().toArray(new File[this.map.size()]);
        }
    }
}
