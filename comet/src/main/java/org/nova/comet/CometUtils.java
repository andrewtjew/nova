package org.nova.comet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.io.Files;

public class CometUtils
{
    static public String toNativePath(String path)
    {
        return path.replace("\\",File.separator).replace("/", File.separator);
    }
    static public String incrementVersion(String version,long majorIncrement,long minorIncrement)
    {
        int index=version.lastIndexOf('.');
        if (index<=0)
        {
            version="0.0";
            index=version.lastIndexOf('.');
        }
        String minor=version.substring(index+1);
        long minorNumber=minorIncrement;
        try
        {
            minorNumber+=Long.parseLong(minor);
        }
        catch (Throwable t)
        {
            minorNumber=0;
        }
        String major=version.substring(0,index);
        long majorNumber=majorIncrement;
        try
        {
            majorNumber+=Long.parseLong(major);
        }
        catch (Throwable t)
        {
            majorNumber=0;
        }
        version=majorNumber+"."+minorNumber;
        return version;
    }

    static public String exec(String directory,String command,boolean out) throws Exception
    {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
        builder.redirectErrorStream(true).directory(new File(directory));
        java.lang.Process process = builder.start();
        StringBuilder sb=new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            for (String line=reader.readLine();line!=null;line=reader.readLine())
            {
                if (out)
                {
                    System.out.println(line);
                }
                sb.append(line);
            }
        }
        return sb.toString();
    }
    static public String exec(String directory,String command) throws Exception
    {
        return CometUtils.exec(directory,command,true);
    }
    static public boolean existsFile(String filePath) throws IOException
    {
        java.io.File file=new java.io.File(filePath);
        return file.exists();
    }

    static public boolean deleteFile(String filePath) throws IOException
    {
        java.io.File file=new java.io.File(filePath);
        return file.delete();
    }
    static public void copyFile(String source,String destination) throws IOException
    {
        Files.copy(new java.io.File(toNativePath(source)),new java.io.FileOutputStream(new java.io.File(toNativePath(destination))));
    }

    static public void moveFile(String source,String destination) throws IOException
    {
        Files.move(new java.io.File(toNativePath(source)),new java.io.File(toNativePath(destination)));
    }

    static public boolean createDirectory(String path) throws IOException
    {
        java.io.File file=new java.io.File(toNativePath(path));
        return file.mkdir();
    }

    static private void deleteDirectory(java.io.File sourcePathFile) throws Exception
    {
        java.io.File[] files = sourcePathFile.listFiles();
        for (java.io.File sourceFile : files)
        {
            if (sourceFile.isDirectory())
            {
                deleteDirectory(sourceFile);
            }
            sourceFile.delete();
        }
        sourcePathFile.delete();
    }
    static public void deleteDirectory(String sourcePath) throws Exception
    {
        java.io.File sourcePathFile=new java.io.File(toNativePath(sourcePath));
        if (sourcePathFile.exists()==false)
        {
            return;
        }
        if (sourcePathFile.isDirectory()==false)
        {
            throw new Exception("Not a directory: sourcePath="+sourcePath);
        }
        deleteDirectory(sourcePathFile);
    }


    static public void cloneDirectory(String sourcePath,String destinationPath) throws Exception
    {
        java.io.File sourcePathFile=new java.io.File(toNativePath(sourcePath));
        if (sourcePathFile.isDirectory()==false)
        {
            throw new Exception("Not a directory: sourcePath="+sourcePath);
        }
        java.io.File destinationPathFile=new java.io.File(toNativePath(destinationPath));
        if (destinationPathFile.exists())
        {
            throw new Exception("Cannot clone to existing directory or file: destinationPath="+destinationPath);
        }
        destinationPathFile.mkdir();
        java.io.File[] files = sourcePathFile.listFiles();
        for (java.io.File sourceFile : files)
        {
            if (sourceFile.isDirectory() == false)
            {
                String name=sourceFile.getName();
                java.io.File destinationFile=new java.io.File(destinationPath + java.io.File.separatorChar + name);
//                destinationFile.createNewFile();
                Files.copy(sourceFile,destinationFile);
            }
            else
            {
                String name = sourceFile.getName();
                cloneDirectory(sourceFile.getCanonicalPath(), destinationPath + java.io.File.separator + name);
            }
        }
    }

    static public void copyDirectory(String sourcePath,String destinationPath) throws Exception
    {
        java.io.File sourcePathFile=new java.io.File(toNativePath(sourcePath));
        if (sourcePathFile.exists()==false)
        {
            return;
        }
        if (sourcePathFile.isDirectory()==false)
        {
            throw new Exception("Source not a directory: sourcePath="+sourcePath);
        }
        java.io.File destinationPathFile=new java.io.File(toNativePath(destinationPath));
        if (destinationPathFile.exists())
        {
            if (destinationPathFile.isDirectory()==false)
            {
                throw new Exception("Destination not a directory: destinationPath="+destinationPath);
            }
        }
        else
        {   
            destinationPathFile.mkdir();
        }
        java.io.File[] files = sourcePathFile.listFiles();
        for (java.io.File sourceFile : files)
        {
            if (sourceFile.isDirectory() == false)
            {
                String name=sourceFile.getName();
                java.io.File destinationFile=new java.io.File(destinationPath + java.io.File.separatorChar + name);
                Files.copy(sourceFile,destinationFile);
            }
            else
            {
                String name = sourceFile.getName();
                copyDirectory(sourceFile.getCanonicalPath(), destinationPath + java.io.File.separator + name);
            }
        }
    }
    
    static public String getLocalHostName() throws Exception
    {
        return org.nova.utils.Utils.getLocalHostName();
    }
    public static String now_UTC_ISO()
    {
        return ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_DATE_TIME);
    }
    static public String readTextFile(String fileName,String encoding) throws Exception
    {
        return org.nova.utils.FileUtils.readTextFile(fileName, encoding);
    }

    static public String readTextFile(String fileName) throws Exception
    {
        return org.nova.utils.FileUtils.readTextFile(fileName);
    }

    static public void writeTextFile(String text,String fileName,String encoding) throws Exception
    {
        org.nova.utils.FileUtils.writeTextFile(fileName, text, encoding);
    }

    static public void writeTextFile(String text,String fileName) throws Exception
    {
        org.nova.utils.FileUtils.writeTextFile(fileName, text);
    }

}
