import java.util.*;
import java.io.*;

public class CollectionManagerExample {

    // (...)
    // Implementation Hidden
    // (...)
    
    private static class YarnNode {
        public final YarnExample yarn;
        public YarnNode left;
        public YarnNode right;

        public YarnNode(YarnExample yarn) {
            this(yarn, null, null);
        }

        public YarnNode(YarnExample yarn, YarnNode left, YarnNode right) {
            this.yarn = yarn;
            this.left = left;
            this.right = right;
        }

        public YarnNode(YarnNode toCopy) {
            this(new YarnExample(toCopy.yarn));
            // intentionally leave left and right null
        }

        public String toString() {
            return yarn.toString();
        }
    }
}
