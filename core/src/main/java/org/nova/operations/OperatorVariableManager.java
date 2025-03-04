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
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.nova.tracing.Trace;

public class OperatorVariableManager
{
	final private HashMap<String, HashMap<String, VariableInstance>> map;
	final private HashMap<String,Applicator> applicators;
	final private OperatorVariableStore store;
	
	public OperatorVariableManager(OperatorVariableStore store)
	{
		this.map=new HashMap<>();
		this.applicators=new HashMap<>();
		registerApplicators(new DefaultApplicator());
		this.store=store;
	}
    public void registerApplicators(Applicator applicator) 
    {
        this.applicators.put(applicator.getClass().getName(),applicator);
    }
	public void register(Trace parent,Object object) throws Throwable
	{
		register(parent,object.getClass().getSimpleName(),object);
	}
	public void register(Trace parent,String category,Object object) throws Throwable
	{
		HashMap<String,VariableInstance> variables=map.get(category);
		if (variables==null)
		{
			variables=new HashMap<>();
			map.put(category, variables);
		}
		for (Field field:object.getClass().getDeclaredFields())
		{
			Class<?> type=field.getType();
			OperatorVariable variable=(OperatorVariable)field.getAnnotation(OperatorVariable.class);
			if (variable==null)
			{
				continue;
			}
			if (Modifier.isFinal(field.getModifiers()))
			{
				throw new Exception("OperatorVariable field cannot be final. Name="+object.getClass().getCanonicalName()+"."+field.getName()+", type="+type.getName());
			}
			if ((type.isPrimitive()==false)&&(type!=String.class)&&(type.isEnum()==false)&&(type!=AtomicInteger.class)&&(type!=AtomicLong.class)&&(type!=AtomicBoolean.class))
			{
				throw new Exception("OperatorVariable annotation must be of the following types only: primitives, String, enum, AtomicBoolean, AtomicInteger, AtomicLong, AtomicDouble. Name="+object.getClass().getCanonicalName()+"."+field.getName()+", type="+type.getName());
			}
			String key=variable.alias().length()==0?field.getName():variable.alias();
			if (variables.containsKey(key))
			{
				throw new Exception("OperatorVariable already registered: name="+object.getClass().getCanonicalName()+"."+field.getName()+", type="+type.getName()+", key="+key);
			}
			Applicator applicator=this.applicators.get(variable.applicator().getName());
            if (applicator==null)
            {
                throw new Exception("No validator registered: name="+object.getClass().getCanonicalName()+"."+field.getName()+", type="+type.getName()+", key="+key);
            }
            if (applicator instanceof DefaultApplicator==false)
            {
                System.out.println("Applicatior:"+applicator.getClass().getSimpleName());
            }
			
            VariableInstance instance=new VariableInstance(applicator,variable, object, field);
			if ((this.store!=null)&&(parent!=null))
			{
			    String valueText=this.store.load(parent, category, instance);
			    if (valueText==null)
			    {
                    valueText=variable.defaultValue();
			    }
                 instance.set(valueText);
			}
			variables.put(key,instance);
		}
	}
	public VariableInstance getInstance(String category,String key)
	{
		synchronized (this)
		{
			HashMap<String, VariableInstance> variables=this.map.get(category);
			if (variables==null)
			{
				return null;
			}
			return variables.get(key);
		}
	}
	
    public ApplicationResult setOperatorVariable(Trace parent,String category,String key,String value) throws Throwable
    {
        synchronized (this)
        {
            HashMap<String, VariableInstance> variables=this.map.get(category);
            if (variables==null)
            {
                return new ApplicationResult(Status.CATEGORY_NOT_FOUND);
            }
            VariableInstance instance=variables.get(key);
            if (instance==null)
            {
                return new ApplicationResult(Status.KEY_NOT_FOUND);
            }
            ApplicationResult result=instance.set(value);
            if (result.status==Status.SUCCESS)
            {
                if (this.store!=null)
                {
                    this.store.save(parent, category,instance, value);
                }
            }
            return result;
        }
    }
	public String[] getCategories()
	{
		synchronized (this)
		{
			String[] categories=new String[this.map.size()];
			int index=0;
			for (String category:this.map.keySet())
			{
				categories[index++]=category;
			}
			return categories;
		}
	}
	public VariableInstance[] getInstances(String category)
	{
		synchronized (this)
		{
			HashMap<String, VariableInstance> variables=this.map.get(category);
			if (variables==null)
			{
				return null;
			}
			return variables.values().toArray(new VariableInstance[variables.size()]);
		}
	}
	public String[] getKeys(String category)
	{
		synchronized (this)
		{
			HashMap<String, VariableInstance> variables=this.map.get(category);
			if (variables==null)
			{
				return null;
			}
			return variables.keySet().toArray(new String[variables.size()]);
		}
	}

}
