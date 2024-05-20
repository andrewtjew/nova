package org.nova.html.bootstrap.remote;

import java.util.ArrayList;
import java.util.List;

import org.nova.html.bootstrap.Alert;
import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.Label;
import org.nova.html.bootstrap.Span;
import org.nova.html.bootstrap.classes.Display;
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


@Path(RemoteSearchSelect.PATH)
@RequiredRoles()
public abstract class RemoteSearchSelect<STATE extends RemoteStateBinding> extends InputText
{
    public static final String PATH = "/$/RemoteSearchSelect";

    final private Item options;
    final private String instanceName;
    final private RemoteStateBinding binding;
    final private Icon searchIcon;
    public RemoteSearchSelect(RemoteStateBinding binding,Icon searchIcon) throws Throwable
    {
        this.searchIcon=searchIcon;
        this.binding=binding;
        binding.setState(this);
        form_control().autocomplete(autocomplete.off).autofocus().rounded(0);
        this.options=new Item().id(id()+"-options").border();

        this.instanceName=RemoteSearchSelect.class.getSimpleName()+id();
        onkeyup(instanceName+".processKeyup(event);");
        onkeydown(instanceName+".processKeydown(event);");
        onblur(instanceName+".processBlur(event);");
        onfocus(instanceName+".post();");
        returnAddInner(new script()).addInner(HtmlUtils.js_new(instanceName, "nova.ui.remote."+RemoteSearchSelect.class.getSimpleName(), binding.getKey()+"="+id() ,PATH,id()));
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
    @Path("/options")
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
              Item item=content.returnAddInner(new Item()).px(3).py(1).id(id()+"-option-"+i).addClass("active-input").onclick(Remote.js_postStatic(new PathAndQuery(PATH+"/select").addQuery(this.binding.getKey(), id()).addQuery("index",i).toString()));
              item.returnAddInner(new Item()).w(100).addInner(option);
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
    @Path("/select")
    public RemoteResponse memberSelect(Trace parent,
            @StateParam STATE state,
        @QueryParam("index") int index 
        ) throws Throwable
    {
        return onSelect(parent, state,index);
    }
    

    boolean outer=false;
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (outer==false)
        {
            outer=true;
            Item item=new Item().d(Display.flex).w(100).style("z-index:1000;");
            item.returnAddInner(new Item()).pt(2).returnAddInner(new Span()).addInner(this.searchIcon).rounded(0).bg(StyleColor.light).border().px(3).py(2);
            Item group=item.returnAddInner(new Item()).flex(Flex.grow,1).bg(StyleColor.warning).position(Position.relative);
            Item inner=group.returnAddInner(new Item()).bg(StyleColor.light).w(100).position(Position.absolute);
            inner.returnAddInner(this);
            inner.returnAddInner(this.options);
            item.compose(composer);
        }
        else
        {
            outer=false;
            super.compose(composer);
        }
    }
    
}
