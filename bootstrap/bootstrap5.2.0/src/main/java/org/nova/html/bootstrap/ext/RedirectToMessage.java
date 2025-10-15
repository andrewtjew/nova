package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.ext.Redirect;
import org.nova.http.client.PathAndQuery;

@Deprecated
public class RedirectToMessage extends Redirect
{
    static public String DEFAULT_MESSAGE_URL="/message";
    static public String DEFAULT_BUTTON_LABEL="OK";
    
    public RedirectToMessage(String messageUrl,String buttonLabel,String title,String message,String url,StyleColor color) throws Throwable
    {
        super(new PathAndQuery(messageUrl).addQuery("buttonLabel",buttonLabel).addQuery("title",title).addQuery("message",message).addQuery("url",url).addQuery("styleColor",color).toString());
    }
    public RedirectToMessage(String title,String message,String url,StyleColor color) throws Throwable
    {
        this(DEFAULT_MESSAGE_URL,DEFAULT_BUTTON_LABEL,title,message,url,color);
    }
    public RedirectToMessage(String title,String message,String url) throws Throwable
    {
        this(title,message,url,StyleColor.info);
    }
    
}
