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
package org.nova.sqldb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
public class FieldMaps
{
    private static HashMap<String, ConstructorFieldMap> maps = new HashMap<>();

    static class ConstructorFieldMap
    {
        final public Object[] parameters;
        final public Constructor<?> constructor;
        final public HashMap<String, Field> map;

        
        public ConstructorFieldMap(Class<?> type, HashMap<String, Field> map) throws Throwable
        {
            Constructor<?>[] constructors = type.getDeclaredConstructors();
            this.map = map;
            int shortest = Integer.MAX_VALUE;
            Constructor<?> shortestConstructor = null;
            for (Constructor<?> constructor : constructors)
            {
                if (constructor.getParameterCount() < shortest)
                {
                    shortest = constructor.getParameterCount();
                    shortestConstructor = constructor;
                }
            }
            this.constructor = shortestConstructor;
            this.parameters = new Object[shortestConstructor.getParameterCount()];
            for (int i = 0; i < shortestConstructor.getParameterCount(); i++)
            {
                Class<?> parameterType = shortestConstructor.getParameterTypes()[i];
                if (parameterType.isPrimitive())
                {
                    if (parameterType == boolean.class)
                    {
                        parameters[i] = false;
                    }
                    else if (parameterType == int.class)
                    {
                        parameters[i] = 0;
                    }
                    else if (parameterType == long.class)
                    {
                        parameters[i] = 0L;
                    }
                    else if (parameterType == float.class)
                    {
                        parameters[i] = 0.0f;
                    }
                    else if (parameterType == double.class)
                    {
                        parameters[i] = 0.0;
                    }
                    else if (parameterType == byte.class)
                    {
                        parameters[i] = (byte) 0;
                    }
                    else if (parameterType == char.class)
                    {
                        parameters[i] = (char) 0;
                    }
                    else if (parameterType == short.class)
                    {
                        parameters[i] = (short) 0;
                    }
                }
                else if (parameterType.isEnum())
                {
                    parameters[i] = parameterType.newInstance();
                }
            }
            shortestConstructor.setAccessible(true);
        }
        public Object newInstance() throws Throwable
        {
            return this.constructor.newInstance(this.parameters);
        }
    }

    public static ConstructorFieldMap get(Class<?> type) throws Throwable
    {
        String key = type.getCanonicalName();
        synchronized (maps)
        {
            ConstructorFieldMap map = maps.get(key);
            if (map != null)
            {
                return map;
            }
        }
        HashMap<String, Field> map = new HashMap<>();
        for (Field field : type.getDeclaredFields())
        {
            ColumnNameAlias alias = (ColumnNameAlias) field.getAnnotation(ColumnNameAlias.class);
            String name = alias != null ? alias.value() : field.getName();
            field.setAccessible(true);
            map.put(name, field);
        }
        ConstructorFieldMap constructorFieldMap=new ConstructorFieldMap(type, map);
        synchronized (maps)
        {
            maps.put(key, constructorFieldMap);
        }
        return constructorFieldMap;
    }
}
