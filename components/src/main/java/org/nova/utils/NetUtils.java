package org.nova.utils;

import java.net.NetworkInterface;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class NetUtils
{
    public static String getLocalHostName() throws Exception
    {
        return java.net.InetAddress.getLocalHost().getHostName();
    }

    public static String getMacAddress() throws Exception
    {
        NetworkInterface network = NetworkInterface.getByInetAddress(java.net.InetAddress.getLocalHost());
        if (network == null)
        {
            return null;
        }
        byte[] mac = network.getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++)
        {
            sb.append(String.format("%02X", mac[i]));
        }
        return sb.toString();
    }

    public static String getClosestClientRemoteHost(HttpServletRequest request) throws Exception
    {
        String host = request.getHeader("x-forwarded-for");
        if (host != null)
        {
            int comma = host.indexOf(',');
            if (comma > 0)
            {
                return host.substring(0, comma);
            }
            return host;
        }
        return request.getRemoteHost();
    }

    public static String extractHostName(String endPoint)
    {
        int index = endPoint.indexOf("//");
        if (index > 0)
        {
            endPoint = endPoint.substring(index + 2);
        }
        index = endPoint.indexOf(':');
        if (index > 0)
        {
            endPoint = endPoint.substring(0, index);
        }
        return endPoint;
    }

    public enum IPAddressFormat
    {
        IPV4, IPV6, INVALID,
    }

    private static Pattern VALID_IPV4_PATTERN = Pattern.compile("(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])", Pattern.CASE_INSENSITIVE);
    private static Pattern VALID_IPV6_PATTERN = Pattern.compile("([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}", Pattern.CASE_INSENSITIVE);

    public static IPAddressFormat resolveIPAddressFormat(String ipAddress)
    {

        Matcher ipv4 = VALID_IPV4_PATTERN.matcher(ipAddress);
        if (ipv4.matches())
        {
            return IPAddressFormat.IPV4;
        }
        Matcher ipv6 = VALID_IPV6_PATTERN.matcher(ipAddress);
        if (ipv6.matches())
        {
            return IPAddressFormat.IPV6;
        }
        return IPAddressFormat.INVALID;
    }
    
    public static String replaceHostNameInURL(String URL,String newHost) throws Exception
    {
        if (URL==null)
        {
            throw new Exception("null URL");
        }
        StringBuilder sb=new StringBuilder();
        int index=URL.indexOf("//");
        if (index<0)
        {
            throw new Exception("Invalid URL:"+URL);
        }
        sb.append(URL.substring(0, index+2));
        sb.append(newHost);
        index=URL.lastIndexOf(':');
        if (index>0)
        {
            sb.append(URL.substring(index));
        }
        return sb.toString();
    }
    public static boolean isLocal(String host)
    {
        if ("0:0:0:0:0:0:0:1".equals(host))
        {
            return true;
        }
        if ("127.0.0.1".equals(host))
        {
            return true;
        }
        return "localhost".equals(host);
    }
}
