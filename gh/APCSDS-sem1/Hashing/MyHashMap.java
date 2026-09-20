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
    private ListNode<MapEntry<K, V>>[] buckets;
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





    }


    public boolean containsValue(Object value)
    {





    }


    public V get(Object key)
    {




    }


    public V put(K key, V value)
    {





    }


    public V remove(Object key)
    {




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



    }


    public Collection<V> values()
    {




    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet()
    {




    }
}
