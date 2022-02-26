import java.util.*;
import java.util.function.Consumer;

/**
 * This hash set class provides
 *
 * @author Grant Yang
 * @version 2022.01.21
 * @param <E> Type of the object to be stored in this set.
 */
public class MyHashSet<E>
{
    private static final int NUM_BUCKETS = 3217;
    private final LinkedList<E>[] buckets;
    private int size;

    /**
     * Constructs a new hash set in O(1)
     */
    public MyHashSet()
    {
        buckets = new LinkedList[NUM_BUCKETS];
        size = 0;

        for (int i = 0; i < NUM_BUCKETS; i++)
            buckets[i] = new LinkedList<>();
    }

    /**
     * Gets the bucket index to use for a specific value by hashing
     * it and taking the remainder with the number of available buckets.
     *
     * @param obj Value to convert to a bucket index
     * @return The index of the bucket for this value.
     */
    private int toBucketIndex(Object obj)
    {
        return Math.abs(obj.hashCode()) % NUM_BUCKETS;
    }

    /**
     * Retrieves the cached size attribute in O(1)
     * @return The number of unique values contained within the set.
     */
    public int size()
    {
        return size;
    }

    /**
     * Checks if the hash set contains a value in a time complexity
     * of O(1) on average (not accounting for the hash function)
     *
     * @param obj Value to query for
     * @return True if the set contains the value, false otherwise.
     */
    public boolean contains(Object obj)
    {
        for (E e : buckets[toBucketIndex(obj)])
            if (e.equals(obj)) return true;
        return false;
    }

    /**
     * Attempts to add a value to the hash set by adding it to
     * the bucket corresponding to its hash code.
     * On average, this function has a time complexity of O(1)
     * (not accounting for the complexity of the type's hash function)
     *
     * @param obj Value to add
     * @return True if the new value was added successfully, false otherwise.
     *         (i.e. the value was already contained in the set)
     */
    public boolean add(E obj)
    {
        if (contains(obj)) return false;
        buckets[toBucketIndex(obj)].add(obj);
        size++;
        return true;
    }

    /**
     * Attempts to remove an object from the hash set by
     * removing it from the corresponding bucket.
     * This function has a time complexity of
     * O(1) in the average case (not counting the complexity of the hash function)
     *
     * @param obj Value to remove
     * @return True if the object was successfully removed,
     *         false otherwise (i.e. the value was not found in the set)
     */
    public boolean remove(Object obj)
    {
        if (!contains(obj)) return false;

        // here, you can just write
        // buckets[toBucketIndex(obj)].remove(obj);
        // but the following code is just way cooler

        Iterator<E> it = buckets[toBucketIndex(obj)].iterator();
        while (!it.next().equals(obj));
        it.remove();

        size--;
        return true;
    }

    /**
     * Converts the hash set into a human-readable string,
     * displaying the buckets and the values contained within them.
     * @return A string representing this set
     */
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < buckets.length; i++)
            if (buckets[i].size() > 0)
                s.append(i).append(":").append(buckets[i]).append(" ");
        return s.toString();
    }

    /**
     * Simple iterator implementation for iterating over all the values
     * contained in the buckets.
     *
     * @author Grant Yang
     * @version 2022.01.21
     */
    private class MyIterator implements Iterator<E>
    {
        private int nextBucket = 1;
        private Iterator<E> currIt = buckets[0].iterator();

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext()
        {
            if (currIt.hasNext()) return true;
            for (int i = nextBucket; i < NUM_BUCKETS; i++)
                if (!buckets[i].isEmpty()) return true;
            return false;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public E next()
        {
            while (!currIt.hasNext() && nextBucket < buckets.length)
                currIt = buckets[nextBucket++].iterator();
            return currIt.next();
        }

        /**
         * Removes from the underlying collection the last element returned
         * by this iterator (optional operation).  This method can be called
         * only once per call to {@link #next}.
         * <p>
         * The behavior of an iterator is unspecified if the underlying collection
         * is modified while the iteration is in progress in any way other than by
         * calling this method, unless an overriding class has specified a
         * concurrent modification policy.
         * <p>
         * The behavior of an iterator is unspecified if this method is called
         * after a call to the {@link #forEachRemaining forEachRemaining} method.
         *
         * @throws UnsupportedOperationException if the {@code remove}
         *                                       operation is not supported by this iterator
         * @throws IllegalStateException         if the {@code next} method has not
         *                                       yet been called, or the {@code remove} method has
         *                                       already been called after the last call to the
         *                                       {@code next} method
         * @implSpec The default implementation throws an instance of
         * {@link UnsupportedOperationException} and performs no other action.
         */
        @Override
        public void remove()
        {
            currIt.remove();
            size--;
        }

        /**
         * Performs the given action for each remaining element until all elements
         * have been processed or the action throws an exception.  Actions are
         * performed in the order of iteration, if that order is specified.
         * Exceptions thrown by the action are relayed to the caller.
         * <p>
         * The behavior of an iterator is unspecified if the action modifies the
         * collection in any way (even by calling the {@link #remove remove} method
         * or other mutator methods of {@code Iterator} subtypes),
         * unless an overriding class has specified a concurrent modification policy.
         * <p>
         * Subsequent behavior of an iterator is unspecified if the action throws an
         * exception.
         *
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         * @implSpec <p>The default implementation behaves as if:
         * <pre>{@code
         *     while (hasNext())
         *         action.accept(next());
         * }</pre>
         * @since 1.8
         */
        @Override
        public void forEachRemaining(Consumer<? super E> action)
        {
            Iterator.super.forEachRemaining(action);
        }
    }

    /**
     * Retrieves an iterator over all the elements in the
     * hash set. This method allows MyHashSet to be used in enhanced for loops.
     * @return An MyIterator object implementing the {@code Iterator<E>} interface
     *         which can be used to go over all the elements in the set.
     */
    public MyIterator iterator()
    {
        return new MyIterator();
    }
}