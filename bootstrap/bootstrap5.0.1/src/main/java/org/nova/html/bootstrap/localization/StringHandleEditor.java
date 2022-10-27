package org.nova.html.bootstrap.localization;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.nova.html.DataTables.Column;
import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.attributes.Size;
import org.nova.html.attributes.unit;
import org.nova.html.bootstrap.Alert;
import org.nova.html.bootstrap.Button;
import org.nova.html.bootstrap.DropdownButton;
import org.nova.html.bootstrap.DropdownItem;
import org.nova.html.bootstrap.ButtonGroup;
import org.nova.html.bootstrap.CardDocument;
import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.CustomSwitch;
import org.nova.html.bootstrap.DropdownMenuItem;
import org.nova.html.bootstrap.Form;
import org.nova.html.bootstrap.FormCheck;
import org.nova.html.bootstrap.InputCheckbox;
import org.nova.html.bootstrap.InputRadio;
import org.nova.html.bootstrap.InputText;
import org.nova.html.bootstrap.Item;
import org.nova.html.bootstrap.LinkButton;
import org.nova.html.bootstrap.Select;
import org.nova.html.bootstrap.Styler;
import org.nova.html.bootstrap.SubmitButton;
import org.nova.html.bootstrap.Table;
import org.nova.html.bootstrap.TableBody;
import org.nova.html.bootstrap.TableHeader;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Flex;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.DropdownButtonMenuGroup;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.bootstrap.ext.DataTables.DataTable;
import org.nova.html.bootstrap.remote.ProceedModalDialog;
import org.nova.html.bootstrap.remote.Switch;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.TableRow;
import org.nova.html.remote.Inputs;
import org.nova.html.remote.RemoteResponse;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.tags.col;
import org.nova.html.tags.div;
import org.nova.html.tags.form_post;
import org.nova.html.tags.i;
import org.nova.html.tags.option;
import org.nova.html.tags.textarea;
import org.nova.html.templating.ReplaceMarker;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
import org.nova.http.client.PathAndQuery;
import org.nova.http.server.Context;
import org.nova.http.server.DeflaterContentDecoder;
import org.nova.http.server.DeflaterContentEncoder;
import org.nova.http.server.JSONContentReader;
import org.nova.http.server.JSONContentWriter;
import org.nova.http.server.JSONPatchContentReader;
import org.nova.http.server.annotations.ContentDecoders;
import org.nova.http.server.annotations.ContentEncoders;
import org.nova.http.server.annotations.ContentReaders;
import org.nova.http.server.annotations.ContentWriters;
import org.nova.http.server.annotations.CookieParam;
import org.nova.http.server.annotations.CookieStateParam;
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.PUT;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
import org.nova.localization.LanguageCode;
import org.nova.localization.Language_ISO_639_1;
import org.nova.localization.SqlStringHandleResolver;
import org.nova.localization.StringHandle;
import org.nova.services.AllowGroups;
import org.nova.services.SessionFilter;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.SqlUtils;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.nova.utils.TypeUtils;


@ContentDecoders({GzipContentDecoder.class,DeflaterContentDecoder.class})
@ContentEncoders({GzipContentEncoder.class,DeflaterContentEncoder.class})
@ContentReaders({JSONContentReader.class, JSONPatchContentReader.class})
@ContentWriters({JSONContentWriter.class, HtmlElementWriter.class, RemoteResponseWriter.class})
@Path("/StringHandleEditor/#")
public class StringHandleEditor
{
    final private Connector connector;
    public StringHandleEditor(TraceManager traceManager,Connector connector)
    {
        this.connector=connector;
//        SqlStringHandleResolver resolver=new SqlStringHandleResolver(traceManager, connector);
//        StringHandle.setResolver(resolver);
    }
    
    
    public void addLanguage(Trace parent,LanguageCode languageCode) throws Exception, Throwable
    {
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            addLanguage(parent, accessor, languageCode);
        }        
    }
    
    
    private void addLanguage(Trace parent,Accessor accessor,LanguageCode languageCode) throws Throwable
    {
        Row row=SqlUtils.executeQueryOne(parent, null, accessor
                , "SELECT * FROM HandleLanguages WHERE Language=?"
                ,languageCode.name());
        if (row==null)
        {
            long id=Insert.table("HandleLanguages").value("Language", languageCode.name()).value("Description",languageCode.getValue().description).value("Active", true)
            .executeAndReturnLongKey(parent, accessor);
            
            RowSet enumSet=accessor.executeQuery(parent, null
                    , "SELECT * FROM HandleEnums"
                    );
            for (Row enumRow:enumSet.rows())
            {
                String enum_=enumRow.getVARCHAR("Enum");
                long enumID=enumRow.getBIGINT("ID");
                if (enum_!=null)
                {
                    Class<?> type=Class.forName(enum_);
                    for (Object value:type.getEnumConstants())
                    {
                        Enum<?> e=(Enum<?>)value;
                        String name=e.name();
                        Row existing=SqlUtils.executeQueryOne(parent, null, accessor
                                , "SELECT * FROM HandleFormats WHERE LanguageID=? AND EnumID=? AND Handle=?"
                                ,id,enumID,name);
                        if (existing==null)
                        {
                            Insert.table("HandleFormats").value("Handle",name).value("LanguageID", id).value("EnumID", enumID).execute(parent, accessor);
                        }
                    }
                }
                else
                {
                    RowSet stringHandleSet=accessor.executeQuery(parent, null
                            , "SELECT * FROM HandleFormats JOIN HandleEnums ON HandleEnums.ID=EnumID WHERE Enum IS NULL"
                            );
                    for (Row stringHandleRow:stringHandleSet.rows())
                    {
                        String name=stringHandleRow.getVARCHAR("Handle");
                        Row existing=SqlUtils.executeQueryOne(parent, null, accessor
                                , "SELECT * FROM HandleFormats WHERE LanguageID=? AND EnumID=? AND Handle=?"
                                ,id,enumID,name);
                        if (existing==null)
                        {
                            Insert.table("HandleFormats").value("Handle",name).value("LanguageID", id).value("EnumID", enumID).execute(parent, accessor);
                        }
                    }
                    
                }
            }
        }
    }
    
    
    public void addDefaultLocales(Trace parent) throws Throwable, Throwable
    {
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            addLanguage(parent, accessor, LanguageCode.en);
            addLanguage(parent, accessor, LanguageCode.fr);
            addLanguage(parent, accessor, LanguageCode.my);
            addLanguage(parent, accessor, LanguageCode.zh);
        }
    }
    CardDocument createInputCard()
    {
        CardDocument card=new CardDocument();
        card.mt(2).w(50);
        card.footer().d(Display.flex).justify_content(Justify.center).mx(3).mb(2);
        return card;
    }
    @GET
    public Element main() throws Throwable
    {
        Page page=new Page();
        ReplaceMarker el=page.content().returnAddInner(new ReplaceMarker("test"));
        el.addInner("Hello");
        return page;
    }
    
    @GET
    public Element handleEnum() throws Throwable
    {
        Page page=new Page();
        
        Form form=page.content().returnAddInner(new Form(method.post));
        form.action("#");
        Item item=form.returnAddInner(new Item()).d(Display.flex).justify_content(Justify.center);
        CardDocument card=createInputCard();
        card.header().addInner("Enter handle name");
        card.body().returnAddInner(new InputText().w(100).name("name"));
        card.footer().returnAddInner(new SubmitButton("Add")).color(StyleColor.primary).w(100);

        item.addInner(card);
        return page;
    }

    @GET
    public Element addHandle() throws Throwable
    {
        Page page=new Page();
        
        Form form=page.content().returnAddInner(new Form(method.post));
        form.action("#");
        Item item=form.returnAddInner(new Item()).d(Display.flex).justify_content(Justify.center);
        CardDocument card=createInputCard();
        card.header().addInner("Enter handle name");
        card.body().returnAddInner(new InputText().w(100).name("name"));
        card.footer().returnAddInner(new SubmitButton("Add")).color(StyleColor.primary).w(100);

        item.addInner(card);
        return page;
    }
    @POST
    public Element addHandle(Trace parent,@QueryParam("name") String name) throws Throwable
    {
        Page page=new Page();
        Item item=page.content().returnAddInner(new Item()).m(2);
        
        name=name.trim();
        if (name.length()==0)
        {
            item.returnAddInner(new Alert()).addInner("Blank name not allowed").color(StyleColor.warning);
            return page;
        }
        if (Character.isJavaIdentifierStart(name.charAt(0))==false)
        {
            item.returnAddInner(new Alert()).addInner("Invalid name: "+name).color(StyleColor.warning);
            return page;
        }
        for (int i=1;i<name.length();i++)
        {
            if (Character.isJavaIdentifierPart(name.charAt(i))==false)
            {
                item.returnAddInner(new Alert()).addInner("Invalid name: "+name).color(StyleColor.warning);
                return page;
            }
        }

        item.returnAddInner(new Alert()).addInner("Added: "+name).color(StyleColor.success);
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            long enumID;
            {
                Row row=SqlUtils.executeQueryOne(parent, null, accessor
                        , "SELECT ID FROM HandleEnums WHERE Enum IS NULL"
                        );
                if (row==null)
                {
                    enumID=Insert.table("HandleEnums").value("Enum", null).executeAndReturnLongKey(parent, accessor);
                }
                else
                {
                    enumID=row.getBIGINT("ID");
                }
            }
            RowSet rowSet=accessor.executeQuery(parent, null
                    , "SELECT * FROM HandleLanguages");
                    
            for (Row languageRow:rowSet.rows())
            {
                long languageID=languageRow.getBIGINT("ID");
                
                Row row=SqlUtils.executeQueryOne(parent, null, accessor
                        , "SELECT * FROM HandleFormats WHERE LanguageID=? AND EnumID=? AND Handle=?"
                        ,languageID,enumID,name);
                
                if (row==null)
                {
                    Insert.table("HandleFormats").value("Handle",name).value("LanguageID", languageID).value("EnumID", enumID).execute(parent, accessor);
                }
            }
        }
        return page;
    }

    @GET
    public Element addLanguage() throws Throwable
    {
        Page page=new Page();
        
        Form form=page.content().returnAddInner(new Form(method.post));
        form.action("#");
        Item item=form.returnAddInner(new Item()).d(Display.flex).justify_content(Justify.center);
        CardDocument card=createInputCard();
        card.header().addInner("Select language");
        Select select=card.body().returnAddInner(new Select().w(100).name("name"));
        for (LanguageCode languageCode:LanguageCode.values())
        {
            Language_ISO_639_1 locale=languageCode.getValue();
            String text=locale.description+" ("+locale.code+")";
            select.returnAddInner(new option()).addInner(text).value(locale.code);
        }
        card.footer().returnAddInner(new SubmitButton("Add")).color(StyleColor.primary).w(100);

        item.addInner(card);
        return page;
    }
    @POST
    public Element addLanguage(Trace parent,@QueryParam("name") String name) throws Throwable
    {
        Page page=new Page();
        Item item=page.content().returnAddInner(new Item()).m(2);
        LanguageCode languageCode=LanguageCode.fromCodeISO_639_1(name);
        Language_ISO_639_1 locale=languageCode.getValue();
        String text=locale.description+" ("+locale.code+")";
        item.returnAddInner(new Alert()).addInner("Added: "+text).color(StyleColor.success);
        addLanguage(parent, languageCode);
        return page;
    }

    @GET
    public Element addEnum() throws Throwable
    {
        Page page=new Page();
        
        Form form=page.content().returnAddInner(new Form(method.post));
        form.action("#");
        Item item=form.returnAddInner(new Item()).d(Display.flex).justify_content(Justify.center);
        CardDocument card=createInputCard();
        card.header().addInner("Enter fully qualified enum name");
        card.body().returnAddInner(new InputText().w(100).name("name"));
        card.footer().returnAddInner(new SubmitButton("Add")).color(StyleColor.primary).w(100);
        
        item.addInner(card);
        return page;
    }

    @POST
    public Element addEnum(Trace parent,@QueryParam("name") String name) throws Throwable
    {
        Page page=new Page();
        Item item=page.content().returnAddInner(new Item()).m(2);

        Class<?> type;
        try
        {
            type=Class.forName(name);
            if (type.isEnum()==false)
            {
                throw new Exception();
            }
        }
        catch (Throwable t)
        {
            item.returnAddInner(new Alert()).addInner("Not an enum: "+name).color(StyleColor.warning);
            return page;
        }
        
        item.returnAddInner(new Alert()).addInner("Added: "+name).color(StyleColor.success);
        addEnum(parent,type,item);
        return page;
    }
    
    public void addEnum(Trace parent,Class<?> type,Item item) throws Throwable
    {
    	String name=type.getCanonicalName();
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            long enumID;
            {
                Row row=SqlUtils.executeQueryOne(parent, null, accessor
                        , "SELECT ID FROM HandleEnums WHERE Enum=?"
                        ,name);
                if (row==null)
                {
                    enumID=Insert.table("HandleEnums").value("Enum", name).value("Active", true).executeAndReturnLongKey(parent, accessor);
                }
                else
                {
                    enumID=row.getBIGINT("ID");
                }
            }
            
            HashSet<String> staleHandles=new HashSet<String>();
            {
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT Handle FROM HandleFormats WHERE EnumID=? GROUP BY Handle",enumID);
                for (Row row:rowSet.rows())
                {
                    staleHandles.add(row.getVARCHAR(0));
                }                
            }
            

            {
                TableHeader header=new TableHeader();
                header.addRow("Handles");
                Table table=new Table(header);
                if (item!=null)
                {
                	item.addInner(table);
                }
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleLanguages");
                for (Object value:type.getEnumConstants())
                {
                    Enum<?> e=(Enum<?>)value;
                    String handle=e.name();
                    staleHandles.remove(handle);
                    for (Row languageRow:rowSet.rows())
                    {
                        long languageID=languageRow.getBIGINT("ID");
                        
                        Row row=SqlUtils.executeQueryOne(parent, null, accessor
                                , "SELECT * FROM HandleFormats WHERE LanguageID=? AND EnumID=? AND Handle=?"
                                ,languageID,enumID,handle);
                        if (row==null)
                        {
                            Insert.table("HandleFormats").value("Handle",handle).value("LanguageID", languageID).value("EnumID", enumID).execute(parent, accessor);
                        }
                    }
                    TableRow tr=new TableRow();
                    tr.add(handle);
                    table.addInner(tr);
                }
            }
            
            TableHeader header=new TableHeader();
            header.addRow("Removed: "+staleHandles.size());
            Table table=new Table(header);
            if (item!=null)
            {
            	item.returnAddInner(new Table(header));
            }
            for (String handle:staleHandles)
            {
                accessor.executeUpdate(parent, null
                        , "DELETE FROM HandleFormats WHERE Handle=? AND EnumID=?"
                        ,handle,enumID);
                TableRow tr=new TableRow();
                tr.add(handle);
                table.addInner(tr);
            }
            
        }
    }

    static class UserState
    {
        public Long enumID;
        public Long languageID;
        public String handle;
    }
    
    @GET
    public Element viewByLanguages(Trace parent,@CookieStateParam("X-Nova-UserState") UserState userState,@QueryParam("languageID") Long languageID,@QueryParam("enumID") Long enumID)  throws Throwable
    {
        
        
        Page page=new Page();
        
        Item main=page.content().returnAddInner(new Item()).m(2);
        
        Item bar=main.returnAddInner(new Item()).d(Display.flex).mb(2).border(Edge.bottom).pb(1);
        
//        {
//            DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup("Format Specifiers"));
//            group.me(2);
//            group.button().color(StyleColor.secondary).sm();
//
//            DropdownMenuItem dropDownItem=group.menu().returnAddInner(new DropdownMenuItem());
//            DropdownItem item=dropDownItem.returnAddInner(new DropdownItem());
//            Table table=dropDownItem.returnAddInner(new Table());i
//            TableHeader heading=table.returnAddInner(new TableHeader());
//            heading.addRow("Specifier","Description");
//            TableBody body=table.returnAddInner(new TableBody());
//            body.addRow("%b","test");
//            body.addRow("%b","test");
//            body.addRow("%b","test");
//            body.addRow("%b","test");
//            
//        }
        
        if (languageID==null)
        {
            languageID=userState.languageID;
        }
        if (enumID==null)
        {
            enumID=userState.enumID;
        }
        
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            {
                bar.returnAddInner(new Item()).addInner("Enum:").me(1).mt(1);
                DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup());
                group.button().color(StyleColor.secondary).sm().me(2);
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleEnums WHERE Active=?"
                        , true
                        );
                boolean found=false;
                String groupEnum=null;
                for (Row row:rowSet.rows())
                {
                    long id=row.getBIGINT("ID");
                    String enum_=row.getVARCHAR("Enum");
                    if (enumID==null)
                    {
                        userState.enumID=id;
                        enumID=id;
                    }
                    if (enum_==null)
                    {
                        enum_="{String}";
                    }
                    if (enumID==id)
                    {
                        groupEnum=enum_;
                        found=true;
                    }
                    if (found==false)
                    {
                        groupEnum=enum_;
                    }
    
                    DropdownMenuItem dropDownItem=new DropdownMenuItem(enum_,"?enumID="+id);
                    group.menu().returnAddInner(dropDownItem);
                }
                if (found)
                {
                    userState.enumID=enumID;
                }
                else
                {
                    enumID=userState.enumID;
                }
                group.button().addInner(groupEnum);
                
            }
            //Build language dropdown
            {
                bar.returnAddInner(new Item()).addInner("Language:").me(1).mt(1);
                DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup());
                group.button().color(StyleColor.primary).sm();
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleLanguages WHERE Active=?",true);
                boolean found=false;
                String groupLanguage=null;
                for (Row row:rowSet.rows())
                {
                    long id=row.getBIGINT("ID");
                    String description=row.getVARCHAR("Description");
                    
                    if (languageID==null)
                    {
                        userState.languageID=id;
                        languageID=id;
                    }
                    if (id==languageID)
                    {
                        groupLanguage=description;
                        found=true;
                    }
                    if (found==false)
                    {
                        groupLanguage=description;
                    }
    
                    DropdownMenuItem dropDownItem=new DropdownMenuItem(description,"?languageID="+id);
                    dropDownItem.d(Display.flex).justify_content(Justify.between);
                    
//                    FormCheck formCheck=dropDownItem.returnAddInner(new FormCheck()).switch_().mt(1);
//                    InputCheckbox checkbox=formCheck.addInputCheckbox(null).checked(active);
                    group.menu().returnAddInner(dropDownItem);
                }
                if (found)
                {
                    userState.languageID=languageID;
                }
                else
                {
                    languageID=userState.languageID;
                }
                group.button().addInner(groupLanguage);
            }
            
            
            
            RowSet handleRowSet=accessor.executeQuery(parent, null
                    , "SELECT * FROM HandleFormats WHERE LanguageID=? AND enumID=?",languageID,enumID);
            
            DataTableOptions options=new DataTableOptions();
            options.lengthMenu=new int[]{25,100,1000};
            Column formatColumn=new Column();
            formatColumn.searchable=false;
            options.columns=new Column[] {formatColumn,null};
            
            ColumnDef formatColumnDef=new ColumnDef(0);
            formatColumnDef.width(new Size(25,unit.percent));
            
            options.columnDefs=new ColumnDef[] {formatColumnDef};
            DataTable table=new DataTable(page,options);
//            table.w_auto();
            Styler.style(table).w(100);
            main.addInner(table);
            table.header().addRow("Handle","Format");
            for (Row handleRow:handleRowSet.rows())
            {
                String handle=handleRow.getVARCHAR("Handle");
                String format=handleRow.getVARCHAR("Format");
                String key=handle+"-"+languageID+"-"+enumID;

//                form_post post=table.body().returnAddInner(new form_post()).action(new PathAndQuery("/StringHandleEditor/save").addQuery("handle", handle).toString());
//                TableRow tr=post.returnAddInner(new TableRow());
                Item handleCell=new Item().d(Display.flex).justify_content(Justify.between);
                handleCell.addInner(handle);
                Item ui=handleCell.returnAddInner(new Item()).d(Display.flex);//.justify_content(Justify.end);
                LinkButton languageButton=ui.returnAddInner(new LinkButton()).tabindex(-1).addInner(new Icon(Icons.LANGUAGE)).sm().color(StyleColor.dark).ms(1).title("View by language").pt(0).px(1);
                languageButton.href(new PathAndQuery("/StringHandleEditor/viewByHandles").addQuery("enumID", enumID).addQuery("handle",handle).toString());
                Button saveButton=ui.returnAddInner(new Button()).tabindex(-1).id("button-save-"+key).addInner(new Icon(Icons.SAVE)).sm().color(StyleColor.primary).ms(1).title("Save changes").pt(0).px(1);
                
                Item formatCell=new Item();
                textarea ta=formatCell.returnAddInner(new textarea()).id("textarea-"+key).name("textarea-"+key);
                Styler.style(ta).w(100);
                ta.addInner(format);
                ta.oninput(HtmlUtils.js_call("nova.handle.onTextAreaChange", key));
                if (TypeUtils.isNullOrEmpty(format))
                {
                    ta.rows(1);
                }
                ta.placeholder("Undefined");
                saveButton.disabled();
                table.body().addRow(handleCell,formatCell);

                Inputs inputs=new Inputs();
                inputs.trace();
                inputs.add(ta);
                String action=new PathAndQuery("/StringHandleEditor/save")
                        .addQuery("handle", handle)
                        .addQuery("languageID", languageID)
                        .addQuery("enumID", enumID)
                        .toString();
                String saveScript=inputs.js_post(action);
                saveButton.onclick(saveScript);
                ta.onchange(saveScript);
            }
        }

        return page;
    }
    @GET
    public Element viewByHandles(Trace parent,@CookieStateParam(value="X-Nova-UserState") UserState userState,@QueryParam("enumID") Long enumID,@QueryParam("handle") String handle) throws Throwable
    {
        Page page=new Page();
        if (handle==null)
        {
            handle=userState.handle;
        }
        if (enumID==null)
        {
            enumID=userState.enumID;
        }
        
        Item main=page.content().returnAddInner(new Item()).m(2);
        
        Item bar=main.returnAddInner(new Item()).d(Display.flex).mb(2).border(Edge.bottom).pb(1);
        
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            { //enum drop down
                bar.returnAddInner(new Item()).addInner("Enum:").ms(2).me(1).mt(1);
                DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup());
                group.button().color(StyleColor.secondary).sm().me(2);
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleEnums WHERE Active=?"
                        ,true
                        );
                boolean found=false;
                String groupEnum=null;
                for (Row row:rowSet.rows())
                {
                    long id=row.getBIGINT("ID");
                    String enum_=row.getVARCHAR("Enum");
                    if (enumID==null)
                    {
                        enumID=id;
                        userState.enumID=id;
                    }
                    if (enum_==null)
                    {
                        enum_="{String}";
                    }
                    if (enumID==id)
                    {
                        found=true;
                        groupEnum=enum_;
                        enumID=id;
                    }
                    if (found==false)
                    {
                        groupEnum=enum_;
                    }
    
                    DropdownMenuItem dropDownItem=new DropdownMenuItem(enum_,"?enumID="+id);
                    group.menu().returnAddInner(dropDownItem);
                }
                if (found)
                {
                    userState.enumID=enumID;
                }
                else
                {
                    enumID=userState.enumID;
                }
                group.button().addInner(groupEnum);
            }
            {
                bar.returnAddInner(new Item()).addInner("Handle:").me(1).mt(1);
                DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup());
                group.button().color(StyleColor.primary).sm();
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT Handle FROM HandleFormats WHERE EnumID=? GROUP BY Handle",enumID);
                boolean found=false;
                for (Row row:rowSet.rows())
                {
                    String h=row.getVARCHAR("Handle");
                    userState.handle=h;
                    if (handle==null)
                    {
                        handle=h;
                    }
                    if (h.equals(handle))
                    {
                        found=true;
                    }
    
                    DropdownMenuItem dropDownItem=new DropdownMenuItem(h,"?handle="+h);
                    group.menu().returnAddInner(dropDownItem);
                }
                if (found)
                {
                    userState.handle=handle;
                }
                else
                {
                    handle=userState.handle;
                }
                group.button().addInner(handle);
            }
            
            RowSet handleRowSet=accessor.executeQuery(parent, null
                    , "SELECT Format,LanguageID,Language,Description FROM HandleFormats JOIN HandleLanguages ON HandleFormats.LanguageID=HandleLanguages.ID WHERE EnumID=? AND Handle=? AND HandleLanguages.Active=? ORDER BY LanguageID"
                    ,enumID,handle,true);
            
            DataTableOptions options=new DataTableOptions();
            options.lengthMenu=new int[]{25,100,1000};
            Column formatColumn=new Column();
            formatColumn.searchable=false;
            options.columns=new Column[] {formatColumn,null};
            
            ColumnDef formatColumnDef=new ColumnDef(0);
            formatColumnDef.width(new Size(25,unit.percent));
            
            options.columnDefs=new ColumnDef[] {formatColumnDef};
            DataTable table=new DataTable(page,options);
//            table.w_auto();
            Styler.style(table).w(100);
            main.addInner(table);
            table.header().addRow("Language","Format");
            for (Row handleRow:handleRowSet.rows())
            {
                String description=handleRow.getVARCHAR("Description");
                String format=handleRow.getVARCHAR("Format");
                long languageID=handleRow.getBIGINT("LanguageID");
                String key=handle+"-"+languageID+"-"+enumID;
                Item languageCell=new Item().d(Display.flex).justify_content(Justify.between);
                languageCell.addInner(description);
                Item ui=languageCell.returnAddInner(new Item()).d(Display.flex);//.justify_content(Justify.end);
                LinkButton languageButton=ui.returnAddInner(new LinkButton()).tabindex(-1).addInner(new Icon(Icons.HANDLES)).sm().color(StyleColor.dark).ms(1).title("View by handle").pt(0).px(1);
                languageButton.href(new PathAndQuery("/StringHandleEditor/viewByLanguages").addQuery("enumID", enumID).addQuery("languageID",languageID).toString());
                Button saveButton=ui.returnAddInner(new Button()).tabindex(-1).id("button-save-"+key).addInner(new Icon(Icons.SAVE)).sm().color(StyleColor.primary).ms(1).title("Save changes").pt(0).px(1);
                
                Item formatCell=new Item();
                textarea ta=formatCell.returnAddInner(new textarea()).id("textarea-"+key).name("textarea-"+key);
                Styler.style(ta).w(100);
                ta.addInner(format);
                ta.oninput(HtmlUtils.js_call("nova.handle.onTextAreaChange", key));
                if (TypeUtils.isNullOrEmpty(format))
                {
                    ta.rows(1);
                }
                ta.placeholder("Undefined");
                saveButton.disabled();
                table.body().addRow(languageCell,formatCell);

                Inputs inputs=new Inputs();
                inputs.trace();
                inputs.add(ta);
                String action=new PathAndQuery("/StringHandleEditor/save")
                        .addQuery("handle", handle)
                        .addQuery("languageID", languageID)
                        .addQuery("enumID", enumID)
                        .toString();
                String saveScript=inputs.js_post(action);
                saveButton.onclick(saveScript);
                ta.onchange(saveScript);
            }
        }

        return page;
    }
    
    @POST
    public RemoteResponse save(Trace parent,@QueryParam("handle") String handle,@QueryParam("languageID") long languageID,@QueryParam("enumID") long enumID,Context context) throws Throwable, Throwable
    {
        String key=handle+"-"+languageID+"-"+enumID;
        RemoteResponse response=new RemoteResponse();
        response.trace();
        String value=context.getHttpServletRequest().getParameter("textarea-"+key);
  //      System.out.println("value:"+value);
        boolean undefined=TypeUtils.isNullOrSpace(value);
        if (undefined)
        {
            value=null;
        }
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            
                int updated=accessor.executeUpdate(parent, null
                        , "UPDATE HandleFormats SET Format=? WHERE Handle=? AND LanguageID=? AND EnumID=?"
                        ,value,handle,languageID,enumID);
        }
        response.script(HtmlUtils.js_call("nova.handle.disableSaveButton", key,undefined)+";");
        return response;
    }
    CardDocument createSettingsCard()
    {
        CardDocument card=new CardDocument();
        card.mt(2).w(100);
        return card;
    }

    @GET
    public Element nextUndefined(Trace parent,@CookieStateParam(value="X-Nova-UserState") UserState userState) throws Throwable
    {
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            Row row=SqlUtils.executeQueryOne(parent, null, accessor
                    ,"SELECT TOP(1) * FROM HandleFormats JOIN HandleEnums ON HandleEnums.ID=EnumID JOIN HandleLanguages ON HandleLanguages.ID=LanguageID WHERE Format IS NULL AND HandleEnums.Active=? AND HandleLanguages.Active=?"
                    ,true,true);
            if (row!=null)
            {
                long enumID=row.getBIGINT("EnumID");
                String handle=row.getVARCHAR("Handle");
                return viewByHandles(parent, userState,enumID, handle);
            }
        }

        Page page=new Page();
        Item main=page.content().returnAddInner(new Item()).m(2);
        main.returnAddInner(new Alert()).addInner("No undefined handles found").color(StyleColor.success);
        return page;
    }
    
    @GET
    public Element settings(Trace parent,@QueryParam("enumID") Long enumID,@QueryParam("handle") String handle) throws Throwable
    {
        Page page=new Page();
        
//        Item main=page.content().returnAddInner(new Item()).d(Display.flex).justify_content(Justify.center).returnAddInner(new Item());
//        Col main=page.content().returnAddInner(new org.nova.html.bootstrap.Row()).returnAddInner(new Col(10)).mx_auto();

        org.nova.html.bootstrap.Row bRow=new org.nova.html.bootstrap.Row();
        page.content().returnAddInner(bRow);
        bRow.returnAddInner(new Col(2));
        Col main=bRow.returnAddInner(new Col(8));
        bRow.returnAddInner(new Col(2));
        
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            { 
               
                CardDocument card=main.returnAddInner(this.createSettingsCard());
                card.header().addInner("Languages");
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleLanguages ORDER BY Language");
                for (Row row:rowSet.rows())
                {
                    long id=row.getBIGINT("ID");
                    String description=row.getVARCHAR("Description");
                    boolean active=row.getBIT("Active");
                    Item item=card.body().returnAddInner(new Item());
                    item.d(Display.flex).justify_content(Justify.between);
                    item.border(Edge.bottom).py(1);
                    item.addInner(description);
                    String name="name_"+id;
                    Item ui=item.returnAddInner(new Item()).d(Display.flex).justify_content(Justify.between);
                    Switch s=ui.returnAddInner(new Switch(name,active,"/StringHandleEditor/setLanguage?ID="+id));
                    s.pt(1);
                    ProceedModalDialog dialog=ui.returnAddInner(new ProceedModalDialog(new Inputs()
                            ,new PathAndQuery("/StringHandleEditor/deleteLanguage").addQuery("ID", id).toString()
                            , "Confirmation required","Delete "+description+"?"
                            ));
                    ui.returnAddInner(new Button()).addInner("X").sm().color(StyleColor.danger).onclick(dialog.js_show());
                }
            }
        }
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            { 
               
                CardDocument card=main.returnAddInner(this.createSettingsCard());
                card.header().addInner("Enums");
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleEnums ORDER BY Enum");
                for (Row row:rowSet.rows())
                {
                    long id=row.getBIGINT("ID");
                    String enum_=row.getVARCHAR("Enum");
                    if (enum_==null)
                    {
                        enum_="{String}";
                    }
                    boolean active=row.getBIT("Active");
                    Item item=card.body().returnAddInner(new Item());
                    item.border(Edge.bottom).py(1);
                    item.d(Display.flex).justify_content(Justify.between);
                    item.addInner(enum_);
                    String name="name_"+id;
                    Item ui=item.returnAddInner(new Item()).d(Display.flex).justify_content(Justify.between);
                    Switch s=ui.returnAddInner(new Switch(name,active,"/StringHandleEditor/setEnum?ID="+id));
                    s.pt(1);
                    ProceedModalDialog dialog=ui.returnAddInner(new ProceedModalDialog(new Inputs(), "Confirmation required", "Delete "+enum_+"?",new PathAndQuery("/StringHandleEditor/deleteEnum").addQuery("ID", id).toString()));
                    ui.returnAddInner(new Button()).addInner("X").sm().color(StyleColor.danger).onclick(dialog.js_show());
                }
            }
        }
        return page;
    }
    @POST
    public RemoteResponse setEnum(Trace parent,@QueryParam("ID") long ID,Context context) throws Throwable, Throwable
    {
        RemoteResponse response=new RemoteResponse();
        String value=context.getHttpServletRequest().getParameter("name_"+ID);
        boolean checked="true".equalsIgnoreCase(value);
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            int updated=accessor.executeUpdate(parent, null
                    , "UPDATE HandleEnums SET Active=? WHERE ID=?"
                    ,checked,ID);
        }
        return response;
    }

    @POST
    public RemoteResponse deleteLanguage(Trace parent,@QueryParam("ID") long ID) throws Throwable, Throwable
    {
        RemoteResponse response=new RemoteResponse();
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            accessor.executeUpdate(parent, null
                    , "DELETE HandleFormats WHERE LanguageID=?"
                    ,ID);

            accessor.executeUpdate(parent, null
                    , "DELETE HandleLanguages WHERE ID=?"
                    ,ID);
            
            response.location("/StringHandleEditor/settings");
        }
        return response;
    }
    @POST
    public RemoteResponse deleteEnum(Trace parent,@QueryParam("ID") long ID) throws Throwable, Throwable
    {
        RemoteResponse response=new RemoteResponse();
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            accessor.executeUpdate(parent, null
                    , "DELETE HandleFormats WHERE EnumID=?"
                    ,ID);

            accessor.executeUpdate(parent, null
                    , "DELETE HandleEnums WHERE ID=?"
                    ,ID);
            
            response.location("/StringHandleEditor/settings");
        }
        return response;
    }
    @POST
    public RemoteResponse setLanguage(Trace parent,@QueryParam("ID") long ID,Context context) throws Throwable, Throwable
    {
        RemoteResponse response=new RemoteResponse();
        String value=context.getHttpServletRequest().getParameter("name_"+ID);
        boolean checked="true".equalsIgnoreCase(value);
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            int updated=accessor.executeUpdate(parent, null
                    , "UPDATE HandleLanguages SET Active=? WHERE ID=?"
                    ,checked,ID);
        }
        return response;
    }
    
    
    
    
    
    
}
