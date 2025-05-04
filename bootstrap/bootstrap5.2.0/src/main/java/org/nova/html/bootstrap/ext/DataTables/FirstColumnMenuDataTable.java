package org.nova.html.bootstrap.ext.DataTables;

import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableColumnOrder;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.DataTables.DataTableOrder;
import org.nova.html.bootstrap.TableHeadRow;
import org.nova.html.properties.Length;
import org.nova.html.properties.Unit;

public class FirstColumnMenuDataTable extends DataTable 
{
	static private DataTableOptions addOptions(DataTableOptions options,Length menuWidth,int target)
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
    
	public FirstColumnMenuDataTable(DataTableOptions options,Length menuWidth,Object...columnNames) 
    {
        super(addOptions(options,menuWidth,0));
        
        TableHeadRow row=new TableHeadRow();
        row.add("");
        row.add(columnNames);
        header().addInner(row);
    }
	
	
	public FirstColumnMenuDataTable(Length menuWidth,Object...columnNames) 
	{
	    this(new DataTableOptions(),menuWidth,columnNames);
	}

	public FirstColumnMenuDataTable(Object...columnNames) 
	{
		this(new Length(1,Unit.em),columnNames);
	}

	public FirstColumnMenuDataTable(DataTableOptions options,Object...columnNames) 
    {
        this(options,new Length(1,Unit.em),columnNames);
    }


}
