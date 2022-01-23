import java.util.*;
/**
 * MyHashMap acts like a map.
 * 
 * @author  
 * @version 
 * @param <K>   the type of key
 * @param <V>   the type of value
 */
public class MyHashMap<K, V> implements Map<K, V>
{
    private static final int NUM_BUCKETS = 5;

    // this was originally `ListNode` but I assume List is correct
    private List<MapEntry<K, V>>[] buckets;

    private int size;

    /**
     * a constructor
     */
    public MyHashMap()
    {



    }
    
    /**
     * @param obj
     *            the object to find the bucket index for
     * @return the correct bucket index for that object
     */
    private int toBucketIndex(Object obj)
    {
        return Math.abs(obj.hashCode()) % NUM_BUCKETS;
    }


    public int size()
    {
        return size;
    }


    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key)
    {
        return false;




    }


    public boolean containsValue(Object value)
    {
        return false;




    }


    public V get(Object key)
    {

        return null;


    }


    public V put(K key, V value)
    {

        return null;



    }


    public V remove(Object key)
    {

        return null;


    }


    public void putAll(Map<? extends K, ? extends V> m)
    {
        for (K key : m.keySet())
        {
            put(key, m.get(key));
        }
    }


    public void clear()
    {
        for (int i = 0; i < NUM_BUCKETS; i++)
        {
            buckets[i] = null;
        }
    }


    public Set<K> keySet()
    {

        return null;

    }


    public Collection<V> values()
    {

        return null;


    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet()
    {


        return null;

    }
}
