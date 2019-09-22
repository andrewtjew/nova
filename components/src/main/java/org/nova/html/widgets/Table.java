package org.nova.html.widgets;

import org.nova.html.attributes.Style;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Head;
import org.nova.html.tags.link;
import org.nova.html.tags.table;
import org.nova.html.tags.tbody;
import org.nova.html.tags.tr;

public class Table extends Element
{
    final private table table;
    final private tbody tbody;
    private TableHeader header;
    private TableFooter footer;
    
    public Table()
    {
        this(null,"Table",null);
    }
    
    private static String DEFAULT_CSS_FILE_PATH="/resources/html/widgets/Table/style.css";
    
    static public link DEFAULT_CSS_LINK()
    {
        return new link().rel(link_rel.stylesheet).type("text/css").href(DEFAULT_CSS_FILE_PATH);
    }
    
    
    public Table(Head head,String class_,String cssFilePath)
    {
        this.table=new table();
        this.tbody=new tbody();

        this.table.addClass(class_);
        link link=new link().rel(link_rel.stylesheet).type("text/css").href(cssFilePath);
        if (head!=null)
        {
            head.add(class_,link);
        }
        else
        {
            this.table().addInner(link);
        }
    }
    public Table(Head head,String class_)
    {
        this(head,class_,DEFAULT_CSS_FILE_PATH);
    }
    public Table(Head head)
    {
        this(head,"Table");
    }
    
    public table table()
    {
        return this.table;
    }
    public tbody tbody()
    {
        return this.tbody;
    }
    
    public Table style(Style value)
    {
        this.table.style(value);
        return this;
    }
    
    public Table style(String value)
    {
        this.table.style(value);
        return this;
    }
    
    public void setHeader(TableHeader header)
    {
        this.header=header;
    }
    public void setHeader(Object...objects)
    {
        this.header=new TableHeader().add(objects);
    }
    public TableRow returnAddRow()
    {
        TableRow row=new TableRow();
        this.tbody.addInner(row);
        return row;
    }
    public void addRow(TableRow row)
    {
        this.tbody.addInner(row);
    }
    public void addRow(tr row)
    {
        this.tbody.addInner(row);
    }
    public void addRow(Object...objects)
    {
        this.tbody.addInner(new TableRow().add(objects));
    }
    public void setFooter(TableFooter footer)
    {
        this.footer=footer;
    }
    public void setFooterInline(Object...objects)
    {
        this.footer=new TableFooter().add(objects);
    }

    @Override
    public void compose(Composer composer) throws Throwable
    {
        this.table.addInner(this.header);
        this.table.addInner(this.tbody);
        this.table.addInner(this.footer);
        composer.compose(this.table);
    }
}
