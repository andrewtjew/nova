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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.testing.Debugging;
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
                public SqlType getSqlType() throws Throwable
                {
                    long value=defaultVARCHARLength;
                    Length length=this.field.getDeclaredAnnotation(Length.class);
                    if (length!=null)
                    {
                        value=length.value();
                    }
                    return new SqlType("VARCHAR",true,value);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("BIT",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("BIT",false);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("INT",false);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("INT",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("BIGINT",false);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("BIGINT",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("FLOAT",false);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("FLOAT",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("DOUBLE",false);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("DOUBLE",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("SMALLINT",false);
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
                    field.set(object,row.getSMALLINT(getColumnName(typeName)));
                    
                }

                @Override
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("SMALLINT",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("TIME",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("DATE",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("TIMESTAMP",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("TIMESTAMP",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("DATE",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    return new SqlType("TIME",true);
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
                public SqlType getSqlType() throws Throwable
                {
                    long value=defaultVARCHARLength;
                    Length length=this.field.getDeclaredAnnotation(Length.class);
                    if (length!=null)
                    {
                        value=length.value();
                    }
                    return new SqlType("VARBINARY",true,value);
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
                        public SqlType getSqlType() throws Throwable
                        {
                            return new SqlType("SMALLINT",true);
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
                        public SqlType getSqlType() throws Throwable
                        {
                            return new SqlType("INT",true);
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

    
    
    protected GraphObjectDescriptor register(Class<? extends NodeObject> type) throws Exception
    {
        GraphObjectDescriptor descriptor=null;
        String simpleTypeName=type.getSimpleName();
        
        synchronized (descriptorMap)
        {
            descriptor= descriptorMap.get(simpleTypeName);
        }
        if (descriptor==null)
        {
            if ((type.getSuperclass()==RelationNodeObject.class)||(type.getSuperclass()==IdentityRelationNodeObject.class))
            {
                ParameterizedType genericSuperClass=(ParameterizedType)type.getGenericSuperclass();
                Type[] arguments=genericSuperClass.getActualTypeArguments();
                for (Type argument:arguments)
                {
                    this.relationNodeObjectMap.put((Class<? extends Relation_>)argument, (Class<? extends RelationNodeObject<?>>)type);
                }
            }
                

            
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
            if (type.getSuperclass()==IdentityNodeObject.class)
            {
                objectType=GraphObjectType.IDENTITY_NODE;
            }
            else if (type.getSuperclass()==IdentityRelationNodeObject.class)
            {
                objectType=GraphObjectType.IDENTITY_NODE;
            }
            else if (type.getSuperclass()==NodeObject.class)
            {
                objectType=GraphObjectType.NODE;
            }
            else if (type.getSuperclass()==RelationNodeObject.class)
            {
                objectType=GraphObjectType.NODE;
            }
            else
            {
                throw new Exception(type.getName()+" needs to extend from a subclass of NodeObject.");
            }
            
            descriptor = new GraphObjectDescriptor(simpleTypeName,type,objectType,map.values().toArray(new FieldDescriptor[map.size()]));
            synchronized (descriptorMap)
            {
                descriptorMap.put(simpleTypeName, descriptor);
            }
        }
        return descriptor;
    }
    
    protected GraphObjectDescriptor getGraphObjectDescriptor(Class<? extends NodeObject> type) throws Exception
    {
        String simpleTypeName=type.getSimpleName();
        return descriptorMap.get(simpleTypeName);
    }
    
    
    final private HashMap<String,GraphObjectDescriptor> descriptorMap=new HashMap<String, GraphObjectDescriptor>();
    final private HashMap<String, FieldDescriptor> columnAccessorMap=new HashMap<>();
    final private Connector connector;
    final private HashMap<Class<? extends Relation_>, Class<? extends RelationNodeObject<?>>> relationNodeObjectMap=new HashMap<>();
    
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
    

    public void upgradeTable(Trace parent,GraphAccessor graphAccessor,String catalog,Class<? extends NodeObject> type) throws Throwable
    {
        String table=type.getSimpleName();
        GraphObjectDescriptor descriptor=this.register(type);
        
        Accessor accessor=graphAccessor.accessor;
        if (accessor.executeQuery(parent,"existTable:"+table,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?",table,catalog).getRow(0).getBIGINT(0)==0)
        {
            StringBuilder sql=new StringBuilder();
            if (TypeUtils.isDerivedFrom(type, IdentityRelationNodeObject.class)||TypeUtils.isDerivedFrom(type, IdentityNodeObject.class))
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
                sql.append(columnAccessor.getSqlType().getSql());
                sql.append(",");
            }
            if (TypeUtils.isDerivedFrom(type, IdentityRelationNodeObject.class)||TypeUtils.isDerivedFrom(type, IdentityNodeObject.class))
            {
                sql.append("PRIMARY KEY (`_id`),KEY `index` (`_nodeId`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");
            }
            else
            {
                sql.append("PRIMARY KEY (`_nodeId`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");
            }
            if (TEST)
            {
                Debugging.log(sql);
            }
            accessor.executeUpdate(parent, "createTable:"+table, sql.toString());
        }
        else
        {
            RowSet rowSet=accessor.executeQuery(parent,"columnsOfTable:"+table,"SELECT * FROM information_schema.columns WHERE table_name=? AND table_schema=?",table,catalog);

            String after="_eventId";
            Stack<String> alters=new Stack<String>();

            if (Graph.TEST)
            {
                if (type.getSimpleName().equals("AppointmentStatus"))
                {
                    Debugging.log("Catalog="+catalog+", type="+type.getSimpleName());
                }
            }
            int rowIndex=0;
            int fieldIndex=0;
            Row[] orderedRows=new Row[rowSet.size()];
            for (int i=0;i<rowSet.size();i++)
            {
                Row row=rowSet.getRow(i);
                int position=(int)row.getBIGINT("ORDINAL_POSITION");
                orderedRows[position-1]=row;
            }
            while (fieldIndex<descriptor.columnAccessors.length)
            {
                FieldDescriptor columnAccessor=descriptor.columnAccessors[fieldIndex];
                if (columnAccessor.isInternal())
                {
                    fieldIndex++;
                    rowIndex++;
                    continue;
                }
                String fieldName=columnAccessor.getName();
                SqlType fieldSqlType=columnAccessor.getSqlType();
                if (rowIndex<rowSet.size())
                {
                    Row row=orderedRows[rowIndex];
                    String columnName=row.getVARCHAR("COLUMN_NAME");
                    if (columnName.equals("_eventId"))
                    {
                        rowIndex++;
                        continue;
                    }
                    int compareResult=fieldName.compareTo(columnName);
                    if (compareResult==0)
                    {
                        String dataType=row.getVARCHAR("DATA_TYPE").toUpperCase();
                        Long length=row.getNullableBIGINT("CHARACTER_MAXIMUM_LENGTH");
                        boolean nullable=row.getVARCHAR("IS_NULLABLE").equals("YES");
                         if (length==null)
                         {
                             
                         }
                        if (fieldSqlType.isEqual(dataType, nullable, length)==false)
                        {
                            if (fieldSqlType.isLengthAcceptable(length)==false)
                            {
                                throw new Exception("Catalog="+catalog+", type="+type.getSimpleName()+", field="+fieldName+", field type="+fieldSqlType+", db type="+dataType);
                            }
                            alters.push(" CHANGE COLUMN `"+fieldName+"` `"+fieldName+"` "+fieldSqlType.getSql()+" DEFAULT NULL");
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
                        if (rowIndex<=rowSet.size()-1)
                        {
                            rowIndex++;
                            if (TEST)
                            {
                                Debugging.log("Unused column: columnName="+columnName+", table="+table);
                            }
                            continue;
                        }
                    }
                }
                else
                {
                    fieldIndex++;
                }
                alters.push(" ADD COLUMN `"+fieldName+"` "+fieldSqlType.getSql()+" AFTER `"+after+'`');
            }
            if (alters.size()>0)
            {
                StringBuilder sql=new StringBuilder("ALTER TABLE `"+catalog+"`."+descriptor.getTableName());
                sql.append(alters.pop());
                while (alters.size()>0)
                {
                    sql.append(','+alters.pop());
                    
                }
                sql.append(';');
                if (TEST)
                {
                    Debugging.log(sql);
                }
                accessor.executeUpdate(parent, "alterTable:"+table, sql.toString());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <RELATION extends Relation_> Class<? extends RelationNodeObject<RELATION>> getRelationNodeType(RELATION relation)
    {
        return (Class<? extends RelationNodeObject<RELATION>>) this.relationNodeObjectMap.get(relation.getClass());
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
                        ,"CREATE TABLE `_event` (`id` bigint NOT NULL AUTO_INCREMENT,`created` datetime NOT NULL,`creatorId` bigint NOT NULL,PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=547 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"
                        );
            }

            if (accessor.executeQuery(parent,"existTable:_link"
                    ,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?","_link",catalog).getRow(0).getBIGINT(0)==0)
            {
                
                
                accessor.executeUpdate(parent, "createTable:_link"
                        ,"CREATE TABLE `_link` (`nodeId` bigint NOT NULL,`fromNodeId` bigint NOT NULL,`toNodeId` bigint NOT NULL,`relationValue` int DEFAULT NULL,PRIMARY KEY (`nodeId`),KEY `link` (`fromNodeId`,`relationValue`,`toNodeId`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"
                        );
            }

            if (accessor.executeQuery(parent,"existTable:_node"
                    ,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?","_node",catalog).getRow(0).getBIGINT(0)==0)
            {
                accessor.executeUpdate(parent, "createTable:_node"
                        ,"CREATE TABLE `_node` (`id` bigint NOT NULL AUTO_INCREMENT,`eventId` bigint NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"
                        );
            }
            if (accessor.executeQuery(parent,"existTable:_version"
                    ,"SELECT count(*) FROM information_schema.tables WHERE table_name=? AND table_schema=?","_node",catalog).getRow(0).getBIGINT(0)==0)
            {
                accessor.executeUpdate(parent, "createTable:_version"
                        ,"CREATE TABLE `_version` (`version` varchar(50) NOT NULL,PRIMARY KEY (`version`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"
                        );
            }
        }
    }
}
