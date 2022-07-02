package org.nova.sqldb.graph;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.nova.collections.ContentCache;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Insert;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.Select;
import org.nova.sqldb.SqlUtils;
import org.nova.sqldb.Transaction;
import org.nova.sqldb.Update;
import org.nova.tracing.Trace;

public class Graph
{

    static abstract public class ColumnAccessor
    {
        final Field field;
        final boolean graphField;
        
        public ColumnAccessor(Field field)
        {
            this.field = field;
            this.graphField=field.getName().startsWith("_");
        }
        
        public abstract void set(Object object,String typeName,Row row) throws Throwable;        
        public void set(Object object,Object value) throws Throwable
        {
            this.field.set(object, value);
        }
        public abstract Object get(Object object) throws Throwable;

        
        public String getName()
        {
            return this.field.getName();
        }
        
        public boolean isGraphField()
        {
            return this.graphField;
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
    
    ColumnAccessor getColumnAccessor(Field field) throws Exception
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
        else if (type == Timestamp.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getTIMESTAMP(getSelectColumnName(typeName)));
                    
                }
            };
        }
        else if (type == byte[].class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getVARBINARY(getSelectColumnName(typeName)));
                    
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
            for (Class<?> interfaceType:type.getInterfaces())
            {
                if (ShortEnummerable.class==interfaceType)
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
                else if (IntegerEnummerable.class==interfaceType)
                {
                    accessor=new ColumnAccessor(field)
                    {
                        @Override
                        public void set(Object object, String typeName, Row row) throws Throwable
                        {
                            Integer value=row.getNullableINTEGER(getSelectColumnName(typeName));
                            Object enumValue=null;
                            if (value!=null)
                            {
                                for (Object enumConstantObject:type.getEnumConstants())
                                {
                                    if (((IntegerEnummerable)enumConstantObject).getValue()==value)
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
                    throw new Exception(type.getName()+" must include an Enumerable interface");
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
    
    ColumnAccessor[] getColumnAccessors(Class<?> type) throws Exception
    {
        ColumnAccessor[] columnAccessors=null;
        synchronized (COLUMN_ACCESSOR_ARRAY_MAP)
        {
            columnAccessors= COLUMN_ACCESSOR_ARRAY_MAP.get(type.getTypeName());
        }
        if (columnAccessors==null)
        {
            HashMap<String, ColumnAccessor> map = new HashMap<String, ColumnAccessor>();
            for (Class<?> c = type; c != null; c = c.getSuperclass())
            {
                for (Field field : c.getDeclaredFields())
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
            }
            columnAccessors = map.values().toArray(new ColumnAccessor[map.size()]);
            synchronized (COLUMN_ACCESSOR_ARRAY_MAP)
            {
                COLUMN_ACCESSOR_ARRAY_MAP.put(type.getName(), columnAccessors);
            }
        }
        return columnAccessors;
    }

    static class NodeObjectKey
    {
        final String typeName;
        final long nodeId;
        
        NodeObjectKey(String typeName,long nodeId)
        {
            this.typeName=typeName;
            this.nodeId=nodeId;
        }

        @Override
        public boolean equals(Object other)
        {
            if ((other instanceof NodeObjectKey)==false)
            {
                return false;
            }
            if (other==this)
            {
                return true;
            }
            NodeObjectKey otherKey=(NodeObjectKey)other;
            return (this.nodeId==otherKey.nodeId)&&(this.typeName.equals(otherKey.typeName));
        }
    }
    
    static class Cache<VALUE> extends ContentCache<NodeObjectKey,VALUE>
    {
        final Graph graph;
        
        Cache(Graph graph)
        {
            this.graph=graph;
        }

        @Override
        protected ValueSize<VALUE> load(Trace parent, NodeObjectKey key) throws Throwable
        {
            return null;
        }
    }
    static class NodeObjectCache extends Cache<NodeObject>
    {
        NodeObjectCache(Graph graph)
        {
            super(graph);
            // TODO Auto-generated constructor stub
        }


    }

    final private NodeObjectCache cache;
    
    final private HashMap<String,ColumnAccessor[]> COLUMN_ACCESSOR_ARRAY_MAP=new HashMap<String, ColumnAccessor[]>();
    final private HashMap<String, ColumnAccessor> COLUMN_ACCESSOR_MAP=new HashMap<>();
    final private Connector connector;
    
    public Graph(Connector connector)
    {
        this.connector=connector;
        this.cache=new NodeObjectCache(this);
    }
    public Connector getConnector()
    {
        return this.connector;
    }
    
    public GraphTransaction beginTransaction(Trace parent,String category,long creatorId,boolean atomic) throws Throwable
    {
        return new GraphTransaction(parent,this,category,creatorId,atomic);
    }

    public <OBJECT extends NodeObject> OBJECT getNodeObject(Class<OBJECT> type,long nodeId) throws Throwable
    {
        return (OBJECT)this.cache.get(new NodeObjectKey(type.getSimpleName(), nodeId));
    }
    void evict(String typeName,long nodeId)
    {
        this.cache.evict(new NodeObjectKey(typeName,nodeId));
    }
    
}
