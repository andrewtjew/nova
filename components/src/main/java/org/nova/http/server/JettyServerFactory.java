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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.nova.utils.FileUtils;

public class JettyServerFactory
{
    static public Server createServer(ThreadPool threadPool, int port)
    {
        Server server = new Server(threadPool);
        HttpConfiguration config = new HttpConfiguration();
        config.setOutputBufferSize(65536);
//        config.setOutputBufferSize(8192);
        ServerConnector connector = new ServerConnector(server,new HttpConnectionFactory(config));
        connector.setIdleTimeout(30*60*1000);
        connector.setPort(port);
        server.addConnector(connector);
        return server;
    }

    static public Server createServer(int threads, int port)
    {
        return createServer(new ExecutorThreadPool(threads, threads, 0), port);
    }

    static public Server createHttpsServer(int threads, int port, String serverCertificateKeyStorePath, String serverCertificatePassword,String clientCertificateKeyStorePath,String clientCertificatePassword,String keyManagerPassword)
    {
        HttpConfiguration config=new HttpConfiguration();
        config.setOutputBufferSize(65536);
        config.setRequestHeaderSize(8192);
        config.setResponseHeaderSize(8192);
        config.setSecurePort(port);
        return createHttpsServer(new ExecutorThreadPool(threads, threads, 0),config,serverCertificateKeyStorePath,serverCertificatePassword,clientCertificateKeyStorePath,clientCertificatePassword,keyManagerPassword);
    }
    
    static public Server createHttpsServer(ThreadPool threadPool, HttpConfiguration config, String serverCertificateKeyStorePath, String serverCertificateKeyPassword,String clientCertificateKeyStorePath,String clientCertificatePassword,String keyManagerPassword)
    {
        Server server = new Server(threadPool);
        config.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslContextFactory = new SslContextFactory();
        if ((serverCertificateKeyPassword!=null)&&(serverCertificateKeyStorePath!=null))
        {
            sslContextFactory.setKeyStorePath(FileUtils.toNativePath(serverCertificateKeyStorePath));
            sslContextFactory.setKeyStorePassword(serverCertificateKeyPassword);
        }
        if ((clientCertificatePassword!=null)&&(clientCertificateKeyStorePath!=null))
        {
            sslContextFactory.setNeedClientAuth(true);
            sslContextFactory.setTrustStorePath(FileUtils.toNativePath(clientCertificateKeyStorePath));
            sslContextFactory.setTrustStorePassword(clientCertificatePassword);
        }

        
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);
//        sslContextFactory.setTrustAll(true);
//        sslContextFactory.setRenegotiationAllowed(true);
        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(config));
        sslConnector.setPort(config.getSecurePort());
        server.setConnectors(new Connector[]{sslConnector});
        
        return server;
    }
}
