package org.nova.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nova.utils.TypeUtils;

import com.mysql.fabric.xmlrpc.base.Array;

public class Packet
{
    private byte[] buffer;
    private int insideDataSize;

    
    static void read(InputStream inputStream,byte[] buffer,int offset,int size) throws Throwable
    {
        int totalRead=0;
        while (totalRead!=size)
        {
            int read=inputStream.read(buffer,offset+totalRead,size-totalRead);
            if (read<0)
            {
                throw new Exception();
            }
            totalRead+=read;
        }
    }
    
    
    public static Packet readFromProxyStream(InputStream inputStream) throws Throwable
    {
        byte[] dataSizeBytes=new byte[4]; 
        read(inputStream,dataSizeBytes,0,4);
        int insideDataSize=TypeUtils.bigEndianBytesToInt(dataSizeBytes, 0);
        byte[] buffer=new byte[insideDataSize+4];
        for (int i=0;i<4;i++)
        {
            buffer[i]=dataSizeBytes[i];
        }
        read(inputStream,buffer,4,insideDataSize);
        return new Packet(buffer,insideDataSize);
    }

    public Packet(byte[] buffer,int insideDataSize)
    {
        this.buffer=buffer;
        this.insideDataSize=insideDataSize;
    }

    public Packet()
    {
        this.buffer=new byte[4];
        TypeUtils.bigEndianIntToBytes(0,this.buffer,0);
        this.insideDataSize=0;
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
            this.insideDataSize=read+4;
            TypeUtils.bigEndianIntToBytes(insideDataSize,this.buffer,0);
        }
        return read;
    }
    public void writeToProxyStream(OutputStream outputStream) throws Throwable
    {
        outputStream.write(this.buffer,0,this.insideDataSize+4);
        outputStream.flush();
    }
    
    public int getDataSize()
    {
        return this.insideDataSize;
    }
    
    public int getPort()
    {
        return TypeUtils.bigEndianBytesToInt(this.buffer,4);
    }
    
    public void writeToStream(OutputStream outputStream) throws Throwable
    {
        outputStream.write(this.buffer,8,this.insideDataSize-4);
        outputStream.flush();
    }
}
