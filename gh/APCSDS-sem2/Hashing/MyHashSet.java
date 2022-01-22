import java.util.*;
/**
* comment this class completely, and in accordance with the style guide.
*/

public class MyHashSet<E>
{
	private static final int NUM_BUCKETS = 5;
	private LinkedList<E>[] buckets;
	private int size;

	public MyHashSet()
	{
		buckets = new LinkedList[NUM_BUCKETS];
		size = 0;

		//MISSING CODE
	}

	//returns the index of the bucket where obj might be found
	private int toBucketIndex(Object obj)
	{
		throw new RuntimeException("IMPLEMENT ME!");
	}

	public int size()
	{
		return size;
	}

	public boolean contains(Object obj)
	{
		throw new RuntimeException("IMPLEMENT ME!");
	}

	// if obj is not present in this set, adds obj and
	// returns true; otherwise returns false
	public boolean add(E obj)
	{
		throw new RuntimeException("IMPLEMENT ME!");
	}

	// if obj is present in this set, removes obj and
	// returns true; otherwise returns false
	public boolean remove(Object obj)
	{
		throw new RuntimeException("IMPLEMENT ME!");
	}

	public String toString()
	{
		String s = "";
		for (int i = 0; i < buckets.length; i++)
			if (buckets[i].size() > 0)
				s += i + ":" + buckets[i] + " ";
		return s;
	}
}