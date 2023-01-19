 package xp.nova.sqldb.graph;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.testing.Testing;
import org.nova.tracing.Trace;
import org.nova.utils.TypeUtils;

public class Graph
{
    static final boolean TEST=true;
    
    private int defaultVARCHARLength=45;
    
    FieldDescriptor getColumnAccessor(Field field) throws Exception
    {
        FieldDescriptor descriptor=null;
        synchronized(columnAccessorMap)
        {
            descriptor=columnAccessorMap.get(field.getName());
        }
        if (descriptor!=null)
        {
            return descriptor;
        }
        Class<?> type=field.getType();
        if (type == String.class)
        {
            descriptor=new GetColumnAccessor(field)
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
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableBIT(getColumnName(typeName)));
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "bit DEFAULT NULL";
                }
            };
        }
        else if (type == boolean.class)
        {
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getBIT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "bit NOT NULL";
                }
            };
        }
        else if (type == int.class)
        {
            descriptor=new GetColumnAccessor(field)
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
            descriptor=new GetColumnAccessor(field)
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
            descriptor=new GetColumnAccessor(field)
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
            descriptor=new GetColumnAccessor(field)
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
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getREAL(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "float NOT NULL";
                }
            };
        }
        else if (type == Float.class)
        {
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableREAL(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "float DEFAULT NULL";
                }
            };
        }
        else if (type == double.class)
        {
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getFLOAT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "double NOT NULL";
                }
            };
        }
        else if (type == Double.class)
        {
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getNullableFLOAT(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "double DEFAULT NULL"; //Using mysql native convention. 
                }
            };
        }
        else if (type == short.class)
        {
            descriptor=new GetColumnAccessor(field)
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
            descriptor=new FieldDescriptor(field)
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
                    return "time DEFAULT NULL";
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
            descriptor=new FieldDescriptor(field)
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
                    return "date DEFAULT NULL";
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
            descriptor=new FieldDescriptor(field)
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
                    return "timestamp DEFAULT NULL";
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
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getTIMESTAMP(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "timestamp DEFAULT NULL";
                }
            };
        }
        else if (type == Date.class)
        {
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getDATE(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "date DEFAULT NULL";
                }
            };
        }
        else if (type == Time.class)
        {
            descriptor=new GetColumnAccessor(field)
            {
                @Override
                public void set(Object object, String typeName, Row row) throws Throwable
                {
                    field.set(object,row.getTIME(getColumnName(typeName)));
                    
                }

                @Override
                public String getSqlType() throws Throwable
                {
                    return "time DEFAULT NULL";
                }
            };
        }
        else if (type == byte[].class)
        {
            descriptor=new GetColumnAccessor(field)
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
            descriptor=new GetColumnAccessor(field)
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
                    descriptor=new FieldDescriptor(field)
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
                    descriptor=new FieldDescriptor(field)
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
            if (descriptor==null)
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
        synchronized(columnAccessorMap)
        {
            columnAccessorMap.put(type.getName(), descriptor);
        }
        return descriptor;
    }
    
    protected Connector getConnector()
    {
        return this.connector;
    }

    
    
    protected GraphObjectDescriptor register(Class<? extends GraphObject> type) throws Exception
    {
        GraphObjectDescriptor descriptor=null;
        String simpleTypeName=type.getSimpleName();
        synchronized (descriptorMap)
        {
            descriptor= descriptorMap.get(simpleTypeName);
        }
        if (descriptor==null)
        {
            HashMap<String, FieldDescriptor> map = new HashMap<String, FieldDescriptor>();
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
                        FieldDescriptor accessor=getColumnAccessor(field);
                        map.put(field.getName(), accessor);
                    }
                }
            }
            GraphObjectType objectType;
            if (type.getSuperclass()==NodeObject.class)
            {
                objectType=GraphObjectType.NODE;
            }
            else if (type.getSuperclass()==IdentityNodeObject.class)
            {
                objectType=GraphObjectType.IDENTITY_NODE;
            }
            else if (type.getSuperclass()==LinkObject.class)
            {
                objectType=GraphObjectType.LINK;
            }
            else
            {
                throw new Exception(type.getName()+" needs to extend from a subclass of GraphObject.");
            }
            
            descriptor = new GraphObjectDescriptor(simpleTypeName,type,objectType,map.values().toArray(new FieldDescriptor[map.size()]));
            synchronized (descriptorMap)
            {
                descriptorMap.put(simpleTypeName, descriptor);
            }
        }
        return descriptor;
    }
    
    protected GraphObjectDescriptor getGraphObjectDescriptor(Class<? extends GraphObject> type) throws Exception
    {
        String simpleTypeName=type.getSimpleName();
        return descriptorMap.get(simpleTypeName);
    }
    
    
    final private HashMap<String,GraphObjectDescriptor> descriptorMap=new HashMap<String, GraphObjectDescriptor>();
    final private HashMap<String, FieldDescriptor> columnAccessorMap=new HashMap<>();
    final private Connector connector;
    
    public Graph(Connector connector)
    {
        this.connector=connector;
    }
    
    public Map<String,GraphObjectDescriptor> getGraphObjectDescriptorMap()
    {
        return this.descriptorMap;
    }
    
    public GraphAccessor openGraphAcessor(Trace parent,String catalog) throws Throwable
    {
        return new GraphAccessor(this,this.connector.openAccessor(parent, null, catalog));
    }
    

    public void upgradeTable(Trace parent,GraphAccessor graphAccessor,String catalog,Class<? extends GraphObject> type) throws Throwable
    {
        String table=type.getSimpleName();
        GraphObjectDescriptor descriptor=this.register(type);
        
        Accessor accessor=graphAccessor.accessor;
        if (accessor.executeQuery(parent,"existTable:"+table,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?",table,catalog).getRow(0).getBIGINT(0)==0)
        {
            StringBuilder sql=new StringBuilder();
            if (TypeUtils.isDerivedFrom(type, IdentityNodeObject.class))
            {
            	sql.append("CREATE TABLE `"+table+"` (`_id` bigint NOT NULL AUTO_INCREMENT,`_nodeId` bigint NOT NULL,`_eventId` bigint NOT NULL,");
            }
            else
            {
            	sql.append("CREATE TABLE `"+table+"` (`_nodeId` bigint NOT NULL,`_eventId` bigint NOT NULL,");
            }
            for (FieldDescriptor columnAccessor:descriptor.columnAccessors)
            {
                if (columnAccessor.isInternal())
                {
                    continue;
                }
                sql.append("`"+columnAccessor.getName()+"` ");
                sql.append(columnAccessor.getSqlType());
                sql.append(",");
            }
            if (TypeUtils.isDerivedFrom(type, IdentityNodeObject.class))
            {
                sql.append("PRIMARY KEY (`_id`),KEY `index` (`_nodeId`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");
            }
            else
            {
                sql.append("PRIMARY KEY (`_nodeId`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");
            }
            if (TEST)
            {
                Testing.log(sql);
            }
            accessor.executeUpdate(parent, "createTable:"+table, sql.toString());
        }
        else
        {
            RowSet rowSet=accessor.executeQuery(parent,"columnsOfTable:"+table,"SELECT * FROM information_schema.columns WHERE table_name=? AND table_schema=?",table,catalog);

            String after="_eventId";
            Stack<String> alter=new Stack<String>();

            if (Graph.TEST)
            {
                if (type.getSimpleName().equals("AppointmentStatus"))
                {
                    Testing.log("Catalog="+catalog+", type="+type.getSimpleName());
                }
            }
            int rowIndex=2;
            int fieldIndex=0;
            while (fieldIndex<descriptor.columnAccessors.length)
            {
                FieldDescriptor columnAccessor=descriptor.columnAccessors[fieldIndex];
                if (columnAccessor.isInternal())
                {
                    fieldIndex++;
                    continue;
                }
                String fieldName=columnAccessor.getName();
                String fieldSqlType=columnAccessor.getSqlType();
                if (rowIndex<rowSet.size())
                {
                    Row row=rowSet.getRow(rowIndex);
                    String columnName=row.getVARCHAR("COLUMN_NAME");
                    int compareResult=fieldName.compareTo(columnName);
                    if (compareResult==0)
                    {
                        String sqlType=row.getVARCHAR("DATA_TYPE");
                        Long length=row.getNullableBIGINT("CHARACTER_MAXIMUM_LENGTH");
                        if (length!=null)
                        {
                            sqlType=sqlType+"("+length+")";
                        }
                        if (row.getVARCHAR("IS_NULLABLE").equals("YES"))
                        {
                            sqlType=sqlType+" DEFAULT NULL";
                        }
                        else
                        {
                            sqlType=sqlType+" NOT NULL";
                        }
                        if (sqlType.equalsIgnoreCase(fieldSqlType)==false)
                        {
                            throw new Exception("Catalog="+catalog+", type="+type.getSimpleName()+", field="+fieldName+", field type="+fieldSqlType+", db type="+sqlType);
                        }
                        after=columnName;
                        fieldIndex++;
                        rowIndex++;
                        continue;
                    }
                    else if (compareResult<0)
                    {
                        fieldIndex++;
                    }
                    else
                    {
                        after=columnName;
                        if (rowIndex<rowSet.size()-1)
                        {
                            rowIndex++;
                            if (TEST)
                            {
                                Testing.log("Unused column: columnName="+columnName+", table="+table);
                            }
                            continue;
                        }
                    }
                }
                else
                {
                    fieldIndex++;
                }
                alter.push(" ADD COLUMN `"+fieldName+"` "+fieldSqlType+" AFTER `"+after+'`');
            }
            if (alter.size()>0)
            {
                StringBuilder sql=new StringBuilder("ALTER TABLE `"+catalog+"`."+descriptor.getTableName());
                sql.append(alter.pop());
                while (alter.size()>0)
                {
                    sql.append(','+alter.pop());
                    
                }
                sql.append(';');
                if (TEST)
                {
                    Testing.log(sql);
                }
                accessor.executeUpdate(parent, "alterTable:"+table, sql.toString());
            }
        }
    }

    public void createCatalog(Trace parent,String catalog) throws Throwable
    {
        try (Accessor accessor=this.connector.openAccessor(parent))
        {
            if (accessor.executeQuery(parent,"existCatalog:"+catalog,"SELECT count(*) FROM information_schema.schemata WHERE SCHEMA_NAME=?",catalog).getRow(0).getBIGINT(0)==0)
            {
                accessor.executeUpdate(parent, "createCatalog:"+catalog,"CREATE DATABASE `"+catalog+'`');
            }
        }
        try (Accessor accessor=this.connector.openAccessor(parent,null,catalog))
        {
            if (accessor.executeQuery(parent,"existTable:_event"
                    ,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?","_event",catalog).getRow(0).getBIGINT(0)==0)
            {
                accessor.executeUpdate(parent, "createTable:_event"
                        ,"CREATE TABLE `_event` (`id` bigint NOT NULL AUTO_INCREMENT,`created` datetime NOT NULL,`creatorId` bigint NOT NULL,`source` varchar(256) DEFAULT NULL,PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=547 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"
                        );
            }

            if (accessor.executeQuery(parent,"existTable:_link"
                    ,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?","_link",catalog).getRow(0).getBIGINT(0)==0)
            {
                accessor.executeUpdate(parent, "createTable:_link"
                        ,"CREATE TABLE `_link` (`id` bigint NOT NULL AUTO_INCREMENT, `fromNodeId` bigint NOT NULL, `toNodeId` bigint NOT NULL, `eventId` bigint NOT NULL,`relation` int NOT NULL,`type` varchar(50) DEFAULT NULL,PRIMARY KEY (`id`),KEY `link` (`fromNodeId`,`toNodeId`,`relation`,`type`)) ENGINE=InnoDB AUTO_INCREMENT=218 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"
                        );
            }

            if (accessor.executeQuery(parent,"existTable:_node"
                    ,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?","_node",catalog).getRow(0).getBIGINT(0)==0)
            {
                accessor.executeUpdate(parent, "createTable:_node"
                        ,"CREATE TABLE `_node` (`id` bigint NOT NULL AUTO_INCREMENT,`eventId` bigint NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"
                        );
            }
        }
    }
}
