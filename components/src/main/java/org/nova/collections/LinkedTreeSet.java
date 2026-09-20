package org.nova.collections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class LinkedTreeSet<V extends Comparable<V>> extends AbstractSet<V> implements Iterable<V>
{
    public static class ValueBox<V>
    {
        private V value;
        private ValueBox<V> previous;
        private ValueBox<V> next;
        public ValueBox(V value)
        {
            this.value=value;
        }
    }
    
    private static class ValueBoxComparator<V extends Comparable<V>> implements Comparator<ValueBox<V>>
    {
        private Comparator<V> comparator;
        public ValueBoxComparator(Comparator<V> comparator)
        {
            this.comparator=comparator;
        }
        @Override
        public int compare(ValueBox<V> o1, ValueBox<V> o2)
        {
            if (this.comparator==null)
            {
                if (o1.value==null && o2.value==null)
                {
                    return 0;
                }
                if (o1.value==null)
                {
                    return -1;
                }
                return o1.value.compareTo(o2.value);
            }
            return this.comparator.compare(o1.value, o2.value);
        }
    }
    
    private final ValueBoxComparator<V> comparator;
    private final TreeSet<ValueBox<V>> treeSet;
    
    public LinkedTreeSet() 
    {
        this(null);
    }

    public LinkedTreeSet(Comparator<V> comparator) 
    {
        this.comparator = new ValueBoxComparator<>(comparator);
        this.treeSet = new TreeSet<>(this.comparator);
    }

//    @Override
//    public Comparator<? super V> comparator()
//    {
//        return this.comparator.comparator;
//    }
    

    public V first()
    {
        var valueBox = this.treeSet.first();
        if (valueBox == null) 
        {
            return null;
        }
        return valueBox.value;
    }

    public V last()
    {
        var valueBox = this.treeSet.last();
        if (valueBox == null) 
        {
            return null;
        }
        return valueBox.value;
    }

    public int size()
    {
        return this.treeSet.size();
    }

    public V lower(V e)
    {
        ValueBox<V> searchBox = new ValueBox<>(e);
        ValueBox<V> lowerBox = this.treeSet.lower(searchBox);
        if (lowerBox == null) 
        {
            return null;
        }
        return lowerBox.value;
    }

    public V floor(V e)
    {
        ValueBox<V> searchBox = new ValueBox<>(e);
        ValueBox<V> floorBox = this.treeSet.floor(searchBox);
        if (floorBox == null) 
        {
            return null;
        }
        return floorBox.value;
    }

    public V ceiling(V e)
    {
        ValueBox<V> searchBox = new ValueBox<>(e);
        ValueBox<V> ceilingBox = this.treeSet.ceiling(searchBox);
        if (ceilingBox == null) 
        {
            return null;
        }
        return ceilingBox.value;
    }

    public V higher(V e)
    {
        ValueBox<V> searchBox = new ValueBox<>(e);
        ValueBox<V> higherBox = this.treeSet.higher(searchBox);
        if (higherBox == null) 
        {
            return null;
        }
        return higherBox.value;
    }

    public boolean add(V e)
    {
        ValueBox<V> newBox = new ValueBox<>(e);
        var lowerBox=this.treeSet.lower(newBox);
        if (lowerBox!=null)
        {
            newBox.previous=lowerBox;
            newBox.next=lowerBox.next;
            lowerBox.next=newBox;
            if (newBox.next!=null)
            {
                newBox.next.previous=newBox;
            }
        }
        else
        {
            var first=this.treeSet.first();
            if (first!=null)
            {
                first.previous=newBox;
                newBox.next=first;
            }
        }
        return this.treeSet.add(newBox);
    }
    
    public boolean remove(V e)
    {
        ValueBox<V> searchBox = new ValueBox<>(e);
        var start=this.treeSet.lower(searchBox);
        if (start==null)
        {
            start=this.treeSet.first();
        }
        if (start==null)
        {
            return false;
        }
        var end=this.treeSet.higher(searchBox);
        if (end==null)
        {
            end=this.treeSet.last();
        }
        for (var box=start;box!=null;box=box.next)
        {
            if (box.value==e)
            {
                this.treeSet.remove(box);
                if (box.previous!=null)
                {
                    box.previous.next=box.next;
                }
                if (box.next!=null)
                {
                    box.next.previous=box.previous;
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public Iterator<V> iterator()
    {
        return new Iterator<V>() 
        {
            private final Iterator<ValueBox<V>> treeSetIterator = treeSet.iterator();

            @Override
            public boolean hasNext() 
            {
                return treeSetIterator.hasNext();
            }

            @Override
            public V next() 
            {
                ValueBox<V> valueBox = treeSetIterator.next();
                return valueBox.value;
            }
        };
    }
}
