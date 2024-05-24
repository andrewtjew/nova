package org.nova.html.bootstrap.ext.input;

import org.nova.html.bootstrap.classes.BreakPoint;

public class FormCol
{
    final BreakPoint breakPoint;
    final int columns;
    final boolean auto;
    public FormCol(BreakPoint breakPoint,int columns)
    {
        this.breakPoint=breakPoint;
        this.columns=columns;
        this.auto=false;
    }
    public FormCol(BreakPoint breakPoint,boolean auto)
    {
        this.breakPoint=breakPoint;
        this.columns=0;
        this.auto=auto;
    }
    public FormCol(boolean auto)
    {
        this.breakPoint=null;
        this.columns=0;
        this.auto=auto;
    }
    public FormCol(int columns)
    {
        this.breakPoint=null;
        this.columns=columns;
        this.auto=false;
    }
    public FormCol()
    {
        this.breakPoint=null;
        this.columns=0;
        this.auto=false;
    }
    
    
}
