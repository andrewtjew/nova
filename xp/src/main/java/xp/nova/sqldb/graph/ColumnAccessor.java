package xp.nova.sqldb.graph;

import java.lang.reflect.Field;

import org.nova.sqldb.Row;

abstract class ColumnAccessor
{
    final Field field;
    final boolean graphField;
    
    public ColumnAccessor(Field field)
    {
        this.field = field;
        this.graphField=field.getAnnotation(GraphField.class)!=null;
    }
    
    public abstract void set(Object object,String typeName,Row row) throws Throwable;        
    public void set(Object object,Object value) throws Throwable
    {
        this.field.set(object, value);
    }
    public boolean isGraphfield()
    {
        return this.graphField;
    }

    public abstract Object get(Object object) throws Throwable;
    public abstract String getSqlType() throws Throwable;

    
    public String getName()
    {
        return this.field.getName();
    }
    
    protected String getColumnName(String typeName)
    {
        if (typeName!=null)
        {
            return typeName+'.'+this.field.getName();
        }
        return this.field.getName();
    }
    
}