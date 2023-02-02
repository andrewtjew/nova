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

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Request;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.TagElement;
import org.nova.html.remoting.ModalOption;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.json.ObjectMapper;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

public class HtmlUtils
{
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
//        return "document.getElementById('"+form.id()+"').submit();";
        return js_call("document.getElementById", form.id())+".submit()";
    }
    
    public static String js_callWithDelay(long delay,String function,Object...parameters)
    {
        String call=js_statement(function, parameters);
        return "setTimeout(function(){"+call+"},"+delay+");";
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

    public static String js_setElementProperty(String id,String property,String value)
    {
        return "document.getElementById('"+id+"')."+property+"='"+value+"'";
    }  
    public static String js_setElementProperty(TagElement<?> element,String property,String value)
    {
        return js_setElementProperty(element.id(), property, value);
    }  
    public static String js_elementCall(String id,String function,Object...parameters)
    {
        return js_call("document.getElementById('"+id+"')."+function,parameters);
    }  
    public static String js_elementCall(TagElement<?> element,String function,Object...parameters)
    {
        return js_elementCall(element.id(), function, parameters);
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
    
    public static String js_new(String instanceName,String className,Object...parameters)
    {
        return "var "+instanceName+"=new "+js_call(className,parameters)+";";
    }    
    
    // eg js_callIfElementProperty("e1","checked==true","alert","hello");
    public static String js_callIfElementProperty(String id,String expression,String function,Object...parameters)
    {
        return "if (document.getElementById('"+id+"')."+expression+"){"
                +js_call(QuotationMark.SINGLE,function,parameters)
                +";}";
    }
    
    public static String js_call(String function,Object...parameters)
    {
        return js_call(QuotationMark.SINGLE,function,parameters);
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
    public static String js_copyToClipboard(TagElement<?> element)
    {
        return "var copyText=getElementById('"+element.id()+"');copyText.select();document.execCommand('Copy');";
//        return "getElementById('"+element.id()+"').select();document.execCommand('Copy');";
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
        request.setAttribute(Request.MULTIPART_CONFIG_ELEMENT,new MultipartConfigElement(directory));
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
        request.setAttribute(Request.MULTIPART_CONFIG_ELEMENT,new MultipartConfigElement(directory));
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
    
    
    /*
    public static String disableSubmitFunction()
    {
        return "return !(window.event && window.event.keyCode == 13);";
    }
    */
//    public final static LiteralHtml STATE_AND_CODE_OPTIONS=new LiteralHtml("<option value='AL'>Alabama</option><option value='AK'>Alaska</option><option value='AZ'>Arizona</option><option value='AR'>Arkansas</option><option value='CA'>California</option><option value='CO'>Colorado</option><option value='CT'>Connecticut</option><option value='DE'>Delaware</option><option value='DC'>District Of Columbia</option><option value='FL'>Florida</option><option value='GA'>Georgia</option><option value='HI'>Hawaii</option><option value='ID'>Idaho</option><option value='IL'>Illinois</option><option value='IN'>Indiana</option><option value='IA'>Iowa</option><option value='KS'>Kansas</option><option value='KY'>Kentucky</option><option value='LA'>Louisiana</option><option value='ME'>Maine</option><option value='MD'>Maryland</option><option value='MA'>Massachusetts</option><option value='MI'>Michigan</option><option value='MN'>Minnesota</option><option value='MS'>Mississippi</option><option value='MO'>Missouri</option><option value='MT'>Montana</option><option value='NE'>Nebraska</option><option value='NV'>Nevada</option><option value='NH'>New Hampshire</option><option value='NJ'>New Jersey</option><option value='NM'>New Mexico</option><option value='NY'>New York</option><option value='NC'>North Carolina</option><option value='ND'>North Dakota</option><option value='OH'>Ohio</option><option value='OK'>Oklahoma</option><option value='OR'>Oregon</option><option value='PA'>Pennsylvania</option><option value='RI'>Rhode Island</option><option value='SC'>South Carolina</option><option value='SD'>South Dakota</option><option value='TN'>Tennessee</option><option value='TX'>Texas</option><option value='UT'>Utah</option><option value='VT'>Vermont</option><option value='VA'>Virginia</option><option value='WA'>Washington</option><option value='WV'>West Virginia</option><option value='WI'>Wisconsin</option><option value='WY'>Wyoming</option>");
//    public final static LiteralHtml STATE_CODE_OPTIONS=new LiteralHtml("<option value='AL'></option><option value='AK'></option><option value='AZ'></option><option value='AR'></option><option value='CA'></option><option value='CO'></option><option value='CT'></option><option value='DE'></option><option value='DC'></option><option value='FL'></option><option value='GA'></option><option value='HI'></option><option value='ID'></option><option value='IL'></option><option value='IN'></option><option value='IA'></option><option value='KS'></option><option value='KY'></option><option value='LA'></option><option value='ME'></option><option value='MD'></option><option value='MA'></option><option value='MI'></option><option value='MN'></option><option value='MS'></option><option value='MO'></option><option value='MT'></option><option value='NE'></option><option value='NV'></option><option value='NH'></option><option value='NJ'> Jersey</option><option value='NM'></option><option value='NY'></option><option value='NC'></option><option value='ND'></option><option value='OH'></option><option value='OK'></option><option value='OR'></option><option value='PA'></option><option value='RI'></option><option value='SC'></option><option value='SD'></option><option value='TN'></option><option value='TX'></option><option value='UT'></option><option value='VT'></option><option value='VA'></option><option value='WA'></option><option value='WV'></option><option value='WI'></option><option value='WY'></option>");
//    public final static LiteralHtml STATE_OPTIONS=new LiteralHtml("<option>Alabama</option><option>Alaska</option><option>Arizona</option><option>Arkansas</option><option>California</option><option>Colorado</option><option>Connecticut</option><option>Delaware</option><option>District Of Columbia</option><option>Florida</option><option>Georgia</option><option>Hawaii</option><option>Idaho</option><option>Illinois</option><option>Indiana</option><option>Iowa</option><option>Kansas</option><option>Kentucky</option><option>Louisiana</option><option>Maine</option><option>Maryland</option><option>Massachusetts</option><option>Michigan</option><option>Minnesota</option><option>Mississippi</option><option>Missouri</option><option>Montana</option><option>Nebraska</option><option>Nevada</option><option>New Hampshire</option><option>New Jersey</option><option>New Mexico</option><option>New York</option><option>North Carolina</option><option>North Dakota</option><option>Ohio</option><option>Oklahoma</option><option>Oregon</option><option>Pennsylvania</option><option>Rhode Island</option><option>South Carolina</option><option>South Dakota</option><option>Tennessee</option><option>Texas</option><option>Utah</option><option>Vermont</option><option>Virginia</option><option>Washington</option><option>West Virginia</option><option>Wisconsin</option><option>Wyoming</option>");
    
    
}
