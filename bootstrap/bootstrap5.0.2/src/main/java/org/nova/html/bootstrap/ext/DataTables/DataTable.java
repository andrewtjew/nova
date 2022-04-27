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

import java.util.ArrayList;

import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.bootstrap.BootStrapPage;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.Styling;
import org.nova.html.bootstrap.TableBody;
import org.nova.html.bootstrap.TableFooter;
import org.nova.html.bootstrap.TableHeader;
import org.nova.html.ext.TableRow;
import org.nova.html.deprecated.ObjectBuilder;
import org.nova.html.elements.Composer;
import org.nova.html.elements.GlobalEventTagElement;
import org.nova.html.elements.TagElement;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Head;
import org.nova.html.tags.link;
import org.nova.html.tags.script;
import org.nova.html.tags.table;
import org.nova.html.tags.tbody;
import org.nova.html.tags.tr;
import org.nova.utils.TypeUtils;

//!!! Requires jquery

public class DataTable  extends StyleComponent<DataTable>
{
    final private TableHeader header;
    final private TableBody body;
    final private TableFooter footer;
//    final private DataTableOptions options;
    public static String CSS="<link rel='stylesheet' type='text/css' href='https://cdn.datatables.net/v/bs5/dt-1.11.5/datatables.min.css'/>";
    public static String SCRIPT="<script type='text/javascript' src='https://cdn.datatables.net/v/bs5/dt-1.11.5/datatables.min.js'></script>";
    final private DataTableOptions options;

    public DataTable(DataTableOptions options)
    {
        super("table", "table");
        if (options==null)
        {
            options=new DataTableOptions();
        }
        this.options=options;
        this.header=returnAddInner(new TableHeader());
        this.body=returnAddInner(new TableBody());
        this.footer=returnAddInner(new TableFooter());
    }    

    public TableHeader header()
    {
        return this.header;
    }
    
    public TableFooter footer()
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
        StringBuilder sb=new StringBuilder();
        sb.append("$(document).ready(function(){$('#").append(id()).append("').DataTable(");
        {
            ObjectBuilder ob=new ObjectBuilder();
            ob.add(this.options);
            sb.append(ob.toString());
        }
        sb.append(");});");

        if (sb.length()>0)
        {
            script script=new script().addInner(sb.toString());
            composer.getStringBuilder().append(script.getHtml());
        }
        super.compose(composer);
    }
    
    
}