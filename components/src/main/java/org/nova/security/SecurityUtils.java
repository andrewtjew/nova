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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashSet;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.nova.utils.TypeUtils;

public class SecurityUtils
{
    public static final int ITERATIONS = 10000;
    public static final int KEY_LENGTH = 256;

    public static byte[] computeHashHMACSHA512(SecretKey key,byte[] data) throws Throwable
    {
        Mac mac=Mac.getInstance("HmacSHA512");
        mac.init(key);
        return mac.doFinal(data);
    }

    
    public static byte[] computeHashSHA256(byte[] data) throws Throwable
    {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.update(data, 0, data.length);
        return sha256.digest();
    }

    public static byte[] computeHashSHA256(int[] data, int dataBlockSize) throws Throwable
    {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        int index = 0;
        byte[] buffer = new byte[dataBlockSize * 4];
        while (index < data.length)
        {
            if (data.length - index < dataBlockSize)
            {
                dataBlockSize = data.length - index;
            }
            TypeUtils.toBytesLittleEndian(data, buffer, index, 0, dataBlockSize);
            sha256.update(buffer, 0, buffer.length);
        }

        return sha256.digest();
    }

    public static final byte[] encrypt(String password, String salt, byte[] data) throws Exception
    {
        SecretKey secretKey = buildKey(password, salt);
        Cipher AesCipher = Cipher.getInstance("AES");
        AesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return AesCipher.doFinal(data);
    }

    public static final byte[] encrypt(SecretKey secretKey, byte[] data) throws Exception
    {
        Cipher AesCipher = Cipher.getInstance("AES");
        AesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return AesCipher.doFinal(data);
    }

    public static byte[] decrypt(String password, String salt, byte[] bytes) throws Exception
    {
        SecretKey secretKey = buildKey(password, salt);
        try
        {
            Cipher AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.DECRYPT_MODE, secretKey);
            return AesCipher.doFinal(bytes);
        }
        finally
        {
            secretKey.destroy();
        }
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] bytes) throws Exception
    {
        Cipher AesCipher = Cipher.getInstance("AES");
        AesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        return AesCipher.doFinal(bytes);
    }

    static public String encrypt(SecretKey key,byte[] initializationVector,long businessId,long id) throws Throwable
    {
        if ((businessId<0)||(id<0))
        {
            throw new Exception();
        }
        if ((id>>>32)>0)
        {
            throw new Exception();
        }
        if ((businessId>>>32)>0)
        {
            throw new Exception();
        }
        long value=(businessId<<32)+id;
        byte[] bytes=TypeUtils.bigEndianLongToBytes(value);
        Cipher cipher = Cipher.getInstance("TripleDES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(initializationVector));
        byte[] encryptedBytes=cipher.doFinal(bytes);
        long encrypted=TypeUtils.bigEndianBytesToLong(encryptedBytes);
        int length=15;
        int digits=CODE_ALPHABET.length();
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<length;i++)
        {
            long index=Long.remainderUnsigned(encrypted, digits);
            sb.append(CODE_ALPHABET.charAt((int)index));
            encrypted=Long.divideUnsigned(encrypted, digits);
        }
 //       sb.append(CODE_ALPHABET.charAt(random%length));
        return sb.toString();
    }
    static public Long decrypt(SecretKey key,byte[] initializationVector,long businessId,String code) throws Throwable
    {
        int digits=CODE_ALPHABET.length();
        if (businessId<0)
        {
            throw new Exception();
        }
        if ((businessId>>>32)>0)
        {
            throw new Exception();
        }
        long encrypted=0;
        for (int i=code.length()-1;i>=0;i--)
        {
            char c=code.charAt(i);
            int index=CODE_ALPHABET.indexOf(c);
            if (index<0)
            {
                return null;
            }
            encrypted=encrypted*digits+index;
        }
        
        byte[] encryptedBytes=TypeUtils.bigEndianLongToBytes(encrypted);
        
        
        Cipher cipher = Cipher.getInstance("TripleDES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(initializationVector));
        byte[] bytes=cipher.doFinal(encryptedBytes);
        long value=TypeUtils.bigEndianBytesToLong(bytes);
        long decrypedBusinessId=value>>>32;
        if (decrypedBusinessId!=businessId)
        {
            return null;
        }
        long id=value&0xffffffffL;
        return id;
    }

    
    
    static public SecretKey buildKey3DES(byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException
    {
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
//        DESKeySpec spec = new DESKeySpec(key);
////        return factory.generateSecret(spec);
        return new SecretKeySpec(key, "TripleDES");
    }

    static public SecretKey buildKey(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public static boolean compareHash(String password, String salt, byte[] encryptedPassword) throws Throwable
    {
        if ((salt==null)||(encryptedPassword==null))
        {
            buildKey("password","salt");
            return false;
        }
        return Arrays.equals(hash(password,salt), encryptedPassword);
    }
    public static byte[] hash(String password, String salt) throws Throwable
    {
        SecretKey secretKey = buildKey(password, salt);
        try
        {
            return secretKey.getEncoded();
        }
        finally
        {
            secretKey.destroy();
        }
    }
    
    final static String CODE_ALPHABET = "ACEFGHJKMNPQRTWYZ23679";

    public static byte[] generateRandomBytes(int length)
    {
        byte[] bytes=new byte[length];
        RANDOM.nextBytes(bytes);
        return bytes;
    }
    
    
    public static String generateVerificationCode(int length)
    {
        byte[] bytes = new byte[length];
        bytes[0] = (byte) (RANDOM.nextInt(26) + 'A');
        for (int i = 1; i < length; i++)
        {
            do
            {
                bytes[i] = (byte) (RANDOM.nextInt(26) + 'A');
            }
            while (bytes[i] == bytes[i - 1]);
        }
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }
    
    public static String generateUserFixedUnique(long number,int length) throws Exception
    {
        StringBuilder sb=new StringBuilder();
        int index=0;
        int codes=CODE_ALPHABET.length()-1;
        while (number!=0)
        {
            sb.append(CODE_ALPHABET.charAt((int)(number%codes)));
            number=number/codes;
            index++;
            if (index>length-1)
            {
                break;
            }
        }
        if (number>0)
        {
            throw new Exception();
        }
        sb.append(CODE_ALPHABET.charAt(codes));
        index++;
        while (index<length)
        {
            sb.append(CODE_ALPHABET.charAt(RANDOM.nextInt()%codes));
            index++;
        }
        return sb.toString();
    }

    
    public static String generateUserVerificationCode(int length)
    {
        byte[] bytes = new byte[length];
        bytes[0] = (byte) (CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
        for (int i = 1; i < length; i++)
        {
            do
            {
                bytes[i] = (byte) (CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
            }
            while (bytes[i] == bytes[i - 1]);
        }
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    static private SecureRandom RANDOM=new SecureRandom();
    
    public static String generateNummericVerificationCode(int length)
    {
        byte[] bytes = new byte[length];
        bytes[0] = (byte) (RANDOM.nextInt(9) + '1');
        for (int i = 1; i < length; i++)
        {
            do
            {
                bytes[i] = (byte) (RANDOM.nextInt(10) + '0');
            }
            while (bytes[i] == bytes[i - 1]);
        }
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    public static boolean isGoodPassword(String password, int minimumLength)
    {
        if (password.length() < minimumLength)
        {
            return false;
        }
        boolean lower = false;
        boolean upper = false;
        boolean digit = false;
        int total = 0;
        for (int i = 0; i < password.length(); i++)
        {
            char c = password.charAt(i);
            if (Character.isDigit(c))
            {
                digit = true;
            }
            else if (Character.isUpperCase(c))
            {
                upper = true;
            }
            else if (Character.isLowerCase(c))
            {
                lower = true;
            }
            if (i > 0)
            {
                int diff = c - password.charAt(i - 1);
                diff *= diff;
                if (diff > 1)
                {
                    diff = 4;
                }
                total += diff;
            }
        }
        if (total <= minimumLength)
        {
            return false;
        }
        if (lower == false)
        {
            return false;
        }
        if (upper == false)
        {
            return false;
        }
        if (digit == false)
        {
            return false;
        }
        String lowerPassword = password.toLowerCase();
        if (lowerPassword.contains("pass"))
        {
            return false;
        }
        if (lowerPassword.contains("qwer"))
        {
            return false;
        }
        if (lowerPassword.contains("asdf"))
        {
            return false;
        }
        if (lowerPassword.contains("zxcv"))
        {
            return false;
        }
        return true;
    }
    
    public static boolean isGoodNummericCode(String password, int minimumLength,int maximumLength)
    {
        if (password.length() > maximumLength)
        {
            return false;
        }
        if (password.length() < minimumLength)
        {
            return false;
        }
        int total = 0;
        for (int i = 0; i < password.length(); i++)
        {
            char c = password.charAt(i);
            if (Character.isDigit(c)==false)
            {
                return false;
            }
            if (i > 0)
            {
                int diff = c - password.charAt(i - 1);
                diff *= diff;
                if (diff > 1)
                {
                    diff = 4;
                }
                total += diff;
            }
        }
        if (total <= minimumLength)
        {
            return false;
        }
        return true;
    }
   
    public static int getUniqueCharacters(String text)
    {
        if (text==null)
        {
            return 0;
        }
        HashSet<Character> set=new HashSet();
        for (int i=0;i<text.length();i++)
        {
            set.add(text.charAt(i));
        }
        return set.size();
    }
}
