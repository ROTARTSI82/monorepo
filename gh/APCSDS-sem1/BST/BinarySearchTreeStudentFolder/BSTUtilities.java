//a collection of static methods for operating on binary search trees
public abstract class BSTUtilities
{
	//precondition:  t is a binary search tree in ascending order
	//postcondition: returns true if t contains the value x;
	//               otherwise, returns false
	public static boolean contains(TreeNode t, Comparable x, TreeDisplay display)
	{
		throw new RuntimeException("IMPLEMENT ME!!!");
	}

	//precondition:  t is a binary search tree in ascending order
	//postcondition: if t is empty, returns a new tree containing x;
	//               otherwise, returns t, with x having been inserted
	//               at the appropriate position to maintain the binary
	//               search tree property; x is ignored if it is a
	//               duplicate of an element already in t; only one new
	//               TreeNode is created in the course of the traversal
	public static TreeNode insert(TreeNode t, Comparable x, TreeDisplay display)
	{
		throw new RuntimeException("IMPLEMENT ME!!!");
	}

	//precondition:  t is a binary search tree in ascending order
	//postcondition: returns a pointer to a binary search tree,
	//               in which the value at node t has been deleted
	//               (and no new TreeNodes have been created)
	private static TreeNode deleteNode(TreeNode t, TreeDisplay display)
	{
		throw new RuntimeException("IMPLEMENT ME!!!");
	}

	//precondition:  t is a binary search tree in ascending order
	//postcondition: returns a pointer to a binary search tree,
	//               in which the value x has been deleted (if present)
	//               (and no new TreeNodes have been created)
	public static TreeNode delete(TreeNode t, Comparable x, TreeDisplay display)
	{
		throw new RuntimeException("IMPLEMENT ME!!!");
	}
}