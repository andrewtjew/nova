package org.nova.sqldb.graph;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.HashMap;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.SqlUtils;
import org.nova.sqldb.Transaction;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public class GraphTransaction implements AutoCloseable
{
    final private Transaction transaction;
    final private Accessor accessor;
    final private Trace trace;
    final private Long creatorId;

    private Long eventId;
    
    private GraphTransaction(Trace parent,String category,Connector connector,Long creatorId) throws Throwable
    {
        this.trace=new Trace(parent, category);
        this.accessor=connector.openAccessor(parent);
        this.transaction=this.accessor.beginTransaction(category);
        this.creatorId=creatorId;
    }
    public GraphTransaction(Trace parent,String category,Connector connector,long creatorId) throws Throwable
    {
        this(parent,category,connector,(Long)creatorId);
    }
    public GraphTransaction(Trace parent,String category,Connector connector) throws Throwable
    {
        this(parent,category,connector,null);
    }
    
    public long getEventId() throws Throwable
    {
        if (this.creatorId==null)
        {
            throw new Exception("Graph instance not created in update mode");
        }
        if (this.eventId==null)
        {
            this.eventId=this.accessor.executeUpdateAndReturnGeneratedKeys(this.trace,"getEventId"
                    ,"INSERT INTO _event (creatorId,created,source) VALUES(?,?,?)"
                    ,this.creatorId,SqlUtils.now(),this.trace.getCategory()
                    ).getAsLong(0);
        }
        return this.eventId;
    }
    
    public void commit() throws Exception
    {
        this.transaction.commit();
    }
    
    
    @Override
    public void close() throws Exception
    {
        this.transaction.close();
        this.accessor.close();
        this.trace.close();
    }
    
    public Node createNode() throws Throwable
    {
        long eventId=this.getEventId();
        
        long id=this.accessor.executeUpdateAndReturnGeneratedKeys(trace,"createNode"
                ,"INSERT INTO _node (createdEventId) VALUES(?)"
                ,eventId).getLong(0);
        return new Node(this,id);
    }
    
    
    final Accessor getAccessor()
    {
        return this.accessor;
    }
    final Trace getTrace()
    {
        return this.trace;
    }
    
    static final private HashMap<String,ColumnAccessor[]> COLUMN_ACCESSOR_ARRAY_MAP=new HashMap<String, ColumnAccessor[]>();
    static final private HashMap<String, ColumnAccessor> COLUMN_ACCESSOR_MAP=new HashMap<>();

    static abstract public class ColumnAccessor
    {
        final Field field;
        
        public ColumnAccessor(Field field)
        {
            this.field = field;
        }
        
        public abstract void set(Object object,String typeName,Row row) throws Throwable;        
        public abstract Object get(Object object) throws Throwable;

        
        public String getName()
        {
            return this.field.getName();
        }
        
        protected String getSelectColumnName(String typeName)
        {
            if (typeName!=null)
            {
                return typeName+'.'+this.field.getName();
            }
            return this.field.getName();
        }
        
    }

    static abstract public class DefaultGetColumnAccessor extends ColumnAccessor
    {
        public DefaultGetColumnAccessor(Field field)
        {
            super(field);
          }

        @Override
        public Object get(Object object) throws Throwable
        {
            return field.get(object);
        }
    }
   
    
    
    static ColumnAccessor getColumnAccessor(Field field) throws Exception
    {
        ColumnAccessor accessor;
        synchronized(COLUMN_ACCESSOR_MAP)
        {
            accessor=COLUMN_ACCESSOR_MAP.get(field.getName());
        }
        if (accessor!=null)
        {
            return accessor;
        }
        Class<?> type=field.getType();
        if (type == String.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getVARCHAR(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == Boolean.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableBIT(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == boolean.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getBIT(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == int.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getINTEGER(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == Integer.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableINTEGER(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == long.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getBIGINT(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == Long.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableBIGINT(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == float.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getREAL(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == Float.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableREAL(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == double.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getFLOAT(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == Double.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableFLOAT(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == short.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getSMALLINT(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == Short.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableSMALLINT(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type.isEnum())
        {
            for (Class<?> i:type.getInterfaces())
            {
                if (ShortEnummerable.class==i)
                {
                    accessor=new ColumnAccessor(field)
                    {
                        @Override
                        public void set(Object object, String typeName, Row row) throws Throwable
                        {
                            Short value=row.getNullableSMALLINT(getSelectColumnName(typeName));
                            Object enumValue=null;
                            if (value!=null)
                            {
                                for (Object enumConstantObject:type.getEnumConstants())
                                {
                                    if (((ShortEnummerable)enumConstantObject).getValue()==value)
                                    {
                                        enumValue=enumConstantObject;
                                        break;
                                    }
                                }
                            }
                            field.set(object, enumValue);
                        }

                        @Override
                        public Object get(Object object) throws Throwable
                        {
                            
                            object=field.get(object);
                            if (object==null)
                            {
                                return null;
                            }
                            return ((ShortEnummerable)object).getValue();
                        }
                    };
                }
                else
                {
                    throw new Exception();
                }
            }
        }
        else if (type == BigDecimal.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getDECIMAL(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else
        {
            throw new Exception();
        }
        synchronized(COLUMN_ACCESSOR_MAP)
        {
            COLUMN_ACCESSOR_MAP.put(type.getName(), accessor);
        }
        return accessor;
    }
    

    static ColumnAccessor[] getColumnAccessors(Class<?> type) throws Exception
    {
        ColumnAccessor[] columnAccessors=null;
        synchronized (COLUMN_ACCESSOR_ARRAY_MAP)
        {
            columnAccessors= COLUMN_ACCESSOR_ARRAY_MAP.get(type.getTypeName());
        }
        if (columnAccessors==null)
        {
            HashMap<String, ColumnAccessor> map = new HashMap<String, ColumnAccessor>();
            for (Field field : type.getDeclaredFields())
            {
                int modifiers = field.getModifiers();
                if (Modifier.isTransient(modifiers))
                {
                    continue;
                }
                if (Modifier.isStatic(modifiers))
                {
                    continue;
                }
                if (map.containsKey(field.getName()) == false)
                {
                    try
                    {
                        field.setAccessible(true);
                    }
                    catch (Throwable t)
                    {
                        continue;
                    }
                    ColumnAccessor accessor=getColumnAccessor(field);
                    map.put(field.getName(), accessor);
                }
            }
            columnAccessors = map.values().toArray(new ColumnAccessor[map.size()]);
            synchronized (COLUMN_ACCESSOR_ARRAY_MAP)
            {
                COLUMN_ACCESSOR_ARRAY_MAP.put(type.getName(), columnAccessors);
            }
        }
        return columnAccessors;
    }
    
}
