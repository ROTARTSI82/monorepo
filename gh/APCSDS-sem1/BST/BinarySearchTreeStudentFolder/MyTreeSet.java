package BinarySearchTreeStudentFolder;

/**
 * @author Grant Yang
 * @param <E> The type of the objects that this set will contain
 * @version 2022.01.17
 */
public class MyTreeSet<E extends Comparable>
{
    private TreeNode root;
    private int size;
    private final TreeDisplay display;

    /**
     * Constructs an empty TreeSet.
     * Time complexity: O(1)
     */
    public MyTreeSet()
    {
        root = null;
        size = 0;
        display = new TreeDisplay();

        //wait 1 millisecond when visiting a node
        display.setDelay(1);
    }

    /**
     * size() is a getter for a cached property.
     * It retrieves the size of the TreeSet in O(1)
     * @return The number of unique elements currently stored in the set.
     */
    public int size()
    {
        return size;
    }

    /**
     * Queries if the set contains a specific value
     * in O(log n) with n being the number of elements in the set.
     * It recursively traverses down a binary search tree starting at the root,
     * looking in the right subtree if the value is greater than a node
     * and looking left if lesser until the value is found or a leaf node is reached.
     * @param obj The value to look for
     * @return True if the set contains the value, false otherwise.
     */
    public boolean contains(E obj)
    {
        return BSTUtilities.contains(root, obj, display);
    }

    /**
     * add() attempts to insert the specified value into the set,
     * taking O(log n) in either case (with n being the number of elements in the set)
     * @postcondition If the value is already in the set, nothing happens. Otherwise,
     *                the value is inserted into the set.
     * @param obj The value to be inserted into the set
     * @return True if the value was inserted (i.e. it wasn't already in the set),
     *         false otherwise.
     */
    public boolean add(E obj)
    {
        if (contains(obj))
        {
            return false;
        }

        root = BSTUtilities.insert(root, obj, display);
        size++;
        return true;
    }

    /**
     * remove() attempts the remove a value from the set,
     * taking O(log n) with n being the number of elements in the set
     * in either case.
     * @postcondition If the element was not in the set, no operation occurs.
     *                Otherwise, it is removed from the set.
     * @param obj The value to remove from the set
     * @return True if a value has been removed (it was in the set before),
     *         false if nothing was changed.
     */
    public boolean remove(E obj)
    {
        if (contains(obj))
        {
            root = BSTUtilities.delete(root, obj, display);
            size--;
            return true;
        }

        return false;
    }

    /**
     * Converts the set into a human-readable string in
     * O(n), with n being the number of elements in the set.
     * @return A string containing the values in the set separated by
     *         commas in infix traversal order.
     */
    @Override
    public String toString()
    {
        return toString(root);
    }

    /**
     * Recursively converts a node to a human-readable string
     * in O(n) with n being the number of child nodes.
     * @param t The node to turn into a string
     * @return An infix order traversal of the tree rooted at t.
     */
    private String toString(TreeNode t)
    {
        if (t == null)
            return " ";
        return toString(t.getLeft()) + t.getValue() + toString(t.getRight());
    }
}