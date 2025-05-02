package org.nova.html.bootstrap.ext.DataTables;

import org.nova.html.DataTables.ColumnDef;
import org.nova.html.DataTables.DataTableOptions;
import org.nova.html.bootstrap.TableHeadRow;
import org.nova.html.properties.Size;
import org.nova.html.properties.unit;

public class LastColumnMenuDataTable extends DataTable 
{
	static private DataTableOptions addOptions(DataTableOptions options,Size menuWidth,int target)
	{
	    ColumnDef menuColumnDefs=new ColumnDef(target);
	    menuColumnDefs.orderable=false;
	    menuColumnDefs.searchable=false;
	    menuColumnDefs.width(menuWidth);
	    options.columnDefs=new ColumnDef[] {menuColumnDefs};
	    return options;
	}
    
	private LastColumnMenuDataTable(DataTableOptions options,Size menuWidth,Object...columnNames) 
    {
        super(addOptions(options,menuWidth,columnNames.length));
        
        TableHeadRow row=new TableHeadRow();
        row.add(columnNames);
        row.add("");
        header().addInner(row);
    }
	
	
	public LastColumnMenuDataTable(Size menuWidth,Object...columnNames) 
	{
	    this(new DataTableOptions(),menuWidth,columnNames);
	}

	public LastColumnMenuDataTable(Object...columnNames) 
	{
		this(new Size(1,unit.em),columnNames);
	}

	public LastColumnMenuDataTable(DataTableOptions options,Object...columnNames) 
    {
        this(options,new Size(1,unit.em),columnNames);
    }


}
