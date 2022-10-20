package org.nova.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TreeListMap<K,V> extends TreeMap<K,List<V>>
{

    /**
     * 
     */
    private static final long serialVersionUID = 6257980654157294984L;

    public void putInList(K key,V value)
    {
        List<V> list=get(key);
        if (list==null)
        {
            list=new ArrayList<V>();
            put(key,list);
        }
        list.add(value);
    }
}
