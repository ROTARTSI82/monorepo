import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An implementation of a dynamically resizing array
 * @author Grant Yang
 * @version 2021.10.09
 * @param <E> The type stored in the array
 */
public class MyArrayList<E>
{
    private int size;
    private Object[] values; // (Java doesn't let us make an array of type E)

    private byte modificationId = 0;

    /**
     * Constructs a new ArrayList with no elements and capacity of a single element
     */
    public MyArrayList()
    {
        size = 0;
        values = new Object[1];
    }

    /**
     * Constructs a human-readable string representation
     * @return A human-readable representation of this ArrayList as a String
     */
    public String toString()
    {
        if (size() == 0)
            return "[]";

        String s = "[";
        for (int i = 0; i < size() - 1; i++)
            s += values[i] + ", ";
        return s + values[size() - 1] + "]";
    }

    /**
     * @postcondition replaces the array with one that is
     *               twice as long, and copies all of the
     *               old elements into it
     */
    private void doubleCapacity()
    {
        Object[] newVals = new Object[getCapacity() * 2];
        for (int i = 0; i < size(); i++)
            newVals[i] = values[i];

        values = newVals;
    }

    /**
     * Queries the capacity of the ArrayList
     * @return The length of the underlying array
     */
    public int getCapacity()
    {
        return values.length;
    }

    /**
     * Queries the size of the ArrayList
     * @return The number of elements contained in the ArrayList
     */
    public int size()
    {
        return size;
    }

    /**
     * Retrieves the element at a specific index
     * @precondition 0 <= index < size()
     * @param index Index of the element to get (0-indexed)
     * @return The value at index
     */
    public E get(int index)
    {
        return (E) values[index];
    }

    /**
     * Sets the value of a specific element
     * @postcondition Replaces the element at position index with obj
     * @precondition 0 <= index < size()
     * @param index The index of the element to replace
     * @param obj The value to replace it with
     * @return The element formerly at the specified position
     */
    public E set(int index, E obj)
    {
        if (index >= size()) 
            throw new IndexOutOfBoundsException("Cannot get " + index +
                " in list of " + size());
        E prev = (E) values[index];
        values[index] = obj;
        modificationId++;
        return prev;
    }

    /**
     * Inserts an element at the end of the list
     * @postcondition appends obj to end of list;
     * @param obj The value of the object to add to the end
     * @return Always true
     */
    public boolean add(E obj)
    {
        modificationId++;
        /* if values is already full, call doubleCapacity before adding */
        if (size() == getCapacity())
            doubleCapacity();

        values[size++] = obj;
        return true;
    }

    /**
     * Removes a certain element from the list, shifting elements behind it up.
     * @postcondition removes element from position index, moving elements
     *               at position index + 1 and higher to the left
     *               (subtracts 1 from their indices) and adjusts size
     * @precondition 0 <= index < size()
     * @param index The index of the element to remove
     * @return The element formerly at the specified position
     */
    public E remove(int index)
    {
        if (index >= size()) 
            throw new IndexOutOfBoundsException("Cannot remove " + index +
                    " in list of " + size());
        E ret = (E) values[index];
        size--;
        for (int i = index; i < size(); i++)
            values[i] = values[i + 1];
        modificationId++;
        return ret;
    }

    /**
     * Constructs an Iterator over this list
     * @return An iterator to iterate over this array in sequential order
     *         starting from the 1st element
     */
    public Iterator<E> iterator()
    {
        return new MyArrayListIterator(modificationId);
    }

    /**
     * Constructs a ListIterator over this list
     * @return A ListIterator to iterate over this array and perform
     *         the operations specified in the java.util.ListIterator<E> interface
     */
    public ListIterator<E> listIterator()
    {
        return new MyArrayListListIterator(modificationId);
    }

    /**
     * Inserts an element at a specific position into the list
     * @precondition  0 <= index <= size
     * @postcondition inserts obj at position index,
     *               moving elements at position index and higher
     *               to the right (adds 1 to their indices) and adjusts size
     * @param index The index to insert the new element at
     * @param obj The value to insert
     */
    public void add(int index, E obj)
    {
        if (index > size())
            throw new IndexOutOfBoundsException("Cannot add " + index +
                    " in list of " + size());

        if (size() == getCapacity())
            doubleCapacity();

        for (int i = size++; i > index; i--)
            values[i] = values[i - 1];
        values[index] = obj;
        modificationId++;
    }

    /**
     * Inner class iterator to iterate over all elements of a MyArrayList
     * in sequential order.
     */
    private class MyArrayListIterator implements Iterator<E>
    {
        // the index of the value that will be returned by next()
        protected int nextIndex = 0;
        protected byte expectedModId;
        protected boolean isReady = true;
        protected boolean forward = true;

        protected void checkConcurrentMod()
        {
            if (modificationId != expectedModId)
                throw new ConcurrentModificationException("List modified through" +
                        " non-iterator method");
        }

        protected int getIndexForRemove()
        {
            return nextIndex - 1;
        }

        private MyArrayListIterator(byte modId)
        {
            this.expectedModId = modId;
        }

        /**
         * Queries if more elements can be retrieved with next()
         * @return True if more elements may be retrieved with next()
         * @throws ConcurrentModificationException if the list was modified through
         *                                         some method outside the iterator.
         */
        @Override
        public boolean hasNext()
        {
            checkConcurrentMod();
            return nextIndex < size() && nextIndex >= 0;
        }

        /**
         * Queries and discards the next element
         * @precondition haxNext()
         * @postcondition Increments the iterator to point to the next element
         * @return The value of the next element
         * @throws ConcurrentModificationException if the list was modified through
         *                                        some method outside the iterator.
         * @throws NoSuchElementException If no element can be retrieved.
         */
        @Override
        public E next()
        {
            checkConcurrentMod();
            if (!hasNext())
                throw new NoSuchElementException("No next element");

            isReady = true;
            forward = true;
            return get(nextIndex++);
        }

        /**
         * Removes the element that was just returned by next() or previous()
         * @precondition next() or previous() has just been called successfully
         * @postcondition removes the last element that was returned by next
         * @throws IllegalStateException if next() or previous() was not called
         *                               (the precondition was not met)
         * @throws ConcurrentModificationException if the list was modified through
         *                                        some method outside the iterator.
         */
        @Override
        public void remove()
        {
            checkConcurrentMod();
            int targetIndex = getIndexForRemove();
            if (targetIndex >= size() || targetIndex < 0 || !isReady)
                throw new IllegalStateException("Modifier not called immediately after" +
                        " next() or previous()");

            MyArrayList.this.remove(targetIndex);
            if (forward) nextIndex--;

            expectedModId = modificationId;
            isReady = false;
        }
    }

    private class MyArrayListListIterator extends MyArrayListIterator implements ListIterator<E>
    {
        private MyArrayListListIterator(byte mid)
        {
            super(mid);
        }

        @Override
        protected int getIndexForRemove()
        {
            return nextIndex - (forward ? 1 : 0);
        }

        /**
         * Queries if there are more elements behind the cursor to be retrieved
         * @return {@code true} if more elements can be retrieved by {@code previous()}
         * @throws ConcurrentModificationException if the list was modified through
         *                                         some method outside the iterator.
         */
        @Override
        public boolean hasPrevious()
        {
            checkConcurrentMod();
            return nextIndex > 0 && nextIndex <= size();
        }

        /**
         * Retrieves the previous element.
         * @precondition hasPrevious()
         * @postcondition The cursor was moved to the left
         *                (i.e. the iterator was decremented to point to the previous element)
         * @return the previous element in the list
         * @throws NoSuchElementException if the iterator has no previous element
         * @throws ConcurrentModificationException if the list was modified through
         *                                        some method outside the iterator.
         */
        @Override
        public E previous()
        {
            checkConcurrentMod();
            if (!hasPrevious())
                throw new NoSuchElementException("previous() called with no previous element");

            isReady = true;
            forward = false;
            return get(--nextIndex);
        }

        /**
         * Retrieves the index of the element to the right of the cursor.
         * @return Returns the index of the element about to be returned by next()
         * @throws ConcurrentModificationException if the list was modified through
         *                                         some method outside the iterator.
         */
        @Override
        public int nextIndex()
        {
            checkConcurrentMod();
            return nextIndex;
        }

        /**
         * Retrieves the index of the element to the left of the cursor.
         * @return Returns the index of the element about to be returned by previous()
         * @throws ConcurrentModificationException if the list was modified through
         *                                         some method outside the iterator.
         */
        @Override
        public int previousIndex() {
            checkConcurrentMod();
            return nextIndex - 1;
        }

        /**
         * Sets the value of the last element returned by next() or previous()
         * @param e The new value to replace the old one
         * @precondition next() or previous() had just been called successfully
         * @postcondition The value of the element has been updated accordingly
         * @throws IllegalStateException If the precondition was not met
         *                               (next() or previous() not called)
         * @throws ConcurrentModificationException if the list was modified through
         *                                         some method outside the iterator.
         */
        @Override
        public void set(E e)
        {
            checkConcurrentMod();

            int targetIndex = nextIndex - (forward ? 1 : 0);
            if (targetIndex >= size() || targetIndex < 0 || !isReady)
                throw new IllegalStateException("Modifier not called immediately"
                        + " after next() or previous()");

            MyArrayList.this.set(targetIndex, e);
            isReady = false;
            expectedModId = modificationId;
        }

        /**
         * Inserts an element to the left of the cursor so that
         * previous() would return the newly added element and
         * the value of next() is not affected.
         * @postcondition The value was inserted accordingly
         * @param e The value to insert
         * @throws ConcurrentModificationException if the list was modified through
         *                                         some method outside the iterator.
         */
        @Override
        public void add(E e)
        {
            checkConcurrentMod();
            if (nextIndex > size() || nextIndex < 0 || !isReady)
                throw new IllegalStateException("This should be impossible. What did you do‽" +
                        " You know what you did.");

            MyArrayList.this.add(nextIndex, e);
            nextIndex++;
            expectedModId = modificationId;
            isReady = false;
        }
    }
}
