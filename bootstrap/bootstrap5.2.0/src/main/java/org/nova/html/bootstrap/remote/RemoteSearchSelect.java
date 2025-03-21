package org.nova.html.bootstrap.remote;

import java.util.ArrayList;
import java.util.List;

import org.nova.html.bootstrap.Alert;
import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Position;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.enums.autocomplete;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.remote.Remote;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.remote.RemoteStateBinding;
import org.nova.html.tags.script;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.services.RequiredRoles;
import org.nova.tracing.Trace;


@RequiredRoles()
public abstract class RemoteSearchSelect<STATE extends RemoteStateBinding> extends RemoteStateItem
{
    public static final String PATH = "/$/RemoteSearchSelect";

    final private Item options;
    final private String instanceName;
    final private InputText inputText;
    public RemoteSearchSelect(RemoteStateBinding binding,Icon searchIcon,String searchMessage) throws Throwable
    {
        super(binding);
        style("z-index:1;");
        
        Item group=returnAddInner(new Item()).input_group().position(Position.relative);
        Span messageSpan=group.returnAddInner(new Span()).input_group_text();
        if (searchIcon!=null)
        {
            messageSpan.addInner(searchIcon);
        }
        this.inputText=group.returnAddInner(new InputText()).placeholder(searchMessage);
        this.inputText.form_control().autocomplete(autocomplete.off).autofocus();

        this.options=group.returnAddInner(new Item()).id(this.inputText.id()+"-options").border(Edge.bottom).border(Edge.start).border(Edge.end);
        this.options.style("top:2.25em;right:0px;left:0;");
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
    public RemoteResponse select(Trace parent,
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
