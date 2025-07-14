package org.nova.html.bootstrap.ext.DataTables;

import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.bootstrap.TableHeadRow;
import org.nova.html.properties.Length;
import org.nova.html.properties.Unit;
import org.nova.html.properties.width;

public class LastColumnMenuDataTable extends DataTable 
{
	static private DataTableOptions addOptions(DataTableOptions options,width menuWidth,int target)
	{
	    ColumnDef menuColumnDefs=new ColumnDef(target);
	    menuColumnDefs.orderable=false;
	    menuColumnDefs.searchable=false;
	    menuColumnDefs.width(menuWidth);
	    options.columnDefs=new ColumnDef[] {menuColumnDefs};
	    return options;
	}
    
	private LastColumnMenuDataTable(DataTableOptions options,width menuWidth,Object...columnNames) 
    {
        super(addOptions(options,menuWidth,columnNames.length));
        
        TableHeadRow row=new TableHeadRow();
        row.add(columnNames);
        row.add("");
        header().addInner(row);
    }
	
	
	public LastColumnMenuDataTable(width menuWidth,Object...columnNames) 
	{
	    this(new DataTableOptions(),menuWidth,columnNames);
	}

	public LastColumnMenuDataTable(Object...columnNames) 
	{
		this(new width(1,Unit.em),columnNames);
	}

	public LastColumnMenuDataTable(DataTableOptions options,Object...columnNames) 
    {
        this(options,new width(1,Unit.em),columnNames);
    }


}
