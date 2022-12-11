package xp.nova.sqldb.graph;

import java.util.Arrays;
import java.util.Comparator;

public class GraphObjectDescriptor
{
    final FieldDescriptor[] columnAccessors;
    final private GraphObjectType entityType;
    final private String tableName;
    final private String typeName;
    final private Class<? extends GraphObject> type;
    
    GraphObjectDescriptor(String typeName,Class<? extends GraphObject> type,GraphObjectType entityType,FieldDescriptor[] columnnAccessors)
    {
        this.type=type;
        this.entityType=entityType;
        
        Arrays.sort(columnnAccessors,new Comparator<FieldDescriptor>()
        {
            @Override
            public int compare(FieldDescriptor o1, FieldDescriptor o2)
            {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        this.columnAccessors=columnnAccessors;
        this.typeName=typeName;
//        this.tableAlias='`'+typeName+'`';
        this.tableName='`'+typeName+'`';
    }

    public Class<? extends GraphObject> getType()
    {
        return this.type;
    }
    public FieldDescriptor[] getColumnAccessors()
    {
        return this.columnAccessors;
    }
//    String getTableAlias()
//    {
//        return this.tableAlias;
//    }
    String getTableAlias(String namespace)
    {
        return '`'+getNamespaceTypeName(namespace)+'`';
    }
    String getTableName()
    {
        return this.tableName;
    }
    GraphObjectType getObjectType()
    {
        return this.entityType;
    }
    String getTypeName()
    {
        return this.typeName;
    }
    String getNamespaceTypeName(String namespace)
    {
        if (namespace!=null)
        {
            return namespace+'.'+this.typeName;
        }
        return this.typeName;
    }
}