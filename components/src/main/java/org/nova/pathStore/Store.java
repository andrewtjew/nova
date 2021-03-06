/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.pathStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nova.utils.Utils;

public class Store<A,V,AV extends AttributeValue<A,V>>
{
    final Node<A,V,AV> root;
    
    public Store()
    {
        this.root=new Node<A,V,AV>();
    }
    
    private void add(AV attributeValue,int index,Node<A,V,AV> node) throws Exception
    {
        String[] pathElements=attributeValue.getPathElements();
        if (index==pathElements.length)
        {
            if (node.attributeValue!=null)
            {
     //BUG!!!!           throw new Exception("/"+Utils.combine(pathElements, "/")+" is already added. Choose a different path");
            }
            node.attributeValue=attributeValue;
        }
        else
        {
            Node<A,V,AV> childNode;
            String key=pathElements[index];
            if (node.nodes==null)
            {
                node.nodes=new HashMap<>();
                childNode=new Node<>();
                node.nodes.put(key, childNode);
            }
            else
            {
                childNode=node.nodes.get(key);
                if (childNode==null)
                {
                    childNode=new Node<>();
                    node.nodes.put(key, childNode);
                }
            }
            add(attributeValue,index+1,childNode);
        }
    }
    

    public void add(AV attributeValue,String...pathElements) throws Exception
    {
        synchronized(this)
        {
            add(attributeValue,0,this.root);
        }
    }

    public static String[] toPathElements(String path) throws Exception
    {
        if (path.startsWith("/")==false)
        {
            throw new Exception("Path must start with /:"+path);
        }
        if (path.length()==1)
        {
            return new String[0];
        }
        return Utils.split(path.substring(1),'/');
    }

    public void add(String path,AV attributeValue) throws Exception
    {
        add(attributeValue,toPathElements(path));
    }

    private Node<A,V,AV> getNode(String[] pathElements,Node<A,V,AV> node,int index)
    {
        if (index==pathElements.length)
        {
            return node;
        }
        else
        {
            String key=pathElements[index];
            Node<A,V,AV> child=node.nodes.get(key);
            if (child==null)
            {
                return null;
            }
            return getNode(pathElements,child,index+1);
        }
    }
    
    public AV get(String...pathElements)
    {
        synchronized(this)
        {
            Node<A,V,AV> node=getNode(pathElements,this.root,0);
            if (node!=null)
            {
                return node.attributeValue;
            }
            return null;
        }        
    }
    
    public AV get(String path) throws Exception
    {
        return get(toPathElements(path));
    }
    
    private void get(List<AV> list,Node<A,V,AV> node)
    {
        if (node.attributeValue!=null)
        {
            list.add(node.attributeValue);
        }
        if (node.nodes!=null)
        {
            for (Node<A,V,AV> child:node.nodes.values())
            {
                get(list,child);
            }
        }
    }

    public List<AV> getIncludingBelow(String...pathElements)
    {
        ArrayList<AV> list=new ArrayList<>(); 
        synchronized(this)
        {
            Node<A,V,AV> node=getNode(pathElements,this.root,0);
            
            if (node!=null)
            {
                get(list,node);
                return list;
            }
            return null;
        }        
    }
    
    public List<AV> getIncludingBelow(String path) throws Exception
    {
        return getIncludingBelow(toPathElements(path));
    }
    
    private AV remove(String[] pathElements,Node<A,V,AV> node,int index)
    {
        if (index==pathElements.length-1)
        {
            AV attributeValue=node.attributeValue;
            if (attributeValue!=null)
            {
                node.nodes=null;
            }
            return attributeValue;
        }
        else
        {
            String key=pathElements[index];
            Node<A,V,AV> child=node.nodes.get(key);
            if (child==null)
            {
                return null;
            }
            AV attributeValue=remove(pathElements,child,index+1);
            if (attributeValue!=null)
            {
                if (child.nodes==null)
                {
                    node.nodes.remove(key);
                    if (node.nodes.size()==0)
                    {
                        node.nodes=null;
                    }
                }
            }
            return attributeValue;
        }
    }
    public AV remove(String...pathElements)
    {
        synchronized(this)
        {
            return remove(pathElements,this.root,0);
        }        
    }

    public AV remove(String path) throws Exception
    {
        return remove(toPathElements(path));
    }
}
