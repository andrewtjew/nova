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
package org.nova.frameworks;

import org.apache.http.client.HttpClient;
import org.nova.http.client.HttpClientConfiguration;
import org.nova.http.client.HttpClientFactory;
import org.nova.http.client.JSONClient;

public class ServerApplicationUtils
{
    static class HttpClientEndPointConfiguration
    {
        public String clientCertificateStorePath;
        public String serverCertificateStorePath;
        public String clientCertificatePasswordKey;
        public String serverCertificatePasswordKey;
        public String endPoint;
        public String clusterName;
        public HttpClientConfiguration httpClientConfiguration;
        public long reconnectWaitMs=0;
        public long idleConnectionTimeoutMs=0;
    }
    
    public static JSONClient createJSONClient(ServerApplication application,String configurationName) throws Throwable
    {
        HttpClientEndPointConfiguration configuration=application.getConfiguration().getJSONObject(configurationName, null, HttpClientEndPointConfiguration.class);
        if (configuration==null)
        {
            return null;
        }
        HttpClient client;
        if (configuration.endPoint.startsWith("https://"))
        {
            String clientCertificateStorePassword=application.getVault().get(configuration.clientCertificatePasswordKey);
            String serverCertificateStorePassword=application.getVault().get(configuration.serverCertificatePasswordKey);
            client=HttpClientFactory.createSSLClient(configuration.httpClientConfiguration, configuration.clientCertificateStorePath, clientCertificateStorePassword, configuration.serverCertificateStorePath, serverCertificateStorePassword, configuration.clusterName);
        }
        else
        {
            client=HttpClientFactory.createClient(configuration.httpClientConfiguration);
        }

        
        return new JSONClient(application.getTraceManager(),application.getLogger(),application.getTimerScheduler(),configuration.idleConnectionTimeoutMs,configuration.reconnectWaitMs,configuration.endPoint,client);
    }
    public static JSONClient createClient(ServerApplication application,String configurationName) throws Throwable
    {
        HttpClientEndPointConfiguration configuration=application.getConfiguration().getJSONObject(configurationName, null, HttpClientEndPointConfiguration.class);
        if (configuration==null)
        {
            return null;
        }
        HttpClient client;
        if (configuration.endPoint.startsWith("https://"))
        {
            String clientCertificateStorePassword=application.getVault().get(configuration.clientCertificatePasswordKey);
            String serverCertificateStorePassword=application.getVault().get(configuration.serverCertificatePasswordKey);
            client=HttpClientFactory.createSSLClient(configuration.httpClientConfiguration, configuration.clientCertificateStorePath, clientCertificateStorePassword, configuration.serverCertificateStorePath, serverCertificateStorePassword, configuration.clusterName);
        }
        else
        {
            client=HttpClientFactory.createClient(configuration.httpClientConfiguration);
        }

        
        return new JSONClient(application.getTraceManager(),application.getLogger(),configuration.endPoint,client);
    }
}
