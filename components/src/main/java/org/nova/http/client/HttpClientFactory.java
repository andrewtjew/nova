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
package org.nova.http.client;

import java.io.File;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.nova.utils.FileUtils;
import org.nova.utils.Utils;

public class HttpClientFactory
{
    static private boolean verify(String name, SSLSession session)
    {
        try
        {
            for (Certificate certificate:session.getPeerCertificates())
            {
                if (certificate instanceof X509Certificate)
                {
                    X509Certificate x509=(X509Certificate)certificate;
                    Collection<List<?>> alternativeNames=x509.getSubjectAlternativeNames();
                    if (alternativeNames!=null)
                    {
                        //For now, we don't support alternate name in certficates
                        //This is OK as long as we create our own certificates.
                        for (List<?> list:x509.getSubjectAlternativeNames())
                        {
                            for (Object object:list)
                            {
                                //If we support alternate name, we check here...
                            }
                        }
                    }
                    else
                    {
                        Principal principal=x509.getIssuerX500Principal();
                        if (principal!=null)
                        {
                            String principalName=principal.getName();
                            String[] keyValues=Utils.split(principalName, ',');
                            for (String keyValue:keyValues)
                            {
                                String[] pair=Utils.split(keyValue, '=');
                                if (pair.length==2)
                                {
                                    if ("CN".equals(pair[0]))
                                    {
                                        if (name.equals(pair[1]))
                                        {
                                            return true;
                                        }
                                    }
                                            
                                }
                                
                            }
                        }
                    }
                }
            }
            return false;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    static class ClusterNameVerifier implements HostnameVerifier
    {
        final private String clusterName;
        
        public ClusterNameVerifier(String clusterName)
        {
            this.clusterName=clusterName;
        }
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            return HttpClientFactory.verify(this.clusterName, session);
        }
        
    }
    
    final static HostnameVerifier STRICT_HOSTNAME_VERIFIER=new HostnameVerifier()
    {
        
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            if ("localhost".equals(hostname))
            {
                return true;
            }
            return HttpClientFactory.verify(hostname, session);
        }
    };

    final static HostnameVerifier TRUE_HOSTNAME_VERIFIER=new HostnameVerifier()
    {
        
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    };
    
    
    static public HttpClient createSSLClient(HttpClientConfiguration configuration,String clientCertficateStorePath, String clientCertficateStorePassword,String serverCertificateStorePath,String serverCertificateStorePassword,String clusterName) throws Throwable
    {
        return createSSLClient(configuration,clientCertficateStorePath, clientCertficateStorePassword,serverCertificateStorePath,serverCertificateStorePassword,clusterName,"TLSv1.2",null);
    }

    static private HttpClientConnectionManager buildConnectionManager(HttpClientConfiguration configuration,SSLConnectionSocketFactory sslConnectionSocketFactory)
    {
        if (configuration==null)
        {
            configuration=new HttpClientConfiguration();
        }
        PoolingHttpClientConnectionManager connectionManager=new PoolingHttpClientConnectionManager();
        if (sslConnectionSocketFactory!=null)
        {
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory> create().register("https", sslConnectionSocketFactory)
                    .build(); 
            connectionManager=new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        }
        else
        {
            connectionManager=new PoolingHttpClientConnectionManager();
        }
        connectionManager.setDefaultMaxPerRoute(configuration.maxPerRoute);
        connectionManager.setMaxTotal(configuration.maxConnections);
        return connectionManager;
    }
    
    static private RequestConfig buildRequestConfig(HttpClientConfiguration configuration)
    {
        if (configuration==null)
        {
            configuration=new HttpClientConfiguration();
        }
        RequestConfig config=RequestConfig.custom().
                setSocketTimeout(configuration.socketTimeoutMs).
                setConnectTimeout(configuration.connectTimeoutMs).
                setExpectContinueEnabled(configuration.expectContinueEnabled).
                setConnectionRequestTimeout(configuration.connectionRequestTimeoutMs).
                build();
        return config;
    }

    static public HttpClient createSSLClient(HttpClientConfiguration configuration,String clientCertficateStorePath, String clientCertficateStorePassword,String serverCertificateStorePath,String serverCertificateStorePassword,String clusterName,String tls,HostnameVerifier verifier) throws Throwable
    {
//        KeyStore clientCertficateStore=CertificateUtils.getKeyStore(new PathResource(new File(FileUtils.toNativePath(clientCertficateStorePath))), "JKS", null, clientCertficateStorePassword);
//        KeyStore serverCertificateStore=CertificateUtils.getKeyStore(new PathResource(new File(FileUtils.toNativePath(serverCertificateStorePath))), "JKS", null, serverCertificateStorePassword);
        KeyStore clientCertficateStore=KeyStore.getInstance(new File(FileUtils.toNativePath(clientCertficateStorePath)), clientCertficateStorePassword.toCharArray());
        KeyStore serverCertificateStore=KeyStore.getInstance(new File(FileUtils.toNativePath(serverCertificateStorePath)), serverCertificateStorePassword.toCharArray());
        SSLContextBuilder contextBuilder=new SSLContextBuilder().
                loadKeyMaterial(clientCertficateStore,clientCertficateStorePassword.toCharArray()).  
                loadTrustMaterial(serverCertificateStore,null);


        if (verifier==null)
        {
            verifier=clusterName!=null?new ClusterNameVerifier(clusterName):STRICT_HOSTNAME_VERIFIER;
        }
        SSLConnectionSocketFactory connectionSocketFactory=new SSLConnectionSocketFactory(contextBuilder.build(),new String[]{tls},null,verifier);

        RequestConfig config=buildRequestConfig(configuration);
        HttpClients.custom().setDefaultRequestConfig(config);
        HttpClientConnectionManager connectionManager=buildConnectionManager(configuration,connectionSocketFactory);
        
        return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(config).setSSLSocketFactory(connectionSocketFactory).build();
    }
    static public HttpClient createSSLClient(HttpClientConfiguration configuration,String clientCertficateStorePath, String clientCertficateStorePassword,String serverCertificateStorePath,String serverCertificateStorePassword) throws Throwable
    {
        return createSSLClient(configuration,clientCertficateStorePath,clientCertficateStorePassword,serverCertificateStorePath,serverCertificateStorePassword,null,"TLSv1.2",null);
    }
    static public HttpClient createSSLClient(HttpClientConfiguration configuration) throws Throwable
    {
        SSLContextBuilder contextBuilder=new SSLContextBuilder().loadTrustMaterial(null,new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory connectionSocketFactory=new SSLConnectionSocketFactory(contextBuilder.build(),new String[]{"TLSv1.2"},null,new HostnameVerifier()
        {
            @Override
            public boolean verify(String hostname, SSLSession session)
            {
                return true;
            }
        });

        if (configuration==null)
        {
            configuration=new HttpClientConfiguration();
        }

        RequestConfig config=buildRequestConfig(configuration);
        HttpClients.custom().setDefaultRequestConfig(config);
        HttpClientConnectionManager connectionManager=buildConnectionManager(configuration,connectionSocketFactory);
        
        return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(config).setSSLSocketFactory(connectionSocketFactory).build();
    }
    
    static public HttpClient createSSLClient() throws Throwable
    {
        return createSSLClient(null);
    }
    
    static public HttpClient createClient()
    {
        return createClient(new HttpClientConfiguration());
    }

    static public HttpClient createClient(HttpClientConfiguration configuration)
    {
        RequestConfig config=buildRequestConfig(configuration);
        HttpClientConnectionManager connectionManager=buildConnectionManager(configuration,null);

        return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(config).build();
    }

}
