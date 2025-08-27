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
import java.util.HashMap;
import org.nova.annotations.Description;

public class SchemaWriter
{
	static public void writeSchema(OutputStream outputStream, Class<?> type) throws IOException
	{
		new SchemaWriter().write(outputStream, type);
	}
	
	private final HashMap<String,String> references=new HashMap<>();
	
	
    public void write(OutputStream outputStream, Class<?> type) throws IOException
    {
        outputStream.write(write(type).getBytes(StandardCharsets.UTF_8));
    }

    public String write(Class<?> contentType) throws IOException
    {
        StringBuilder sb=new StringBuilder();
        writeIndent(sb,0).append("{\r\n");
        writeIndent(sb,1).append("\"$schema\":\"http://json-schema.org/draft-04/schema#\",\r\n");
        if (contentType.isArray())
        {
            writeIndent(sb,1).append("\"type\":\"array\",\r\n");
            writeIndent(sb,1).append("\"items\":{\r\n");
            writeObjectSchema(sb,"#",contentType.getComponentType(),2);
            writeIndent(sb,1).append("}\r\n");
        }
        else
        {
            writeObjectSchema(sb,"#",contentType,1);
        }
        writeIndent(sb,0).append("}\r\n");
        return sb.toString();
    }
	
	private StringBuilder writeIndent(StringBuilder sb,int indentLevel)
	{
		for (int i=0;i<indentLevel;i++)
		{
			sb.append("  ");
		}
		return sb;
	}
	
	private void writeObjectSchema(StringBuilder sb,String path,Class<?> type,int indentLevel)
	{
		String name=type.getName();
		String reference=this.references.get(name);
		if (this.references.containsKey(name))
		{
			writeIndent(sb,indentLevel).append("\"$ref\":\""+reference+"\"\r\n");
			return;
		}
		this.references.put(name,path);
		writeIndent(sb,indentLevel).append("\"type\":\"object\",\r\n");
		writeIndent(sb,indentLevel).append("\"properties\":{\r\n");
		boolean needComma=false;
		for (Field field: type.getDeclaredFields())
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
			if (needComma)
			{
				sb.append(",\r\n");
			}
			else
			{
				needComma=true;
			}
			writeIndent(sb, indentLevel+1).append('"').append(field.getName()).append("\":{\r\n");
			Class<?> fieldType=field.getType();
			Description description=field.getAnnotation(Description.class);
			if (description!=null)
			{
				writeIndent(sb, indentLevel+2).append("\"description\":\""+description.value()+"\",\r\n");
			}
			
			if (field.getType().isArray())
			{
				writeIndent(sb,indentLevel+2).append("\"type\":\"array\",\r\n");
				writeIndent(sb,indentLevel+2).append("\"items\":{\r\n");
				fieldType=fieldType.getComponentType();
				indentLevel++;
			}
			if ((fieldType==Boolean.class)||(fieldType==boolean.class))
			{
				writeIndent(sb, indentLevel+2).append("\"type\":\"boolean\"\r\n");
			}
			else if ((fieldType==Integer.class)||(fieldType==int.class)||(fieldType==Short.class)||(fieldType==short.class)||(fieldType==Long.class)||(fieldType==long.class))
			{
				writeIndent(sb, indentLevel+2).append("\"type\":\"integer\"\r\n");
				
			}
			else if ((fieldType==Float.class)||(fieldType==float.class)||(fieldType==Double.class)||(fieldType==double.class))
			{
				writeIndent(sb, indentLevel+2).append("\"type\":\"number\"\r\n");
			}
			else if (fieldType==String.class)
			{
				writeIndent(sb, indentLevel+2).append("\"type\":\"string\"\r\n");
			}
			else 
			{
				writeObjectSchema(sb,path+"/"+field.getName(),fieldType,indentLevel+2);
			}
			if (field.getType().isArray())
			{
				indentLevel--;
				writeIndent(sb,indentLevel+2).append("}\r\n");
			}
			writeIndent(sb,indentLevel+1).append("}");
		}
		sb.append("\r\n");
		writeIndent(sb,indentLevel).append("}\r\n");
	}
	
}
