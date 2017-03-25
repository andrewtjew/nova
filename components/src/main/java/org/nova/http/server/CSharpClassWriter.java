package org.nova.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.events.Characters;

import org.nova.annotations.Description;
import org.nova.core.Utils;
import org.nova.frameworks.InteropTarget;
import org.nova.json.ObjectExample;
import org.nova.json.SchemaWriter;

public class CSharpClassWriter
{
    public String write(String host, String namespace, Collection<Class<?>> types, int columns, InteropTarget target) 
    {
        HashMap<String, Class<?>> roots = new HashMap<>();
        for (Class<?> type : types)
        {
            if (type.isArray())
            {
                type = type.getComponentType();
            }
            roots.put(type.getCanonicalName(), type);
        }
        HashMap<String, Class<?>> dependents = new HashMap<>();
        for (Class<?> type : types)
        {
            if (type.isArray())
            {
                type = type.getComponentType();
            }
            discoverDependents(roots, dependents, type);
        }
        StringBuilder sb = new StringBuilder();

        sb.append("// Auto-generated on host " + host + " for target " + target + " on " + Utils.millisToLocalDateTime(System.currentTimeMillis()) + "\r\n\r\n");
        if (target == InteropTarget.CSHARP_DATACONTRACT)
        {
            sb.append("using System.Runtime.Serialization;\r\n");
        }
        sb.append("namespace " + namespace);
        sb.append("\r\n{\r\n");
        writeIndent(sb, 1).append("// --- Request and Response Classes ---\r\n\r\n");
        roots.keySet().stream().sorted().forEach(key ->
        {
            write(sb, roots.get(key), 1, columns, target);
            sb.append("\r\n");
        });
        if (dependents.size() > 0)
        {
            writeIndent(sb, 1).append("// --- Depending Classes --------------\r\n\r\n");
            dependents.keySet().stream().sorted().forEach(key ->
            {
                write(sb, dependents.get(key), 1, columns, target);
                sb.append("\r\n");
            });
        }
        sb.append("}\r\n");
        return sb.toString();
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

    private void write(StringBuilder sb, Class<?> type, int indentLevel, int columns, InteropTarget target)
    {
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
                    //should not happen.
                }
                catch (SecurityException e)
                {
                    writeComments(sb, indentLevel + 1, "// ***Application error***", columns);
                    writeComments(sb, indentLevel + 1, "// Unable to generate description for this field!", columns);
                }
                writeIndent(sb, indentLevel + 1).append(enumValue.toString()+",\r\n\r\n");
            }
            writeIndent(sb, indentLevel).append("}\r\n");

        }
        else
        {
            if (target == InteropTarget.CSHARP_DATACONTRACT)
            {
                writeIndent(sb, indentLevel).append("[DataContract]\r\n");
            }

            writeIndent(sb, indentLevel).append("public class " + type.getSimpleName() + "\r\n");
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
                writeIndent(sb, indentLevel + 1).append("public ");
                if (fieldType == boolean.class)
                {
                    sb.append("bool");
                }
                else if (fieldType == Boolean.class)
                {
                    sb.append("bool?");
                }
                else if (fieldType == Integer.class)
                {
                    sb.append("int?");
                }
                else if (fieldType == int.class)
                {
                    sb.append("int");
                }
                else if (fieldType == Short.class)
                {
                    sb.append("short?");
                }
                else if (fieldType == short.class)
                {
                    sb.append("short");
                }
                else if (fieldType == Long.class)
                {
                    sb.append("long?");
                }
                else if (fieldType == long.class)
                {
                    sb.append("long");
                }
                else if (fieldType == Float.class)
                {
                    sb.append("float?");
                }
                else if (fieldType == float.class)
                {
                    sb.append("float");
                }
                else if (fieldType == Double.class)
                {
                    sb.append("double?");
                }
                else if (fieldType == double.class)
                {
                    sb.append("double");
                }
                else if (fieldType == byte.class)
                {
                    sb.append("byte");
                }
                else if (fieldType == Byte.class)
                {
                    sb.append("byte?");
                }
                else if (fieldType == char.class)
                {
                    sb.append("char");
                }
                else if (fieldType == Character.class)
                {
                    sb.append("char?");
                }
                else if (fieldType == String.class)
                {
                    sb.append("string");
                }
                else
                {
                    if (fieldType.isArray())
                    {
                        sb.append(fieldType.getComponentType().getSimpleName());
                    }
                    else
                    {
                        sb.append(fieldType.getSimpleName());
                    }
                }
                if (field.getType().isArray())
                {
                    sb.append("[]");
                }
                sb.append(' ').append(field.getName()).append(";\r\n\r\n");
            }
            writeIndent(sb, indentLevel).append("}\r\n");
        }
    }

    private void discoverDependents(HashMap<String, Class<?>> roots, HashMap<String, Class<?>> dependents, Class<?> type)
    {
        if (roots.containsKey(type.getCanonicalName()) == false)
        {
            if (dependents.containsKey(type.getCanonicalName()))
            {
                return;
            }
            dependents.put(type.getCanonicalName(), type);
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
            if (fieldType.isArray())
            {
                fieldType = fieldType.getComponentType();
            }
            if (fieldType == boolean.class)
            {
                continue;
            }
            else if (fieldType == Boolean.class)
            {
                continue;
            }
            else if (fieldType == Integer.class)
            {
                continue;
            }
            else if (fieldType == int.class)
            {
                continue;
            }
            else if (fieldType == Short.class)
            {
                continue;
            }
            else if (fieldType == short.class)
            {
                continue;
            }
            else if (fieldType == Long.class)
            {
                continue;
            }
            else if (fieldType == long.class)
            {
                continue;
            }
            else if (fieldType == Float.class)
            {
                continue;
            }
            else if (fieldType == float.class)
            {
                continue;
            }
            else if (fieldType == Double.class)
            {
                continue;
            }
            else if (fieldType == double.class)
            {
                continue;
            }
            else if (fieldType == byte.class)
            {
                continue;
            }
            else if (fieldType == Byte.class)
            {
                continue;
            }
            else if (fieldType == char.class)
            {
                continue;
            }
            else if (fieldType == Character.class)
            {
                continue;
            }
            else if (fieldType == String.class)
            {
                continue;
            }
            if (type.isArray())
            {
                fieldType = fieldType.getComponentType();
            }
            discoverDependents(roots, dependents, fieldType);
        }
    }

    private StringBuilder writeIndent(StringBuilder sb, int indentLevel)
    {
        for (int i = 0; i < indentLevel; i++)
        {
            sb.append("  ");
        }
        return sb;
    }

}