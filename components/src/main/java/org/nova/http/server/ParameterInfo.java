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
package org.nova.http.server;

import java.lang.annotation.Annotation;

public class ParameterInfo
{
	final private ParameterSource source;
	final private int index;
	final private Class<?> type;
	final private Object defaultValue;
	final private int pathIndex;
	final private String name;
	final private Annotation annotation;
	public ParameterInfo(ParameterSource source,Annotation annotation,String name,int index,Class<?> type,Object defaultValue)
	{
		this.source=source;
        this.annotation=annotation;
		this.name=name;
		this.index=index;
		this.type=type;
		this.defaultValue=defaultValue;
		this.pathIndex=0;
	}
	public ParameterInfo(ParameterInfo info,int pathIndex)
	{
		this.source=info.source;
        this.annotation=info.annotation;
		this.name=info.name;
		this.index=info.index;
		this.type=info.type;
		this.defaultValue=info.defaultValue;
		this.pathIndex=pathIndex;
	}
	public ParameterSource getSource()
	{
		return source;
	}
	public int getIndex()
	{
		return index;
	}
	public Class<?> getType()
	{
		return this.type;
	}
	public Object getDefaultValue()
	{
		return this.defaultValue;
	}
	public int getPathIndex()
	{
		return pathIndex;
	}
	public String getName()
	{
		return name;
	}
//	public Class<?> getParameterType()
//	{
//	    return this.type;
//	}
	public Annotation getAnnotation()
	{
	    return this.annotation;
	}
}
