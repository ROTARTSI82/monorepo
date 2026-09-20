import java.util.*;
/**
 * MyHashMap acts like a map.
 * 
 * @author  Grant Yang
 * @version  2022.02.26
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
        buckets = new List[NUM_BUCKETS];
        for (int i = 0; i < NUM_BUCKETS; i++)
            buckets[i] = new ArrayList<MapEntry<K, V>>();
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
        List<MapEntry<K, V>> bucket = buckets[toBucketIndex(key)];
        for (MapEntry<K, V> entry : bucket)
            if (entry.getKey().equals(key))
                return true;
        return false;
    }


    public boolean containsValue(Object value)
    {
        for (List<MapEntry<K, V>> bucket : buckets)
            for (MapEntry<K, V> entry : bucket)
                if (entry.getValue().equals(value)) 
                    return true;
        return false;
    }


    public V get(Object key)
    {
        List<MapEntry<K, V>> bucket = buckets[toBucketIndex(key)];
        for (MapEntry<K, V> entry : bucket)
            if (entry.getKey().equals(key))
                return entry.getValue();
        return null;
    }


    public V put(K key, V value)
    {
        List<MapEntry<K, V>> bucket = buckets[toBucketIndex(key)];
        for (MapEntry<K, V> entry : bucket)
            if (entry.getKey().equals(key))
            {
                V ret = entry.getValue();
                entry.setValue(value);
                return ret;
            }
        
        bucket.add(new MapEntry<K, V>(key, value));
        size++;
        return null;
    }


    public V remove(Object key)
    {
        Iterator<MapEntry<K, V>> it = buckets[toBucketIndex(key)].iterator();
        while (it.hasNext()) 
        {
            MapEntry<K, V> next = it.next();
            if (next.getKey().equals(key))
            {
                it.remove();
                size--;
                return next.getValue();
            }
        }

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
            buckets[i] = new ArrayList<MapEntry<K, V>>();
        }
    }


    public Set<K> keySet()
    {
        Set<K> ret = new HashSet<K>();
        for (List<MapEntry<K, V>> bucket : buckets)
            for (MapEntry<K, V> entry : bucket)
                ret.add(entry.getKey());
        return ret;
    }


    public Collection<V> values()
    {
        Collection<V> ret = new ArrayList<V>();
        for (List<MapEntry<K, V>> bucket : buckets)
            for (MapEntry<K, V> entry : bucket)
                ret.add(entry.getValue());
        return ret;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet()
    {
        Set<java.util.Map.Entry<K, V>> ret = new HashSet<java.util.Map.Entry<K, V>>();
        for (List<MapEntry<K, V>> bucket : buckets)
            for (MapEntry<K, V> entry : bucket)
                ret.add(entry);
        return ret;
    }
}
