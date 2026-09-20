public class StatefulHeapUtil extends HeapUtils
{
    private Comparable[] arr;
    private int heapSize;

    public StatefulHeapUtil(Comparable[] arr, int size)
    {
        this.arr = arr;
        this.heapSize = size;
    }


}
