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
package org.nova.operations;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VariableInstance
{
	private OperatorVariable variable;
	private final Object object;
	private final Field field;
	private long modified;
	private final Applicator applicator;
	VariableInstance(Applicator applicator,OperatorVariable variable,Object object,Field field) throws Throwable
	{
		field.setAccessible(true);
		this.variable=variable;
		this.field=field;
		this.object=object;
		this.applicator=applicator;
	}
	
	void setOperatorVariable(OperatorVariable variable)
	{
	    this.variable=variable;
	}

	Object parse(String valueText)
	{
        Class<?> type = this.field.getType();
        Object value=null;
        if (valueText!=null)
        {
            try
            {
                if (type.isEnum())
                {
                    value=Enum.valueOf((Class<Enum>) field.getType(), valueText);
                }
                else if (type == String.class)
                {
                    value=valueText;
                }
                else if (type == boolean.class)
                {
                    value=Boolean.parseBoolean(valueText);
                }
                else if (type == byte.class)
                {
                    value = Byte.parseByte(valueText);
                }
                else if (type == short.class)
                {
                    value = Short.parseShort(valueText);
                }
                else if (type == int.class)
                {
                    value= Integer.parseInt(valueText);
                }
                else if (type == long.class)
                {
                    value = Long.parseLong(valueText);
                }
                else if (type == float.class)
                {
                    value = Float.parseFloat(valueText);
                }
                else if (type == double.class)
                {
                    value = Double.parseDouble(valueText);
                }
                else if (type == AtomicBoolean.class)
                {
                    value = Boolean.parseBoolean(valueText);
                }
                else if (type == AtomicInteger.class)
                {
                    value = Integer.parseInt(valueText);
                }
                else if (type == AtomicLong.class)
                {
                    value = Long.parseLong(valueText);
                }
                else if (type == AtomicLong.class)
                {
                    value = Double.parseDouble(valueText);
                }
            }
            catch (Throwable t)
            {
                return null;
            }
        }
        return value;
	}
	
	public ApplicationResult set(String valueText) throws Throwable
	{
        Class<?> type = this.field.getType();
        Object value=parse(valueText);
        
        try
        {
            ApplicationResult result=this.applicator.apply(this, value);
            if (result.getStatus()!=Status.SUCCESS)
            {
                return result;
            }
            value=result.getResult();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return new ApplicationResult(Status.VALIDATION_FAILED,null,t.getMessage());
        }
                
        
        if (type.isEnum())
        {
            this.field.set(this.object,value);
        }
        else if (type == String.class)
        {
            this.field.set(this.object,value);
        }
        else if (type == boolean.class)
        {
            if (value!=null)
            {
                this.field.set(this.object, value);
            }
        }
        else if (type == byte.class)
        {
            if (value!=null)
            {
                byte typeValue=(byte)value;
                if (variable.maximum().length() > 0)
                {
                    byte maximum = Byte.parseByte(variable.maximum());
                    if (typeValue > maximum)
                    {
                        typeValue=maximum;
                    }
                }
                if (variable.minimum().length() > 0)
                {
                    byte minimum = Byte.parseByte(variable.minimum());
                    if (typeValue < minimum)
                    {
                        typeValue=minimum;
                    }
                }
                this.field.setByte(this.object, typeValue);
            }
        }
        else if (type == short.class)
        {
            if (value!=null)
            {
                short typeValue = (short)value;
                if (variable.maximum().length() > 0)
                {
                    short maximum = Short.parseShort(variable.maximum());
                    if (typeValue > maximum)
                    {
                        typeValue=maximum;
                    }
                }
                if (variable.minimum().length() > 0)
                {
                    short minimum = Short.parseShort(variable.minimum());
                    if (typeValue < minimum)
                    {
                        typeValue=minimum;
                    }
                }
                this.field.setShort(this.object, typeValue);
            }
        }
        else if (type == int.class)
        {
            if (value!=null)
            {
                int typeValue=(int)value;
                if (variable.maximum().length() > 0)
                {
                    int maximum = Integer.parseInt(variable.maximum());
                    if (typeValue > maximum)
                    {
                        typeValue=maximum;
                    }
                }
                if (variable.minimum().length() > 0)
                {
                    int minimum = Integer.parseInt(variable.minimum());
                    if (typeValue < minimum)
                    {
                        typeValue=minimum;
                    }
                }
                this.field.setInt(this.object, typeValue);
            }
        }
        else if (type == long.class)
        {
            if (value!=null)
            {
                long typeValue=(long)value;
                if (variable.maximum().length() > 0)
                {
                    long maximum = Long.parseLong(variable.maximum());
                    if (typeValue > maximum)
                    {
                        typeValue=maximum;
                    }
                }
                if (variable.minimum().length() > 0)
                {
                    long minimum = Long.parseLong(variable.minimum());
                    if (typeValue < minimum)
                    {
                        typeValue=minimum;
                    }
                }
                this.field.setLong(this.object, typeValue);
            }
        }
        else if (type == float.class)
        {
            if (value!=null)
            {
                float typeValue=(float)value;
                if (variable.maximum().length() > 0)
                {
                    float maximum = Float.parseFloat(variable.maximum());
                    if (typeValue > maximum)
                    {
                        typeValue=maximum;
                    }
                }
                if (variable.minimum().length() > 0)
                {
                    float minimum = Float.parseFloat(variable.minimum());
                    if (typeValue < minimum)
                    {
                        typeValue=minimum;
                    }
                }
                this.field.setFloat(this.object, typeValue);
            }
        }
        else if (type == double.class)
        {
            if (value!=null)
            {
                double typeValue=(double)value;
                if (variable.maximum().length() > 0)
                {
                    double maximum = Double.parseDouble(variable.maximum());
                    if (typeValue > maximum)
                    {
                        typeValue=maximum;
                    }
                }
                if (variable.minimum().length() > 0)
                {
                    double minimum = Double.parseDouble(variable.minimum());
                    if (typeValue < minimum)
                    {
                        typeValue=minimum;
                    }
                }
                this.field.setDouble(this.object, typeValue);
            }
        }
        else if (type == AtomicBoolean.class)
        {
            if (value!=null)
            {
                boolean typeValue=(boolean)value;
                ((AtomicBoolean) this.field.get(this.object)).set(typeValue);
            }
        }
        else if (type == AtomicInteger.class)
        {
            if (value!=null)
            {
                int typeValue=(int)value;
                if (variable.maximum().length() > 0)
                {
                    int maximum = Integer.parseInt(variable.maximum());
                    if (typeValue > maximum)
                    {
                        return new ApplicationResult(Status.SET_FAILED,null,"Out of range: maximum="+maximum+", value="+typeValue);
                    }
                }
                if (variable.minimum().length() > 0)
                {
                    int minimum = Integer.parseInt(variable.minimum());
                    if (typeValue < minimum)
                    {
                        return new ApplicationResult(Status.SET_FAILED,null,"Out of range: minimum="+minimum+", value="+typeValue);
                    }
                }
                ((AtomicInteger) this.field.get(this.object)).set(typeValue);
            }
        }
        else if (type == AtomicLong.class)
        {
            if (value!=null)
            {
                long typeValue=(long)value;
                if (variable.maximum().length() > 0)
                {
                    long maximum = Long.parseLong(variable.maximum());
                    if (typeValue > maximum)
                    {
                        return new ApplicationResult(Status.SET_FAILED,null,"Out of range: maximum="+maximum+", value="+typeValue);
                    }
                }
                if (variable.minimum().length() > 0)
                {
                    long minimum = Long.parseLong(variable.minimum());
                    if (typeValue < minimum)
                    {
                        return new ApplicationResult(Status.SET_FAILED,null,"Out of range: minimum="+minimum+", value="+typeValue);
                    }
                }
                ((AtomicLong) this.field.get(this.object)).set(typeValue);
            }
        }
        this.modified=System.currentTimeMillis();
        return new ApplicationResult(Status.SUCCESS);
	}
	@SuppressWarnings("unchecked")
	public void setEnumValue(String value) throws Throwable
	{
		field.set(object, Enum.valueOf((Class<Enum>) field.getType(), value));
		this.modified=System.currentTimeMillis();
	}
	public Object getObject()
	{
		return object;
	}
	public Field getField()
	{
		return field;
	}
	public String getName()
	{
		return variable.alias().length()==0?field.getName():variable.alias();
	}
	public Object getValue() throws Throwable
	{
		return field.get(this.object);
	}
	public OperatorVariable getOperatorVariable()
	{
		return variable;
	}
	public long getModified()
	{
		return modified;
	}
}
