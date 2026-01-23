/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.html.bootstrap.ext.DataTables;

import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.TableBody;
import org.nova.html.bootstrap.TableFoot;
import org.nova.html.bootstrap.TableHead;
import org.nova.html.bootstrap.TableHeadRow;
import org.nova.html.deprecated.ObjectBuilder;
import org.nova.html.elements.Composer;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.LiteralHtml;
import org.nova.html.ext.TableRow;
import org.nova.html.tags.link;
import org.nova.html.tags.script;

public class DataTable  extends StyleComponent<DataTable>
{
    final private TableHead header;
    final private TableBody body;
    final private TableFoot footer;
    
    public static script SCRIPT=new script().src("https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js");
    public static link CSS=new link().rel(link_rel.stylesheet).href("https://cdn.datatables.net/1.13.4/css/jquery.dataTables.min.css");
    
    final private DataTableOptions options;

    public DataTable(DataTableOptions options) throws Throwable
    {
        super("table", "table");
        if (options==null)
        {
            options=new DataTableOptions();
        }
        this.options=options;
        this.header=returnAddInner(new TableHead());
        this.body=returnAddInner(new TableBody());
        this.footer=returnAddInner(new TableFoot());
    }    
    public DataTable(DataTableOptions options,Object...columnNames) throws Throwable 
    {
        this(options);
        TableHeadRow row=new TableHeadRow();
        row.add(columnNames);
        header().addInner(row);
    }

    
    public TableHead header()
    {
        return this.header;
    }
    
    public TableFoot footer()
    {
        return this.footer;
    }
    
    public TableBody body()
    {
        return this.body;
    }
    public DataTable w_auto()
    {
        addClass("w-auto");
        return this;
    }
    public DataTable display_nowrap()
    {
        addClass("display nowrap");
        return this;
    }
    public DataTable hover()
    {
        addClass("table-hover");
        return this;
    }
    public DataTable striped()
    {
        addClass("table-striped");
        return this;
    }
    public DataTable bordered()
    {
        addClass("table-bordered");
        return this;
    }
    public DataTable borderless()
    {
        addClass("table-borderless");
        return this;
    }
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        this.addInner(new script().addInner(new LiteralHtml(js_new())));
        super.compose(composer);
    }
//    public String js_ready() throws Throwable
//    {
//        return js_ready(id(),options);
//    }
//    static public String js_ready(String id,DataTableOptions options) throws Throwable
//    {
//        StringBuilder sb=new StringBuilder();
//        sb.append("$(document).ready(function(){$('#").append(id).append("').DataTable(");
//        {
//            ObjectBuilder ob=new ObjectBuilder();
//            ob.add(options);
//            sb.append(ob.toString());
//        }
//        sb.append(");});");
//        return sb.toString();
//    }
    static public String js_draw(String id) throws Throwable
    {
        StringBuilder sb=new StringBuilder();
        sb.append("$('#").append(id).append("').DataTable().draw();");
        return sb.toString();
    }
    public String js_draw() throws Throwable
    {
        return js_draw(id());
    }
    
    static public String js_new(String id,DataTableOptions options) throws Throwable
    {
        System.out.println("id-------------------------"+id);
        StringBuilder sb=new StringBuilder();
        sb.append("$('#").append(id).append("').DataTable(");
        {
            ObjectBuilder ob=new ObjectBuilder();
            ob.add(options);
            sb.append(ob.toString());
        }
        sb.append(");");
        return sb.toString();
        
    }
    public String js_new() throws Throwable
    {
        return js_new(id(),options);
    }
    public TableRow returnAddBodyRow()
    {
        return body().returnAddInner(new TableRow());
    }
    
}