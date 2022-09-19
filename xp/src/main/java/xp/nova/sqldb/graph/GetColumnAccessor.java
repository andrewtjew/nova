package xp.nova.sqldb.graph;

import java.lang.reflect.Field;

abstract class GetColumnAccessor extends ColumnAccessor
{
    public GetColumnAccessor(Field field)
    {
        super(field);
      }

    @Override
    public Object get(Object object) throws Throwable
    {
        return field.get(object);
    }
}