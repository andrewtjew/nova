package org.nova.html.bootstrap.ext.DataTables;

import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.bootstrap.TableHeadRow;
import org.nova.html.properties.Length_;
import org.nova.html.properties.Unit_;

public class LastColumnMenuDataTable extends DataTable 
{
	static private DataTableOptions addOptions(DataTableOptions options,Length_ menuWidth,int target)
	{
	    ColumnDef menuColumnDefs=new ColumnDef(target);
	    menuColumnDefs.orderable=false;
	    menuColumnDefs.searchable=false;
	    menuColumnDefs.width(menuWidth);
	    options.columnDefs=new ColumnDef[] {menuColumnDefs};
	    return options;
	}
    
	private LastColumnMenuDataTable(DataTableOptions options,Length_ menuWidth,Object...columnNames) throws Throwable 
    {
        super(addOptions(options,menuWidth,columnNames.length));
        
        TableHeadRow row=new TableHeadRow();
        row.add(columnNames);
        row.add("");
        header().addInner(row);
    }
	
	
	public LastColumnMenuDataTable(Length_ menuWidth,Object...columnNames) throws Throwable 
	{
	    this(new DataTableOptions(),menuWidth,columnNames);
	}

	public LastColumnMenuDataTable(Object...columnNames) throws Throwable 
	{
		this(new Length_(1,Unit_.em),columnNames);
	}

	public LastColumnMenuDataTable(DataTableOptions options,Object...columnNames) throws Throwable 
    {
        this(options,new Length_(1,Unit_.em),columnNames);
    }


}
