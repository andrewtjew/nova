package org.nova.html.bootstrap4.DataTables;

import java.util.ArrayList;

import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableColumnOrder;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.bootstrap4.StyleComponent;
import org.nova.html.bootstrap4.TableRow;
import org.nova.html.elements.Composer;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Head;
import org.nova.html.tags.link;
import org.nova.html.tags.script;
import org.nova.html.tags.tbody;
import org.nova.html.tags.tr;
import org.nova.html.widgets.ObjectBuilder;
import org.nova.html.widgets.TableFooter;
import org.nova.html.widgets.TableHeader;
import org.nova.utils.TypeUtils;

//!!! Requires jquery

public class DataTableBootstrap4 extends StyleComponent<DataTableBootstrap4>
{
    final private tbody tbody;
    private DataTableOptions options;

    private TableHeader header;
    private TableFooter footer;

    public DataTableBootstrap4(Head head,DataTableOptions options)
    {
        this(head,options,"https://cdn.datatables.net/v/bs4/dt-1.10.18/datatables.min.css","https://cdn.datatables.net/v/bs4/dt-1.10.18/datatables.min.js");
    }

    public DataTableBootstrap4(Head head)
    {
        this(head,null);
    }

    public DataTableBootstrap4(DataTableOptions options)
    {
        this(null,options);
    }
    
    public DataTableBootstrap4(Head head,DataTableOptions options,String cssFilePath,String scriptFilePath)
    {
        super("table","table");
        this.tbody=new tbody();
        this.options=options;

        if (head!=null)
        {
            String key=DataTableBootstrap4.class.getCanonicalName();
            script script=new script().src(scriptFilePath);
            head.add(key,script);
            link link=new link().rel(link_rel.stylesheet).type("text/css").href(cssFilePath);
            head.add(key,link);
        }
    }
    
    public DataTableBootstrap4 bordered()
    {
        addClass("table-bordered");
        return this;
    }
    
    public DataTableBootstrap4 striped()
    {
        addClass("table-striped");
        return this;
    }
    
    public DataTableBootstrap4 setHeader(TableHeader header)
    {
        this.header=header;
        return this;
    }
    public DataTableBootstrap4 setHeader(Object...objects)
    {
        this.header=new TableHeader().add(objects);
        return this;
    }
    public DataTableBootstrap4 setHeaderWithBlankColumnsNotOrderable(Object...objects)
    {
        if (this.options==null)
        {
            this.options=new DataTableOptions();
        }
        if (this.options.columnDefs==null)
        {
            ArrayList<Integer> list=new ArrayList<>();
            for (int i=0;i<objects.length;i++)
            {
                Object object=objects[i];
                if (object==null)
                {
                    list.add(i);
                }
                else if (object instanceof String)
                {
                    if (((String)object).length()==0)
                    {
                        list.add(i);
                    }
                }
            }
            if (list.size()>0)
            {
                ColumnDef columnDef=new ColumnDef(TypeUtils.intListToArray(list));
                columnDef.orderable=false;
                columnDef.searchable=false;
                this.options.columnDefs=new ColumnDef[]{columnDef}; 
            }
        }
        
        this.header=new TableHeader().add(objects);
        return this;
    }
    public TableRow returnAddRow()
    {
        TableRow row=new TableRow();
        this.tbody.addInner(row);
        return row;
    }
    public DataTableBootstrap4 addRow(TableRow row)
    {
        this.tbody.addInner(row);
        return this;
    }
    public DataTableBootstrap4 addRow(tr row)
    {
        this.tbody.addInner(row);
        return this;
    }
    public DataTableBootstrap4 addRow(Object...objects)
    {
        this.tbody.addInner(new TableRow().add(objects));
        return this;
    }
    public DataTableBootstrap4 setFooter(TableFooter footer)
    {
        this.footer=footer;
        return this;
    }
    public DataTableBootstrap4 setFooter(Object...objects)
    {
        this.footer=new TableFooter().add(objects);
        return this;
    }
    
    public tbody tbody()
    {
        return this.tbody;
    }
    public TableHeader header()
    {
        return this.header;
    }
    public TableFooter footer()
    {
        return this.footer;
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        StringBuilder sb=new StringBuilder();
        //sb.append("$(document).ready(function(){$('#").append(id()).append("').DataTable(");

        String function="f_"+id();
        sb.append("$(document).ready("+function+");function "+function+"(){$('#").append(id()).append("').DataTable(");
                
        boolean commaNeeded=false;
//        sb.append('{');
    //    if (this.options==null)
        {
            ObjectBuilder ob=new ObjectBuilder();
            ob.add(this.options);
            sb.append(ob.toString());
        }
//        sb.append(");});");
        sb.append(");}");

        this.addInner(this.header);
        this.addInner(this.tbody);
        this.addInner(this.footer);

        

        if (sb.length()>0)
        {
            script script=new script().addInner(sb.toString());
            composer.getStringBuilder().append(script.toString());
        }
        //this.compose(composer);
        super.compose(composer);
    }

    
}