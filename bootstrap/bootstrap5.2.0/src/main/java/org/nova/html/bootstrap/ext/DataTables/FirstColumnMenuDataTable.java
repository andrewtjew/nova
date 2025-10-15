package org.nova.html.bootstrap.ext.DataTables;

import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableColumnOrder;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.DataTables.DataTableOrder;
import org.nova.html.bootstrap.TableHeadRow;
import org.nova.html.properties.Length_;
import org.nova.html.properties.Style;
import org.nova.html.properties.Unit_;
import org.nova.html.properties.width;

public class FirstColumnMenuDataTable extends DataTable 
{
	static private DataTableOptions addOptions(DataTableOptions options,Length_ menuWidth,int target)
	{
	    ColumnDef menuColumnDefs=new ColumnDef(target);
	    menuColumnDefs.orderable=false;
	    menuColumnDefs.searchable=false;
        
	    
	    menuColumnDefs.width(menuWidth);
	    options.columnDefs=new ColumnDef[] {menuColumnDefs};
	    
	    DataTableColumnOrder order=new DataTableColumnOrder(1, DataTableOrder.asc);
	    options.order(order);
	    return options;
	}
    
	public FirstColumnMenuDataTable(DataTableOptions options,Length_ menuWidth,Object...columnNames) 
    {
        super(addOptions(options,menuWidth,0));
        
        TableHeadRow row=new TableHeadRow();
        row.add("");
        row.add(columnNames);
        header().addInner(row);
    }
	
	
	public FirstColumnMenuDataTable(Length_ menuWidth,Object...columnNames) 
	{
	    this(new DataTableOptions(),menuWidth,columnNames);
	}

	public FirstColumnMenuDataTable(Object...columnNames) 
	{
		this(new Length_(1,Unit_.em),columnNames);
	}

	public FirstColumnMenuDataTable(DataTableOptions options,Object...columnNames) 
    {
        this(options,new Length_(1,Unit_.em),columnNames);
    }


}
