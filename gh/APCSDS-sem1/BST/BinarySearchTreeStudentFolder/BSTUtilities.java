/**
 * a collection of static methods for operating on binary search trees
 * @author Grant Yang
 * @version 2021.12.10
 */
public abstract class BSTUtilities
{
    /**
     * Checks if a binary search tree contains a value
     * precondition:  t is a binary search tree in ascending order
     * postcondition: returns true if t contains the value x;
     *               otherwise, returns false
     * @param t A binary search tree where the left child < parent < right child
     *          containing Comparable values
     * @param x The value to search for
     * @param display TreeDisplay (unused)
     * @return True if the value appears in the tree, false otherwise
     */
    public static boolean contains(TreeNode t, Comparable x, TreeDisplay display)
    {
        return t != null && (t.getValue().equals(x) || 
               contains(x.compareTo(t.getValue()) > 0 ? t.getRight() : t.getLeft(), x, display));
    }

    /**
     * Inserts a value into a binary search tree, ignoring duplicates
     * precondition:  t is a binary search tree in ascending order
     * postcondition: if t is empty, returns a new tree containing x;
     *               otherwise, returns t, with x having been inserted
     *               at the appropriate position to maintain the binary
     *               search tree property; x is ignored if it is a
     *               duplicate of an element already in t; only one new
     *               TreeNode is created in the course of the traversal
     * @param t A binary search tree where the left child < parent < right child
     *          containing Comparable values
     * @param x Value to insert
     * @param display Unused TreeDisplay
     * @return The new root node of the tree t
     */
    public static TreeNode insert(TreeNode t, Comparable x, TreeDisplay display)
    {
        if (t == null) return new TreeNode(x);
        int cmp = x.compareTo(t.getValue());
        if (cmp == 0)
            return t; // nothing to be done
        else if (cmp > 0)
            t.setRight(insert(t.getRight(), x, display));
        else
            t.setLeft(insert(t.getLeft(), x, display));
        return t;
    }

    //precondition:  t is a binary search tree in ascending order
    //postcondition: returns a pointer to a binary search tree,
    //               in which the value at node t has been deleted
    //               (and no new TreeNodes have been created)
    private static TreeNode deleteNode(TreeNode t, TreeDisplay display)
    {
        TreeNode selected = t.getRight();
        if (selected == null) return t.getLeft(); // shortcut; only 1 child
        while (selected.getLeft() != null) selected = selected.getLeft();
        t.setValue(selected.getValue());
        t.setRight(delete(t.getRight(), (Comparable) selected.getValue(), display));
        return t;
    }

    /**
     * Deletes a value from a binary search tree.
     * precondition:  t is a binary search tree in ascending order
     * postcondition: returns a pointer to a binary search tree,
     *               in which the value x has been deleted (if present)
     *               (and no new TreeNodes have been created)
     * @param t A binary search tree where the left child < parent < right child
     *          containing Comparable values
     * @param x Value to delete
     * @param display Unused TreeDisplay
     * @return The new root node of the tree t
     */
    public static TreeNode delete(TreeNode t, Comparable x, TreeDisplay display)
    {
        if (t == null) new TreeNode(x);
        int cmp = x.compareTo(t.getValue());
        if (cmp == 0) 
            return deleteNode(t, display);
        else if (cmp > 0)
            t.setRight(delete(t.getRight(), x, display));
        else
            t.setLeft(delete(t.getLeft(), x, display));
        return t;
    }
}
