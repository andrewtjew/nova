package xp.nova.sqldb.graph;

import java.util.Arrays;
import java.util.Comparator;

public class GraphObjectDescriptor
{
    final FieldDescriptor[] fieldDescriptors;
    final private GraphObjectType graphObjectType;
    final private String tableName;
    final private String typeName;
    final private Class<? extends NodeObject> type;
    
    GraphObjectDescriptor(String typeName,Class<? extends NodeObject> type,GraphObjectType graphObjectType,FieldDescriptor[] fieldDescriptors)
    {
        this.type=type;
        this.graphObjectType=graphObjectType;
        
        Arrays.sort(fieldDescriptors,new Comparator<FieldDescriptor>()
        {
            @Override
            public int compare(FieldDescriptor o1, FieldDescriptor o2)
            {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        this.fieldDescriptors=fieldDescriptors;
        this.typeName=typeName;
        this.tableName='`'+typeName+'`';
    }

    public Class<? extends NodeObject> getType()
    {
        return this.type;
    }
    public FieldDescriptor[] getColumnAccessors()
    {
        return this.fieldDescriptors;
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
        return this.graphObjectType;
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