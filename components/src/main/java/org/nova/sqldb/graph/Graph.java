package org.nova.sqldb.graph;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.nova.collections.ContentCache;
import org.nova.html.tags.col;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Row;
import org.nova.sqldb.graph.Graph.ColumnAccessor;
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
    
    enum EntityType
    {
        NODE,
        NODE_ATTRIBUTE,
        LINK,
    }
    
    static class EntityMeta
    {
        final private ColumnAccessor[] columnAccessors;
        final private EntityType entityType;
        final private String tableAlias;
        final private String tableName;
        final private String typeName;
        
        EntityMeta(String typeName,EntityType entityType,ColumnAccessor[] columnnAccessors)
        {
            this.entityType=entityType;
            this.columnAccessors=columnnAccessors;
            this.typeName=typeName;
            this.tableAlias='`'+typeName+'`';
            this.tableName="`e_"+typeName+'`';
        }

        String getTableAlias()
        {
            return this.tableAlias;
        }
        String getTableName()
        {
            return this.tableName;
        }
        EntityType getEntityType()
        {
            return this.entityType;
        }
        ColumnAccessor[] getColumnAccessors()
        {
            return this.columnAccessors;
        }
        String getTypeName()
        {
            return this.typeName;
        }
    }
    
    
    EntityMeta getEntityMeta(Class<?> type) throws Exception
    {
        EntityMeta entityMeta=null;
        String typeName=type.getSimpleName();
        synchronized (ENTITY_META_MAP)
        {
            entityMeta= ENTITY_META_MAP.get(typeName);
        }
        if (entityMeta==null)
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
            EntityType entityType;
            if (type.getSuperclass()==NodeEntity.class)
            {
                entityType=EntityType.NODE;
            }
            else if (type.getSuperclass()==LinkEntity.class)
            {
                entityType=EntityType.LINK;
            }
            else
            {
                throw new Exception();
            }
            
            entityMeta = new EntityMeta(typeName,entityType,map.values().toArray(new ColumnAccessor[map.size()]));
            synchronized (ENTITY_META_MAP)
            {
                ENTITY_META_MAP.put(type.getName(), entityMeta);
            }
        }
        return entityMeta;
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
    static class NodeObjectCache extends Cache<NodeEntity>
    {
        NodeObjectCache(Graph graph)
        {
            super(graph);
            // TODO Auto-generated constructor stub
        }


    }

    final private NodeObjectCache cache;
    
    final private HashMap<String,EntityMeta> ENTITY_META_MAP=new HashMap<String, EntityMeta>();
    final private HashMap<String, ColumnAccessor> COLUMN_ACCESSOR_MAP=new HashMap<>();
    final private Connector connector;
    final private HashMap<String,Class<? extends NodeEntity>> linkedMap=new HashMap<String, Class<? extends NodeEntity>>();
    
    public Graph(Connector connector)
    {
        this.connector=connector;
        this.cache=new NodeObjectCache(this);
    }
    public Connector getConnector()
    {
        return this.connector;
    }
    
    public GraphAccess beginAccess(Trace parent,String category,Long creatorId,boolean beginTransaction) throws Throwable
    {
        return new GraphAccess(parent,this,category,creatorId,beginTransaction);
    }

//    public <OBJECT extends NodeObject> OBJECT getNodeObject(Class<OBJECT> type,long nodeId) throws Throwable
//    {
//        return (OBJECT)this.cache.get(new NodeObjectKey(type.getSimpleName(), nodeId));
//    }
    void evict(String typeName,long nodeId)
    {
        this.cache.evict(new NodeObjectKey(typeName,nodeId));
    }
    public void createTable(Trace parent,Class<?> type) throws Throwable
    {
        String table="e_"+type.getSimpleName();
        
        StringBuilder sql=new StringBuilder();
        sql.append("CREATE TABLE `"+table+"` (`_nodeId` bigint NOT NULL,`_createdEventId` bigint NOT NULL,");
        EntityMeta meta=this.getEntityMeta(type);
        for (ColumnAccessor columnAccessor:meta.columnAccessors)
        {
            if (columnAccessor.isGraphfield())
            {
                continue;
            }
            sql.append("`"+columnAccessor.getName()+"` ");
            sql.append(columnAccessor.getSqlType());
            sql.append(",");
        }
        sql.append("PRIMARY KEY (`_nodeId`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");
        
        
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            if (accessor.executeQuery(parent,"createTable","SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?",table,this.database).getRow(0).getBIGINT(0)==0)
            {
                accessor.executeUpdate(parent, "createTable", sql.toString());
            }
        }
    }
//    public String getTableAlias(String typeName)
//    {
//        return '`'+typeName+'`';
//    }
//    public String getTableName(String typeName)
//    {
//        return "`e_"+typeName+'`';
//    }
//    public String getTableName(Class<? extends NodeEntity> type)
//    {
//        return "`e_"+type.getSimpleName()+'`';
//    }
    
//    @SuppressWarnings("unchecked")
//    public Class<? extends NodeEntity> getLinkedName(Class<? extends Link<?>> type) throws ClassNotFoundException
//    {
//        String key=type.getSimpleName();
//        Class<? extends NodeEntity> value=null;
//        synchronized(this.linkedMap)
//        {
//            this.linkedMap.get(key);
//        }
//        if (value==null)
//        {
//            Type generic=type.getGenericSuperclass();
//            ParameterizedType parametized=(ParameterizedType)generic;
//            Type actual=parametized.getActualTypeArguments()[0];
//            String className=actual.getTypeName();
//            value=(Class<? extends NodeEntity>)Class.forName(className);
//            synchronized(this.linkedMap)
//            {
//                this.linkedMap.put(key, value);
//            }
//        }
//        return value;
//    }
    
    
}
