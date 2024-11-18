package xp.nova.sqldb.graph;

import org.nova.utils.TypeUtils;

public record NamespaceGraphObjectDescriptor(String namespace,GraphObjectDescriptor descriptor)
{
    public NamespaceGraphObjectDescriptor(String namespace,GraphObjectDescriptor descriptor)
    {
        this.namespace=TypeUtils.isNullOrEmpty(namespace)?null:namespace;
        this.descriptor=descriptor;
    }
    
    public String getNamespaceTypeName()
    {
        if (this.namespace==null)
        {
            return this.descriptor.getTypeName();
        }
        return this.namespace+this.descriptor.getTypeName();
    }
}
