package xp.nova.sqldb.graph;

import java.util.Arrays;
import java.util.Comparator;

class Meta
{
    final ColumnAccessor[] columnAccessors;
    final private GraphObjectType entityType;
    final private String tableAlias;
    final private String tableName;
    final private String typeName;
    final private Class<? extends GraphObject> type;
    Meta(String typeName,Class<? extends GraphObject> type,GraphObjectType entityType,ColumnAccessor[] columnnAccessors)
    {
        this.type=type;
        this.entityType=entityType;
        
        Arrays.sort(columnnAccessors,new Comparator<ColumnAccessor>()
        {
            @Override
            public int compare(ColumnAccessor o1, ColumnAccessor o2)
            {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
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