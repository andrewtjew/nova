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
package org.nova.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.nova.utils.TypeUtils;

public class SecurityUtils
{
    public static byte[] computeHashSHA256(byte[] data) throws Throwable
    {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.update(data, 0, data.length);
        return sha256.digest();
    }
    
    public static byte[] computeHashSHA256(int[] data,int dataBlockSize) throws Throwable
    {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        int index=0;
        byte[] buffer=new byte[dataBlockSize*4];
        while (index<data.length)
        {
            if (data.length-index<dataBlockSize)
            {
                dataBlockSize=data.length-index;
            }
            TypeUtils.toBytesLittleEndian(data, buffer,index,0,dataBlockSize);
            sha256.update(buffer, 0, buffer.length);
        }
            
        return sha256.digest();
    }
    
    public static final byte[] encrypt(String password,String salt,byte[] data) throws Exception
    {
        SecretKey secretKey = buildKey(password,salt);
        Cipher AesCipher = Cipher.getInstance("AES");
        AesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return AesCipher.doFinal(data);
    }

    public static final byte[] encrypt(SecretKey secretKey,byte[] data) throws Exception
    {
        Cipher AesCipher = Cipher.getInstance("AES");
        AesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return AesCipher.doFinal(data);
    }


    static public SecretKey buildKey(String password,String salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1000, 256);
        SecretKey secretKey=factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(),"AES");
    }
    
    public static byte[] decrypt(String password,String salt,byte[] bytes) throws Exception
    {
        SecretKey secretKey = buildKey(password,salt);
        Cipher AesCipher = Cipher.getInstance("AES");
        AesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        return AesCipher.doFinal(bytes);
    }
    public static byte[] decrypt(SecretKey secretKey,byte[] bytes) throws Exception
    {
        Cipher AesCipher = Cipher.getInstance("AES");
        AesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        return AesCipher.doFinal(bytes);
    }
    
    
    
}
