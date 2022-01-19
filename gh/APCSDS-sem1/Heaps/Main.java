import java.util.Scanner;

/**
 * Tester for all the methods implemented in HeapUtils
 * @author Grant Yang
 * @version 2022.01.13
 */
public class Main
{
    private static final int HEAP_SIZE = 16;

    /**
     * Blocks until the user presses enter
     */
    private static void waitForInp()
    {
        System.out.println("Press enter to continue...");
        new Scanner(System.in).nextLine();
    }

    /**
     * Entry point into the tester
     * @param args Command line arguments passed when java was run
     */
    public static void main(String[] args)
    {
        int heapSize = HEAP_SIZE - 1;
        Comparable[] arr = new Integer[HEAP_SIZE];
        for (int i = 1; i < HEAP_SIZE; i++) arr[i] = (int) (Math.random() * 0x64);

        HeapDisplay disp = new HeapDisplay();
        disp.displayHeap(arr, heapSize);
        waitForInp();

        HeapUtils.buildHeap(arr, heapSize);
        disp.displayHeap(arr, heapSize);
        waitForInp();

        for (int i = 0; i < 4; i++)
        {
            arr = HeapUtils.insert(arr, 100 + (int) (Math.random() * 0x64), heapSize);
            heapSize++;
        }

        disp.displayHeap(arr, heapSize);
        waitForInp();

        HeapUtils.heapSort(arr, heapSize);

        disp.displayHeap(arr, heapSize);
        waitForInp();
    }
}
