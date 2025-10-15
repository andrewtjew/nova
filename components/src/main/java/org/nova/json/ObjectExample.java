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
package org.nova.json;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import org.nova.annotations.Example;

public class ObjectExample
{
	static public void write(OutputStream outputStream, Class<?> contentType) throws IOException
	{
		new ObjectExample().writeExample(outputStream, contentType);
	}
	
	private final HashSet<String> shownClasses=new HashSet<>();
	

	private StringBuilder writeIndent(StringBuilder sb,int indentLevel)
	{
		for (int i=0;i<indentLevel;i++)
		{
			sb.append("  ");
		}
		return sb;
	}

	private int integerValue=2;
	private int fractionValue=1;
	private void writeExample(StringBuilder sb,Class<?> type,Example example,int indentLevel)
	{
	    if ((type==Boolean.class)||(type==boolean.class))
		{
	        if (example!=null)
	        {
	            Boolean.parseBoolean(example.value());
                sb.append(example.value());
	        }
	        else
	        {
	            sb.append(++integerValue%1==1?"true":"false");
	        }
		}
		else if ((type==Integer.class)||(type==int.class)||(type==Short.class)||(type==short.class)||(type==Long.class)||(type==long.class)||(type==byte.class)||(type==Byte.class))
		{
            if (example!=null)
            {
                Long.parseLong(example.value());
                sb.append(example.value());
            }
            else
            {
                sb.append(integerValue++);
            }
		}
		else if ((type==Float.class)||(type==float.class)||(type==Double.class)||(type==double.class))
		{
            if (example!=null)
            {
                Double.parseDouble(example.value());
                sb.append(example.value());
            }
            else
            {
                sb.append(integerValue+++"."+fractionValue++);
            }
		}
        else if (type==String.class)
        {
            if (example!=null)
            {
                sb.append("\""+example.value()+"\"");
            }
            else
            {
                sb.append("\""+integerValue+++" bottles\"");
            }
        }
        else if (type.isEnum())
        {
            Object[] enums=type.getEnumConstants();
            sb.append("\""+enums[(integerValue++)%enums.length]+"\"");
        }
		else if (type.isArray())
		{
            if (this.shownClasses.contains(type.getComponentType().getName())==false)
            {
    			sb.append("[");
    			writeExample(sb,type.getComponentType(),example,indentLevel+1);
    			sb.append(",");
    			writeExample(sb,type.getComponentType(),example,indentLevel+1);
    			sb.append("\r\n");
    			writeIndent(sb, indentLevel).append("]");
            }
		}
		else
		{
			this.shownClasses.add(type.getName());
			sb.append("{\r\n");
			boolean commaNeeded=false;
			for (Field field: type.getDeclaredFields())
			{
                Class<?> fieldType=field.getType();
                if (this.shownClasses.contains(fieldType.getName())==false)
                {
    				int modifiers = field.getModifiers();
    				if (Modifier.isTransient(modifiers))
    				{
    					continue;
    				}
    				if (Modifier.isStatic(modifiers))
    				{
    					continue;
    				}
    				if (commaNeeded)
    				{
    					sb.append(",\r\n");
    				}
    				else
    				{
    					commaNeeded=true;
    				}
	                writeIndent(sb, indentLevel+1).append("\""+field.getName()+"\": ");
	                writeExample(sb,fieldType,field.getAnnotation(Example.class),indentLevel+1);
		        }
			}
			sb.append("\r\n");
			writeIndent(sb, indentLevel).append("}");
		}
	}
	
	
	public void writeExample(OutputStream outputStream, Class<?> contentType) throws IOException
	{
		StringBuilder sb=new StringBuilder();
		writeExample(sb,contentType,null,0);
		outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
	}
	

}
