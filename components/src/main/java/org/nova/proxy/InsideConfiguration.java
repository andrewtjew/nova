package org.nova.proxy;

public class InsideConfiguration 
{
    public int proxyPort=11778;
    public int outsideListenPort=11777;
    public String proxyEndPoint="localhost";
    public int proxyReceiveBufferSize=65536;
    public int proxySendBufferSize=65536;
    public int proxyReadTimeout=30000;
    
    public int keepAliveInterval=10000;
    public int reconnectDelay=1000;
    public int maxReconnectDelay=10000;
    
    public String hostEndPoint="localhost";
    public int hostReceiveBufferSize=65536;
    public int hostSendBufferSize=65536;
    public int hostReadTimeout=30000;
    public int hostPort=11779;
}