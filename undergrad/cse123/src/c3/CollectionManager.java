import java.io.PrintStream;
import java.util.*;
import java.util.function.Function;

public class CollectionManager {
    
    private BSTNode root;

    public CollectionManager() {
        root = null;
    }

    private BSTNode readRecursor(Scanner in) {
        Email res = Email.parse(in);
        if (res == null)
            return null;
        return new BSTNode(res, readRecursor(in), readRecursor(in));
    }

    public CollectionManager(Scanner in) {
        root = readRecursor(in);
    }

    private BSTNode addRecursor(Email item, BSTNode tree) {
        if (tree == null)
            return new BSTNode(item, null, null);
        if (item.equals(tree.item))
            return tree;
        if (item.compareTo(tree.item) < 0)
            tree.left = addRecursor(item, tree.left);
        else
            tree.right = addRecursor(item, tree.right);
        return tree;
    }

    public void add(Email item) {
        root = addRecursor(item, root);
    }

    private boolean containsRecursor(Email item, BSTNode tree) {
        if (tree == null)
            return false;
        if (item.equals(tree.item))
            return true;
        if (item.compareTo(tree.item) < 0)
            return containsRecursor(item, tree.left);
        return containsRecursor(item, tree.right);
    }

    public boolean contains(Email item) {
        return containsRecursor(item, root);
    }

    private Map.Entry<BSTNode, BSTNode> rmMaxMin(BSTNode tree, boolean maximize) {
        if (maximize) {
            if (tree.right == null)
                return new AbstractMap.SimpleEntry<>(tree.left, tree);
        } else {
            if (tree.left == null)
                return new AbstractMap.SimpleEntry<>(tree.right, tree);
        }
        return null;
    }

    private void saveRecursor(PrintStream out, BSTNode tree) {
        if (tree != null) {
            out.println(tree.item);
            saveRecursor(out, tree.left);
            saveRecursor(out, tree.right);
        } else {
            out.println("null");
        }
    }

    public void save(PrintStream output) {
        saveRecursor(output, root);
    }

    private BSTNode rmRoot(BSTNode tree) {
        if (tree.left != null) {
            BSTNode cursor = tree.left;
            if (cursor.right == null) {
                cursor.right = tree.right;
                return cursor;
            }

            while (cursor.right.right != null)
                cursor = cursor.right;
            BSTNode ret = cursor.right;
            ret.left = tree.left;
            ret.right = tree.right;
            cursor.right = null;
            return ret;
        } else if (tree.right != null) {
            BSTNode cursor = tree.right;
            if (cursor.left == null) {
                // cursor.left = tree.left;
                return cursor;
            }

            while (cursor.left.left != null)
                cursor = cursor.left;
            BSTNode ret = cursor.left;
            // ret.left = tree.left;
            ret.right = tree.right;
            cursor.left = null;
            return ret;
        }

        return null;
    }

    private BSTNode rmAll(BSTNode tree, Function<Email, Boolean> predicate) {
        if (tree == null)
            return null;
        tree.left = rmAll(tree.left, predicate);
        tree.right = rmAll(tree.right, predicate);
        if (predicate.apply(tree.item))
            return rmRoot(tree);
        return tree;
    }

    private static class BSTNode {
        public final Email item;
        public BSTNode left, right;

        public BSTNode(Email item, BSTNode left, BSTNode right) {
            this.item = item;
            this.left = left;
            this.right = right;
        }
    }
}
