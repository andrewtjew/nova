package org.nova.html.remoting;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;

public class DocumentInputs
{
    final StringBuilder sb;
    final QuotationMark mark;
    final private HashMap<String,ArrayList<String>> radios;

    public DocumentInputs(QuotationMark mark)
    {
        this.sb=new StringBuilder();
        this.mark=mark;
        this.radios=new HashMap<>();
    }
    public DocumentInputs()
    {
        this(QuotationMark.SINGLE);
    }
    
    private void addName(String name)
    {
        if (this.sb.length()>0)
        {
            sb.append("+"+this.mark+"&");
        }
        sb.append(name);
        sb.append("="+this.mark+"+");
    }
    
    
    public DocumentInputs add(String name,Object value) throws UnsupportedEncodingException
    {
        if (value==null)
        {
            return this;
        }
        if (this.sb.length()>0)
        {
            sb.append("+"+this.mark+"&");
        }
        sb.append(name);
        sb.append("=");
        this.sb.append(URLEncoder.encode(value.toString(), "UTF-8"));
        this.sb.append(this.mark);
        
        return this;
    }
    
    public InputElement<?> returnAdd(InputElement<?> inputElement)
    {
        String name=inputElement.name();
        if (name!=null)
        {
            switch (inputElement.getInputType())
            {
                case button:
                case color:
                case date:
                case datetime_local:
                case month:
                case number:
                case password:
                case search:
                case email:
                case tel:
                case text:
                case textarea:
                case time:
                case url:
                case hidden:
                case submit:
                case range:
                case week:
                case file:
                {
                    addName(name);
                    String document="encodeURIComponent(document.getElementsByName("+this.mark+name+this.mark+")[0].value)";
                    this.sb.append(document);
                }
                    break;
                
                case checkbox:
                {
                    String document="document.getElementsByName("+this.mark+name+this.mark+")[0].checked";
                    this.sb.append(document);
                }
                    break;

                case select:
                {
                    String document="document.getElementsByName("+this.mark+name+this.mark+")[0]";
                    addName(name);
                    this.sb.append(document);
                    this.sb.append(".options["+document+".selectedIndex].value");
                }
                    break;

                case radio:
                    break;

                case image:
                    break;
                case reset:
                    break;
                default:
                    break;
            }
        }
        return inputElement;
    }

    public String js_post(String path)
    {
        if (this.sb.length()>0)
        {
            return "org.nova.html.remoting.post("+this.mark+path+"?"+this.sb.toString()+")";
        }
        else
        {
            return "org.nova.html.remoting.post("+this.mark+path+this.mark+")";
        }
    }
    public String js_get(String path)
    {
        if (this.sb.length()>0)
        {
            return "org.nova.html.remoting.get("+this.mark+path+"?"+this.sb.toString()+")";
        }
        else
        {
            return "org.nova.html.remoting.get("+this.mark+path+this.mark+")";
        }
    }
    
}
