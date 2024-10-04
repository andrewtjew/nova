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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.NetworkInterface;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.TimeZone;

public class Utils
{
    static public int compare(String a,String b)
    {
        if ((a==null)&&(b==null))
        {
            return 0;
        }
        if (a==null)
        {
            return 1;
        }
        if (b==null)
        {
            return -1;
        }
        return a.compareTo(b);
    }
    
    public static String capitalize(String word)
    {
        if (TypeUtils.isNullOrEmpty(word))
        {
            return word;
        }
        return word.substring(0,1).toUpperCase()+word.substring(1).toLowerCase();
    }
    
    public static String[] splitUsingWhiteSpace(String string)
    {
        int count=0;
        for (int i=0;i<string.length();i++)
        {
            if (Character.isWhitespace(string.charAt(i))==false)
            {
                count++;
                for (i++;i<string.length();i++)
                {
                    if (Character.isWhitespace(string.charAt(i)))
                    {
                        break;
                    }
                }
            }
        }
        String[] fragments=new String[count];
        int index=0;
        for (int i=0;i<string.length();i++)
        {
            if (Character.isWhitespace(string.charAt(i))==false)
            {
                int begin=i;
                for (i++;i<string.length();i++)
                {
                    if (Character.isWhitespace(string.charAt(i)))
                    {
                        fragments[index++]=string.substring(begin,i);
                        break;
                    }
                }
            }
        }
        return fragments;
    }
    
    public static String[] split(String string,char splitCharacter)
	{
		int count=1;
		for (int i=0;i<string.length();i++)
		{
			if (string.charAt(i)==splitCharacter)
			{
				count++;
			}
		}
		String[] fragments=new String[count];
		int begin=0;
		int index=0;
		for (int i=0;i<string.length();i++)
		{
			if (string.charAt(i)==splitCharacter)
			{
				fragments[index++]=string.substring(begin,i);
				begin=i+1;
			}
		}
		fragments[index]=string.substring(begin,string.length());
		return fragments;
	}
    
    public static String[] splitLn(String string)
    {
        int count=1;
        for (int i=0;i<string.length();i++)
        {
            if (string.charAt(i)=='\n')
            {
                count++;
            }
        }
        String[] lines=new String[count];
        int begin=0;
        int index=0;
        for (int i=0;i<string.length();i++)
        {
            if (string.charAt(i)=='\n')
            {
                if ((i>0)&&(string.charAt(i-1)=='\r'))
                {
                    lines[index++]=string.substring(begin,i-1);
                }
                else
                {
                    lines[index++]=string.substring(begin,i);
                }
                begin=i+1;
            }
        }
        
        lines[index]=string.substring(begin,string.length());
        return lines;
    }
    
//    public static String[] splitUsingCommaDeprecated(String string,boolean doNotSplitIfNextCharacterIsSpace)
//    {
//        int count=1;
//        for (int i=0;i<string.length();i++)
//        {
//            if (string.charAt(i)==',')
//            {
//                if (doNotSplitIfNextCharacterIsSpace)
//                {
//                    if (i+1<string.length())
//                    {
//                        if (string.charAt(i+1)!=' ')
//                        {
//                            count++;
//                        }
//                    }
//                }
//                else
//                {
//                    count++;
//                }
//            }
//        }
//        String[] fragments=new String[count];
//        int begin=0;
//        int index=0;
//        for (int i=0;i<string.length();i++)
//        {
//            if (string.charAt(i)==',')
//            {
//                if (doNotSplitIfNextCharacterIsSpace)
//                {
//                    if (i+1<string.length())
//                    {
//                        if (string.charAt(i+1)==' ')
//                        {
//                            continue;
//                        }
//                    }
//                }
//                fragments[index++]=string.substring(begin,i);
//                begin=i+1;
//            }
//        }
//        fragments[index]=string.substring(begin,string.length());
//        return fragments;
//    }
    public static String combine(Iterable<?> iterable,String seperator)
    {
        StringBuilder sb=new StringBuilder();
        for (Object item:iterable)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
    public static String combine(Iterator<?> iterator,String seperator)
    {
        StringBuilder sb=new StringBuilder();
        while (iterator.hasNext())
        {
            Object object=iterator.next();
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(object);
        }
        return sb.toString();
    }
    public static String combine(Enum<?>[] iterable,String seperator)
    {
        StringBuilder sb=new StringBuilder();
        for (Enum<?> item:iterable)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    public static String combine(long[] items,String seperator)
    {
        StringBuilder sb=new StringBuilder();
        for (long item:items)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
    public static String combine(byte[] items,String seperator)
    {
        StringBuilder sb=new StringBuilder();
        for (byte item:items)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
    public static String combine(String seperator,String...items)
    {
        StringBuilder sb=new StringBuilder();
        for (String item:items)
        {
            if (TypeUtils.isNullOrEmpty(item))
            {
                continue;
            }
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
    public static String combine(double[] items,String seperator)
    {
        StringBuilder sb=new StringBuilder();
        for (double item:items)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
    public static String combine(int[] items,String seperator)
    {
        StringBuilder sb=new StringBuilder();
        for (long item:items)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
    public static String combine(String[] items,String seperator)
    {
        if (items==null)
        {
            return "";
        }
        StringBuilder sb=new StringBuilder();
        for (String item:items)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
    public static String combineSkipNullOrEmpty(String[] items,String seperator)
    {
        if (items==null)
        {
            return "";
        }
        StringBuilder sb=new StringBuilder();
        for (String item:items)
        {
            if (TypeUtils.isNullOrEmpty(item))
            {
                continue;
            }
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            sb.append(item);
        }
        return sb.toString();
    }
    
    public static String combine(Iterable<?> iterable,String seperator,String pre,String post)
    {
        StringBuilder sb=new StringBuilder();
        for (Object item:iterable)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            if (pre!=null)
            {
                sb.append(pre);
            }
            sb.append(item);
            if (post!=null)
            {
                sb.append(post);
            }
        }
        return sb.toString();
    }
    public static String combine(long[] items,String seperator,String pre,String post)
    {
        StringBuilder sb=new StringBuilder();
        for (long item:items)
        {
            if (sb.length()>0)
            {
                sb.append(seperator);
            }
            if (pre!=null)
            {
                sb.append(pre);
            }
            sb.append(item);
            if (post!=null)
            {
                sb.append(post);
            }
        }
        return sb.toString();
    }
    
	public static String nowToLocalDateTimeString()
	{
		return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

    public static LocalDateTime millisToLocalDateTime(long millis)
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), TimeZone.getDefault().toZoneId());
    }
    public static ZonedDateTime millisToUTCDateTime(long millis)
    {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("UTC"));
    }

    public static String millisToLocalDateTimeString(long millis)
    {
        return millisToLocalDateTime(millis).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String millisToUTCDateTimeString(long millis)
    {
        return millisToUTCDateTime(millis).format(DateTimeFormatter.ISO_DATE_TIME);
    }
	public static String millisToDateTimeString(long dateTime,String defaultValue)
	{
		if (dateTime<=0)
		{
			return defaultValue;
		}
		return millisToLocalDateTimeString(dateTime);
	}

    static public DateTimeFormatter LOCALDATETIME_FILENAME_FORMATTER=DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"); 
    static public DateTimeFormatter SHORT_LOCALDATETIME_FILENAME_FORMATTER=DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"); 

    public static String millisToLocalDateTimeFileName(long millis)
    {
        return millisToLocalDateTime(millis).format(LOCALDATETIME_FILENAME_FORMATTER);
    }
    
    public static String millisToShortLocalDateTimeFileName(long millis)
    {
        return millisToLocalDateTime(millis).format(SHORT_LOCALDATETIME_FILENAME_FORMATTER);
    }
    
    

	public static String millisToDurationString(long millis)
	{
	    if (millis==0)
	    {
	        return "0";
	    }
		StringBuilder sb=new StringBuilder();
		long days=millis/(1000*3600*24);
		long hours=(millis-days*1000*3600*24)/(1000*3600);
		long minutes=(millis-days*1000*3600*24-hours*1000*3600)/(1000*60);
		long seconds=(millis-days*1000*3600*24-hours*1000*3600-minutes*1000*60)/1000;
		long milliseconds=(millis-days*1000*3600*24-hours*1000*3600-minutes*1000*60-seconds*1000);
		if (days<1)
		{
			return hours+":"+minutes+":"+seconds+"."+milliseconds;
		}
		return days+" days "+hours+":"+minutes+":"+seconds+"."+milliseconds;
		
	}

	public static String millisToNiceDurationString(long millis)
    {
	    if (millis==0)
	    {
	        return "0";
	    }
        StringBuilder sb=new StringBuilder();
        long days=millis/(1000*3600*24);
        if (days>1)
        {
            sb.append(days+" days");
        }
        else if (days==1)
        {
            sb.append(days+" day");
        }
        long hours=(millis-days*1000*3600*24)/(1000*3600);
        if ((hours>1)||(sb.length()>0))
        {
            if (sb.length()>0)
            {
                sb.append(", ");
            }
            sb.append(hours+" hours");
        }
        else if (hours==1)
        {
            if (sb.length()>0)
            {
                sb.append(", ");
            }
            sb.append(hours+" hour");
        }
        
        long minutes=(millis-days*1000*3600*24-hours*1000*3600)/(1000*60);
        if ((minutes>1)||(sb.length()>0))
        {
            if (sb.length()>0)
            {
                sb.append(", ");
            }
            sb.append(minutes+" minutes");
        }
        else if (minutes==1)
        {
            if (sb.length()>0)
            {
                sb.append(", ");
            }
            sb.append(minutes+" minute");
        }
        long seconds=(millis-days*1000*3600*24-hours*1000*3600-minutes*1000*60)/1000;
        if ((seconds>1)||(sb.length()>0))
        {
            if (sb.length()>0)
            {
                sb.append(", ");
            }
            sb.append(seconds+" seconds");
        }
        else if (seconds==1)
        {
            if (sb.length()>0)
            {
                sb.append(", ");
            }
            sb.append(seconds+" second");
        }
        long ms=millis-days*1000*3600*24-hours*1000*3600-minutes*1000*60-seconds*1000;
        if ((ms>1)||(sb.length()>0))
        {
            if (sb.length()>0)
            {
                sb.append(", ");
            }
            sb.append(ms+" ms");
        }
        else if (ms==1)
        {
            if (sb.length()>0)
            {
                sb.append(", ");
            }
            sb.append(ms+" ms");
        }
        return sb.toString();
    }

	public static String nanosToDurationString(long nanos)
	{
		return millisToDurationString(nanos/1000000);
	}
	
	public static int simpleHash(byte[] array)
	{
		int hash=0x811c9dc5;
		for (byte item:array)
		{
			hash=(hash^item)*0x01000193;
		}
		return hash;
	}
	
    public static String getLocalHostName() throws Exception
    {
        return java.net.InetAddress.getLocalHost().getHostName();
    }
    public static String getLocalAddress() throws Exception
    {
        return java.net.InetAddress.getLocalHost().getHostAddress();
    }

	public static String getMacAddress() throws Exception
    {
        NetworkInterface network = NetworkInterface.getByInetAddress(java.net.InetAddress.getLocalHost());
        if (network==null)
        {
            return ""; //Hack
        }
        byte[] mac = network.getHardwareAddress();
        if (mac==null)
        {
        	return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) 
        {
            sb.append(String.format("%02X", mac[i]));
        }
        return sb.toString();
    }
	
	public static String toString(StackTraceElement[] elements) 
	{
		return toString(elements,0);
	}

	public static String toString(StackTraceElement[] elements,int start) 
	{
		StringBuffer sb=new StringBuffer();
		for (int i=start;i<elements.length;i++)
		{
			sb.append(elements[i].toString());
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	
	public static String getStrackTraceAsString(Throwable t)
	{
		try
		{
    		try (ByteArrayOutputStream outputStream=new ByteArrayOutputStream())
    		{
    			try (PrintWriter writer=new PrintWriter(outputStream)) 
    			{
    				t.printStackTrace(writer);
    				writer.flush();
    			}
    			outputStream.flush();
    			return new String(outputStream.toByteArray());
    		}
		}
		catch (Throwable tt)
		{
		    throw new RuntimeException();
		}
	}

	public static String toString(Throwable t)
	{
		try (ByteArrayOutputStream outputStream=new ByteArrayOutputStream())
		{
			try (PrintStream printStream=new PrintStream(outputStream))
			{
				t.printStackTrace(printStream);
				printStream.flush();
				return new String(outputStream.toByteArray());
			}
		}
		catch (IOException e)
		{
			return null;
		}
	}
	public static int occurs(String string,String pattern)
	{
		int occurs=0;
		if (string!=null)
		{
    		for (int index=string.indexOf(pattern);index>=0;index=string.indexOf(pattern,index+pattern.length()))
    		{
    			occurs++;
    		}
		}
		return occurs;
	}
	static public boolean equals(String a,String b)
	{
	    if ((a==null)&&(b==null))
	    {
	        return true;
	    }
        if ((a!=null)&&(b==null))
        {
            return false;
        }
        if ((a==null)&&(b!=null))
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
        if ((a!=null)&&(b==null))
        {
            return false;
        }
        if ((a==null)&&(b!=null))
        {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }

    public static long parseLocalToUTCDateTime(String local,int timeZoneOffsetSeconds)
    {
        LocalDateTime localDateTime=LocalDateTime.parse(local);
        ZonedDateTime zoned=ZonedDateTime.ofInstant(localDateTime, ZoneOffset.ofTotalSeconds(timeZoneOffsetSeconds), ZoneId.of("UTC"));
        return zoned.toInstant().toEpochMilli();
    }	
    
    
    static public String replaceUsingEnvironmentVariables(String text)
    {
        int index=0;
        int end=0;
        int start=0;
        StringBuilder sb=new StringBuilder();
        while (index<text.length())
        {
            start=text.indexOf('%',index);
            if (start<0)
            {
                end=text.length();
                sb.append(text.substring(index,end));
                break;
            }
            end=text.indexOf('%',start+1);
            if (end<0)
            {
                end=text.length();
                sb.append(text.substring(index,end));
                break;
            }
            sb.append(text.substring(index,start));
            String name=text.substring(start+1,end);
            String value=System.getenv(name);
            if (value!=null)
            {
                sb.append(value);
            }
            else
            {
                sb.append(text.substring(start,end+1));
            }
            index=end+1;
        }
        return sb.toString();
    }

}



