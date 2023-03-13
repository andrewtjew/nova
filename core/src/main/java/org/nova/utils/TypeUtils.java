/*******************************************************************************
 * Copyright (C) 2016-2019 Kat Fung Tjew
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
package org.nova.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.nova.core.ObjectBox;

public class TypeUtils
{
    static public <OBJECT> OBJECT replaceIfNull(OBJECT value,OBJECT nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }
    static public boolean replaceIfNull(Boolean value,boolean nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }
    static public byte replaceIfNull(Byte value,byte nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }
    static public char replaceIfNull(Character value,char nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }
    static public short replaceIfNull(Short value,short nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }
    static public int replaceIfNull(Integer value,int nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }
    static public long replaceIfNull(Long value,long nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }
    static public float replaceIfNull(Float value,float nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }
    static public double replaceIfNull(Double value,double nullReplacementValue)
    {
        return (value==null)?nullReplacementValue:value;
    }

    static public boolean isNullOrZero(Long value)
    {
        if (value==null)
        {
            return true;
        }
        return value==0L;
    }
    static public boolean isNullOrZero(Integer value)
    {
        if (value==null)
        {
            return true;
        }
        return value==0;
    }
    static public boolean isNullOrZero(Short value)
    {
        if (value==null)
        {
            return true;
        }
        return value==0;
    }
    
    static public short replaceNullWithZero(Short value)
    {
        return (value==null)?0:value;
    }
    static public int replaceNullWithZero(Integer value)
    {
        return (value==null)?0:value;
    }
    static public long replaceNullWithZero(Long value)
    {
        return (value==null)?0L:value;
    }
    static public float replaceNullWithZero(Float value)
    {
        return (value==null)?0.0f:value;
    }
    static public double replaceNullWithZero(Double value)
    {
        return (value==null)?0.0:value;
    }
    
    static public List<Integer> intArrayToList(int[] values)
    {
        ArrayList<Integer> list=new ArrayList<>();
        for (int value:values)
        {
            list.add(value);
        }
        return list;
    }
    
    static public List<Long> longArrayToList(long[] values)
    {
        ArrayList<Long> list=new ArrayList<>();
        for (long value:values)
        {
            list.add(value);
        }
        return list;
    }
    static public long[] longListToArray(List<Long> list)
    {
        long[] array=new long[list.size()];
        for (int i=0;i<array.length;i++)
        {
            array[i]=list.get(i);
        }
        return array;
    }
    static public byte[] byteListToArray(List<Byte> list)
    {
        byte[] array=new byte[list.size()];
        for (int i=0;i<array.length;i++)
        {
            array[i]=list.get(i);
        }
        return array;
    }
    static public int[] intListToArray(List<Integer> list)
    {
        int[] array=new int[list.size()];
        for (int i=0;i<array.length;i++)
        {
            array[i]=list.get(i);
        }
        return array;
    }
    static public short[] shortListToArray(List<Short> list)
    {
        short[] array=new short[list.size()];
        for (int i=0;i<array.length;i++)
        {
            array[i]=list.get(i);
        }
        return array;
    }
    static public double[] doubleListToArray(List<Double> list)
    {
        double[] array=new double[list.size()];
        for (int i=0;i<array.length;i++)
        {
            array[i]=list.get(i);
        }
        return array;
    }

    public static int getAncestorDistance(Class<?> derived,Class<?> superClass)
    {
        int distance=0;
        while (derived!=superClass)
        {
            derived=derived.getSuperclass();
            if (derived==null)
            {
                return -1;
            }
            distance++;
        }
        return distance;
    }

    public static boolean isDerivedFrom(Class<?> type,Class<?> from)
    {
        while (type!=from)
        {
            type=type.getSuperclass();
            if (type==null)
            {
                return false;
            }
        }
        return true;
    }
    public static <T> void addToList(List<T> list,T[] array)
    {
        if (array==null)
        {
            return;
        }
        for (T item:array)
        {
            list.add(item);
        }
    }
    
    static public String bigEndianByteArrayToHexString(byte[] bytes)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) 
        {
            byte value=bytes[i];
            int high=(value&0xf0)>>4;
            if (high<10)
            {
                sb.append((char)(high+'0'));
            }
            else
            {
                sb.append((char)(high-10+'A'));
            }

            int low=value&0xf;
            if (low<10)
            {
                sb.append((char)(low+'0'));
            }
            else
            {
                sb.append((char)(low-10+'A'));
            }
        }
        return sb.toString();        
    }
    static int hexToInt(char c) throws Exception
    {
        if ((c>='a')&&(c<='f'))
        {
            return c-'a'+10;
        }
        else if ((c>='A')&&(c<='F'))
        {
            return c-'A'+10;
        }
        else if ((c>='0')&&(c<='9'))
        {
            return c-'0';
        }
        throw new Exception();
    }
    
    static public byte[] bigEndianHexStringToByteArray(String string) throws Exception
    {
        if (string.length()%2!=0)
        {
            throw new Exception();
        }
        byte[] bytes=new byte[string.length()/2];
        for (int i=0;i<string.length();i+=2)
        {
            int high=hexToInt(string.charAt(i));
            int low=hexToInt(string.charAt(i+1));
            bytes[i/2]=(byte)(high*16+low);
        }
        return bytes;
    }
    
    public static int bigEndianBytesToInt(byte[] bytes,int index) 
    {
        return ((bytes[index+0]&0xFF)<<24)|((bytes[index+1]&0xFF)<<16)|((bytes[index+2]&0xFF)<<8)|(bytes[index+3]&0xFF);
        
    }
    public static int bigEndianBytesToInt(byte[] bytes) 
    {
        return bigEndianBytesToInt(bytes,0);
    }
    public static void toBytesLittleEndian(int[] source,byte[] dest,int sourceOffset,int destOffset,int len) 
    {
        for (int i=sourceOffset;i<sourceOffset+len;i++)
        {
            destOffset=toBytesLittleEndian(source[i],dest,destOffset);
        }
    }
    public static int toBytesLittleEndian(int value,byte[] dest,int index) 
    {
        dest[index++]=(byte)(value&0xFF);
        dest[index++]=(byte)((value>>8)&0xFF);
        dest[index++]=(byte)((value>>16)&0xFF);
        dest[index++]=(byte)((value>>24)&0xFF);
        return index;
    }
    public static int bigEndianIntToBytes(int value,byte[] bytes,int index) 
    {
        bytes[index++]=(byte)((value>>24)&0xFF);
        bytes[index++]=(byte)((value>>16)&0xFF);
        bytes[index++]=(byte)((value>>8)&0xFF);
        bytes[index++]=(byte)(value&0xFF);
        return index;
    }
    public static byte[] bigEndianIntToBytes(int value) 
    {
        byte[] bytes=new byte[4];
        bytes[0]=(byte)((value>>24)&0xFF);
        bytes[1]=(byte)((value>>16)&0xFF);
        bytes[2]=(byte)((value>>8)&0xFF);
        bytes[3]=(byte)(value&0xFF);
        return bytes;
    }
    public static int bigEndianLongToBytes(long value,byte[] bytes,int index) 
    {
        bytes[index++]=(byte)((value>>56)&0xFF);
        bytes[index++]=(byte)((value>>48)&0xFF);
        bytes[index++]=(byte)((value>>40)&0xFF);
        bytes[index++]=(byte)((value>>32)&0xFF);
        bytes[index++]=(byte)((value>>24)&0xFF);
        bytes[index++]=(byte)((value>>16)&0xFF);
        bytes[index++]=(byte)((value>>8)&0xFF);
        bytes[index++]=(byte)(value&0xFF);
        return index;
    }
    public static byte[] bigEndianLongToBytes(long value) 
    {
        byte[] bytes=new byte[8];
        bytes[0]=(byte)((value>>56)&0xFF);
        bytes[1]=(byte)((value>>48)&0xFF);
        bytes[2]=(byte)((value>>40)&0xFF);
        bytes[3]=(byte)((value>>32)&0xFF);
        bytes[4]=(byte)((value>>24)&0xFF);
        bytes[5]=(byte)((value>>16)&0xFF);
        bytes[6]=(byte)((value>>8)&0xFF);
        bytes[7]=(byte)(value&0xFF);
        return bytes;
    }
    public static long bigEndianBytesToLong(byte[] bytes,int index) 
    {
        return ((bytes[index+0]&0xFFL)<<56)|((bytes[index+1]&0xFFL)<<48)|((bytes[index+2]&0xFFL)<<40)|((bytes[index+3]&0xFFL)<<32)| 
                ((bytes[index+4]&0xFFL)<<24)|((bytes[index+5]&0xFFL)<<16)|((bytes[index+6]&0xFFL)<<8)|(bytes[index+7]&0xFFL);
    }
    public static long bigEndianBytesToLong(byte[] bytes) 
    {
        return bigEndianBytesToLong(bytes,0);
    }
    
    static public boolean isTrue(Boolean value)
    {
        if (value==null)
        {
            return false;
        }
        return value;
    }
    static public boolean isFalse(Boolean value)
    {
        if (value==null)
        {
            return false;
        }
        return value==false;
    }
    static public boolean isNullOrFalse(Boolean value)
    {
        if (value==null)
        {
            return true;
        }
        return !value;
    }
    static public boolean isNullOrSpace(String text)
    {
        if (text==null)
        {
            return true;
        }
        if (text.trim().length()==0) //Don't use isBlank for compatibilty with older JVMs
        {
            return true;
        }
        return false;
    }
    static public boolean isNullOrEmpty(String text)
    {
        if (text==null)
        {
            return true;
        }
        if (text.length()==0)
        {
            return true;
        }
        return false;
    }
    
    public static long parseLong(String value,long defaultValue)
    {
        if (value==null)
        {
            return defaultValue;
        }
        return Long.parseLong(value);
    }
    
    public static int parseInt(String value,int defaultValue)
    {
        if (value==null)
        {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }
    public static long[] parseLongs(String[] values,long defaultValue)
    {
        long[] results=new long[values.length];
        for (int i=0;i<results.length;i++)
        {
            results[i]=parseLong(values[i],defaultValue);
        }
        return results;
    }
    public static long[] parseLongs(String[] values)
    {
        long[] results=new long[values.length];
        for (int i=0;i<results.length;i++)
        {
            results[i]=Long.parseLong(values[i]);
        }
        return results;
    }

    public static int[] parseInts(String[] values,int defaultValue)
    {
        int[] results=new int[values.length];
        for (int i=0;i<results.length;i++)
        {
            results[i]=parseInt(values[i],defaultValue);
        }
        return results;
    }
    public static int[] parseInts(String[] values)
    {
        int[] results=new int[values.length];
        for (int i=0;i<results.length;i++)
        {
            results[i]=Integer.parseInt(values[i]);
        }
        return results;
    }

    public static short parseShort(String value,short defaultValue)
    {
        if (value==null)
        {
            return defaultValue;
        }
        return Short.parseShort(value);
    }

    public static short[] parseShorts(String[] values,short defaultValue)
    {
        short[] results=new short[values.length];
        for (int i=0;i<results.length;i++)
        {
            results[i]=parseShort(values[i],defaultValue);
        }
        return results;
    }
    public static short[] parseShorts(String[] values)
    {
        short[] results=new short[values.length];
        for (int i=0;i<results.length;i++)
        {
            results[i]=Short.parseShort(values[i]);
        }
        return results;
    }
    
  //enums are not included as they can be represented as ints, longs, shorts, strings, etc.
    public static boolean isSingleRepresentationValueType(Class<?> type) 
    {
        if (type == boolean.class)
        {
            return true;
        }
        else if (type == Boolean.class)
        {
            return true;
        }
        else if (type == Integer.class)
        {
            return true;
        }
        else if (type == int.class)
        {
            return true;
        }
        else if (type == Short.class)
        {
            return true;
        }
        else if (type == short.class)
        {
            return true;
        }
        else if (type == Long.class)
        {
            return true;
        }
        else if (type == long.class)
        {
            return true;
        }
        else if (type == Float.class)
        {
            return true;
        }
        else if (type == float.class)
        {
            return true;
        }
        else if (type == Double.class)
        {
            return true;
        }
        else if (type == double.class)
        {
            return true;
        }
        else if (type == byte.class)
        {
            return true;
        }
        else if (type == Byte.class)
        {
            return true;
        }
        else if (type == char.class)
        {
            return true;
        }
        else if (type == Character.class)
        {
            return true;
        }
        else if (type == String.class)
        {
            return true;
        }
        else if (type == BigDecimal.class)
        {
            return true;
        }
        return false;
    }

    //enums are not included as they can be represented as ints, longs, shorts, strings, etc.
    public static boolean isValueType(Class<?> type) 
    {
        if (type == boolean.class)
        {
            return true;
        }
        else if (type == Boolean.class)
        {
            return true;
        }
        else if (type == Integer.class)
        {
            return true;
        }
        else if (type == int.class)
        {
            return true;
        }
        else if (type == Short.class)
        {
            return true;
        }
        else if (type == short.class)
        {
            return true;
        }
        else if (type == Long.class)
        {
            return true;
        }
        else if (type == long.class)
        {
            return true;
        }
        else if (type == Float.class)
        {
            return true;
        }
        else if (type == float.class)
        {
            return true;
        }
        else if (type == Double.class)
        {
            return true;
        }
        else if (type == double.class)
        {
            return true;
        }
        else if (type == byte.class)
        {
            return true;
        }
        else if (type == Byte.class)
        {
            return true;
        }
        else if (type == char.class)
        {
            return true;
        }
        else if (type == Character.class)
        {
            return true;
        }
        else if (type == String.class)
        {
            return true;
        }
        else if (type.isEnum())
        {
            return true;
        }
        else if (type == BigDecimal.class)
        {
            return true;
        }
        return false;
    }
    
    static public String trimToMaxLength(String string,int maxLength)
    {
        if (string==null)
        {
            return string;
        }
        int length=string.length();
        if (length>maxLength)
        {
            length=maxLength;
        }
        return string.substring(0, length);
    }
    static public String trim(String string)
    {
        if (string==null)
        {
            return string;
        }
        return string.trim();
    }
    static public boolean equals(Integer a,Integer b)
    {
        if ((a==null)&&(b==null))
        {
            return true;
        }
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.equals(b);
    }
    static public boolean equals(Double a,Double b)
    {
        if ((a==null)&&(b==null))
        {
            return true;
        }
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.equals(b);
    }
    static public boolean equals(Float a,Float b)
    {
        if ((a==null)&&(b==null))
        {
            return true;
        }
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.equals(b);
    }
    static public boolean equals(Boolean a,Boolean b)
    {
        if ((a==null)&&(b==null))
        {
            return true;
        }
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.equals(b);
    }
    static public boolean equals(Short a,Short b)
    {
        if ((a==null)&&(b==null))
        {
            return true;
        }
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.equals(b);
    }
    static public boolean equals(Long a,Long b)
    {
        if ((a==null)&&(b==null))
        {
            return true;
        }
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.equals(b);
    }
    
    static public boolean equals(String a,String b)
    {
        if ((a==null)&&(b==null))
        {
            return true;
        }
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.equals(b);
    }
    static public boolean equalsIgnoreCase(String a,String b)
    {
        if ((a==null)&&(b==null))
        {
            return true;
        }
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }
    static public boolean containsIgnoreCase(String a,String b)
    {
        if ((a==null)||(b==null))
        {
            return false;
        }
        return a.toLowerCase().contains(b.toLowerCase());
    }
    static public <OBJECT> OBJECT unbox(ObjectBox box)
    {
    	if (box==null)
    	{
    		return null;
    	}
    	return box.get();
    }
}



