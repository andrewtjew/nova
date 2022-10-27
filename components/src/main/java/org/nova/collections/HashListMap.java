package org.nova.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashListMap<K,V> extends HashMap<K,List<V>>
{

    /**
     * 
     */
    private static final long serialVersionUID = -1693269658302549385L;

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
    
    public int getSize(K key)
    {
        List<V> list=get(key);
        if (list==null)
        {
            return 0;
        }
        return list.size();
    }
}
