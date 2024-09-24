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
package org.nova.http.server;

import java.util.concurrent.Executors;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.MultiPartFormDataCompliance;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.nova.utils.FileUtils;

public class JettyServerFactory
{
    static private Server createServer(ThreadPool threadPool, int port)
    {
        Server server = new Server(threadPool);
        HttpConfiguration config = new HttpConfiguration();
        config.setOutputBufferSize(65536);
        config.setMultiPartFormDataCompliance(MultiPartFormDataCompliance.RFC7578);

        ServerConnector connector = new ServerConnector(server,new HttpConnectionFactory(config));
        connector.setIdleTimeout(30*60*1000);
        connector.setPort(port);
        server.addConnector(connector);
        return server;
    }

    final static boolean MIGRATE_VIRTUAL_THREADS=true;
    
    static private ThreadPool createThreadPool(int threads)
    {
        if (threads>0)
        {
            return new ExecutorThreadPool(threads, threads);
        }
        if (MIGRATE_VIRTUAL_THREADS)
        {
            if (threads<=0)
            {
                System.err.println("MIGRATE: Jetty is using virtual threads");
            }
            else
            {
                System.err.println("MIGRATE: Jetty max threads: "+threads);
            }
        }
        QueuedThreadPool threadPool=new QueuedThreadPool();
        threadPool.setVirtualThreadsExecutor(Executors.newVirtualThreadPerTaskExecutor());
        return threadPool;
    }
    static public Server createServer(int threads, int port)
    {
        return createServer(createThreadPool(threads), port);
    }
    static public Server createServer(int port)
    {
        return createServer(0,port);
    }

    static public Server createHttpsServer(int port, String serverCertificateKeyStorePath, String serverCertificatePassword,String clientCertificateKeyStorePath,String clientCertificatePassword,String keyManagerPassword)
    {
        return createHttpsServer(port, serverCertificateKeyStorePath, serverCertificatePassword,clientCertificateKeyStorePath,clientCertificatePassword,keyManagerPassword);
    }
    static public Server createHttpsServer(int threads, int port, String serverCertificateKeyStorePath, String serverCertificatePassword,String clientCertificateKeyStorePath,String clientCertificatePassword,String keyManagerPassword)
    {
        HttpConfiguration config=new HttpConfiguration();
        config.setOutputBufferSize(65536);
        config.setRequestHeaderSize(8192);
        config.setResponseHeaderSize(8192);
        config.setSecurePort(port);
        
        config.setMultiPartFormDataCompliance(MultiPartFormDataCompliance.RFC7578);
        return createHttpsServer(createThreadPool(threads),config,serverCertificateKeyStorePath,serverCertificatePassword,clientCertificateKeyStorePath,clientCertificatePassword,keyManagerPassword);
    }
    
    static private Server createHttpsServer(ThreadPool threadPool, HttpConfiguration config, String serverCertificateKeyStorePath, String serverCertificateKeyPassword,String clientCertificateKeyStorePath,String clientCertificatePassword,String keyManagerPassword)
    {
        Server server = new Server(threadPool);
        config.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory.Server sslContextFactoryServer = new SslContextFactory.Server();
        if ((serverCertificateKeyPassword!=null)&&(serverCertificateKeyStorePath!=null))
        {
            sslContextFactoryServer.setKeyStorePath(FileUtils.toNativePath(serverCertificateKeyStorePath));
            sslContextFactoryServer.setKeyStorePassword(serverCertificateKeyPassword);
        }
        if ((clientCertificatePassword!=null)&&(clientCertificateKeyStorePath!=null))
        {
            sslContextFactoryServer.setNeedClientAuth(true);
            sslContextFactoryServer.setTrustStorePath(FileUtils.toNativePath(clientCertificateKeyStorePath));
            sslContextFactoryServer.setTrustStorePassword(clientCertificatePassword);
        }

        
        sslContextFactoryServer.setKeyManagerPassword(keyManagerPassword);
//        sslContextFactory.setTrustAll(true);
//        sslContextFactory.setRenegotiationAllowed(true);
        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactoryServer, "http/1.1"), new HttpConnectionFactory(config));
        sslConnector.setPort(config.getSecurePort());
        server.setConnectors(new Connector[]{sslConnector});
        
        return server;
    }
}
