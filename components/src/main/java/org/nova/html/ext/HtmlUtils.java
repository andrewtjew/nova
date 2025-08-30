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
package org.nova.html.ext;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import org.eclipse.jetty.server.Request;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.TagElement;
import org.nova.html.properties.Display_;
import org.nova.html.remoting.ModalOption;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.annotations.DefaultValue;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

public class HtmlUtils
{
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    public static Object getDefaultValue(Method method, DefaultValue defaultValue, Class<?> type) throws Exception
    {
        if (defaultValue == null)
        {
            return null;
        }
        try
        {
            if (type == int.class)
            {
                return Integer.parseInt(defaultValue.value());
            }
            else if (type == Integer.class)
            {
                if (defaultValue.value().length()==0)
                {
                    return null;
                }
                return Integer.parseInt(defaultValue.value());
            }
            else if (type == long.class)
            {
                return Long.parseLong(defaultValue.value());
            }
            else if (type == Long.class)
            {
                if (defaultValue.value().length()==0)
                {
                    return null;
                }
                return Long.parseLong(defaultValue.value());
            }
            else if (type == short.class)
            {
                return Short.parseShort(defaultValue.value());
            }
            else if (type == Short.class)
            {
                if (defaultValue.value().length()==0)
                {
                    return null;
                }
                return Short.parseShort(defaultValue.value());
            }
            else if (type == float.class)
            {
                return Float.parseFloat(defaultValue.value());
            }
            else if (type == Float.class)
            {
                if (defaultValue.value().length()==0)
                {
                    return null;
                }
                return Float.parseFloat(defaultValue.value());
            }
            else if (type == double.class)
            {
                return Double.parseDouble(defaultValue.value());
            }
            else if (type == Double.class)
            {
                if (defaultValue.value().length()==0)
                {
                    return null;
                }
                return Double.parseDouble(defaultValue.value());
            }
            else if (type == boolean.class)
            {
                String value = defaultValue.value().toLowerCase();
                if (value.equals("true"))
                {
                    return true;
                }
                if (value.equals("false"))
                {
                    return false;
                }
            }
            else if (type == Boolean.class)
            {
                if (defaultValue.value().length()==0)
                {
                    return null;
                }
                String value = defaultValue.value().toLowerCase();
                if (value.equals("true"))
                {
                    return true;
                }
                if (value.equals("false"))
                {
                    return false;
                }
            }
            else if (type == String.class)
            {
                return defaultValue.value();
            }
            else if (type == BigDecimal.class)
            {
                if (defaultValue.value().length()==0)
                {
                    return null;
                }
                return new BigDecimal(Long.parseLong(defaultValue.value()));
            }
            else if (type.isEnum())
            {
                return Enum.valueOf((Class<Enum>) type, defaultValue.value());
            }
        }
        catch (Throwable t)
        {
        }
        throw new Exception("Unable to parse @DefaultValue value. Value=" + defaultValue.value() + ". Site=" + method.getName());
    }
    
    @Deprecated
    public static String returnFunction(String function,Object...parameters)
    {
        StringBuilder sb=new StringBuilder(function+"(");
        boolean commaNeeded=false;
        for (Object parameter:parameters)
        {
            if (commaNeeded==false)
            {
                commaNeeded=true;
            }
            else
            {
                sb.append(',');
            }
            if (parameter==null)
            {
                sb.append("null");
            }
            else 
            {
                Class<?> type=parameter.getClass();
                if (type==String.class)
                {
                    sb.append("'"+toReturnStringParameter(parameter.toString())+"'");
                }
                else if ((type==byte.class)
                        ||(type==short.class)
                        ||(type==int.class)
                        ||(type==long.class)
                        ||(type==float.class)
                        ||(type==double.class)
                        ||(type==BigDecimal.class)
                        ||(type==Byte.class)
                        ||(type==Short.class)
                        ||(type==Integer.class)
                        ||(type==Long.class)
                        ||(type==Float.class)
                        ||(type==Double.class)
                        )
                {
                    sb.append(parameter);
                }
                else
                {
                    sb.append("'"+parameter.toString()+"'");
                }
            }
        }
        sb.append(");");
        return sb.toString();
    }

    @Deprecated
    public static String escapeQuotes(String text)
    {
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<text.length();i++)
        {
            char c=text.charAt(i);
            if (c=='"')
            {
                sb.append("&quot;");
            }
            else if (c=='\'')
            {
                sb.append("&#39;");
            }
            else 
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    @Deprecated
    public static String js_disableElementAfterCall(String code)
    {
        return "(function(){this.disabled=true;"+code+";})();";
    }
    @Deprecated
    public static String js_hideModalAndCall(String modalId,String code)
    {
        return "(function(){$('#"+modalId+"').modal('"+ModalOption.hide+"');"+code+";})();";
    }
    @Deprecated
    public static String confirmPOST(String title,String text,PathAndQuery post,Object content,PathAndQuery success) throws Throwable
    {
        String data=content==null?null:ObjectMapper.writeObjectToString(content);
        return js_statement("confirmPOST",title,text,post.toString(),data,success.toString());
    }
    
    @Deprecated
    public static String js_peekPassword(String id)
    {
        return "var x = document.getElementById('"+id+"');if (x.type === 'password') {x.type = 'text';} else {x.type = 'password';}";
    }
    public static String js_peekPassword(String id,String toggleElementId,String toggleShowText,String toggleHideText)
    {
        return "var x = document.getElementById('"+id+"');var t=document.getElementById('"+toggleElementId+"');if (x.type === 'password') {x.type = 'text';t.innerHTML='"+toggleHideText+"';} else {x.type = 'password';t.innerHTML='"+toggleShowText+"';}";
    }
    
    public static String js_select_location()
    {
    	return "window.location=this.options[this.selectedIndex].value;";
    }
    public static String js_location(PathAndQuery builder)
    {
        return "window.location='"+builder.toString()+"'";
    }
    public static String js_location(QuotationMark mark,PathAndQuery builder)
    {
        return "window.location="+mark.toString()+builder.toString()+mark.toString();
    }
    public static String js_location(String url)
    {
        return "window.location='"+url+"'";
    }
    public static String js_focus(InputElement element)
    {
        return "document.getElementById('"+element.id()+"').focus();";
    }
    
    public static List<String> getSelectionNames(Context context,String prefix)
    {
        ArrayList<String> names=new ArrayList<>();
        if (context==null)
        {
            return null;
        }
        for (Entry<String, String[]> entry:context.getHttpServletRequest().getParameterMap().entrySet())
        {
            String[] values=entry.getValue();
            if ((values.length==1)&&("false".equals(values[0])==false))
            {
                if (prefix==null)
                {
                    names.add(entry.getKey());
                }
                else
                {
                    String key=entry.getKey();
                    if (key.startsWith(prefix))
                    {
                        names.add(key.substring(prefix.length()));
                    }
                }
            }
        }
        return names;
    }

    public static boolean isSelected(Context context,String value)
    {
        if (context==null)
        {
            return false;
        }
        for (Entry<String, String[]> entry:context.getHttpServletRequest().getParameterMap().entrySet())
        {
            String[] values=entry.getValue();
            if ((values.length==1)&&("false".equals(values[0])==false))
            {
                String key=entry.getKey();
                if (value.equals(key))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsParameter(Context context,String name)
    {
        return context.getHttpServletRequest().getParameterMap().containsKey(name);
    }

    public static List<Long> getSelectionLongList(Context context,String prefix)
    {
        ArrayList<Long> longs=new ArrayList<>();
        if (context==null)
        {
            return null;
        }
        for (String name:getSelectionNames(context, prefix))
        {
            try
            {
                longs.add(Long.parseLong(name));
            }
            catch (Throwable t)
            {
            }
        }
        return longs;
    }

    public static Map<Long,Long> getSelectionLongLongMap(Context context,String prefix)
    {
        if (context==null)
        {
            return null;
        }
        HashMap<Long,Long> map=new HashMap<>();
        for (Entry<String, String[]> entry:context.getHttpServletRequest().getParameterMap().entrySet())
        {
            String[] values=entry.getValue();
            if (values.length==1)
            {
                String name=null;
                if (prefix==null)
                {
                    name=entry.getKey();
                }
                else
                {
                    String key=entry.getKey();
                    if (key.startsWith(prefix))
                    {
                        name=key.substring(prefix.length());
                    }
                }
                if (name!=null)
                {
                    map.put(Long.parseLong(name), Long.parseLong(values[0]));
                }
                
            }
        }
        return map;
    }
    public static Map<Long,String> getSelectionLongStringMap(Context context,String prefix)
    {
        if (context==null)
        {
            return null;
        }
        HashMap<Long,String> map=new HashMap<>();
        for (Entry<String, String[]> entry:context.getHttpServletRequest().getParameterMap().entrySet())
        {
            String[] values=entry.getValue();
            if (values.length==1)
            {
                String name=null;
                if (prefix==null)
                {
                    name=entry.getKey();
                }
                else
                {
                    String key=entry.getKey();
                    if (key.startsWith(prefix))
                    {
                        name=key.substring(prefix.length());
                    }
                }
                if (name!=null)
                {
                    map.put(Long.parseLong(name), values[0]);
                }
                
            }
        }
        return map;
    }
    public static Map<String,String> getSelectionStringStringMap(Context context,String prefix)
    {
        if (context==null)
        {
            return null;
        }
        HashMap<String,String> map=new HashMap<>();
        for (Entry<String, String[]> entry:context.getHttpServletRequest().getParameterMap().entrySet())
        {
            String[] values=entry.getValue();
            if (values.length==1)
            {
                String name=null;
                if (prefix==null)
                {
                    name=entry.getKey();
                }
                else
                {
                    String key=entry.getKey();
                    if (key.startsWith(prefix))
                    {
                        name=key.substring(prefix.length());
                    }
                }
                if (name!=null)
                {
                    map.put(name, values[0]);
                }
                
            }
        }
        return map;
    }
    
    public static String toReturnStringParameter(String string)
    {
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<string.length();i++)
        {
            char c=string.charAt(i);
            switch (c)
            {
                case '\'':
                    sb.append("\\'");
                    break;
                    
                case '\\':
                    sb.append("\\\\");
                    break;
                    
                    
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
    
    public static String js_submit(FormElement<?> form)
    {
        return js_call("document.getElementById", form.id())+".submit()";
    }
    public static String js_submit(String id)
    {
        return js_call("document.getElementById", id)+".submit()";
    }
    
    public static String js_setTimeout(long delay,String function,Object...parameters)
    {
        String call=js_call(function, parameters);
        return "setTimeout(function(){"+call+";},"+delay+");";
    }


    public static String js_setInterval(long delay,String function,Object...parameters)
    {
        String call=js_call(function, parameters);
        return "setInterval(function(){"+call+";},"+delay+");";
    }

    @Deprecated
    public static String js_returnFunctionWithDelay(long delay,String function,Object...parameters)
    {
        String call=returnFunction(function, parameters);
        return "setTimeout(function(){"+call+"},"+delay+");";
    }

    @Deprecated
    public static String js_elementStatement(String id,String function,Object...parameters)
    {
        return js_statement("document.getElementById('"+id+"')."+function,parameters);
    }  

    @Deprecated
    public static String js_statement(String function,Object...parameters)
    {
        return js_call(function,parameters)+";";
    }

    public static String js_value(String id,Object value)
    {
        return "document.getElementById('"+id+"').value"+"=\""+value+"\"";
    }  
    public static String js_value(String id,String value)
    {
        return "document.getElementById('"+id+"').value"+"=\""+value+"\"";
    }  
    public static String js_property(String id,String property,String value)
    {
        return "document.getElementById('"+id+"')."+property+"=\""+value+"\"";
    }  
    public static String js_property(TagElement<?> element,String property,String value)
    {
        return js_property(element.id(), property, value);
    }  
    public static String js_setAttribute(TagElement<?> element,String attribute,String value)
    {
        return js_setAttribute(element.id(), attribute, value);
    }  
    public static String js_setAttribute(String id,String attribute,String value)
    {
        return "document.getElementById('"+id+"').setAttribute('"+attribute+"','"+value+"')";
    }  
    public static String js_removeAttribute(TagElement<?> element,String attribute)
    {
        return js_removeAttribute(element.id(), attribute);
    }  
    public static String js_display(String id,Display_ display)
    {
        return "document.getElementById('"+id+"').style.display='"+display+"'";
    }  
    public static String js_removeAttribute(String id,String attribute)
    {
        return "document.getElementById('"+id+"').removeAttribute('"+attribute+"')";
    }  
    public static String js_classList_add(String id,String class_)
    {
        return "document.getElementById('"+id+"').classList.add("+"'"+class_+"')";
    }  
    public static String js_classList_remove(String id,String class_)
    {
        return "document.getElementById(\""+id+"\").classList.remove("+"'"+class_+"')";
    }  
    public static String js_style(String id,String style,String value)
    {
        return "document.getElementById(\""+id+"\").style."+style+"="+"'"+value+"'";
    }  
    public static String js_classList_add(TagElement<?> element,String class_)
    {
        return js_classList_add(element.id(), class_);
    }  
    public static String js_classList_remove(TagElement<?> element,String class_)
    {
        return js_classList_remove(element.id(), class_);
    }  

    public static String js_getAttribute(TagElement<?> element,String attribute)
    {
        return js_getAttribute(element.id(), attribute);
    }  
    public static String js_getAttribute(String id,String attribute)
    {
        return "document.getElementById('"+id+"').getAttribute('"+attribute+"')";
    }  
    public static String js_jqueryCall(String id,String function,Object...parameters)
    {
        return js_call("$","#"+id)+"."+js_call(function,parameters);
    }  
    
    public static String js_jqueryCall(TagElement<?> element,String function,Object...parameters)
    {
        return js_call("$","#"+element.id())+"."+js_call(function,parameters);
    }  
    
    public static String escapeString(String string)
    {
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<string.length();i++)
        {
            char c=string.charAt(i);
            switch (c)
            {
                case '"':
                    sb.append("&quot;");
                    break;
                    
                case '\'':
                    sb.append("&apos;");
                    break;
                    
                case '\\':
                    sb.append("\\\\");
                    break;
                    
                    
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String escapeString(QuotationMark mark,String string)
    {
        StringBuilder sb=new StringBuilder();
        switch (mark)
        {
            case SINGLE:
            for (int i=0;i<string.length();i++)
            {
                char c=string.charAt(i);
                switch (c)
                {
                    case '"':
//                        sb.append("&quot;");
                        sb.append("\\\"");
                        break;
                        
                    case '\'':
                        sb.append("\\'");
                        break;
                        
                    case '/':
                        sb.append("\\/");
                        break;

                    case '\\':
                        sb.append("\\\\");
                        break;
                        
                        
                    default:
                        sb.append(c);
                }
            }
            break;

            case DOUBLE:
            for (int i=0;i<string.length();i++)
            {
                char c=string.charAt(i);
                switch (c)
                {
                    case '"':
                        sb.append("\\\"");
                        break;
                        
                    case '\'':
//                        sb.append("&apos;");
                        sb.append("'");
                        break;
                        
                    case '/':
                        sb.append("\\/");
                        break;

                    case '\\':
                        sb.append("\\\\");
                        break;
                        
                        
                    default:
                        sb.append(c);
                }
            }
            break;
        }
        
        return sb.toString();
    }

    public static String escapeAttributeString(String string)
    {
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<string.length();i++)
        {
            char c=string.charAt(i);
            switch (c)
            {
	            case '"':
	                sb.append("&#34;");
	                break;
                
	            case '\'':
	                sb.append("&#39;");
	                break;
                
//                case '\\':
//                    sb.append("\\\\");
//                    break;
//                    
//                case '/':
//                    sb.append("\\/");
//                    break;
                    
                default:
                    sb.append(c);
            }
        }
        
        return sb.toString();
    }
    
    public static String js_element(String id)
    {
        return "document.getElementById('"+id+"')";
    }  
    public static String js_elementInnerHTML(String id,String text)
    {
        return "document.getElementById('"+id+"').innerHTML='"+text+"';";
    }  
    
    public static String js_new(String instanceName,String className,Object...parameters)
    {
        return "window."+instanceName+"=new "+js_call(className,parameters)+";";
    }    
    
    // eg js_callIfElementProperty("e1","checked==true","alert","hello");
    public static String js_callIfElementProperty(String id,String expression,String function,Object...parameters)
    {
        return "if (document.getElementById('"+id+"')."+expression+"){"
                +js_call(QuotationMark.SINGLE,function,parameters)
                +";}";
    }
    public static String js_elementCall(String id,String function,Object...parameters)
    {
        return "document.getElementById('"+id+"')."+js_call(function,parameters);
    }
    public static String js_elementCall(TagElement<?> element,String function,Object...parameters)
    {
        return js_elementCall(element.id(),function,parameters);
    }
    
    public static String js_call(String function,Object...parameters)
    {
        return js_call(QuotationMark.SINGLE,function,parameters);
    }
    public static String js_writeTextToClipboard(String text)
    {
        return js_call("navigator.clipboard.writeText",text);
    }
    
    public static String js_call(QuotationMark mark,String function,Object...parameters)
    {
        StringBuilder sb=new StringBuilder(function+"(");
        boolean commaNeeded=false;
        for (Object parameter:parameters)
        {
            if (commaNeeded==false)
            {
                commaNeeded=true;
            }
            else
            {
                sb.append(',');
            }
            if (parameter==null)
            {
                sb.append("null");
            }
            else 
            {
                Class<?> type=parameter.getClass();
                boolean isArray=type.isArray();
                if (isArray)
                {
                    type=type.getComponentType();
                }
                if (type==String.class)
                {
                    if (isArray)
                    {
                        sb.append('[');
                        for (int i=0;i<Array.getLength(parameter);i++)
                        {
                            if (i>0)
                            {
                                sb.append(',');
                            }
                            sb.append(mark.toString()+escapeString(mark,Array.get(parameter, i).toString())+mark.toString());
                        }
                        sb.append(']');
                    }
                    else
                    {
                        sb.append(mark.toString()+escapeString(mark,parameter.toString())+mark.toString());
                    }
                }
                else if ((type==byte.class)
                        ||(type==short.class)
                        ||(type==int.class)
                        ||(type==long.class)
                        ||(type==float.class)
                        ||(type==double.class)
                        ||(type==boolean.class)
                        ||(type==BigDecimal.class)
                        ||(type==Byte.class)
                        ||(type==Short.class)
                        ||(type==Integer.class)
                        ||(type==Long.class)
                        ||(type==Float.class)
                        ||(type==Double.class)
                        ||(type==Boolean.class)
                        ||(type==JsObject.class)
                        )
                {
                    if (isArray)
                    {
                        sb.append('[');
                        for (int i=0;i<Array.getLength(parameter);i++)
                        {
                            if (i>0)
                            {
                                sb.append(',');
                            }
                            sb.append(Array.get(parameter, i));
                        }
                        sb.append(']');
                    }
                    else
                    {
                        sb.append(parameter);
                    }
                }
                else if (type.isEnum())
                {
                    if (isArray)
                    {
                        sb.append('[');
                        for (int i=0;i<Array.getLength(parameter);i++)
                        {
                            if (i>0)
                            {
                                sb.append(',');
                            }
                            sb.append(mark.toString()+Array.get(parameter, i)+mark.toString());
                        }
                        sb.append(']');
                    }
                    else
                    {
                        sb.append(mark.toString()+parameter+mark.toString());
                    }
                }
                else
                {
                    if (isArray)
                    {
                        sb.append('[');
                        for (int i=0;i<Array.getLength(parameter);i++)
                        {
                            if (i>0)
                            {
                                sb.append(',');
                            }
                            try
                            {
//                                sb.append(mark.toString()+ObjectMapper.writeObjectToString(Array.get(parameter, i))+mark.toString());
                                sb.append(escapeString(ObjectMapper.writeObjectToString(Array.get(parameter, i))));
                            }
                            catch (Throwable e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                        sb.append(']');
                    }
                    else
                    {
                        try
                        {
                            sb.append(escapeString(ObjectMapper.writeObjectToString(parameter)));
                        }
                        catch (Throwable e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static String json_call(String function,Object...parameters) throws Throwable
    {
        StringBuilder sb=new StringBuilder(function+"(");
        boolean commaNeeded=false;
        for (Object parameter:parameters)
        {
            if (commaNeeded==false)
            {
                commaNeeded=true;
            }
            else
            {
                sb.append(',');
            }
            if (parameter==null)
            {
                sb.append("null");
            }
            else 
            {
                sb.append(ObjectMapper.writeObjectToString(parameter));
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    //Not complete.
    public static String toImageSubType(String fileName)
    {
        int index=fileName.lastIndexOf('.');
        if (index<0)
        {
            return null;
        }
        String extension=fileName.substring(index+1).toLowerCase();
        switch (extension)
        {
            case "png":
            case "x-png":
            return "png";
                
            case "bmp":
            case "fif":
            case "gif":
            return extension;
            
            case "tif":
            case "tiff":
            return "tiff";
                
            case "jfif":
            case "jpe":
            case "jpeg":
            case "jpg":
            return "jpeg";

            case "webp":
            return "webp";
            
            default:
                return null;
        }
    }
    
    public static String toHtmlText(String text)
    {
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<text.length();i++)
        {
            char c=text.charAt(i);
            if (c=='<')
            {
                sb.append("&lt;");
            }
            else if (c=='>')
            {
                sb.append("&gt;");
            }
            else if (c=='&')
            {
                sb.append("&amp;");
            }
            else if (c=='"')
            {
                sb.append("&quot;");
            }
            else if (c=='\'')
            {
                sb.append("&#39;");
            }
            else 
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String js_inputTimeZone(InputElement<?> element)
    {
        return js_inputTimeZone(element.id());
    }
    public static String js_inputTimeZone(String id)
    {
        return "document.getElementById('"+id+"').value=Intl.DateTimeFormat().resolvedOptions().timeZone;";
    }
    public static String js_inputNow(InputElement<?> element)
    {
        return js_inputNow(element.id());
    }
    public static String js_inputNow(String id)
    {
        return "document.getElementById('"+id+"').value=new Date();";
    }
    public static String js_copyToClipboard(TagElement<?> element)
    {
//        return "var copyText=getElementById('"+element.id()+"');copyText.select();document.execCommand('Copy');";
        return "getElementById('"+element.id()+"').select();document.execCommand('Copy');";
    }
    public static String js_scollIntoView(String id)
    {
        return "getElementById('"+id+"').scrollIntoView({'behavior':'smooth','block':'start'});";
//        return "alert('hello');$('htlm,body').animate({'scrollTop':$('#"+id+"').offset().top}, 2000);alert('world');";
        
    }

    public static String js_toggle(TagElement<?> toggler,TagElement<?> target)
    {
        return "var c=document.getElementById('"+toggler.id()+"').checked;var t=document.getElementById('"+target.id()+"');t.disabled=!c;if (c==true) {t.classList.remove('disabled');} else {t.classList.add('disabled');}";
    }
    public static void writeFile(Trace parent,Context context,String name,String directory,String overrideFileName) throws Throwable
    {
        HttpServletRequest request=context.getHttpServletRequest();
//        request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT,new MultipartConfigElement(directory));
        Part part=request.getPart(name);
        if (overrideFileName==null)
        {
            overrideFileName=part.getSubmittedFileName();
        }
        part.write(overrideFileName);
    }

    public static void writeDataURL(Trace parent,Context context,String name,String directory,String overrideFileName) throws Throwable
    {
        HttpServletRequest request=context.getHttpServletRequest();
//        request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT,new MultipartConfigElement(directory));
        Part part=request.getPart(name);
        if (overrideFileName==null)
        {
            overrideFileName=part.getSubmittedFileName();
        }
//        part.write(overrideFileName);
        String data=FileUtils.readString(part.getInputStream());
        byte[] bytes = java.util.Base64.getDecoder().decode(data.substring(data.indexOf(",") + 1));
        FileUtils.writeBinaryFile(directory+"\\"+overrideFileName, bytes);
    }
    public static String getRequestPathAndQuery(HttpServletRequest request)
    {
        String query=request.getQueryString();
        if (query==null)
        {
            return request.getRequestURI();
        }
        return request.getRequestURI()+"?"+query;
    }
    public static String getRequestPathAndQuery(Context context)
    {
        return getRequestPathAndQuery(context.getHttpServletRequest());
    }
}
