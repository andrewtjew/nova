package org.nova.html.widgets;

import java.io.OutputStream;

import org.nova.html.elements.Element;
import org.nova.html.elements.InnerElement;
import org.nova.html.tags.table;
import org.nova.html.tags.td;
import org.nova.html.tags.tr;


public class TwoColumnTable extends Table
{
    final private td seperator;
    
    public TwoColumnTable(String seperator)
    {
        this.seperator=seperator!=null?new td().addInner(seperator):null;
    }
    
    public void addItems(Element first,Element second)
    {
        this.addBodyRow(new tr().addInners(new td().addInner(first),this.seperator,new td().addInner(second)));
    }
    public void addItems(Object first,Element second)
    {
        this.addBodyRow(new tr().addInners(new td().addInner(first),this.seperator,new td().addInner(second)));
    }
    public void addItems(Element first,Object second)
    {
        this.addBodyRow(new tr().addInners(new td().addInner(first),this.seperator,new td().addInner(second)));
    }
    public void addItems(Object first,Object second)
    {
        this.addBodyRow(new tr().addInners(new td().addInner(first),this.seperator,new td().addInner(second)));
    }
    public void addSecond(Element second,boolean noSeperator)
    {
        tr tr=new tr().addInner(new td());
        if (this.seperator!=null)
        {
            if (noSeperator)
            {
                tr.addInner(new td());
            }
            else
            {
                tr.addInner(this.seperator);
            }
        }
        tr.addInner(new td().addInner(second));
        this.addBodyRow(tr);
    }
    public void addFirstItem(Element first,boolean noSeperator)
    {
        tr tr=new tr().addInner(new td().addInner(first));
        if (this.seperator!=null)
        {
            if (noSeperator)
            {
                tr.addInner(new td());
            }
            else
            {
                tr.addInner(this.seperator);
            }
        }
        tr.addInner(new td());
        this.addBodyRow(tr);
    }
    

}