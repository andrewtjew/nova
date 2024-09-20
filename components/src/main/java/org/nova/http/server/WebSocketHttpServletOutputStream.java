package org.nova.http.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

public class WebSocketHttpServletOutputStream extends ServletOutputStream
{
    final private ByteArrayOutputStream outputStream;
    
    WebSocketHttpServletOutputStream(int bufferSize)
    {
        this.outputStream=new ByteArrayOutputStream(bufferSize);
    }
    @Override
    public boolean isReady()
    {
        return true;
    }
    @Override
    public void setWriteListener(WriteListener writeListener)
    {
        try
        {
            writeListener.onWritePossible();
        }
        catch (IOException e)
        {
        }
    }
    @Override
    public void write(int arg0) throws IOException
    {
        this.outputStream.write(arg0);
    }
    
    void reset()
    {
        this.outputStream.reset();
    }
    
    ByteArrayOutputStream getByteArrayOutputStream()
    {
        return this.outputStream;
    }
}