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
import org.nova.html.bootstrap.ButtonGroup;
import org.nova.html.bootstrap.CardDocument;
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
import org.nova.html.bootstrap.TableHeader;
import org.nova.html.bootstrap.classes.AlignSelf;
import org.nova.html.bootstrap.classes.Display;
import org.nova.html.bootstrap.classes.Edge;
import org.nova.html.bootstrap.classes.Justify;
import org.nova.html.bootstrap.classes.StyleColor;
import org.nova.html.bootstrap.ext.DropdownButtonMenuGroup;
import org.nova.html.bootstrap.ext.Icon;
import org.nova.html.bootstrap.ext.DataTables.DataTable;
import org.nova.html.elements.Element;
import org.nova.html.elements.HtmlElementWriter;
import org.nova.html.enums.method;
import org.nova.html.ext.TableRow;
import org.nova.html.localization.LanguageCode;
import org.nova.html.localization.StringHandle;
import org.nova.html.localization.StringHandleSqlResolver;
import org.nova.html.remote.RemoteResponseWriter;
import org.nova.html.remoting.HtmlRemotingWriter;
import org.nova.html.tags.col;
import org.nova.html.tags.form_post;
import org.nova.html.tags.i;
import org.nova.html.tags.option;
import org.nova.html.tags.textarea;
import org.nova.html.templating.ReplaceMarker;
import org.nova.http.server.GzipContentDecoder;
import org.nova.http.server.GzipContentEncoder;
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
@ContentWriters({JSONContentWriter.class, HtmlElementWriter.class, HtmlRemotingWriter.class})
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
    
    private void addLanguageLocale(Trace parent,Accessor accessor,String language,Locale locale) throws Throwable
    {
        if (accessor.executeUpdate(parent, null
                , "UPDATE HandleFormats SET Active=? WHERE language=? AND locale=?"
                , true,language,locale.toString())==0)
        {
            Insert.table("HandleFormats").value("language", language).value("locale", locale.toString()).value("active", true).execute(parent, accessor);
        }
    }
    
    private void addLanguageLocale(Trace parent,String language,Locale locale) throws Throwable
    {
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            addLanguageLocale(parent, accessor, language, locale);
        }
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
//        for (LanguageCode lc:LanguageCode.values())
//        {
//            String code=lc.getValue().code;
//            Locale locale=lc.getValue().locale;
//            System.out.println("locale:"+code+","+locale.getLanguage()+"="+locale.getDisplayName());
//        }
//        for (Locale locale:Locale.getAvailableLocales())
//        {
//            System.out.println("locale:"+locale.getLanguage()+"="+locale.getDisplayName());
//        }
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
                    enumID=Insert.table("HandleEnums").value("Enum", name).executeAndReturnLongKey(parent, accessor);
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
    public Element viewByLanguages(Trace parent,@QueryParam("languageID") Long languageID) throws Throwable
    {
        Page page=new Page();
        
        Item main=page.content().returnAddInner(new Item()).m(2);
        
        Item bar=main.returnAddInner(new Item()).d(Display.flex).mb(2).border(Edge.bottom).pb(1);
        bar.returnAddInner(new Item()).addInner("Language:").me(2).mt(1);
        DropdownButtonMenuGroup group=bar.returnAddInner(new DropdownButtonMenuGroup("Languages"));
        group.button().color(StyleColor.light).sm();
        
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            RowSet rowSet=accessor.executeQuery(parent, null
                    , "SELECT * FROM HandleLanguages");
            for (Row languageRow:rowSet.rows())
            {
                long id=languageRow.getBIGINT("ID");
                if (languageID==null)
                {
                    languageID=id;
                }
                String language=languageRow.getVARCHAR("Language");
                boolean active=languageRow.getBIT("Active");
                DropdownMenuItem dropDownItem=new DropdownMenuItem(language,"#?languageID="+id);
                dropDownItem.d(Display.flex).justify_content(Justify.between);
                
                FormCheck formCheck=dropDownItem.returnAddInner(new FormCheck()).switch_().mt(1);
                InputCheckbox checkbox=formCheck.addInputCheckbox(null).checked(active);
                group.menu().returnAddInner(dropDownItem);
            }
            RowSet handleRowSet=accessor.executeQuery(parent, null
                    , "SELECT * FROM HandleFormats WHERE LanguageID=?",languageID);
            
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
                Item handleCell=new Item().d(Display.flex).justify_content(Justify.between);
                handleCell.addInner(handle);
                Item ui=handleCell.returnAddInner(new Item()).d(Display.flex);//.justify_content(Justify.end);
                Button languageButton=ui.returnAddInner(new Button()).addInner(new Icon("play")).sm().color(StyleColor.primary).ms(1).title("Preview test").pt(0).px(1);
                Button testButton=ui.returnAddInner(new Button()).addInner(new Icon("play")).sm().color(StyleColor.primary).ms(1).title("Preview test").pt(0).px(1);
                Button updateButton=ui.returnAddInner(new Button()).addInner(new Icon("save")).sm().color(StyleColor.primary).ms(1).title("Save changes").pt(0).px(1);
                
                Item formatCell=new Item();
                textarea ta=formatCell.returnAddInner(new textarea());
                if (TypeUtils.isNullOrEmpty(format))
                {
                    testButton.disabled();
                    updateButton.disabled();
                    ta.rows(1);
                }
                ta.addInner("help");
                Styler.style(ta).w(100);
                table.body().addRow(handleCell,formatCell);
            }
        }

        return page;
    }
}
