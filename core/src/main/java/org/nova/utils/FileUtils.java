/*******************************************************************************
 * Copyright (C) 2016-2019 Kat Fung Tjew
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
package org.nova.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils
{
    public static String readTextFile(String fileName, String charset) throws Exception
    {
        return readTextFile(fileName, Charset.forName(charset));
    }

    public static String readTextFile(String fileName) throws Exception
    {
        return readTextFile(fileName, StandardCharsets.UTF_8);
    }

    public static String readTextFile(String fileName, Charset charset) throws Exception
    {
        return new String(readFile(fileName), charset);
    }

    public static String toNativePath(String fileName)
    {
        return fileName.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    }

    public static byte[] readFile(String fileName) throws Exception
    {
        fileName = toNativePath(fileName);
        File file = new File(fileName);
        if (file.isDirectory() == true)
        {
            throw new Exception("File is a directory. Filename=" + fileName);
        }

        long length = file.length();
        if (length > Integer.MAX_VALUE)
        {
            throw new Exception("File too big. Filename=" + fileName);
        }

        try (FileInputStream stream = new FileInputStream(file))
        {
            byte[] bytes = new byte[(int) file.length()];
            stream.read(bytes);
            return bytes;
        }
    }

    public static byte[] readBytes(InputStream stream, int bufferSize) throws IOException
    {
        int count = 0;
        byte[] buffer = new byte[bufferSize];
        byte[] streamBuffer = new byte[bufferSize];
        for (int bytesRead = stream.read(buffer); bytesRead > 0; bytesRead = stream.read(buffer))
        {
            if (bytesRead + count > streamBuffer.length)
            {
                byte[] newStringBuffer = new byte[streamBuffer.length * 2];
                System.arraycopy(streamBuffer, 0, newStringBuffer, 0, count);
                streamBuffer = newStringBuffer;
            }
            System.arraycopy(buffer, 0, streamBuffer, count, bytesRead);
            count += bytesRead;
        }
        if (count==streamBuffer.length)
        {
            return streamBuffer;
        }
        byte[] bytes=new byte[count];
        System.arraycopy(streamBuffer, 0, bytes, 0, count);
        return bytes;
    }

    public static String readString(InputStream stream, int bufferSize, Charset charset) throws IOException
    {
        int count = 0;
        byte[] buffer = new byte[bufferSize];
        byte[] stringBuffer = new byte[bufferSize];
        for (int bytesRead = stream.read(buffer); bytesRead > 0; bytesRead = stream.read(buffer))
        {
            if (bytesRead + count > stringBuffer.length)
            {
                byte[] newStringBuffer = new byte[stringBuffer.length * 2];
                System.arraycopy(stringBuffer, 0, newStringBuffer, 0, count);
                stringBuffer = newStringBuffer;
            }
            System.arraycopy(buffer, 0, stringBuffer, count, bytesRead);
            count += bytesRead;
        }
        return new String(stringBuffer, 0, count, charset);
    }

    public static String readString(InputStream stream, Charset charset) throws IOException
    {
        return readString(stream, 65536, charset);
    }

    public static String readString(InputStream stream) throws IOException
    {
        return readString(stream, StandardCharsets.UTF_8);
    }

    public static void writeTextFile(String fileName, String text) throws Exception
    {
        writeTextFile(fileName, text, StandardCharsets.UTF_8);
    }

    public static void writeTextFile(String fileName, String text, Charset charset) throws Exception
    {
        File file = new File(FileUtils.toNativePath(fileName));
        if (file.isDirectory() == true)
        {
            throw new Exception("File is a directory. Filename=" + fileName);
        }

        try (OutputStream stream = new FileOutputStream(file))
        {
            stream.write(text.getBytes(charset));
        }
    }

    public static void writeTextFile(String fileName, String text, String encoding) throws Exception
    {
        File file = new File(FileUtils.toNativePath(fileName));
        if (file.isDirectory() == true)
        {
            throw new Exception("File is a directory. Filename=" + fileName);
        }

        try (OutputStream stream = new FileOutputStream(file))
        {
            stream.write(text.getBytes(encoding));
        }
    }

    public static void writeBinaryFile(String fileName, byte[] bytes, int offset,int lengtht) throws Exception
    {
        File file = new File(FileUtils.toNativePath(fileName));
        if (file.isDirectory() == true)
        {
            throw new Exception("File is a directory. Filename=" + fileName);
        }

        try (OutputStream stream = new FileOutputStream(file))
        {
            stream.write(bytes,offset,lengtht);
        }
    }
    public static void writeBinaryFile(String fileName, byte[] bytes) throws Exception
    {
        writeBinaryFile(fileName,bytes,0,bytes.length);
    }

    public static String computeHash(MessageDigest digest,File file, int buffer) throws Throwable
    {
        try (FileInputStream fis = new FileInputStream(file))
        {
            byte[] data = new byte[buffer];
            int read = 0;
            while ((read = fis.read(data)) != -1)
            {
                digest.update(data, 0, read);
            }
        }
        byte[] hashBytes = digest.digest();
        return TypeUtils.bigEndianByteArrayToHexString(hashBytes);
    }
    public static String computeHashSHA256(File file, int buffer) throws Throwable
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return computeHash(digest, file, buffer);
    }
 
    public static String computeHashSHA256(String file) throws Throwable
    {
        return computeHashSHA256(new File(file),4096);
    }
    public static String getFileNameWithExtension(String fullFilePath)
    {
        int index=fullFilePath.lastIndexOf(File.separatorChar);
        if (index<0)
        {
            index=0;
        }
        return fullFilePath.substring(index);
    }

}
