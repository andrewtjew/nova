package org.nova.http.server;

import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class WebSocketHttpServletInputStream extends ServletInputStream
{
    int index;
    final private byte[] bytes;
    final private int offset;
    final private int length;

    WebSocketHttpServletInputStream(byte[] bytes,int offset,int length)
    {
        this.bytes=bytes;
        this.offset=offset;
        this.length=length;
        this.index=0;
    }
    @Override
    public boolean isFinished()
    {
        return this.index>=length;
    }

    @Override
    public boolean isReady()
    {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener)
    {
        try
        {
            readListener.onDataAvailable();
            readListener.onAllDataRead();
        }
        catch (IOException e)
        {
        }
    }

    @Override
    public int read() throws IOException
    {
        if (this.index>=this.length)
        {
            return -1;
        }
        return this.bytes[this.offset+(this.index++)];
    }
    
}