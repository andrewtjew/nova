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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.nova.annotations.Description;
import org.nova.frameworks.InteropTarget;
import org.nova.utils.TypeUtils;
import org.nova.utils.Utils;

public class CSharpClassWriter
{
    public String write(String host, String namespace, Collection<Class<?>> types, int columns, InteropTarget target) 
    {
        HashMap<String, Class<?>> dependents = new HashMap<>();
        for (Class<?> type : types)
        {
            discoverDependents(dependents, type);
        }
        StringBuilder sb = new StringBuilder();

        sb.append("// Auto-generated at host " + host + " for target " + target + " on " + Utils.millisToLocalDateTime(System.currentTimeMillis()) + "\r\n\r\n");
        if (target == InteropTarget.CSHARP_DATACONTRACT)
        {
            sb.append("using System.Runtime.Serialization;\r\n");
        }
        sb.append("namespace " + namespace);
        sb.append("\r\n{\r\n");
        if (dependents.size() > 0)
        {
            dependents.keySet().stream().sorted().forEach(key ->
            {
                write(sb, dependents.get(key), 1, columns, target);
                sb.append("\r\n");
            });
        }
        sb.append("}\r\n");
        return sb.toString();
    }

    private void discoverDependents(HashMap<String, Class<?>> dependents, Class<?> type)
    {
        this.level++;
        try
        {
            while (type.isArray())
            {
                type = type.getComponentType();
                
            }
            if (TypeUtils.isSingleRepresentationValueType(type))
            {
                return;
            }
            if (dependents.containsKey(type.getCanonicalName()))
            {
                return;
            }
            dependents.put(type.getCanonicalName(), type);
            if (type.isEnum())
            {
                return;
            }
            for (Field field : type.getDeclaredFields())
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
                Class<?> fieldType = field.getType();
                if (fieldType==Object.class)
                {
                    dependents.remove(type.getCanonicalName());
                    return;
                }
                discoverDependents(dependents, fieldType);
            }
            Class<?> superClass=type.getSuperclass();
            if ((superClass!=null)&&(superClass!=Object.class))
            {
                discoverDependents(dependents, superClass);
            }
        }
        finally
        {
            this.level--;
        }
    }
    
    private List<String> breakIntoLines(String text, int desiredLength)
    {
        ArrayList<String> lines = new ArrayList<>();

        int beginIndex = 0;
        for (;;)
        {
            int point = text.indexOf('.', beginIndex + desiredLength);
            int space = text.indexOf(' ', beginIndex + desiredLength);
            if (point >= 0 && point < space)
            {
                lines.add(text.substring(beginIndex, point + 1));
                beginIndex = point + 2;
            }
            else if (space >= 0)
            {
                lines.add(text.substring(beginIndex, space));
                beginIndex = space + 1;
            }
            else
            {
                break;
            }
            while ((beginIndex < text.length()) && Character.isWhitespace(text.charAt(beginIndex)))
            {
                beginIndex++;
            }
        }
        lines.add(text.substring(beginIndex));
        return lines;
    }

    private void writeComments(StringBuilder sb, int indentLevel, String text, int columns)
    {
        for (String line : breakIntoLines(text, columns - indentLevel * 2))
        {
            writeIndent(sb, indentLevel).append("// " + line + "\r\n");
        }
    }

    private String translateTypeName(Class<?> fieldType)
    {
        if (fieldType == boolean.class)
        {
            return "bool";
        }
        else if (fieldType == Boolean.class)
        {
            return "bool?";
        }
        else if (fieldType == Integer.class)
        {
            return "int?";
        }
        else if (fieldType == int.class)
        {
            return "int";
        }
        else if (fieldType == Short.class)
        {
            return "short?";
        }
        else if (fieldType == short.class)
        {
            return "short";
        }
        else if (fieldType == Long.class)
        {
            return "long?";
        }
        else if (fieldType == long.class)
        {
            return "long";
        }
        else if (fieldType == Float.class)
        {
            return "float?";
        }
        else if (fieldType == float.class)
        {
            return "float";
        }
        else if (fieldType == Double.class)
        {
            return "double?";
        }
        else if (fieldType == double.class)
        {
            return "double";
        }
        else if (fieldType == byte.class)
        {
            return "byte";
        }
        else if (fieldType == Byte.class)
        {
            return "byte?";
            
        }
        else if (fieldType == char.class)
        {
            return "char";
        }
        else if (fieldType == Character.class)
        {
            return "char?";
        }
        else if (fieldType == String.class)
        {
            return "string";
        }
        else if (fieldType == BigDecimal.class)
        {
            return "decimal";
        }
        else
        {
            if (fieldType.isArray())
            {
                return translateTypeName(fieldType.getComponentType())+"[]";
            }
            else
            {
                return fieldType.getSimpleName();
            }
        }
        
    }
    
    private void write(StringBuilder sb, Class<?> type, int indentLevel, int columns, InteropTarget target)
    {
        if (type.isArray())
        {
            return;
        }
        if (type.isPrimitive())
        {
            return;
        }
        if (TypeUtils.isDerivedFrom(type, Number.class))
        {
            return;
        }
            
        Description description = type.getAnnotation(Description.class);
        if (description != null)
        {
            writeComments(sb, indentLevel, description.value(), columns);
        }
        if (type.isEnum())
        {
            writeIndent(sb, indentLevel).append("public enum " + type.getSimpleName() + "\r\n");
            writeIndent(sb, indentLevel).append("{\r\n");
            Object[] enums=type.getEnumConstants();
            for (Object enumValue:enums)
            {
                try
                {
                    description = type.getField(enumValue.toString()).getAnnotation(Description.class);
                    if (description != null)
                    {
                        writeComments(sb, indentLevel + 1, description.value(), columns);
                    }
                }
                catch (NoSuchFieldException e)
                {
                    writeComments(sb, indentLevel + 1, "// ***No such field***", columns);
                    //should not happen.
                }
                catch (SecurityException e)
                {
                    writeComments(sb, indentLevel + 1, "// ***Application error***", columns);
                    writeComments(sb, indentLevel + 1, "// Unable to generate description for this field!", columns);
                }
                @SuppressWarnings("rawtypes")
                Enum e=(Enum)enumValue;
                String name=e.name();
                String toString=e.toString();
                if (name.equals(toString))
                {
                    writeIndent(sb, indentLevel + 1).append(name+",\r\n\r\n");
                }
                else
                {
                    writeIndent(sb, indentLevel + 1).append(name+"="+toString+",\r\n\r\n");
                    
                }
            }
            writeIndent(sb, indentLevel).append("}\r\n");

        }
        else
        {
            if (target == InteropTarget.CSHARP_DATACONTRACT)
            {
                writeIndent(sb, indentLevel).append("[DataContract]\r\n");
            }

            writeIndent(sb, indentLevel).append("public class " + type.getSimpleName());
            Class<?> superClass=type.getSuperclass();
            if ((superClass!=null)&&(superClass!=Object.class))
            {
                sb.append(":"+superClass.getSimpleName());
            }
            sb.append("\r\n");
            writeIndent(sb, indentLevel).append("{\r\n");
            for (Field field : type.getDeclaredFields())
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
                Class<?> fieldType = field.getType();

                description = field.getAnnotation(Description.class);
                if (description != null)
                {
                    writeComments(sb, indentLevel + 1, description.value(), columns);
                }

                if (target == InteropTarget.CSHARP_DATACONTRACT)
                {
                    writeIndent(sb, indentLevel + 1).append("[DataMember]\r\n");
                }
                if (fieldType.isArray()&&fieldType.getComponentType().isEnum())
                {
                    String typeName=fieldType.getSimpleName();
                    String fieldName=field.getName();
                    writeIndent(sb, indentLevel + 1).append("public string[]");
                    sb.append(' ').append(field.getName()).append(";\r\n\r\n");
                    writeIndent(sb, indentLevel + 1).append("public "+typeName+" Get"+fieldName+"(int index)\r\n");
                    writeIndent(sb, indentLevel + 1).append("{\r\n");
                    writeIndent(sb, indentLevel + 2).append("return ("+typeName+")System.Enum.Parse(typeof("+typeName+"),this."+fieldName+"[index]);\r\n");
                    writeIndent(sb, indentLevel + 1).append("}\r\n");
                    writeIndent(sb, indentLevel + 1).append("public void Set"+fieldName+"(int index,"+typeName+" "+fieldName+")\r\n");
                    writeIndent(sb, indentLevel + 1).append("{\r\n");
                    writeIndent(sb, indentLevel + 2).append("this."+fieldName+"[index]="+fieldName+".ToString();\r\n");
                    writeIndent(sb, indentLevel + 1).append("}\r\n");
                    writeIndent(sb, indentLevel + 1).append("public int "+fieldName+"Length()\r\n");
                    writeIndent(sb, indentLevel + 1).append("{\r\n");
                    writeIndent(sb, indentLevel + 2).append("return this."+fieldName+".Length;\r\n");
                    writeIndent(sb, indentLevel + 1).append("}\r\n");
                }
                else if (fieldType.isEnum())
                {
                    String typeName=fieldType.getSimpleName();
                    String fieldName=field.getName();
                    writeIndent(sb, indentLevel + 1).append("public string");
                    sb.append(' ').append(field.getName()).append(";\r\n\r\n");
                    writeIndent(sb, indentLevel + 1).append("public "+typeName+" Get"+fieldName+"()\r\n");
                    writeIndent(sb, indentLevel + 1).append("{\r\n");
                    writeIndent(sb, indentLevel + 2).append("return ("+typeName+")System.Enum.Parse(typeof("+typeName+"),this."+fieldName+");\r\n");
                    writeIndent(sb, indentLevel + 1).append("}\r\n");
                    writeIndent(sb, indentLevel + 1).append("public void Set"+fieldName+"("+typeName+" "+fieldName+")\r\n");
                    writeIndent(sb, indentLevel + 1).append("{\r\n");
                    writeIndent(sb, indentLevel + 2).append("this."+fieldName+"="+fieldName+".ToString();\r\n");
                    writeIndent(sb, indentLevel + 1).append("}\r\n");
                    
                }
                else
                {
                    writeIndent(sb, indentLevel + 1).append("public ");
                    sb.append(translateTypeName(fieldType));
                    sb.append(' ').append(field.getName()).append(";\r\n\r\n");
                }
            }
            writeIndent(sb, indentLevel).append("}\r\n");
        }
    }


    int level=0;
    
    private StringBuilder writeIndent(StringBuilder sb, int indentLevel)
    {
        for (int i = 0; i < indentLevel; i++)
        {
            sb.append("  ");
        }
        return sb;
    }

}
