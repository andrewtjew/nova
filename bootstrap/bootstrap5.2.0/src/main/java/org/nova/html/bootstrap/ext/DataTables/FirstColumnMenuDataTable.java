package org.nova.html.bootstrap.ext.DataTables;

import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableColumnOrder;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.DataTables.DataTableOrder;
import org.nova.html.bootstrap.TableHeadRow;
import org.nova.html.properties.Size;
import org.nova.html.properties.Unit;

public class FirstColumnMenuDataTable extends DataTable 
{
	static private DataTableOptions addOptions(DataTableOptions options,Size menuWidth,int target)
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
    
	public FirstColumnMenuDataTable(DataTableOptions options,Size menuWidth,Object...columnNames) 
    {
        super(addOptions(options,menuWidth,0));
        
        TableHeadRow row=new TableHeadRow();
        row.add("");
        row.add(columnNames);
        header().addInner(row);
    }
	
	
	public FirstColumnMenuDataTable(Size menuWidth,Object...columnNames) 
	{
	    this(new DataTableOptions(),menuWidth,columnNames);
	}

	public FirstColumnMenuDataTable(Object...columnNames) 
	{
		this(new Size(1,Unit.em),columnNames);
	}

	public FirstColumnMenuDataTable(DataTableOptions options,Object...columnNames) 
    {
        this(options,new Size(1,Unit.em),columnNames);
    }


}
