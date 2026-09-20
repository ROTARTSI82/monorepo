/**
 * The Sorter class implements many sorting algorithms
 * and has a GUI to demo the algorithms
 * @author Grant Yang
 * @version 2022.02.26
 */
public class Sorter
{
    private SortDisplay display;
    
    /**
    * main method instantiates a sorter instance
    * Usage: called directly by the IDE or when Java is launched 
    * ------------------------------------------
    * Creates a Sorter object, but calls no methods from Sorter 
    * because the GUI SortDisplay calls sort methods in Sorter
    * 
    * @param args an array of arguments for legacy command line
    *              the values are not used
    */
    public static void main(String[] args)
    {
        Sorter sorter = new Sorter();
    }
    
    /**
    * Constructor: Sorter()
    * Usage:  Sorter aSorter = new Sorter()
    * ________________________________________
    * constructor for Sorter objects.  Creates a new display, which controls
    * all the sorting by means of call-backs to this class.
    */
    public Sorter()
    {
        display = new SortDisplay(this);
    }

    /**
     * indexOfMin finds the index of the minimum element in the array
     * starting at startIndex (inclusive) and returns it in O(n).
     * @param a Array to find the minimum of
     * @param startIndex Index of first element to consider
     * @return An index into `a` of the minimum element found.
     *         In the range of [startIndex, a.length)
     */
    public int indexOfMin(Comparable[] a, int startIndex)
    {
        Comparable min = null;
        int ind = 0;
        for (int i = startIndex; i < a.length; i++)
            if (min == null || a[i].compareTo(min) < 0)
            {
                ind = i;
                min = a[i];
            }

        return ind;
    }

    /**
     * Sorts an array in-place using selection sort in O(n^2).
     * It swaps elements from left to right with the minimum of
     * the array (excluding elements already examined to the left).
     * @postcondition `a` is sorted in ascending order
     * @param a Array to sort
     */
    public void selectionSort(Comparable[] a)
    {
        for (int i = 0; i < a.length - 1; i++)
        {
            int swapWith = indexOfMin(a, i);
            
            Comparable tmp = a[swapWith];
            a[swapWith] = a[i];
            a[i] = tmp;
            display.update();
        }
    }

    /**
     * Inserts the specified index to the sorted array to its left
     * by moving it down until it is in the correct location.
     * This operation takes O(n) with n being the number of elements to the left.
     * @precondition 0 < nextIndex < a.length
     * @postcondition The subarray a[0..nextIndex+1] is sorted in ascending order
     * @param a Array insert in
     * @param nextIndex The index to insert into sorted portion to the left
     */
    public void insert(Comparable[] a, int nextIndex)
    {
        int i;
        Comparable value = a[nextIndex];
        for (i = nextIndex - 1; i >= 0 && a[i].compareTo(value) > 0; i--)
            a[i + 1] = a[i];
        a[i + 1] = value;
        display.update();
    }

    /**
     * Sorts an array using insertion sort in O(n^2).
     * It builds a sorted array from left to right by
     * repeatedly inserting the next element in the unsorted
     * portion by moving it left until it's in the correct spot.
     * @postcondition The array is sorted in ascending order
     * @param a Array to sort
     */
    public void insertionSort(Comparable[] a)
    {
        for (int i = 1; i < a.length; i++)
            insert(a, i);
    }

    /**
     * Sorts an array in O(n log(n)) using merge sort.
     * It recursively mergeSorts() the two halves of the array and
     * merges the sorted halves together in O(n)
     * @postcondition a is sorted in ascending order
     * @param a Array to sort
     */
    public void mergesort(Comparable[] a)
    {
        mergesortHelp(a, 0, a.length - 1);
    }

    /**
     * Sorts a subarray in O(n log(n)) by recursively sorting the two halves
     * and merging them in O(n). It merges by repeatedly moving the minimum
     * of the first elements of the two sorted halves into the new merged array.
     * @precondition 0 <= lowIndex < a.length AND lowIndex <= highIndex < a.length
     * @postcondition the subarray a[lowIndex..highIndex+1] is sorted in ascending order
     * @param a array to sort
     * @param lowIndex Inclusive lower index of the subarray to sort
     * @param highIndex Inclusive higher index of the subarray to sort
     */
    private void mergesortHelp(Comparable[] a, int lowIndex, int highIndex)
    {   
        if (highIndex - lowIndex < 1) return;
        int mid = (lowIndex + highIndex) / 2;
        mergesortHelp(a, lowIndex, mid);
        mergesortHelp(a, mid + 1, highIndex);
        merge(a, lowIndex, mid, highIndex);
    }
    
    /**
    * method merge()
    * Useage: merge(inputArray, lowIndex, midIndex, highIndex)
    *_______________________________________________
    * Merges the two halves of the input array into one.  The method assumes
    * that each half of the input array is sorted as follows:
    * 
    *                a[lowIndex] to a[midIndex] are in increasing order.
    *                a[midIndex + 1] to a[highIndex] are in increasing order.
    * The method creates an array to hold the output.  It then establishes 
    * two pointers into the two halves of the input array.  The values at the
    * pointer locations are compared, and the smallest is added to the output
    * array.  The corresponding pointer is then increased by one.  In the event
    * either half becomes empty, the remaining values are copied to the output
    * array.
    * Postcondition: a[lowIndex] to a[highIndex] are in increasing order.
    *
    * @param a is the input array of Comparable values
    * @param lowIndex is the index into the array a corresponding to the beginning
    *        of the first half of the array to merge
    * @param midIndex is the index of the last value in the first half of the array
    * @param highIndex is the index of the last value in the second half of the array
    */
    private void merge(Comparable[] a, int lowIndex, int midIndex, int highIndex)
    {
        Comparable[] copy = new Comparable[a.length];
        for (int i = lowIndex; i <= highIndex; i++)
            copy[i] = a[i];
        int left = lowIndex;
        int right = midIndex + 1;
        for (int i = lowIndex; i <= highIndex; i++)
        {
            if (right > highIndex ||
                    (left <= midIndex && copy[left].compareTo(copy[right]) < 0))
            {
                a[i] = copy[left];
                left++;
            }
            else
            {
                a[i] = copy[right];
                right++;
            }
            display.update();
        }
    }

    /**     
     * Method: quicksort()
     * Usage: sorter.quicksort(inputArray)
     * -------------------------------------
     * quicksort() does not actual do the sorting,
     * just calls quicksortHelp with parameters (a, 0, a.length-1),
     * which does the actual quick sorting
     * 
     * Postcondition: a[lowIndex] to a[highIndex] are in increasing order
     * @param a - array of comparable elements to be sorted with quick sort
     */
    public void quicksort(Comparable[] a)
    {
        /* To be implemented post the AP Exam */
    }

    /**
     * Method: quicksortHelp()
     * Usage: quicksortHelp(a, low, high)
     * ------------------------------------------
     * Quick soritng is a recursive sorting algorithm that sets a pivot point
     * (lowIndex in this case)
     * and calls partition which rough sort: puts every element less than pivot left of pivot,
     * and every element bigger than pivot right of pivot
     * then quicksortHelp is called on the sections left & right of the pivot point
     * 
     * Base case: section of the array given by low & highIndex has 1 element (high <= low), 
     *         which is "sorted" by definition, therefore nothing is done to it
     * Recursive reduction: the element at lowIndex is sorted as the pivot using partition() and
     * the index where it lands is returned.
     *         The array is then divided from (low,pivot-1) & (pivot+1,high) because index pivot is
     *         already sorted
     *         and quicksortHelp is used again on sections left & right of the pivot element
     * 
     * Postcondition: a[lowIndex] to a[highIndex] are in increasing order
     * @param a - array of comparable elements to be sorted with quick sort
     * @param lowIndex - beginning index of section of array to be sorted
     * @param highIndex - ending index of section of array to be sorted
     */
    private void quicksortHelp(Comparable[] a, int lowIndex, int highIndex)
    {   
        /* To be implemented post the AP Exam */
    }
    
    /**
    * Method partition
    * Usuage: int pivotIndex = partition(a, lowIndex, highIndex)
    *___________________________________________________________
    *
    *Returns the index of the pivot element defined as follows:
    *                All elements on the left side of the pivot (from lowIndex)
    *                are less than or equal to the pivot.
    *                All elements on the right side of the pivot (through highIndex)
    *                are greater than or equal to the pivot.
    * The computation is performed in place.
    * @param a the array to partion
    * @param lowIndex is the index of the start of the part of array a to consider
    * @param highIndex is the index of the end of the part of array a to consider
    * @return the index of the pivot element in array a
    */
    private int partition(Comparable[] a, int lowIndex, int highIndex)
    {
        /* To be implemented post the AP Exam */
        return -1;
    }
}