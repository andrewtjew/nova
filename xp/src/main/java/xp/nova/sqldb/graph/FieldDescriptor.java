package xp.nova.sqldb.graph;

import java.lang.reflect.Field;

import org.nova.sqldb.Row;

abstract public class FieldDescriptor
{
    final Field field;
    final boolean internal;
    
    public FieldDescriptor(Field field)
    {
        this.field = field;
        this.internal=field.getAnnotation(Internal.class)!=null;
    }
    
    public abstract void set(Object object,String typeName,Row row) throws Throwable;        
    public void set(Object object,Object value) throws Throwable
    {
        this.field.set(object, value);
    }
    public boolean isInternal()
    {
        return this.internal;
    }
    
    public abstract Object get(Object object) throws Throwable;
    public abstract SqlType getSqlType() throws Throwable;

    
    public String getName()
    {
        return this.field.getName();
    }
    public Field getField()
    {
        return this.field;
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