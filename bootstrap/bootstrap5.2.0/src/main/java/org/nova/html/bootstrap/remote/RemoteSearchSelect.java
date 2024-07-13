package org.nova.html.bootstrap.remote;

import java.util.ArrayList;
import java.util.List;

import org.nova.html.attributes.Size;
import org.nova.html.bootstrap.Alert;
import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.classes.Rounded;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.elements.Composer;
import org.nova.html.elements.FormElement;
import org.nova.html.elements.TagElement;
import org.nova.html.enums.autocomplete;
import org.nova.html.ext.Content;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.remote.Remote;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.tags.em;
import org.nova.html.tags.form_post;
import org.nova.html.tags.script;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.BrotliContentEncoder;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.RemoteStateBinding;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.services.RequiredRoles;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

import xp.nova.sqldb.graph.Query;
import xp.nova.sqldb.graph.QueryResult;

@RequiredRoles()
public abstract class RemoteSearchSelect<STATE extends RemoteStateBinding> extends RemoteStateItem
{
    public static final String PATH = "/$/RemoteSearchSelect";

    final private Item options;
    final private String instanceName;
    final private InputText inputText;
    public RemoteSearchSelect(RemoteStateBinding binding,Size size,Icon searchIcon,String searchMessage) throws Throwable
    {
        super(binding);
        style("z-index:1000;");
        
        Item group=returnAddInner(new Item()).input_group().position(Position.relative);
        Span messageSpan=group.returnAddInner(new Span()).input_group_text();
        if (searchMessage!=null)
        {
            Item messageItem=messageSpan.returnAddInner(new Item()).addInner(searchMessage);
            if (searchIcon!=null)
            {
                messageItem.pe(2);
            }
        }
        if (searchIcon!=null)
        {
            messageSpan.addInner(searchIcon);
        }
        this.inputText=group.returnAddInner(new InputText());
        this.inputText.form_control().autocomplete(autocomplete.off).autofocus();

        this.options=group.returnAddInner(new Item()).id(this.inputText.id()+"-options").border(Edge.bottom).border(Edge.start).border(Edge.end);
        if (size!=null)
        {
            this.inputText.style("width:"+size.toString());
            this.options.style("top:2.25em;right:0px;width:"+size.toString());
        }
        else if ((searchIcon!=null)&&(searchMessage==null))
        {
            this.options.style("top:2.25em;right:0px;left:2.5em;");
        }
        else
        {
            this.options.style("top:2.25em;right:0px;left:0;");
        }
        this.options.position(Position.absolute).bg(StyleColor.light);

        this.instanceName=RemoteSearchSelect.class.getSimpleName()+id();
        this.inputText.onkeyup(instanceName+".processKeyup(event);");
        this.inputText.onkeydown(instanceName+".processKeydown(event);");
        this.inputText.onblur(instanceName+".processBlur(event);");
        this.inputText.onfocus(instanceName+".post();");
        returnAddInner(new script()).addInner(js_new());
    }
    
    public String js_new()
    {
        return HtmlUtils.js_new(instanceName, "nova.ui.remote."+RemoteSearchSelect.class.getSimpleName(), this.getRemoteStateBinding().getStateKey()+"="+id() ,PATH,this.inputText.id());
    }
    static public class OptionResult
    {
        final String tooMany;
        final List<String> options;
        public OptionResult(String tooMany)
        {
            this.tooMany=tooMany;
            this.options=new ArrayList<>();
        }
        public void add(String option)
        {
            this.options.add(option);
        }
    }
    
    
    public abstract OptionResult getOptions(Trace parent,STATE state,String search) throws Throwable;
    public abstract RemoteResponse onSelect(Trace parent,STATE state,int index) throws Throwable;
    
    @POST
    @Path(PATH+"/options")
    public RemoteResponse options(Trace parent,
            @StateParam STATE state,
        @QueryParam("search") String search
        ) throws Throwable
    {
        Item content=new Item();
        OptionResult options=getOptions(parent,state,search);
        if (options!=null)
        {
          for (int i=0;i<options.options.size();i++)
          {
              String option=options.options.get(i);
//              Item item=content.returnAddInner(new Item()).px(3).py(1).id(this.inputText.id()+"-option-"+i).addClass("active-input").onclick(Remote.js_postStatic(new SecurePathAndQuery(this,PATH+"/select").addQuery("index",i).toString()));
              Item item=content.returnAddInner(new Item()).px(3).py(1).id(this.inputText.id()+"-option-"+i).addClass("active-input").onclick(Remote.js_postStatic(new PathAndQuery(PATH+"/select").addQuery(this.getRemoteStateBinding().getStateKey(), id()).addQuery("index",i).toString()));
              item.returnAddInner(new Item()).addInner(option);
          }
          if (options.tooMany!=null)
          {
              Item item=content.returnAddInner(new Item()).px(2).pt(1);
              item.returnAddInner(new Alert()).addInner(options.tooMany).color(StyleColor.warning);
          }
        }
        RemoteResponse response=new RemoteResponse();
        response.innerHtml(this.options.id(), content);
        return response;
    }
    
    @POST
    @Path(PATH+"/select")
    public RemoteResponse memberSelect(Trace parent,
            @StateParam STATE state,
        @QueryParam("index") int index 
        ) throws Throwable
    {
        return onSelect(parent, state,index);
    }
    
    public InputText input()
    {
        return this.inputText;
    }
}
