package org.nova.html.remote;

import java.util.ArrayList;
import java.util.List;
import org.nova.html.elements.Element;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.NodeElement;
import org.nova.html.elements.InputElement;
import org.nova.html.elements.QuotationMark;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.json.ObjectMapper;

public class Inputs
{
    private String data;
    final private QuotationMark mark;
    final private ArrayList<Input> inputs;
    final private ArrayList<Element> elements;
    final private FormElement<?> form;
    private boolean trace=false;

    public Inputs(FormElement<?> formElement,QuotationMark mark) throws Throwable
    {
        this.elements=new ArrayList<Element>();
        this.mark=mark;
        this.inputs=new ArrayList<Input>();
        this.form=formElement;
        this.addElements(formElement);
    }
    public Inputs(QuotationMark mark) throws Throwable
    {
        this(null,mark);
    }
    public Inputs(FormElement<?> element) throws Throwable
    {
        this(element,QuotationMark.SINGLE);
    }
    public Inputs() throws Throwable
    {
        this(null,QuotationMark.SINGLE);
    }
//    public Inputs add(String name,Object value) throws Throwable
//    {
//        this.inputs.add(new Input(name,value));
//        return this;
//    }
    public Inputs add(Element element)
    {
        this.elements.add(element);
        return this;
    }
    public Element returnAdd(Element element)
    {
        this.elements.add(element);
        return element;
    }
    
    private void addElements() throws Throwable
    {
        for (Element element:this.elements)
        {
            addElements(element);
        }
    }
    
    private void addElements(Element element) throws Throwable
    {
        if (element==null)
        {
            return;
        }
        if (element instanceof InputElement<?>)
        {
            this.inputs.add(new Input((InputElement<?>)element));
            return;
        }
        if (element instanceof RemoteElement)
        {
            Element inner=((RemoteElement)element).render();
            addElements(inner);
            return;
        }
        if (element instanceof NodeElement<?>)
        {
            NodeElement<?> innerElement=(NodeElement<?>)element;
            List<Element> inners=innerElement.getInners();
            if (inners!=null)
            {
                for (Element inner:inners)
                {
                    addElements(inner);
                }
            }
        }
        return;
    }
    
    public Inputs trace()
    {
        this.trace=true;
        return this;
    }
    
    public String getContent() throws Throwable
    {
        if (this.data==null)
        {
            addElements();
            this.data=ObjectMapper.writeObjectToString(this.inputs.toArray(new Input[this.inputs.size()]));
        }
        return this.data;
    }
    
    public String js_call() throws Throwable
    {
        if (form.method()==method.get)
        {
            return js_get(this.form.id(),this.form.action());
        }
        else
        {
            return js_post(this.form.id(),this.form.action());
        }
    }
    public String js_post(String formID,String action) throws Throwable
    {
        String content=getContent();
        return HtmlUtils.js_call(this.mark,"nova.remote.post",formID,action,content,this.trace);
    }
    
    public void buildFormSubmit() throws Throwable
    {
        if (this.form.method()==method.get)
        {
            this.form.onsubmit(js_get()+";return false;");
        }
        else if (this.form.method()==method.post)
        {
            this.form.onsubmit(js_post()+";return false;");
        }
    }

//    public String escapeString(String string)
//    {
//        if (this.innerMark==null)
//        {
//            return string;
//        }
//        String escape=this.innerMark.toString();
//        StringBuilder sb=new StringBuilder();
//        boolean inString=false;
//        for (int i=0;i<string.length();i++)
//        {
//            char c=string.charAt(i);
//            switch (c)
//            {
//                case '"':
//                    sb.append(escape);
//                    inString=!inString;
//                    break;
//
//                case '\\':
//                    sb.append(c);
//                    int next=i+1;
//                    if (next<string.length())
//                    {
//                        sb.append(string.charAt(next));
//                    }
//                    break;
//                    
//                default:
//                    sb.append(c);
//            }
//            
//        }
//        return sb.toString();
//    }
//    
//    private String js_call(QuotationMark mark,String function,Object...parameters)
//    {
//        StringBuilder sb=new StringBuilder(function+"(");
//        boolean commaNeeded=false;
//        for (Object parameter:parameters)
//        {
//            if (commaNeeded==false)
//            {
//                commaNeeded=true;
//            }
//            else
//            {
//                sb.append(',');
//            }
//            if (parameter==null)
//            {
//                sb.append("null");
//            }
//            else 
//            {
//                Class<?> type=parameter.getClass();
//                boolean isArray=type.isArray();
//                if (isArray)
//                {
//                    type=type.getComponentType();
//                }
//                if (type==String.class)
//                {
//                    if (isArray)
//                    {
//                        sb.append('[');
//                        for (int i=0;i<Array.getLength(parameter);i++)
//                        {
//                            if (i>0)
//                            {
//                                sb.append(',');
//                            }
//                            sb.append(mark.toString()+escapeString(Array.get(parameter, i).toString())+mark.toString());
//                        }
//                        sb.append(']');
//                    }
//                    else
//                    {
//                        sb.append(mark.toString()+escapeString(parameter.toString())+mark.toString());
//                    }
//                }
//                else if ((type==byte.class)
//                        ||(type==short.class)
//                        ||(type==int.class)
//                        ||(type==long.class)
//                        ||(type==float.class)
//                        ||(type==double.class)
//                        ||(type==boolean.class)
//                        ||(type==BigDecimal.class)
//                        ||(type==Byte.class)
//                        ||(type==Short.class)
//                        ||(type==Integer.class)
//                        ||(type==Long.class)
//                        ||(type==Float.class)
//                        ||(type==Double.class)
//                        ||(type==Boolean.class)
//                        )
//                {
//                    if (isArray)
//                    {
//                        sb.append('[');
//                        for (int i=0;i<Array.getLength(parameter);i++)
//                        {
//                            if (i>0)
//                            {
//                                sb.append(',');
//                            }
//                            sb.append(Array.get(parameter, i));
//                        }
//                        sb.append(']');
//                    }
//                    else
//                    {
//                        sb.append(parameter);
//                    }
//                }
//                else
//                {
//                    if (isArray)
//                    {
//                        sb.append('[');
//                        for (int i=0;i<Array.getLength(parameter);i++)
//                        {
//                            if (i>0)
//                            {
//                                sb.append(',');
//                            }
//                            sb.append(mark.toString()+Array.get(parameter, i).toString()+mark.toString());
//                        }
//                        sb.append(']');
//                    }
//                    else
//                    {
//                        sb.append(mark.toString()+parameter.toString()+mark.toString());
//                    }
//                }
//            }
//        }
//        sb.append(");");
//        return sb.toString();
//    }
    
    public String js_get(String formID,String action) throws Throwable
    {
        if (this.data==null)
        {
            this.data=ObjectMapper.writeObjectToString(this.inputs.toArray(new Input[this.inputs.size()]));
        }
        return HtmlUtils.js_call(this.mark,"nova.remote.get",formID,action,getContent(),trace);
    }
    public String js_post(String action) throws Throwable
    {
        String formID=this.form!=null?this.form.id():null;
        return js_post(formID,action);
    }

    public String js_action() throws Throwable
    {
        if (form.method()==method.get)
        {
            return js_get(this.form.action());
        }
        else
        {
            return js_post(this.form.action());
        }
    }
    public String js_get(String action) throws Throwable
    {
        String formID=this.form!=null?this.form.id():null;
        return js_get(formID,action);
    }
    public String js_post() throws Throwable
    {
        return js_post(this.form.id(),this.form.action());
    }
    public String js_get() throws Throwable
    {
        return js_get(this.form.id(),this.form.action());
    }

    
}
