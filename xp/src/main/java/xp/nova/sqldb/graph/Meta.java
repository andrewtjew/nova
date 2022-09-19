package xp.nova.sqldb.graph;

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