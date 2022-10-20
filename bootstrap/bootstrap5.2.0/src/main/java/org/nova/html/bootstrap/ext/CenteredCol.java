package org.nova.html.bootstrap.ext;

import org.nova.html.bootstrap.Col;
import org.nova.html.bootstrap.Container;
import org.nova.html.bootstrap.Row;
import org.nova.html.bootstrap.StyleComponent;
import org.nova.html.bootstrap.classes.BreakPoint;
import org.nova.html.elements.Composer;


public class CenteredCol extends StyleComponent<CenteredCol>
{
    final private Integer sideCol;
    final private BreakPoint breakPoint;
    public CenteredCol(BreakPoint breakPoint,int sideCol)
    {
        super("div","col");
        addClass("col",breakPoint);
        this.sideCol=sideCol;
        this.breakPoint=breakPoint;

    }
    public CenteredCol(int sideCol)
    {
        super("div","col");
        this.sideCol=sideCol;
        this.breakPoint=null;
    }
    public CenteredCol(BreakPoint breakPoint)
    {
        super("div","col");
        addClass("col",breakPoint);
        this.sideCol=1;
        this.breakPoint=breakPoint;
    }
    public CenteredCol()
    {
        super("div","col");
        this.sideCol=1;
        this.breakPoint=null;
    }
    
    boolean composed=false;
    
    @Override
    public void compose(Composer composer) throws Throwable
    {
        if (composed)
        {
            super.compose(composer);
            return;
        }
        composed=true;
        Container container=new Container(true);
        Row row=container.returnAddInner(new Row());
        if (this.breakPoint!=null)
        {
            row.addInner(new Col(this.breakPoint,this.sideCol));
            row.addInner(this);
            row.addInner(new Col(this.breakPoint,this.sideCol));
        }
        else
        {
            row.addInner(new Col(this.sideCol));
            row.addInner(this);
            row.addInner(new Col(this.sideCol));
        }
        composer.compose(container);
        
    }

}
