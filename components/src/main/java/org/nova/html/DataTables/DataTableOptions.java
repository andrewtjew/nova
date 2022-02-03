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
package org.nova.html.DataTables;

public class DataTableOptions
{
    static public class Paginate
    {       
        public String previous;
        public String next;
    }
    static public class Language
    {
        public Paginate paginate;
    }
    
    public void lengthMenu(Object...lengths)
    {
        Object[] labels=new Object[lengths.length];
        int[] integerLengths=new int[lengths.length];

        boolean labelsNeeded=false;
        for (int i=0;i<labels.length;i++)
        {
            Object length=lengths[i];
            labels[i]=lengths[i];
            if (length==null)
            {
                integerLengths[i]=-1;
                labelsNeeded=true;
            }
            else if (length instanceof Integer)
            {
                integerLengths[i]=(Integer)length;
            }
            else if (length instanceof Long)
            {
                integerLengths[i]=((Long)length).intValue();
            }
            else if (length instanceof Short)
            {
                integerLengths[i]=(Short)length;
            }
            else
            {
                integerLengths[i]=-1;
                labelsNeeded=true;
            }
        }
        if (labelsNeeded)
        {
            this.lengthMenu=new Object[]{integerLengths,lengths};
        }
        else
        {
            this.lengthMenu=lengths;
        }
    }
    public void order(DataTableColumnOrder...orders)
    {
        Object[][] objects=new Object[orders.length][];
        for (int i=0;i<orders.length;i++)
        {
            DataTableColumnOrder order=orders[i];
        
            objects[i]=new Object[]{order.column,order};
        }
        this.order=objects;
    }
    /*
    public void  column(Column...columns)
    {
        this.columns=columns;
    }
    public void  columnDefs(Column...columns)
    {
        this.columns=columns;
    }
    */
    
    
    public Boolean autoWidth;
    public Boolean deferRender;
    public Boolean info;
    public Boolean lengthChange;
    public Boolean ordering;
    public Boolean paging;
    public Boolean processing;
    public Boolean scrollX;
    public Boolean scrollY;
    public Boolean searching;
    public Boolean serverSide;
    public Boolean stateSave;

    public Object deferLoading;
    public Boolean destroy;
    public Integer displayStart;
    public Object dom;
    public Object lengthMenu;
    public  Object order;
    public Boolean orderCellsTop;
    public Boolean orderClasses;
//    public orderFixedSince;
//    public orderMultiSince;
    public Integer pageLength;
//    public pagingTypeSince
    //public renderer
    public Boolean retrieve;
    public String rowId;
    public Boolean scrollCollapse;
//    public search
    //public searchCols
    public Integer searchDelay;
    public Column[] columns;
    public Column[] columnDefs;
    public Integer stateDuration;
//    public stripeClasses
    
    public Integer tabIndex;
    public Language language;
    
}
