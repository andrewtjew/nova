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
package org.nova.net;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.nova.utils.FileUtils;

public class SocketUtils
{


    public static KeyManager[] getKeyManagers(String keyStorePassword, String keyStorePath, String keyManagerPassword, String algorithm) throws Exception
    {
        if (keyStorePath!= null)
        {
            if (algorithm == null)
            {
                algorithm = KeyManagerFactory.getDefaultAlgorithm();
            }

            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream inputStream = new FileInputStream(FileUtils.toNativePath(keyStorePath)))
            {
                keyStore.load(inputStream, keyStorePassword.toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
            keyManagerFactory.init(keyStore, keyManagerPassword.toCharArray());
            return keyManagerFactory.getKeyManagers();
        }
        return null;
    }

    public static TrustManager[] getTrustManagers(String keyStorePassword, String keyStorePath, String algorithm) throws Exception
    {
        if (keyStorePath!= null)
        {
            if (algorithm == null)
            {
                algorithm = KeyManagerFactory.getDefaultAlgorithm();
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream inputStream = new FileInputStream(FileUtils.toNativePath(keyStorePath)))
            {
                keyStore.load(inputStream, keyStorePassword.toCharArray());
            }
            trustManagerFactory.init(keyStore);
            return trustManagerFactory.getTrustManagers();
        }
        return getTrustAllManagers();
    }

    public static TrustManager[] getTrustAllManagers()
    {
        return new TrustManager[]
        {
            new X509TrustManager() 
            {
                public X509Certificate[] getAcceptedIssuers() 
                {
                    return null;
                }
    
                public void checkClientTrusted(X509Certificate[] certs, String authType) 
                {
                }
    
                public void checkServerTrusted(X509Certificate[] certs, String authType) 
                {
                }
            }
        };
    }
}
