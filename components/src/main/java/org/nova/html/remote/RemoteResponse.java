package org.nova.html.remote;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.text.StringEscapeUtils;
import org.nova.html.elements.Element;
import org.nova.html.elements.QuotationMark;
import org.nova.html.elements.TagElement;
import org.nova.html.ext.HtmlUtils;
import org.nova.http.client.PathAndQuery;
import org.nova.json.ObjectMapper;
import org.nova.localization.LocalTextResolver;

public class RemoteResponse
{
    private final ArrayList<Instruction> instructions;
    boolean trace;
    final LocalTextResolver resolver;
    
    public RemoteResponse(LocalTextResolver resolver)
    {
        this.resolver=resolver;
        this.instructions=new ArrayList<Instruction>();
        this.trace=false;
    }
    public RemoteResponse()
    {
        this(null);
    }
    
    
    
    public RemoteResponse trace(boolean trace)
    {
        this.trace=trace;
        return this;
    }
    public RemoteResponse trace()
    {
        return trace(true);
    }
    public RemoteResponse value(String id,Object value)
    {
        if (value==null)
        {
            return this;
        }
        this.instructions.add(new Instruction(this.trace,Command.value,id,value.toString()));
        return this;
    }
    public RemoteResponse checked(String id,boolean value)
    {
        this.instructions.add(new Instruction(this.trace,Command.checked,id,Boolean.toString(value)));
        return this;
    }
    
    public Instruction[] getInstructions()
    {
    	return this.instructions.toArray(new Instruction[this.instructions.size()]);
    }
    
    public RemoteResponse documentObject(String name,Object documentObject) throws Throwable
    {
        String text=ObjectMapper.writeObjectToString(documentObject);
        this.instructions.add(new Instruction(this.trace,Command.documentObject,name,text));
        return this;
    }
    public RemoteResponse innerHtml(String id,Element element,QuotationMark mark)
    {
        String text=element.getHtml(mark,this.resolver);
        this.instructions.add(new Instruction(this.trace,Command.innerHTML,id,text));
        return this;
    }
    public RemoteResponse add(RemoteResponse response)
    {
        for (Instruction instruction:response.instructions)
        {
            this.instructions.add(instruction);
        }
        return this;
    }
    public RemoteResponse innerHtml(String id,Element element)
    {
        if (element==null)
        {
            this.instructions.add(new Instruction(this.trace,Command.innerHTML,id,null));
        }
        else
        {
            String text=element.getHtml(this.resolver);
            this.instructions.add(new Instruction(this.trace,Command.innerHTML,id,text));
        }
        return this;
    }
    public RemoteResponse outerHtml(String id,Element element,QuotationMark mark)
    {
        String text=element.getHtml(mark,this.resolver);
        this.instructions.add(new Instruction(this.trace,Command.outerHTML,id,text));
        return this;
    }
    public RemoteResponse outerHtml(String id,Element element)
    {
        String text=element.getHtml(this.resolver);
        this.instructions.add(new Instruction(this.trace,Command.outerHTML,id,text));
        return this;
    }
    public RemoteResponse outerHtml(TagElement<?> element)
    {
        String text=element.getHtml(this.resolver);
        this.instructions.add(new Instruction(this.trace,Command.outerHTML,element.id(),text));
        return this;
    }
    
    public RemoteResponse innerText(String id,Object text)
    {
        this.instructions.add(new Instruction(this.trace,Command.innerText,id,StringEscapeUtils.escapeHtml4(text.toString())));
        return this;
    }
    public RemoteResponse innerText(String id,Element element)
    {
        String text=element.getHtml(this.resolver);
        this.instructions.add(new Instruction(this.trace,Command.innerText,id,text));
        return this;
    }
    public RemoteResponse script(String script)
    {
        if (script!=null)
        {
            this.instructions.add(new Instruction(this.trace,Command.script,script));
        }
        return this;
    }
//    public RemoteResponse alert(Object value)
//    {
//        this.instructions.add(new Instruction(this.trace,Command.alert,value==null?null:value.toString()));
//        return this;
//    }
    public RemoteResponse log(Object value)
    {
        this.instructions.add(new Instruction(this.trace,Command.log,value==null?null:value.toString()));
        return this;
    }

    public RemoteResponse location(PathAndQuery pathAndQuery) throws UnsupportedEncodingException
    {
        return location(pathAndQuery.toString());
    }
    public RemoteResponse location(String pathAndQuery) throws UnsupportedEncodingException
    {
        String code=HtmlUtils.js_location(pathAndQuery);
        return script(code);
    }
    public RemoteResponse reload() throws UnsupportedEncodingException
    {
        return script("window.location.reload()");
    }
    
    public RemoteResponse clear()
    {
        this.instructions.clear();
        return this;
    }
//    public RemoteResponse prop(String id,String prop,Object value) throws UnsupportedEncodingException
//    {
//        String code="$('#"+id+"').prop('"+prop+"',"+value+");";
//        return script(code);
//    }
    @Deprecated
    public RemoteResponse showModal(String id)
    {
        QuotationMark mark=QuotationMark.SINGLE;
        String code="$("+mark+"#"+id+mark+").modal("+mark+"show"+mark+");";
        return script(code);
    }
    @Deprecated
    public RemoteResponse hideModal(String id)
    {
        QuotationMark mark=QuotationMark.SINGLE;
        String code="$("+mark+"#"+id+mark+").modal("+mark+"hide"+mark+");";
        return script(code);
    }

    //Allows instruction results to be reorder.
    public int reserve()
    {
        this.instructions.add(null);
        return this.instructions.size()-1;
    }
    
    //Allows instruction results to be reorder.
    public void exchangeReservedWithLast(int reserved)
    {
        this.instructions.set(reserved, this.instructions.get(this.instructions.size()-1));
        this.instructions.remove(this.instructions.size()-1);
    }
    
    public void exchange(int indexA,int indexB)
    {
        Instruction t=this.instructions.get(indexA);
        this.instructions.set(indexA, this.instructions.get(indexB));
        this.instructions.set(indexB, t);
    }
    
}
