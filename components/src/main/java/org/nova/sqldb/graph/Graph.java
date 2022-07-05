package org.nova.sqldb.graph;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

import org.nova.collections.ContentCache;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Row;
import org.nova.tracing.Trace;

public class Graph
{
    private int defaultVARCHARLength=45;
    private String database="graph";
    
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
        public abstract String getSqlType() throws Throwable;

        
        public String getName()
        {
            return this.field.getName();
        }
        
        public boolean isGraphField()
        {
            return this.graphField;
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
        ColumnAccessor accessor=null;
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
                    field.set(object,row.getVARCHAR(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    int value=defaultVARCHARLength;
                    Length length=this.field.getDeclaredAnnotation(Length.class);
                    if (length!=null)
                    {
                        value=length.value();
                    }
                    return "varchar("+value+") DEFAULT NULL";
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
                    field.set(object,row.getNullableBIT(getColumnName(typeName)));
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "bit(1) DEFAULT NULL";
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
                    field.set(object,row.getBIT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "bit(1) NOT NULL";
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
                    field.set(object,row.getINTEGER(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "int NOT NULL";
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
                    field.set(object,row.getNullableINTEGER(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "int DEFAULT NULL";
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
                    field.set(object,row.getBIGINT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "bigint NOT NULL";
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
                    field.set(object,row.getNullableBIGINT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "bigint DEFAULT NULL";
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
                    field.set(object,row.getREAL(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "real NOT NULL";
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
                    field.set(object,row.getNullableREAL(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "real DEFAULT NULL";
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
                    field.set(object,row.getFLOAT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "float NOT NULL";
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
                    field.set(object,row.getNullableFLOAT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "real DEFAULT NULL";
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
                    field.set(object,row.getSMALLINT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "smallint NOT NULL";
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
                    field.set(object,row.getTIMESTAMP(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "timestamp NULL DEFAULT NULL";
                }
            };
        }
        else if (type == Date.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getDATE(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "date NULL DEFAULT NULL";
                }
            };
        }
        else if (type == Time.class)
        {
            accessor=new DefaultGetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getTIME(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "time NULL DEFAULT NULL";
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
                    field.set(object,row.getVARBINARY(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    int value=defaultVARCHARLength;
                    Length length=this.field.getDeclaredAnnotation(Length.class);
                    if (length!=null)
                    {
                        value=length.value();
                    }
                    return "varbinary("+value+") DEFAULT NULL";
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
                    field.set(object,row.getNullableSMALLINT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "smallint DEFAULT NULL";
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
                            Short value=row.getNullableSMALLINT(getColumnName(typeName));
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

                        @Override
                        public String getSqlType() throws Throwable
                        {
                            return "smallint DEFAULT NULL";
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
                            Integer value=row.getNullableINTEGER(getColumnName(typeName));
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

                        @Override
                        public String getSqlType() throws Throwable
                        {
                            return "int DEFAULT NULL";
                        }
                    };
                }
            }
            if (accessor==null)
            {
                throw new Exception(type.getName()+" must include an Enumerable interface");
            }
        }
//        else if (type == BigDecimal.class)
//        {
//            accessor=new DefaultGetColumnAccessor(field)
//            {
//                @Override
//                public void set(Object object, String typeName, Row row) throws Throwable
//                {
//                    field.set(object,row.getDECIMAL(getSelectColumnName(typeName)));
//                    
//                }
//
//                @Override
//                public String getSqlType() throws Throwable
//                {
//                    return "`decimal` DEFAULT NULL";
//                }
//            };
//        }
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
    final private HashMap<String,Class<? extends NodeObject>> linkedMap=new HashMap<String, Class<? extends NodeObject>>();
    
    public Graph(Connector connector)
    {
        this.connector=connector;
        this.cache=new NodeObjectCache(this);
    }
    public Connector getConnector()
    {
        return this.connector;
    }
    
    public GraphTransaction beginTransaction(Trace parent,String category,Long creatorId,boolean atomic) throws Throwable
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
    public void createTable(Trace parent,Class<? extends NodeObject> type) throws Throwable
    {
        String table=type.getSimpleName();
        
        
        StringBuilder sql=new StringBuilder();
        sql.append("CREATE TABLE `"+table+"` (`_id` bigint NOT NULL AUTO_INCREMENT,`_nodeId` bigint NOT NULL,`_createdEventId` bigint NOT NULL,`_retiredEventId` bigint DEFAULT NULL,`_retired` timestamp DEFAULT NULL,");
        ColumnAccessor[] columnAccessors=this.getColumnAccessors(type);
        for (ColumnAccessor columnAccessor:columnAccessors)
        {
            if (columnAccessor.isGraphField())
            {
                continue;
            }
            sql.append("`"+columnAccessor.getName()+"` ");
            sql.append(columnAccessor.getSqlType());
            sql.append(",");
        }
        sql.append("PRIMARY KEY (`_id`)) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");
        
        
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            if (accessor.executeQuery(parent,"createTable","SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?",table,this.database).getRow(0).getBIGINT(0)==0)
            {
                accessor.executeUpdate(parent, "createTable", sql.toString());
            }
        }
    }
    public String getTableName(Class<? extends NodeObject> type)
    {
        return '`'+type.getSimpleName()+'`';
    }
    
    @SuppressWarnings("unchecked")
    public Class<? extends NodeObject> getLinkedName(Class<? extends LinkedNodeObject<?>> type) throws ClassNotFoundException
    {
        String key=type.getSimpleName();
        Class<? extends NodeObject> value=null;
        synchronized(this.linkedMap)
        {
            this.linkedMap.get(key);
        }
        if (value==null)
        {
            Type generic=type.getGenericSuperclass();
            ParameterizedType parametized=(ParameterizedType)generic;
            Type actual=parametized.getActualTypeArguments()[0];
            String className=actual.getTypeName();
            value=(Class<? extends NodeObject>)Class.forName(className);
            synchronized(this.linkedMap)
            {
                this.linkedMap.put(key, value);
            }
        }
        return value;
    }
}
