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
package org.nova.configuration;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.nova.annotations.Alias;
import org.nova.json.ObjectMapper;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

public class Configuration
{
	
	final private HashMap<String, ConfigurationItem> map;

	public Configuration() 
	{
		this.map = new HashMap<>();
	}

	public void addArgs(String[] args) 
	{
		for (String arg:args)
		{
			String[] parts=Utils.split(arg, '=');
			if (parts.length==2)
			{
				this.map.put(parts[0],new ConfigurationItem(parts[0], parts[1], ConfigurationSource.ARGS,null, null));
			}
		}
	}

	public void remove(String key)
	{
	    synchronized(this)
	    {
	        this.map.remove(key);
	    }
	}
	public void add(ConfigurationItem configurationItem)
	{
		synchronized(this)
		{
			this.map.put(configurationItem.getName(),configurationItem);
		}
	}
    public void add(String name,String value,String description)
    {
        synchronized(this)
        {
            this.map.put(name,new ConfigurationItem(name, value, ConfigurationSource.CODE, getSource(Thread.currentThread().getStackTrace()[2]), description));
        }
    }
	public ConfigurationItem getConfigurationItem(String name)
	{
		synchronized (this)
		{
			return this.map.get(name);
		}
	}

	public static String getSource(StackTraceElement element)
	{
		return element.getFileName()+"."+element.getLineNumber();
	}

	public ConfigurationItem getConfigurationItem(String name,String defaultValue) throws Exception
	{
		synchronized (this)
		{
			ConfigurationItem item=this.map.get(name);
			if (item==null)
			{
				item=new ConfigurationItem(name,defaultValue, ConfigurationSource.DEFAULT, getSource(Thread.currentThread().getStackTrace()[4]),null);
				{
					this.map.put(name, item);
				}
			}
			else if (item.getSource()==ConfigurationSource.DEFAULT)
			{
			    if (TypeUtils.equals(defaultValue, item.getValue())==false)
			    {
			        throw new Exception("Inconsistent default values: from "+item.getSourceContext()+"="+item.getValue()+", new="+defaultValue);
			    }
			}
			
			return item;
		}
	}

	public String getValue(String name) throws Exception
	{
		ConfigurationItem item=getConfigurationItem(name);
		if (item==null)
		{
			throw new Exception("No item for name="+name);
		}
		return item.getValue();
	}
	public String getValue(String name,String defaultValue) throws Exception
	{
		ConfigurationItem item=getConfigurationItem(name,defaultValue);
		if (item==null)
		{
			return null;
		}
		return item.getValue();
	}
    public boolean contains(String name) throws Exception
    {
        synchronized (this)
        {
            return this.map.containsKey(name);
        }
    }

	private String getValue(String name,Object defaultValue) throws Exception
    {
        ConfigurationItem item=getConfigurationItem(name,defaultValue==null?null:defaultValue.toString());
        if (item==null)
        {
            return null;
        }
        return item.getValue();
    }
	
	public double getDoubleValue(String name) throws Exception
	{
		String value=getValue(name);
		return Double.parseDouble(value);
	}
	public double getDoubleValue(String name,double defaultValue) throws Exception
	{
		String value=getValue(name,defaultValue);
		if (value==null)
		{
		    return defaultValue;
		}
		return Double.parseDouble(value);
	}
    public Double getNullableDoubleValue(String name) throws Exception
    {
        String value=getValue(name);
        if (value==null)
        {
            return null;
        }
        return Double.parseDouble(value);
    }
    public Double getNullableDoubleValue(String name,Double defaultValue) throws Exception
    {
        String value=getValue(name,defaultValue);
        if (value==null)
        {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }
	public float getFloatValue(String name) throws Exception
	{
		String value=getValue(name);
		return Float.parseFloat(value);
	}
	public float getFloatValue(String name,float defaultValue) throws Exception
	{
		String value=getValue(name,defaultValue);
        if (value==null)
        {
            return defaultValue;
        }
		return Float.parseFloat(value);
	}
    public Float getNullableFloatValue(String name) throws Exception
    {
        String value=getValue(name);
        if (value==null)
        {
            return null;
        }
        return Float.parseFloat(value);
    }
    public Float getNullableFloatValue(String name,Float defaultValue) throws Exception
    {
        String value=getValue(name);
        if (value==null)
        {
            return defaultValue;
        }
        return Float.parseFloat(value);
    }
    
    private long parseLong(String value)
    {
        value=value.trim();
        long scale=1;
        if (value.endsWith("k")||value.endsWith("K"))
        {
            scale=1000L;
            value=value.substring(0, value.length()-1);
        }        
        else if (value.endsWith("m")||value.endsWith("M"))
        {
            scale=1000_000L;
            value=value.substring(0, value.length()-1);
        }        
        else if (value.endsWith("g")||value.endsWith("G"))
        {
            scale=1000_000_000L;
            value=value.substring(0, value.length()-1);
        }        
        else if (value.endsWith("t")||value.endsWith("T"))
        {
            scale=1000_000_000_000L;
            value=value.substring(0, value.length()-1);
        }        
        else if (value.endsWith("p")||value.endsWith("B"))
        {
            scale=1000_000_000_000_000L;
            value=value.substring(0, value.length()-1);
        }        
        long number=Long.parseLong(value);
        long scaledNumber=number*scale;
        if (scaledNumber/scale!=number)
        {
            throw new NumberFormatException("Overflow. value="+value);
        }
        return scaledNumber;
        
    }
    private int parseInt(String value)
    {
        value=value.trim();
        int scale=1;
        if (value.endsWith("k")||value.endsWith("K"))
        {
            scale=1000;
            value=value.substring(0, value.length()-1);           
        }        
        else if (value.endsWith("m")||value.endsWith("M"))
        {
            scale=1000_000;
            value=value.substring(0, value.length()-1);
        }        
        else if (value.endsWith("g")||value.endsWith("G"))
        {
            scale=1000_000_000;
            value=value.substring(0, value.length()-1);
        }        
        int number=Integer.parseInt(value);
        int scaledNumber=number*scale;
        if (scaledNumber/scale!=number)
        {
            throw new NumberFormatException("Overflow. value="+value);
        }
        return scaledNumber;
    }
    
	public long getLongValue(String name) throws Exception
	{
		try
		{
			String value=getValue(name);
			return parseLong(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}
	}
	public long getLongValue(String name,long defaultValue) throws Exception
	{
		try
		{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
			return parseLong(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
	}
    public Long getNullableLongValue(String name) throws Exception
    {
    	try
    	{
	        String value=getValue(name);
	        if (value==null)
	        {
	            return null;
	        }
	        return parseLong(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

    }

    public Long getNullableLongValue(String name,Long defaultValue) throws Exception
    {
    	try
    	{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
	        return parseLong(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

    }
	
    public int getIntegerValue(String name) throws Exception
    {
    	try
    	{
    		String value=getValue(name);
    		return parseInt(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

    }
    public int getIntegerValue(String name,int defaultValue) throws Exception
    {
    	try
    	{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
	        return parseInt(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

    }
    public Integer getNullableIntegerValue(String name) throws Exception
    {
    	try
    	{
	        String value=getValue(name);
	        if (value==null)
	        {
	            return null;
	        }
	        return parseInt(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

    }

    public Integer getNullableIntegerValue(String name,Integer defaultValue) throws Exception
    {
    	try
    	{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
	        return parseInt(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

    }
    
	public short getShortValue(String name) throws Exception
	{
		try
		{
			String value=getValue(name);
			return Short.parseShort(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

	}

	public short getShortValue(String name,short defaultValue) throws Exception
	{
		try
		{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
			return Short.parseShort(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

	}
    public Short getNullableShortValue(String name) throws Exception
    {
    	try
    	{
	        String value=getValue(name);
	        if (value==null)
	        {
	            return null;
	        }
	        return Short.parseShort(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
    }

    public Short getNullableShortValue(String name,Short defaultValue) throws Exception
    {
    	try
    	{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
	        return Short.parseShort(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
    }
	
    public byte getByteValue(String name) throws Exception
    {
    	try
    	{
    		String value=getValue(name);
    		return Byte.parseByte(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

    }

    public byte getByteValue(String name,byte defaultValue) throws Exception
    {
    	try
    	{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
	        return Byte.parseByte(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
    }
    public Byte getNullableByteValue(String name) throws Exception
    {
    	try
    	{
	        String value=getValue(name);
	        if (value==null)
	        {
	            return null;
	        }
	        return Byte.parseByte(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
    }

    public Byte getNullableByteValue(String name,Byte defaultValue) throws Exception
    {
    	try
    	{
	        String value=getValue(name);
	        if (value==null)
	        {
	            return defaultValue;
	        }
	        return Byte.parseByte(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
    }
    
	public boolean getBooleanValue(String name) throws Exception
	{
		try
		{
			String value=getValue(name);
			return Boolean.parseBoolean(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
	}

	public boolean getBooleanValue(String name,boolean defaultValue) throws Exception
	{
		try
		{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
			return Boolean.parseBoolean(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
	}
    public Boolean getNullableBooleanValue(String name) throws Exception
    {
    	try
    	{
	        String value=getValue(name);
	        if (value==null)
	        {
	            return null;
	        }
	        return Boolean.parseBoolean(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			

    }

    public Boolean getNullableBooleanValue(String name,Boolean defaultValue) throws Exception
    {
    	try
    	{
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return defaultValue;
	        }
	        return Boolean.parseBoolean(value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <ENUM> ENUM getEnumValue(String name,Class<ENUM> type) throws Exception
	{
		try
		{
		    String value=getValue(name);
		    if (TypeUtils.isNullOrEmpty(value))
		    {
		        return null;
		    }
			return (ENUM) Enum.valueOf((Class<Enum>)type, value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
	}
	
	@Deprecated
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <ENUM> ENUM getEnumValue(String name,ENUM defaultValue,Class<ENUM> type) throws Exception
	{
		try
		{
		    String value=getValue(name,defaultValue);
		    if (value==null)
		    {
		        return null;
		    }
		    if ("null".equals(value))
		    {
		        return null;
		    }
	        if (TypeUtils.isNullOrEmpty(value))
	        {
	            return null;
	        }
			return (ENUM) Enum.valueOf((Class<Enum>)type, value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public <ENUM> ENUM getEnumValue(String name,ENUM defaultValue) throws Exception
    {
		try
		{
		    Class<ENUM> type=(Class<ENUM>)defaultValue.getClass();
	        String value=getValue(name,defaultValue);
	        if (value==null)
	        {
	            return null;
	        }
	        if ("null".equals(value))
	        {
	            return null;
	        }
	        if (TypeUtils.isNullOrEmpty(value))
	        {
	            return null;
	        }
	        return (ENUM) Enum.valueOf((Class<Enum>)type, value);
		}
		catch (Throwable t)
		{
			ConfigurationItem item=getConfigurationItem(name);
			throw new Exception("name="+name+", value="+item.getValue()+",source="+item.getSource());
		}			
		
    }
	
	public <OBJECT> OBJECT getJSONObject(String name,Class<OBJECT> type) throws Throwable
	{
        synchronized (this)
        {
            return updateJSONObjectValue(name,ObjectMapper.readObject(getValue(name),type));
        }
	}

	public <OBJECT> OBJECT getJSONObject(String name,OBJECT defaultValue,Class<OBJECT> type) throws Throwable
	{
        synchronized (this)
        {
    	    String text=getValue(name,defaultValue==null?null:ObjectMapper.writeObjectToString(defaultValue));
    	    if (text==null)
    	    {
    	        return defaultValue;
    	    }
    		return updateJSONObjectValue(name,ObjectMapper.readObject(text,type));
        }
	}
        
    public <OBJECT> OBJECT getJSONObject(String name,Class<OBJECT> type,String defaultText) throws Throwable
    {
        synchronized (this)
        {
            return updateJSONObjectValue(name,ObjectMapper.readObject(getValue(name,defaultText),type));
        }
    }
    
    private <OBJECT> OBJECT updateJSONObjectValue(String name,OBJECT object) throws Throwable
    {
        String text=ObjectMapper.writeObjectToString(object);
        ConfigurationItem item=this.getConfigurationItem(name);
        add(new ConfigurationItem(name, text, item.getSource(), item.getSourceContext(), item.getDescription()));
        return object;
    }

	public ConfigurationItem[] getConfigurationItemSnapshot()
	{
		synchronized (this)
		{
			return this.map.values().toArray(new ConfigurationItem[this.map.size()]);
		}
	}
	
	@SuppressWarnings("unchecked")
    public <OBJECT> OBJECT getNamespaceObject(String namespace,Class<OBJECT> type) throws Throwable
	{
	    OBJECT object=type.newInstance();
	    for (Field field:type.getDeclaredFields())
	    {
	        Class<?> fieldType=field.getType();
	        
	        String key=namespace+"."+field.getName();
            Alias alias=field.getDeclaredAnnotation(Alias.class);
            if (alias!=null)
            {
                key=namespace+"."+alias.value();
            }
	        ConfigurationItem item=getConfigurationItem(key);
	        if (item==null)
	        {
	            continue;
	        }
            if (fieldType==String.class)
            {
                field.set(object, getValue(key));
            }
            else if (fieldType==long.class)
            {
                field.setLong(object, getLongValue(key));
            }
            else if (fieldType==int.class)
            {
                field.setInt(object, getIntegerValue(key));
            }
            else if (fieldType==double.class)
            {
                field.setDouble(object, getDoubleValue(key));
            }
            else if (fieldType==float.class)
            {
                field.setFloat(object, getFloatValue(key));
            }
            else if (fieldType==short.class)
            {
                field.setShort(object, getShortValue(key));
            }
            else if (fieldType==byte.class)
            {
                field.setByte(object, getByteValue(key));
            }
            else if (fieldType==Long.class)
            {
                field.set(object, getNullableLongValue(key));
            }
            else if (fieldType==Integer.class)
            {
                field.set(object, getNullableIntegerValue(key));
            }
            else if (fieldType==Double.class)
            {
                field.set(object, getNullableDoubleValue(key));
            }
            else if (fieldType==Float.class)
            {
                field.set(object, getNullableFloatValue(key));
            }
            else if (fieldType==Short.class)
            {
                field.set(object, getNullableShortValue(key));
            }
            else if (fieldType==Byte.class)
            {
                field.set(object, getNullableByteValue(key));
            }
            else if (type.isEnum())
            {
                field.set(object, Enum.valueOf((Class<Enum>) fieldType, getValue(key)));
            }
            else 
            {
                field.set(object, ObjectMapper.readObject(getValue(key), fieldType));
            }
	    }
	    return object;
	}
	
	
}
