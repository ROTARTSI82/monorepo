package BinarySearchTreeStudentFolder;

/**
 * A node within a binary tree
 * @author Grant Yang
 * @version 2021.12.1
 */
public class TreeNode
{
    private Object value;
    private TreeNode left;
    private TreeNode right;

    /**
     * Constructs a node containing a certain value
     * @param initValue Value of this node
     */
    public TreeNode(Object initValue)
    { 
        this(initValue, null, null);
    }

    /**
     * Constructs a node containing a certain value with
     * the specified children
     * @param initValue Value of this node
     * @param initLeft Left child
     * @param initRight Right child
     */
    public TreeNode(Object initValue, TreeNode initLeft, TreeNode initRight)
    { 
        value = initValue; 
        left = initLeft; 
        right = initRight; 
    }

    /**
     * Getter for the node's value
     * @return The value of this node
     */
    public Object getValue() 
    { return value; }

    /**
     * Getter for the node's left child
     * @return The node's left child
     */
    public TreeNode getLeft() 
    { return left; }

    /**
     * Setter for the node's right child
     * @return The node's right child
     */
    public TreeNode getRight() 
    { return right; }

    /**
     * Mutator for the node's value
     * @param theNewValue Value to replace the node's old value
     */
    public void setValue(Object theNewValue) 
    { value = theNewValue; }

    /**
     * Mutator for the node's left child
     * @param theNewLeft New left child to connect to
     */
    public void setLeft(TreeNode theNewLeft) 
    { left = theNewLeft; }

    /**
     * Mutator for the node's right child
     * @param theNewRight New right child to connect to
     */
    public void setRight(TreeNode theNewRight) 
    { right = theNewRight; }
}