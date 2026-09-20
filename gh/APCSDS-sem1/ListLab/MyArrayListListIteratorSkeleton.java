import java.util.Iterator;
import java.util.ListIterator;
import java.util.ConcurrentModificationException;

/**
 * A ListIterator that provides bi-directional traversal 
 * over a list collection
 * @author  Anu Datar
 * @version 10/07/2018
 * @param <E> The type that is being iterated over
 */
public class MyArrayListListIteratorSkeleton<E>
{
    private int size; //number of elements in list
    private Object[] values; // Array of elements
    private boolean modified; // Whether elements have been added/removed

    /**
     * Constructor of MyArrayListListIteratorSkeleton
     */
    public MyArrayListListIteratorSkeleton()
    {
        size = 0;
        values = new Object[1];
        modified = false; // Use this to throw a Concurrent 
        // Modification Exception in your iterator class

    }
    // ... All your other methods 
    /**
     * Returns a new iterator for MyArrayList
     * @return a MyArrayListIterator
     */
    public Iterator<E> iterator()
    {
        return new MyArrayListIterator();
    }

    /**
     * Returns a new listIterator for MyArrayList
     * @return a MyArrayListListIterator
     */
    public ListIterator<E> listIterator()
    {
        return new MyArrayListListIterator();
    }


    /**
     * Contains constructor and methods for a MyArrayListIterator
     */
    private class MyArrayListIterator implements Iterator<E>
    {
        // All your iterator methods like hasNext() , next() etc

        /**
         * Removes element that was returned by next()
         */
        public void remove()
        {
            // MyArrayList.this.remove(nextIndex);
        }

        public boolean hasNext()
        {
            return false;
        }

        public E next()
        {
            return null;
        }
    }

    private class MyArrayListListIterator extends MyArrayListIterator implements ListIterator<E>
    {
        // note the extends MyArrayListIterator 
        // Remember this class thus inherits the methods from the parent class.

        private int nextIndex;

        // Index of element that will be returned by the next call of next()
        private int previousIndex;

        private boolean forward; //direction of traversal

        /**
         * Constructs a new MyArrayListListIterator
         */
        public MyArrayListListIterator()
        {
            nextIndex = 0;
            previousIndex = -1;
            forward = true;
        }
        
        /**
         * Returns next element and moves pointer forward
         * @return next Object in MyArryaList
         */
        public E next()
        {
            if (modified)
            {
                throw new ConcurrentModificationException();
            }
            System.out.println("INSERT MISSING CODE HERE");
            return null;
        }

        /**
         * Adds element before element that would be returned by next
         * @param obj The value to add at the iterator's position, which is
         *            right before the value about to be returned by next()
         */
        public void add(E obj)
        {
            System.out.println("INSERT MISSING CODE HERE");
        }

        /**
         * Determines whether there is another element in MyArrayList
         * while traversing in the backward direction
         * @return true if there is another element, false otherwise
         */
        public boolean hasPrevious()
        {
            System.out.println("INSERT MISSING CODE HERE");
            return false;
        }

        /**
         * @postcondition Moves the iterator one element to the left
         * @return The previous element in the list
         */
        public E previous()
        {
            System.out.println("INSERT MISSING CODE HERE");
            return null;
        }

        /**
         * Returns index of the next element 
         * @return index of element that would be 
         *         returned by a call to next()
         */
        public int nextIndex()
        {
            System.out.println("INSERT MISSING CODE HERE");
            return -1;
        }

        /**
         * @return The index of the element that would be returned by previous()
         */
        public int previousIndex()
        {
            System.out.println("INSERT MISSING CODE HERE");
            return -1;
        }

        /**
         * Removes element that was returned by next() pr previous()
         * USE direction FOR THIS
         */
        public void remove()
        {
            System.out.println("INSERT MISSING CODE HERE");
        }

        /**
         * Sets the value of the element that was returned by next() or previous()
         * @param obj The value to replace the old value
         */
        public void set(E obj)
        {
            System.out.println("INSERT MISSING CODE HERE");
        }
    }
}