package xp.nova.sqldb.graph;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.nova.collections.ContentCache;
import org.nova.html.tags.col;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Row;
import org.nova.tracing.Trace;

import xp.nova.sqldb.graph.Graph.ColumnAccessor;

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
        else if (type == LocalTime.class)
        {
            accessor=new ColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    Time value=row.getTIME(getColumnName(typeName));
                    if (value!=null)
                    {
                        field.set(object,value.toLocalTime());
                    }
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "time NULL DEFAULT NULL";
                }

                @Override
                public Object get(Object object) throws Throwable
                {
                    Object value=field.get(object);
                    if (value==null)
                    {
                        return null;
                    }
                    return Time.valueOf((LocalTime)value);
                }
            };
        }
        else if (type == LocalDate.class)
        {
            accessor=new ColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    Date value=row.getDATE(getColumnName(typeName));
                    if (value!=null)
                    {
                        field.set(object,value.toLocalDate());
                    }
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "date NULL DEFAULT NULL";
                }

                @Override
                public Object get(Object object) throws Throwable
                {
                    Object value=field.get(object);
                    if (value==null)
                    {
                        return null;
                    }
                    return Date.valueOf((LocalDate)value);
                }
            };
        }
        else if (type == LocalDateTime.class)
        {
            accessor=new ColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    Timestamp value=row.getTIMESTAMP(getColumnName(typeName));
                    if (value!=null)
                    {
                        field.set(object,value.toLocalDateTime());
                    }
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "timestamp NULL DEFAULT NULL";
                }

                @Override
                public Object get(Object object) throws Throwable
                {
                    Object value=field.get(object);
                    if (value==null)
                    {
                        return null;
                    }
                    return Timestamp.valueOf((LocalDateTime)value);
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
    
    enum GraphObjectType
    {
        NODE_OBJECT,
        LINK_OBJECT,
    }
    
    static class Meta
    {
        final private ColumnAccessor[] columnAccessors;
        final private GraphObjectType entityType;
        final private String tableAlias;
        final private String tableName;
        final private String typeName;
        final private Class<? extends GraphObject> type;
        Meta(String typeName,Class<? extends GraphObject> type,GraphObjectType entityType,ColumnAccessor[] columnnAccessors)
        {
            this.type=type;
            this.entityType=entityType;
            this.columnAccessors=columnnAccessors;
            this.typeName=typeName;
            this.tableAlias='`'+typeName+'`';
            this.tableName='`'+typeName+'`';
        }

        Class<? extends GraphObject> getType()
        {
            return this.type;
        }
        String getTableAlias()
        {
            return this.tableAlias;
        }
        String getTableName()
        {
            return this.tableName;
        }
        GraphObjectType getObjectType()
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
    
    
    Meta getMeta(Class<? extends GraphObject> type) throws Exception
    {
        Meta meta=null;
        String typeName=type.getSimpleName();
        synchronized (ENTITY_META_MAP)
        {
            meta= ENTITY_META_MAP.get(typeName);
        }
        if (meta==null)
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
            GraphObjectType objectType;
            if (type.getSuperclass()==NodeObject.class)
            {
                objectType=GraphObjectType.NODE_OBJECT;
            }
            else if (type.getSuperclass()==LinkObject.class)
            {
                objectType=GraphObjectType.LINK_OBJECT;
            }
            else
            {
                throw new Exception(type.getName()+" needs extend from a subclass of GraphObject.");
            }
            
            meta = new Meta(typeName,type,objectType,map.values().toArray(new ColumnAccessor[map.size()]));
            synchronized (ENTITY_META_MAP)
            {
                ENTITY_META_MAP.put(type.getName(), meta);
            }
        }
        return meta;
    }
    
    final private HashMap<String,Meta> ENTITY_META_MAP=new HashMap<String, Meta>();
    final private HashMap<String, ColumnAccessor> COLUMN_ACCESSOR_MAP=new HashMap<>();
    final private Connector connector;
    
    public Graph(Connector connector)
    {
        this.connector=connector;
    }
    public Connector getConnector()
    {
        return this.connector;
    }
    
    public GraphAccess openAccess(Trace parent,String category,Long creatorId,boolean beginTransaction) throws Throwable
    {
        return new GraphAccess(parent,this,category,creatorId,beginTransaction);
    }
    public void createTable(Trace parent,Class<? extends GraphObject> type) throws Throwable
    {
        String table=type.getSimpleName();
        
        StringBuilder sql=new StringBuilder();
        sql.append("CREATE TABLE `"+table+"` (`_nodeId` bigint NOT NULL,`_eventId` bigint NOT NULL,");
        Meta meta=this.getMeta(type);
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
    
}
