package xp.nova.sqldb.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class PrepareState
    {
        final Graph graph;
        final HashMap<String,GraphObjectDescriptor> map;
        final StringBuilder sources;
        final StringBuilder select;
        final List<NamespaceGraphObjectDescriptor> descriptors;
        int aliasIndex=0;
        
        
        public PrepareState(Graph graph,HashMap<String,GraphObjectDescriptor> map,StringBuilder sources,StringBuilder select,List<NamespaceGraphObjectDescriptor> descriptors)
        {
            this.graph=graph;
            this.map=map;
            this.sources=sources;
            this.select=select;
            this.descriptors=descriptors;
        }
        void addLinkQueries(ArrayList<LinkQuery> linkQueries, String source) throws Throwable
        {
            if (linkQueries == null)
            {
                return;
            }
            for (LinkQuery linkQuery : linkQueries)
            {
//                TypeUtils.addToList(state.parameters,linkQuery.parameters);
                String linkAlias = "_link" + this.aliasIndex;
                String nodeAlias=null;
                String nodeNamespace = linkQuery.nodeNamespace != null ? linkQuery.nodeNamespace + "." : "";
                String linkNamespace = linkQuery.linkNamespace != null ? linkQuery.linkNamespace + "." : "";

//                Class<? extends NodeObject> fromType=null;
                if (linkQuery.nodeTypes==null)
                {
                    this.sources.append(" LEFT JOIN");
                }
                else
                {
                    this.sources.append(" JOIN");
                }
      //          Class<? extends NodeObject> relationFromType=state.graph.getRelationNodeType(linkQuery.relation);
                switch (linkQuery.direction)
                {
                case FROM:
                    nodeAlias = " ON _link" + this.aliasIndex+".toNodeId=";
                    this.sources.append(" _link AS " + linkAlias + source + linkAlias + ".fromNodeId");
                    this.sources.append(" AND "+linkAlias+".relationValue="+linkQuery.relationValue);
                    if (linkQuery.targetNodeType!=null)
                    {
                        this.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.targetNodeType.getSimpleName()+"'");
                    }
                    else if (linkQuery.nodeTypes!=null)
                    {
                        this.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.nodeTypes[0].getSimpleName()+"'");
                    }
                    else if ((linkQuery.optionalNodeTypes!=null)&&(linkQuery.optionalNodeTypes.length==1))
                    {
                        this.sources.append(" AND "+linkAlias+".toNodeType='"+linkQuery.optionalNodeTypes[0].getSimpleName()+"'");
                    }
                    else
                    {
                        throw new Exception("Cannot infer targetNodeType");
                    }
                
                    
                    break;
                case TO:
                    nodeAlias = " ON _link" + this.aliasIndex+".fromNodeId=";
                    this.sources.append(" _link AS " + linkAlias + source + linkAlias + ".toNodeId");
                    this.sources.append(" AND "+linkAlias+".relationValue="+linkQuery.relationValue);
                    if (linkQuery.targetNode!=null)
                    {
                        this.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.targetNode.getClass().getSimpleName()+"'");
                    }
                    else if (linkQuery.nodeTypes!=null)
                    {
                        this.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.nodeTypes[0].getSimpleName()+"'");
                    }
                    else if ((linkQuery.optionalNodeTypes!=null)&&(linkQuery.optionalNodeTypes.length==1))
                    {
                        this.sources.append(" AND "+linkAlias+".fromNodeType='"+linkQuery.optionalNodeTypes[0].getSimpleName()+"'");
                    }
                    else
                    {
                        throw new Exception("Cannot infer targetNodeType. Specify targetNodeType in LinkQuery constructor.");
                    }
                    
                    break;
                default:
                    break;
                }

                if (linkQuery.linkTypes != null)
                {
                    String on = " ON " + linkAlias + ".nodeId=";
                    for (int i = 0; i < linkQuery.linkTypes.length; i++)
                    {
                        Class<? extends NodeObject> type = linkQuery.linkTypes[i];
                        GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
                        this.descriptors.add(new NamespaceGraphObjectDescriptor(linkNamespace,descriptor));
                        this.map.put(linkNamespace+descriptor.getTypeName(), descriptor);
                        String typeName = descriptor.getTypeName();
                        String table = descriptor.getTableName();

                        String as=" ";
                        String alias=table;
                        if (linkQuery.linkNamespace!=null)
                        {
                            alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                            as=" AS "+alias+" ";
                        }

                        this.sources.append(" JOIN " + table + as + on + alias + "._nodeId");
                        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
                        {
                            String fieldColumnName = linkNamespace + columnAccessor.getColumnName(typeName);
                            String tableColumnName = columnAccessor.getColumnName(alias);
                            if (this.select.length()>0)
                            {
                                this.select.append(',');
                            }
                            this.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                        }
                    }
                }
                if (linkQuery.optionalLinkTypes != null)
                {
                    String on = " ON " + linkAlias + ".nodeId=";
                    for (int i = 0; i < linkQuery.optionalLinkTypes.length; i++)
                    {
                        Class<? extends NodeObject> type = linkQuery.optionalLinkTypes[i];
                        GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
                        this.descriptors.add(new NamespaceGraphObjectDescriptor(linkNamespace,descriptor));
                        this.map.put(linkNamespace+descriptor.getTypeName(), descriptor);
                        String typeName = descriptor.getTypeName();
                        String table = descriptor.getTableName();

                        String as=" ";
                        String alias=table;
                        if (linkQuery.linkNamespace!=null)
                        {
                            alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                            as=" AS "+alias+" ";
                        }

                        this.sources.append(" LEFT JOIN " + table + as + on + alias + "._nodeId");
                        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
                        {
                            String fieldColumnName = linkNamespace + columnAccessor.getColumnName(typeName);
                            String tableColumnName = columnAccessor.getColumnName(alias);
                            if (this.select.length()>0)
                            {
                                this.select.append(',');
                            }
                            this.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                        }
                    }
                }
                if ((linkQuery.targetNode!=null)&&(linkQuery.nodeTypes == null)&&(linkQuery.optionalNodeTypes==null))
                {
                    String on = null;
                    switch (linkQuery.direction)
                    {
                    case FROM:
                        on = " ON " + linkAlias + ".toNodeId=";
                        break;
                    case TO:
                        on = " ON " + linkAlias + ".fromNodeId=";
                        break;
                    default:
                        break;
                    }
                    Class<? extends NodeObject> type = linkQuery.targetNodeType;
                    GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
//                    this.descriptors.add(new NamespaceGraphObjectDescriptor(nodeNamespace,descriptor));
                    
                    String typeName = descriptor.getTypeName();
                    String table = descriptor.getTableName();

                    String as=" ";
                    String alias=table;
                    if (linkQuery.linkNamespace!=null)
                    {
                        alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                        as=" AS "+alias+" ";
                    }
                    this.sources.append(" JOIN " + table + as + on + alias + "._nodeId AND "+alias+"._nodeId="+linkQuery.targetNode.getNodeId());
                }
                if (linkQuery.nodeTypes != null)
                {
                    String on = null;
                    switch (linkQuery.direction)
                    {
                    case FROM:
                        on = " ON " + linkAlias + ".toNodeId=";
                        break;
                    case TO:
                        on = " ON " + linkAlias + ".fromNodeId=";
                        break;
                    default:
                        break;
                    }
                    for (int i = 0; i < linkQuery.nodeTypes.length; i++)
                    {
                        Class<? extends NodeObject> type = linkQuery.nodeTypes[i];
                        GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
                        this.map.put(nodeNamespace+descriptor.getTypeName(), descriptor);
                        String typeName = descriptor.getTypeName();
                        String table = descriptor.getTableName();
                        this.descriptors.add(new NamespaceGraphObjectDescriptor(nodeNamespace,descriptor));

                        String as=" ";
                        String alias=table;
                        if (linkQuery.linkNamespace!=null)
                        {
                            alias="`"+linkQuery.linkNamespace+"_"+typeName+"`";
                            as=" AS "+alias+" ";
                        }

                        this.sources.append(" JOIN " + table + as + on + alias + "._nodeId");
                        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
                        {
                            String fieldColumnName = nodeNamespace + columnAccessor.getColumnName(typeName);
                            String tableColumnName = columnAccessor.getColumnName(alias);
                            if (this.select.length()>0)
                            {
                                this.select.append(',');
                            }
                            this.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                        }
                    }
                }
                if (linkQuery.optionalNodeTypes != null)
                {
                    String on = null;
                    switch (linkQuery.direction)
                    {
                    case FROM:
                        on = " ON " + linkAlias + ".toNodeId=";
                        break;
                    case TO:
                        on = " ON " + linkAlias + ".fromNodeId=";
                        break;
                    default:
                        break;
                    }
                    for (int i = 0; i < linkQuery.optionalNodeTypes.length; i++)
                    {
                        Class<? extends NodeObject> type = linkQuery.optionalNodeTypes[i];
                        GraphObjectDescriptor descriptor = this.graph.getGraphObjectDescriptor(type);
                        this.map.put(descriptor.getNamespaceTypeName(linkQuery.nodeNamespace), descriptor);
                        String typeName = descriptor.getTypeName();
                        String table = descriptor.getTableName();
                        this.descriptors.add(new NamespaceGraphObjectDescriptor(nodeNamespace,descriptor));
                        String alias = descriptor.getTableAlias(linkQuery.nodeNamespace);
                        this.sources.append(" LEFT JOIN " + table + "AS " + alias + on + alias + "._nodeId");
                        for (FieldDescriptor columnAccessor : descriptor.getFieldDescriptors())
                        {
                            String fieldColumnName = nodeNamespace + columnAccessor.getColumnName(typeName);
                            String tableColumnName = columnAccessor.getColumnName(alias);
                            if (this.select.length()>0)
                            {
                                this.select.append(',');
                            }
                            this.select.append(tableColumnName + " AS '" + fieldColumnName + '\'');
                        }
                    }
                }
                this.aliasIndex++;
                addLinkQueries(linkQuery.linkQueries, nodeAlias);
            }
        }

        
    }