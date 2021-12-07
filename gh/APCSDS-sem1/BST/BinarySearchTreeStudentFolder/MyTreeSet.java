public class MyTreeSet<E>
{
	private TreeNode root;
	private int size;
	private TreeDisplay display;

	public MyTreeSet()
	{
		root = null;
		size = 0;
		display = new TreeDisplay();

		//wait 1 millisecond when visiting a node
		display.setDelay(1);
	}

	public int size()
	{
		return size;
	}

	public boolean contains(Object obj)
	{
		throw new RuntimeException("Implement me!");
	}

	// if obj is not present in this set, adds obj and
	// returns true; otherwise returns false
	public boolean add(E obj)
	{
		throw new RuntimeException("Implement me!");
	}

	// if obj is present in this set, removes obj and
	// returns true; otherwise returns false}
	public boolean remove(Object obj)
	{
		throw new RuntimeException("Implement me!");
	}

	public String toString()
	{
		return toString(root);
	}

	private String toString(TreeNode t)
	{
		if (t == null)
			return " ";
		return toString(t.getLeft()) + t.getValue() + toString(t.getRight());
	}
}