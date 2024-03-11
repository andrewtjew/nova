package org.nova.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import org.nova.utils.TypeUtils;


public class Packet
{
    private byte[] buffer;
    private int size;

    
    static int read(InputStream inputStream,byte[] buffer,int offset,int size) throws Throwable
    {
        int totalRead=0;
        while (totalRead!=size)
        {
            int read=inputStream.read(buffer,offset+totalRead,size-totalRead);
            if (read<0)
            {
                break;
            }
            totalRead+=read;
        }
        return totalRead;
    }
    
    
    public static Packet readFromProxyStream(InputStream inputStream) throws Throwable
    {
        byte[] dataSizeBytes=new byte[4]; 
        int totalRead;
        try
        {
            totalRead=read(inputStream,dataSizeBytes,0,4);
        }
        catch (SocketTimeoutException ex)
        {
            return null;
        }
        if (totalRead!=4)
        {
            throw new Exception("totalRead="+totalRead);
        }

        int dataSize=TypeUtils.bigEndianBytesToInt(dataSizeBytes, 0);
        byte[] buffer=new byte[dataSize+4];
        for (int i=0;i<4;i++)
        {
            buffer[i]=dataSizeBytes[i];
        }
        try
        {
            totalRead=read(inputStream,buffer,4,dataSize);
        }
        catch (SocketTimeoutException ex)
        {
            return null;
        }
        if (totalRead!=dataSize)
        {
            throw new Exception("totalRead="+totalRead+",insideDataSize="+dataSize);
        }
        return new Packet(buffer,dataSize);
    }

    public Packet(byte[] buffer,int size)
    {
        this.buffer=buffer;
        this.size=size;
    }

    public Packet()
    {
        this.buffer=new byte[4];
        TypeUtils.bigEndianIntToBytes(0,this.buffer,0);
        this.size=0;
    }
    
    public Packet(int capacity,int port)
    {
        this.buffer=new byte[capacity];
        TypeUtils.bigEndianIntToBytes(port,this.buffer,4);
    }
    public Packet(int port)
    {
        this.buffer=new byte[8];
        TypeUtils.bigEndianIntToBytes(0,this.buffer,0);
        TypeUtils.bigEndianIntToBytes(port,this.buffer,4);
    }
    
    public int readFromStream(InputStream inputStream) throws Throwable
    {
        int read=inputStream.read(this.buffer, 8, this.buffer.length-8);
        if (read>0)
        {
            this.size=read+4;
            TypeUtils.bigEndianIntToBytes(size,this.buffer,0);
        }
        return read;
    }
    public void writeToProxyStream(OutputStream outputStream) throws Throwable
    {
        outputStream.write(this.buffer,0,this.size+4);
        outputStream.flush();
    }
    
    public int size()
    {
        return this.size;
    }
    
    public int getPort()
    {
        return TypeUtils.bigEndianBytesToInt(this.buffer,4);
    }
    
    public void writeToStream(OutputStream outputStream) throws Throwable
    {
        outputStream.write(this.buffer,8,this.size-4);
        outputStream.flush();
    }
}
