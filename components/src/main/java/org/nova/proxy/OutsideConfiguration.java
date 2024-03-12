package org.nova.proxy;

public class OutsideConfiguration
{
    public int outsideBacklog=5;
    public int outsideReceiveBufferSize=65536;
    public int outsideSendBufferSize=65536;
    public int outsideReadTimeout=30000;
    public int outsideSendTimeout=-1;
    public long sendPacketsSize=300_000_000;

    public int insidePort=11778;
    public int insideReceiveBufferSize=65536;
    public int insideSendBufferSize=65536;
}