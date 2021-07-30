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
package org.nova.html.deprecated;

import java.util.ArrayList;

import org.nova.core.NameObject;
import org.nova.html.DataTables.DataTableColumnOrder;
import org.nova.html.elements.Composer;
import org.nova.html.elements.Element;
import org.nova.html.ext.Head;
import org.nova.html.tags.script;
import org.nova.json.ObjectMapper;

//!!! Requires jquery

public class OperatorDataTable extends Table
{
    static class Objects extends Element
    {
        final private ArrayList<NameObject> list;
        final String id;
        
        public Objects(String id)
        {
            this.id=id;
            this.list=new ArrayList<>();
        }
        public void add(String name,Object object)
        {
            this.list.add(new NameObject(name, object));
        }
        
        @Override
        public String toString()
        {
            try
            {
                StringBuilder sb=new StringBuilder();
                sb.append("$(document).ready(function(){$('#").append(this.id).append("').DataTable(");
                boolean commaNeeded=false;
                sb.append('{');
                for (NameObject item:this.list)
                {
                    if (commaNeeded)
                    {
                        sb.append(',');
                    }
                    else
                    {
                        commaNeeded=true;
                    }
                    sb.append('"'+item.getName()+"\":");
                    Object value=item.getValue();
                    if (value==null)
                    {
                        sb.append("null");
                        continue;
                    }
                    Class<?> type=value.getClass();
                    if (type==Boolean.class)
                    {
                        sb.append(value);
                        
                    }
                    else if (type==Float.class)
                    {
                        sb.append(value);
                        
                    }
                    else if (type==Double.class)
                    {
                        sb.append(value);
                        
                    }
                    else if (type==Short.class)
                    {
                        sb.append(value);
                        
                    }
                    else if (type==Integer.class)
                    {
                        sb.append(value);
                        
                    }
                    else if (type==Long.class)
                    {
                        sb.append(value);
                        
                    }
                    else if (type==String.class)
                    {
                        //Properly do this the JSON way
                        sb.append('"');
                        sb.append(value);
                        sb.append('"');
                    }
                    else
                    {
//                        sb.append("{'"+item.getName()+"':"+ObjectMapper.write(item.getValue())+"}");
                        sb.append(ObjectMapper.writeObjectToString(value));
                    }
                }
                sb.append('}');
                sb.append(");});");
                return sb.toString();
            }
            catch (Throwable t)
            {
                return t.getMessage();
            }
        }
        @Override
        public void compose(Composer composer) throws Throwable
        {
            composer.getStringBuilder().append(toString());
        }
    }
    
    final private Objects objects;
    
    public OperatorDataTable(Head head)
    {
        this(head,"display");
    }
    
    public OperatorDataTable(Head head,String class_)
    {
        this(head,null,class_,"/resources/html/widgets/DataTable/datatables.min.css","/resources/html/widgets/DataTable/datatables.min.js");
    }
    
    public OperatorDataTable(Head head,String id,String class_,String cssFilePath,String scriptFilePath)
    {
        super(head,class_,cssFilePath);
        this.table().id(id);
        id=this.table().id();
        this.objects=new Objects(id);
        this.table().addInner(new script().addInner(this.objects));
        script script=new script().src(scriptFilePath);
        if (head!=null)
        {
            head.add(OperatorDataTable.class.getCanonicalName(),script);
        }
        else
        {
            this.table().addInner(script);
        }
    }

    public void lengthMenu(int...lengths)
    {
        String[] labels=new String[lengths.length];
        for (int i=0;i<labels.length;i++)
        {
            if (lengths[i]>=0)
            {
                labels[i]=Integer.toString(lengths[i]);
            }
            else
            {
                labels[i]="All";
            }
        }
        lengthMenu(lengths,labels);
    }

    public void order(DataTableColumnOrder...orders)
    {
        Object[][] objects=new Object[orders.length][];
        for (int i=0;i<orders.length;i++)
        {
            DataTableColumnOrder order=orders[i];
        
            objects[i]=new Object[]{order.column,order};
        }
        this.objects.add("order", objects);
    }

    static class Orderable
    {
        boolean orderable=false;
        int targets;
    }
    
    public void disableOrderables(int...columns)
    {
        Orderable[] orderables=new Orderable[columns.length];
        for (int i=0;i<columns.length;i++)
        {
            Orderable orderable=new Orderable();
            orderable.targets=columns[i];
            orderables[i]=orderable;
        }
        this.objects.add("columnDefs", orderables);
    }
    
    
    public void lengthMenu(int[] lengths,String[] labels)
    {
        this.objects.add("lengthMenu", new Object[]{lengths,labels});
    }
    public void paginate(boolean on)
    {
        this.objects.add("bPaginate",on);
    }
    public void filter(boolean on)
    {
        this.objects.add("bFilter",on);
    }

    public void info(boolean on)
    {
        this.objects.add("bInfo",on);
    }
    public void sort(boolean on)
    {
        this.objects.add("bSort",on);
    }

    public void addObject(String name,Object object)
    {
        this.objects.add(name, object);
    }
    
}
