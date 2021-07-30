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
import org.nova.html.bootstrap.TableBody;
import org.nova.html.bootstrap.TableFooter;
import org.nova.html.bootstrap.TableHeader;
import org.nova.html.ext.TableRow;
import org.nova.html.deprecated.ObjectBuilder;
import org.nova.html.elements.Composer;
import org.nova.html.enums.link_rel;
import org.nova.html.ext.Head;
import org.nova.html.tags.link;
import org.nova.html.tags.script;
import org.nova.html.tags.tbody;
import org.nova.html.tags.tr;
import org.nova.utils.TypeUtils;

//!!! Requires jquery

public class DataTableDocument extends DataTable
{
    final private TableHeader header;
    final private TableBody body;
    final private TableFooter footer;
    final private DataTableOptions options;
    
    private DataTableDocument(BootStrapPage page,DataTableOptions options)
    {
        super(options);
        String dataTableKey=DataTable.class.getCanonicalName();
        page.head().add(dataTableKey+".0", new script().src("https://code.jquery.com/jquery-3.5.1.js"));
        page.head().add(dataTableKey+".1", new script().src("https://cdn.datatables.net/1.10.23/js/jquery.dataTables.min.js"));
        page.head().add(dataTableKey+".2", new script().src("https://cdn.datatables.net/1.10.23/js/dataTables.bootstrap5.min.js"));
        page.head().add(dataTableKey+".3",new link().rel(link_rel.stylesheet).type("text/css").href("https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css"));
        page.head().add(dataTableKey+".4",new link().rel(link_rel.stylesheet).type("text/css").href("https://cdn.datatables.net/1.10.23/css/dataTables.bootstrap5.min.css"));
        this.options=options;
        this.header=returnAddInner(new TableHeader());
        this.body=returnAddInner(new TableBody());
        this.footer=returnAddInner(new TableFooter());
    }    

    public DataTableDocument(BootStrapPage page)
    {
        this(page,new DataTableOptions());
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
    
    public DataTableOptions options()
    {
        return this.options;
    }
    
}