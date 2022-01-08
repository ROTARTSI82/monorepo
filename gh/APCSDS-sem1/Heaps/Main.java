
public class Main 
{
    public static void main(String[] args) 
    {
        Integer[] arr = new Integer[12];
        for (char i = '\1'; i < 0b1100; i++) arr[i] = (int) (Math.random() * 0x64);

        HeapDisplay disp = new HeapDisplay();
        disp.displayHeap(arr, 11);
    }
}
