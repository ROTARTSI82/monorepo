/**
 * HeapUtils contains the following collection of static methods
 * to manipulate heaps and sort arrays using heap sort:
 *
 * public static <T> void swap(T[] arr, int ind1, int ind2)
 * public static void heapify(Comparable[] heap, int index, int heapSize)
 * public static void buildHeap(Comparable[] heap, int heapSize)
 * public static Comparable remove(Comparable[] heap, int heapSize)
 * public static Comparable[] insert(Comparable[] heap, Comparable item, int heapSize)
 * public static void heapSort(Comparable[] arr, int heapSize)
 *
 * @author Grant Yang
 * @version 2022.01.08
 */
public class HeapUtils
{

    /**
     * Swaps two elements at specific indices in an array
     * This operation has a time complexity of O(1)
     * @precondition ind1 and ind2 are in bounds to the array
     *               i.e. they are in the range of [0, arr.length)
     * @postcondition The element at index ind2 can now be found at ind1
     *                and the element at ind1 can be found at ind2
     * @param arr Array to swap elements in. This array will be modified by swap()
     * @param ind1 One of the indices to swap
     * @param ind2 One of the indices to swap
     * @param <T> Type of the array elements
     */
    public static <T> void swap(T[] arr, int ind1, int ind2)
    {
        T tmp = arr[ind1];
        arr[ind1] = arr[ind2];
        arr[ind2] = tmp;
    }

    /**
     * heapify() rearranges elements until the entire subtree rooted at `index`
     * satisfies the max-heap property by recursively swapping parents with their children.
     * If the element at `index` is less than one of its children, it is swapped with its
     * largest child. Then, heapify() is recursively called on that child to bubble the value
     * down into the correct spot.
     * This function has a time complexity of O(log(n)), with n being the number of elements
     * in the heap.
     * @precondition heapSize < heap.length and 0 < index <= heapSize.
     *               The left and right subtrees of the node at index satisfy the max-heap property
     * @postcondition The value at index has been bubbled down to its proper spot
     *                and the tree rooted at index will satisfy the max-heap property
     * @param heap The heap to heapify. It is stored in an array with the first element
     *             at index 1 such that the parent of a node at index i can be found at i / 2
     *             and children may be found at 2 * i and 2 * i + 1 respectively.
     * @param index Index of the root element of the subheap to heapify
     * @param heapSize The size of the heap, specifying the range of values considered to be
     *                 part of the heap. Elements with indices outside the range of [1, heapSize]
     *                 will not be modified.
     */
    public static void heapify(Comparable[] heap, int index, int heapSize)
    {
        if (index * 2 > heapSize) return; // nothing to do! this is a leaf
        if (index * 2 + 1 > heapSize)
        {
            // single child
            if (heap[index * 2].compareTo(heap[index]) > 0)
                swap(heap, index * 2, index);
        }
        else if (heap[index * 2].compareTo(heap[index]) > 0
                || heap[index * 2 + 1].compareTo(heap[index]) > 0)
        {
            int maxChild;
            if (heap[index * 2 + 1].compareTo(heap[index * 2]) > 0)
                maxChild = index * 2 + 1;
            else
                maxChild = index * 2;
            swap(heap, index, maxChild);
            heapify(heap, maxChild, heapSize);
        }
    }

    /**
     * buildHeap() builds a heap by calling heapify() on all non-leaf nodes
     * from the deepest nodes to the shallowest until it gets to the root.
     * Thus, it arranges all subtrees into a max-heap starting at the bottom
     * before calling heapify() on a parent so that heapify()'s precondition is met.
     * Eventually, it is able to call heapify() on the root and make the entire
     * range into a heap.
     * This function has a time complexity of O(n log(n)), with n being the number
     * of elements in the heap
     * @precondition heapSize < heap.length
     * @poscondition The elements with indices in the range of [1, heapSize]
     *               are rearranged such that they form a level-order traversal of a max-heap
     * @param heap The heap to heapify. It is stored in an array with the first element
     *             at index 1 such that the parent of a node at index i can be found at i / 2
     *             and children may be found at 2 * i and 2 * i + 1 respectively.
     * @param heapSize The size of the heap, specifying the range of values considered to be
     *                 part of the heap. Elements with indices outside the range of [1, heapSize]
     *                 will not be modified.
     */
    public static void buildHeap(Comparable[] heap, int heapSize)
    {
        for (int i = heapSize / 2; i > 0; i--)
            heapify(heap, i, heapSize);
    }

    /**
     * remove() removes the root node of a heap by swapping it with the last
     * element in the heap and bubbling the new value in the root down using heapify(),
     * swapping it with its children until it is greater than all of its children.
     * This operation has a time complexity of O(log n),
     * with n being the number of elements in the heap.
     * @precondition heapSize < heap.length
     *               Elements with indices in the range of [1, heapSize] form a
     *               level-order traversal of a max-heap.
     * @postcondition The element at index 1 has been moved
     *                to index heapSize, but elements in the range of
     *                [1, heapSize) still satisfy the max-heap property,
     *                and the heap still contains all the values it did
     *                before (except for the root, which has been moved to the end).
     *                At the end, the heapSize effectively decreases by 1
     * @param heap The heap to remove from. It is stored in an array with the first element
     *             at index 1 such that the parent of a node at index i can be found at i / 2
     *             and children may be found at 2 * i and 2 * i + 1 respectively.
     * @param heapSize The size of the heap, which specifies the range of values considered to be
     *                  part of the heap. Elements with indices outside the range of [1, heapSize]
     *                  will not be modified.
     * @return Returns the value of the element that was removed from the heap
     */
    public static Comparable remove(Comparable[] heap, int heapSize)
    {
        swap(heap, 1, heapSize);
        heapify(heap, 1, heapSize - 1);
        return heap[heapSize];
    }

    /**
     * insert() inserts a value into the heap by inserting it
     * at the bottom of the heap and bubbling it up until its
     * parent is greater than it. Note that this method creates
     * a copy of the heap and returns it, and that the array that
     * was passed in is NOT modified!
     * In theory, this operation should take a time complexity of O(log(n)),
     * with n being the number of elements in the heap. However, due to the need
     * to copy all elements into a new array, it takes O(n) with n being
     * the number of elements in the array.
     * @precondition heapSize < heap.length
     *               Elements with indices in the range of [1, heapSize] form a
     *               level-order traversal of a max-heap.
     * @param heap The heap to insert into. It is stored in an array with the first element
     *             at index 1 such that the parent of a node at index i can be found at i / 2
     *             and children may be found at 2 * i and 2 * i + 1 respectively.
     * @param item The value to insert into the heap
     * @param heapSize The size of the heap, specifying the range of values considered to be
     *                 part of the heap. Elements with indices in the range of [1, heapSize]
     *                 will be considered part of the heap.
     * @return The new heap, which is now one larger.
     *         The elements with indices in the range of [1, heapSize + 1]
     *         form a max-heap with the new value inserted appropriately.
     *         Elements that were formerly found after the heap can still be found at
     *         1 index higher due to the new value that was inserted into the heap.
     */
    public static Comparable[] insert(Comparable[] heap, Comparable item, int heapSize)
    {
        Comparable[] ret = new Comparable[heap.length + 1];
        for (int i = 0; i <= heapSize; i++)
            ret[i] = heap[i];

        heapSize++;
        ret[heapSize] = item;
        int iter = heapSize;
        while (iter > 1)
        {
            if (ret[iter].compareTo(ret[iter / 2]) > 0)
                swap(ret, iter, iter / 2);
            else
                iter = 0; // break;
            iter /= 2;
        }

        for (int i = heapSize + 1; i < ret.length; i++)
            ret[i] = heap[i - 1];
        return ret;
    }

    /**
     * heapSort() sorts an array in ascending order using a heap.
     * It arranges the values in [1, heapSize] into a heap and repeatedly calls
     * remove() until the heap is emptied, resulting in a sorted array.
     * In practice, heapSize SHOULD be equal to one less than the size of the array.
     * This function has a time complexity of O(n log(n)), with n being the number
     * of elements that must be sorted.
     * @precondition heapSize < arr.length
     * @postcondition The elements with indices in the range of [1, heapSize]
     *                are sorted in ascending order
     * @param arr Array to sort, which may contain arbitrary values
     * @param heapSize Size of the heap to use. Elements with indices outside the range of
     *                 [1, heapSize] will not be modified.
     */
    public static void heapSort(Comparable[] arr, int heapSize)
    {
        // assert heapSize == arr.length - 1;
        buildHeap(arr, heapSize);
        for (; heapSize > 1; heapSize--)
            remove(arr, heapSize);
    }
}
