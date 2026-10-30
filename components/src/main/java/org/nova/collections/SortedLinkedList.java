//package org.nova.collections;
//
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.NoSuchElementException;
//import java.util.TreeSet;
//
///**
// * A doubly linked list that keeps its elements in sorted order at all times.
// * Elements are ordered either by their natural ordering (via {@link Comparable})
// * or by a {@link Comparator} supplied at construction time.
// * Unlike a {@link java.util.TreeSet}, duplicate elements are permitted: duplicates
// * are kept in stable insertion order relative to each other.
// * <p>
// * Internally, a {@link TreeSet} of nodes is used to locate the sorted insertion
// * point, the equal-value neighborhood (for {@code contains}/{@code remove}), and
// * the head/tail in O(log n) time, instead of scanning the list linearly. The
// * nodes are additionally linked together (previous/next) so that iteration and
// * head/tail access is O(1) per step, giving the combined behavior of a sorted
// * linked list.
// * <p>
// * Note: {@link #get(int)} still requires an O(n) traversal since a plain
// * {@link TreeSet} does not maintain order-statistics (rank) information.
// *
// * @param <V> the element type
// */
//public class SortedLinkedList<V> implements Iterable<V>
//{
//    private static class Node<V>
//    {
//        private final V value;
//        private final long sequence;
//        private Node<V> previous;
//        private Node<V> next;
//
//        public Node(V value, long sequence)
//        {
//            this.value = value;
//            this.sequence = sequence;
//        }
//    }
//
//    private static class NodeComparator<V> implements Comparator<Node<V>>
//    {
//        private final Comparator<? super V> comparator;
//
//        public NodeComparator(Comparator<? super V> comparator)
//        {
//            this.comparator = comparator;
//        }
//
//        @SuppressWarnings("unchecked")
//        private int compareValues(V a, V b)
//        {
//            if (this.comparator != null)
//            {
//                return this.comparator.compare(a, b);
//            }
//            return ((Comparable<? super V>) a).compareTo(b);
//        }
//
//        @Override
//        public int compare(Node<V> a, Node<V> b)
//        {
//            int result = compareValues(a.value, b.value);
//            if (result != 0)
//            {
//                return result;
//            }
//            return Long.compare(a.sequence, b.sequence);
//        }
//    }
//
//    private final NodeComparator<V> nodeComparator;
//    private final TreeSet<Node<V>> tree;
//    private Node<V> head;
//    private Node<V> tail;
//    private long sequenceCounter;
//
//    public SortedLinkedList()
//    {
//        this(null);
//    }
//
//    public SortedLinkedList(Comparator<? super V> comparator)
//    {
//        this.nodeComparator = new NodeComparator<>(comparator);
//        this.tree = new TreeSet<>(this.nodeComparator);
//    }
//
//    /**
//     * Inserts the value in its sorted position in O(log n) time.
//     */
//    public boolean add(V value)
//    {
//        Node<V> node = new Node<>(value, this.sequenceCounter++);
//        this.tree.add(node);
//        link(node);
//        return true;
//    }
//
//    public boolean addAll(Collection<? extends V> values)
//    {
//        boolean changed = false;
//        for (V value : values)
//        {
//            changed |= add(value);
//        }
//        return changed;
//    }
//
//    private void link(Node<V> node)
//    {
//        Node<V> lower = this.tree.lower(node);
//        Node<V> higher = this.tree.higher(node);
//        node.previous = lower;
//        node.next = higher;
//        if (lower != null)
//        {
//            lower.next = node;
//        }
//        else
//        {
//            this.head = node;
//        }
//        if (higher != null)
//        {
//            higher.previous = node;
//        }
//        else
//        {
//            this.tail = node;
//        }
//    }
//
//    private void unlink(Node<V> node)
//    {
//        if (node.previous != null)
//        {
//            node.previous.next = node.next;
//        }
//        else
//        {
//            this.head = node.next;
//        }
//        if (node.next != null)
//        {
//            node.next.previous = node.previous;
//        }
//        else
//        {
//            this.tail = node.previous;
//        }
//        node.previous = null;
//        node.next = null;
//    }
//
//    public V first()
//    {
//        if (this.head == null)
//        {
//            throw new NoSuchElementException();
//        }
//        return this.head.value;
//    }
//
//    public V last()
//    {
//        if (this.tail == null)
//        {
//            throw new NoSuchElementException();
//        }
//        return this.tail.value;
//    }
//
//    public V peekFirst()
//    {
//        return this.head == null ? null : this.head.value;
//    }
//
//    public V peekLast()
//    {
//        return this.tail == null ? null : this.tail.value;
//    }
//
//    public V removeFirst()
//    {
//        if (this.head == null)
//        {
//            throw new NoSuchElementException();
//        }
//        Node<V> node = this.head;
//        this.tree.remove(node);
//        unlink(node);
//        return node.value;
//    }
//
//    public V removeLast()
//    {
//        if (this.tail == null)
//        {
//            throw new NoSuchElementException();
//        }
//        Node<V> node = this.tail;
//        this.tree.remove(node);
//        unlink(node);
//        return node.value;
//    }
//
//    /**
//     * Locates the first node whose value compares equal (per this list's ordering) to
//     * {@code value}, positioned in O(log n) via the backing tree, and returns it, or
//     * {@code null} if none is found. If several nodes compare equal, the (insertion-order)
//     * lowest one whose value is actually {@code equals} to the target is returned.
//     */
//    @SuppressWarnings("unchecked")
//    private Node<V> findNode(Object value)
//    {
//        Node<V> probe = new Node<>((V) value, Long.MIN_VALUE);
//        Node<V> candidate = this.tree.ceiling(probe);
//        while (candidate != null && this.nodeComparator.compareValues(candidate.value, (V) value) == 0)
//        {
//            if (candidate.value == null ? value == null : candidate.value.equals(value))
//            {
//                return candidate;
//            }
//            candidate = this.tree.higher(candidate);
//        }
//        return null;
//    }
//
//    public boolean remove(Object value)
//    {
//        Node<V> node = findNode(value);
//        if (node == null)
//        {
//            return false;
//        }
//        this.tree.remove(node);
//        unlink(node);
//        return true;
//    }
//
//    public boolean removeAllOccurrences(Object value)
//    {
//        boolean changed = false;
//        Node<V> node;
//        while ((node = findNode(value)) != null)
//        {
//            this.tree.remove(node);
//            unlink(node);
//            changed = true;
//        }
//        return changed;
//    }
//
//    public boolean contains(Object value)
//    {
//        return findNode(value) != null;
//    }
//
//    /**
//     * Returns the element at the given index. Requires an O(n) traversal since a plain
//     * {@link TreeSet} does not maintain order-statistics (rank) information.
//     */
//    public V get(int index)
//    {
//        if (index < 0 || index >= size())
//        {
//            throw new IndexOutOfBoundsException(Integer.toString(index));
//        }
//        Node<V> current = this.head;
//        for (int i = 0; i < index; i++)
//        {
//            current = current.next;
//        }
//        return current.value;
//    }
//
//    public int size()
//    {
//        return this.tree.size();
//    }
//
//    public boolean isEmpty()
//    {
//        return this.tree.isEmpty();
//    }
//
//    public void clear()
//    {
//        this.tree.clear();
//        this.head = null;
//        this.tail = null;
//    }
//
//    public Comparator<? super V> comparator()
//    {
//        return this.nodeComparator.comparator;
//    }
//
//    @Override
//    public Iterator<V> iterator()
//    {
//        return new Iterator<V>()
//        {
//            private Node<V> current = SortedLinkedList.this.head;
//            private Node<V> lastReturned;
//
//            @Override
//            public boolean hasNext()
//            {
//                return this.current != null;
//            }
//
//            @Override
//            public V next()
//            {
//                if (this.current == null)
//                {
//                    throw new NoSuchElementException();
//                }
//                this.lastReturned = this.current;
//                this.current = this.current.next;
//                return this.lastReturned.value;
//            }
//
//            @Override
//            public void remove()
//            {
//                if (this.lastReturned == null)
//                {
//                    throw new IllegalStateException();
//                }
//                SortedLinkedList.this.tree.remove(this.lastReturned);
//                unlink(this.lastReturned);
//                this.lastReturned = null;
//            }
//        };
//    }
//
//    public Iterator<V> descendingIterator()
//    {
//        return new Iterator<V>()
//        {
//            private Node<V> current = SortedLinkedList.this.tail;
//            private Node<V> lastReturned;
//
//            @Override
//            public boolean hasNext()
//            {
//                return this.current != null;
//            }
//
//            @Override
//            public V next()
//            {
//                if (this.current == null)
//                {
//                    throw new NoSuchElementException();
//                }
//                this.lastReturned = this.current;
//                this.current = this.current.previous;
//                return this.lastReturned.value;
//            }
//
//            @Override
//            public void remove()
//            {
//                if (this.lastReturned == null)
//                {
//                    throw new IllegalStateException();
//                }
//                SortedLinkedList.this.tree.remove(this.lastReturned);
//                unlink(this.lastReturned);
//                this.lastReturned = null;
//            }
//        };
//    }
//
//    @Override
//    public String toString()
//    {
//        StringBuilder builder = new StringBuilder();
//        builder.append('[');
//        Node<V> current = this.head;
//        while (current != null)
//        {
//            builder.append(current.value);
//            if (current.next != null)
//            {
//                builder.append(", ");
//            }
//            current = current.next;
//        }
//        builder.append(']');
//        return builder.toString();
//    }
//}
