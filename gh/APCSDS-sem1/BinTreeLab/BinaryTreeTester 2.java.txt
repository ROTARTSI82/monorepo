import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BinaryTreeTester
{
    public void test()
    {
	    TreeDisplay display = new TreeDisplay();
        // to get the display to send back the values when it visits a node:
        display.setTester(this);
        // test to see that the call back works
        TreeNode t = TreeUtil.createRandom(6);
        display.displayTree(t);
        TreeUtil.preOrder(t, display);
    }
    /**
    * called by the display object to send back the node value
    * when a node is visited
    */
    public void sendValue(Object value)
    {
        System.out.println(value);
    }
}
