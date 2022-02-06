package org.nova.html.bootstrap.localization;

import java.lang.reflect.Constructor;
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
import org.nova.html.bootstrap.remote.Switch;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.enums.method;
import org.nova.html.ext.HtmlUtils;
import org.nova.html.ext.InputHidden;
import org.nova.html.ext.TableRow;
import org.nova.html.localization.LanguageCode;
import org.nova.html.localization.StringHandle;
import org.nova.html.localization.StringHandleSqlResolver;
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
import org.nova.http.server.annotations.Filters;
import org.nova.http.server.annotations.GET;
import org.nova.http.server.annotations.POST;
import org.nova.http.server.annotations.PUT;
import org.nova.http.server.annotations.Path;
import org.nova.http.server.annotations.QueryParam;
import org.nova.http.server.annotations.StateParam;
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
        StringHandleSqlResolver resolver=new StringHandleSqlResolver(traceManager, connector);
        StringHandle.setResolver(resolver);
    }
    
    private void addLanguage(Trace parent,Accessor accessor,LanguageCode languageCode) throws Throwable
    {
        Row row=SqlUtils.executeQueryOne(parent, null, accessor
                , "SELECT * FROM HandleLanguages WHERE Language=?"
                ,languageCode.name());
        if (row==null)
        {
            Insert.table("HandleLanguages").value("Language", languageCode.name()).value("Active", true).execute(parent, accessor);
        }
    }
    
    
    public void addDefaultLocales(Trace parent) throws Throwable, Throwable
    {
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            addLanguage(parent, accessor, LanguageCode.English);
            addLanguage(parent, accessor, LanguageCode.French);
            addLanguage(parent, accessor, LanguageCode.Malay);
            addLanguage(parent, accessor, LanguageCode.Chinese);
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
        
        type.getEnumConstants();
    
        item.returnAddInner(new Alert()).addInner("Added: "+name).color(StyleColor.success);
        TableHeader header=new TableHeader();
        header.addRow("Formats");
        Table table=item.returnAddInner(new Table(header));
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
            RowSet rowSet=accessor.executeQuery(parent, null
                    , "SELECT * FROM HandleLanguages");
                    
            for (Object value:type.getEnumConstants())
            {
                Enum<?> e=(Enum<?>)value;
                String handle=e.name();
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
        return page;
    }

    @GET
    public Element viewByLanguages(Trace parent,@QueryParam("languageID") Long languageID,@QueryParam("enumID") Long enumID) throws Throwable
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
//            Table table=dropDownItem.returnAddInner(new Table());
//            TableHeader heading=table.returnAddInner(new TableHeader());
//            heading.addRow("Specifier","Description");
//            TableBody body=table.returnAddInner(new TableBody());
//            body.addRow("%b","test");
//            body.addRow("%b","test");
//            body.addRow("%b","test");
//            body.addRow("%b","test");
//            
//        }
        
        
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            {
                bar.returnAddInner(new Item()).addInner("Enum:").me(1).mt(1);
                DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup());
                group.button().color(StyleColor.secondary).sm().me(2);
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleEnums");
                for (Row row:rowSet.rows())
                {
                    long id=row.getBIGINT("ID");
                    String enum_=row.getVARCHAR("Enum");
                    if (enumID==null)
                    {
                        enumID=id;
                    }
                    if (enum_==null)
                    {
                        enum_="{String}";
                    }
                    if (enumID==id)
                    {
                        group.button().addInner(enum_);
                    }
    
                    DropdownMenuItem dropDownItem=new DropdownMenuItem(enum_,"?enumID="+id);
                    group.menu().returnAddInner(dropDownItem);
                }
            }
            //Build language dropdown
            {
                bar.returnAddInner(new Item()).addInner("Language:").me(1).mt(1);
                DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup());
                group.button().color(StyleColor.primary).sm();
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleLanguages WHERE Active=?",true);
                for (Row row:rowSet.rows())
                {
                    long id=row.getBIGINT("ID");
                    String language=row.getVARCHAR("Language");
//                    boolean active=row.getBIT("Active");
                    if (languageID==null)
                    {
                        languageID=id;
                    }
                    if (id==languageID)
                    {
                        group.button().addInner(language);
                    }
    
                    DropdownMenuItem dropDownItem=new DropdownMenuItem(language,"?languageID="+id);
                    dropDownItem.d(Display.flex).justify_content(Justify.between);
                    
//                    FormCheck formCheck=dropDownItem.returnAddInner(new FormCheck()).switch_().mt(1);
//                    InputCheckbox checkbox=formCheck.addInputCheckbox(null).checked(active);
                    group.menu().returnAddInner(dropDownItem);
                }
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
//                form_post post=table.body().returnAddInner(new form_post()).action(new PathAndQuery("/StringHandleEditor/save").addQuery("handle", handle).toString());
//                TableRow tr=post.returnAddInner(new TableRow());
                Item handleCell=new Item().d(Display.flex).justify_content(Justify.between);
                handleCell.addInner(handle);
                Item ui=handleCell.returnAddInner(new Item()).d(Display.flex);//.justify_content(Justify.end);
                LinkButton languageButton=ui.returnAddInner(new LinkButton()).tabindex(-1).addInner(new Icon(Icons.LANGUAGE)).sm().color(StyleColor.dark).ms(1).title("View by language").pt(0).px(1);
                languageButton.href(new PathAndQuery("/StringHandleEditor/viewByHandles").addQuery("enumID", enumID).addQuery("handle",handle).toString());
                Button previewButton=ui.returnAddInner(new Button()).tabindex(-1).id("button-test-"+handle).addInner(new Icon(Icons.PREVIEW)).sm().color(StyleColor.primary).ms(1).title("Preview test").pt(0).px(1);
                Button saveButton=ui.returnAddInner(new Button()).tabindex(-1).id("button-save-"+handle).addInner(new Icon(Icons.SAVE)).sm().color(StyleColor.primary).ms(1).title("Save changes").pt(0).px(1);
                
                Item formatCell=new Item();
                textarea ta=formatCell.returnAddInner(new textarea()).id("textarea-"+handle).name("textarea-"+handle);
                Styler.style(ta).w(100);
                ta.addInner(format);
                ta.oninput(HtmlUtils.js_call("nova.handle.onTextAreaChange", handle));
                if (TypeUtils.isNullOrEmpty(format))
                {
                    previewButton.disabled();
                    ta.rows(1);
                }
                ta.placeholder("Undefined");
                saveButton.disabled();
                table.body().addRow(handleCell,formatCell);

                Inputs inputs=new Inputs();
                inputs.trace();
                inputs.add(ta);
                String action=new PathAndQuery("/StringHandleEditor/save")
                        .addQuery("key", handle)
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
    public Element viewByHandles(Trace parent,@QueryParam("enumID") Long enumID,@QueryParam("handle") String handle) throws Throwable
    {
        Page page=new Page();
        
        Item main=page.content().returnAddInner(new Item()).m(2);
        
        Item bar=main.returnAddInner(new Item()).d(Display.flex).mb(2).border(Edge.bottom).pb(1);
        
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            { //enum drop down
                bar.returnAddInner(new Item()).addInner("Enum:").ms(2).me(1).mt(1);
                DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup());
                group.button().color(StyleColor.secondary).sm().me(2);
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT * FROM HandleEnums");
                for (Row row:rowSet.rows())
                {
                    long id=row.getBIGINT("ID");
                    String enum_=row.getVARCHAR("Enum");
                    if (enumID==null)
                    {
                        enumID=id;
                    }
                    if (enumID==id)
                    {
                        group.button().addInner(enum_);
                        enumID=id;
                    }
    
                    DropdownMenuItem dropDownItem=new DropdownMenuItem(enum_,"?enumID="+id);
                    group.menu().returnAddInner(dropDownItem);
                }
            }
            {
                bar.returnAddInner(new Item()).addInner("Handle:").me(1).mt(1);
                String selectedLanguage=null;
                DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup());
                group.button().color(StyleColor.primary).sm();
                RowSet rowSet=accessor.executeQuery(parent, null
                        , "SELECT Handle FROM HandleFormats WHERE EnumID=? GROUP BY Handle",enumID);
                for (Row row:rowSet.rows())
                {
                    String h=row.getVARCHAR("Handle");
                    if (handle==null)
                    {
                        handle=h;
                    }
                    if (h.equals(handle))
                    {
                        group.button().addInner(handle);
                    }
    
                    DropdownMenuItem dropDownItem=new DropdownMenuItem(h,"?handle="+h);
                    group.menu().returnAddInner(dropDownItem);
                }
                group.button().addInner(selectedLanguage);
            }
            
            RowSet handleRowSet=accessor.executeQuery(parent, null
                    , "SELECT Format,LanguageID,Language FROM HandleFormats JOIN HandleLanguages ON HandleFormats.LanguageID=HandleLanguages.ID WHERE EnumID=? AND Handle=? AND HandleLanguages.Active=? ORDER BY LanguageID"
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
                String language=handleRow.getVARCHAR("Language");
                String format=handleRow.getVARCHAR("Format");
                long languageID=handleRow.getBIGINT("LanguageID");
                Item languageCell=new Item().d(Display.flex).justify_content(Justify.between);
                languageCell.addInner(language);
                Item ui=languageCell.returnAddInner(new Item()).d(Display.flex);//.justify_content(Justify.end);
                LinkButton languageButton=ui.returnAddInner(new LinkButton()).tabindex(-1).addInner(new Icon(Icons.LANGUAGE)).sm().color(StyleColor.dark).ms(1).title("View by language").pt(0).px(1);
                languageButton.href(new PathAndQuery("/StringHandleEditor/viewByLanguages").addQuery("enumID", enumID).addQuery("languageID",languageID).toString());
                Button previewButton=ui.returnAddInner(new Button()).tabindex(-1).id("button-test-"+languageID).addInner(new Icon(Icons.PREVIEW)).sm().color(StyleColor.primary).ms(1).title("Preview test").pt(0).px(1);
                Button saveButton=ui.returnAddInner(new Button()).tabindex(-1).id("button-save-"+languageID).addInner(new Icon(Icons.SAVE)).sm().color(StyleColor.primary).ms(1).title("Save changes").pt(0).px(1);
                
                Item formatCell=new Item();
                textarea ta=formatCell.returnAddInner(new textarea()).id("textarea-"+languageID).name("textarea-"+handle);
                Styler.style(ta).w(100);
                ta.addInner(format);
                ta.oninput(HtmlUtils.js_call("nova.handle.onTextAreaChange", languageID));
                if (TypeUtils.isNullOrEmpty(format))
                {
                    previewButton.disabled();
                    ta.rows(1);
                }
                ta.placeholder("Undefined");
                saveButton.disabled();
                table.body().addRow(languageCell,formatCell);

                Inputs inputs=new Inputs();
                inputs.trace();
                inputs.add(ta);
                String action=new PathAndQuery("/StringHandleEditor/save")
                        .addQuery("key", languageID)
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
    
    CardDocument createSettingsCard()
    {
        CardDocument card=new CardDocument();
        card.mt(2).w(100);
        return card;
    }

    @GET
    public Element nextUndefined(Trace parent) throws Throwable
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
                return viewByHandles(parent, enumID, handle);
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
                    String language=row.getVARCHAR("Language");
                    boolean active=row.getBIT("Active");
                    Item item=card.body().returnAddInner(new Item());
                    item.d(Display.flex).justify_content(Justify.between);
                    item.border(Edge.bottom).py(1);
                    item.addInner(language);
                    String name="name_"+id;
                    Item ui=item.returnAddInner(new Item()).d(Display.flex).justify_content(Justify.between);
                    Switch s=ui.returnAddInner(new Switch(name,active,"/StringHandleEditor/setLanguage?ID="+id));
                    s.pt(1);
                    ui.returnAddInner(new Button()).addInner("X").sm().color(StyleColor.danger);
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
                    ui.returnAddInner(new Button()).addInner("X").sm().color(StyleColor.danger);
                    
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
    
    @POST
    public RemoteResponse save(Trace parent,@QueryParam("key") String key,@QueryParam("handle") String handle,@QueryParam("languageID") long languageID,@QueryParam("enumID") long enumID,Context context) throws Throwable, Throwable
    {
        RemoteResponse response=new RemoteResponse();
        response.trace();
        String value=context.getHttpServletRequest().getParameter("textarea-"+handle);
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
    
    
    
    
    
}
