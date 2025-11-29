import java.io.*;
import java.util.*;

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

    // returns (new root, max node)
    private Map.Entry<BSTNode, BSTNode> rmMaxOrMin(BSTNode tree, boolean maximize) {
        if (maximize) {
            if (tree.right == null)
                return new AbstractMap.SimpleEntry<>(tree.left, tree);
            Map.Entry<BSTNode, BSTNode> res = rmMaxOrMin(tree.right, true);
            tree.right = res.getKey();
            return new AbstractMap.SimpleEntry<>(tree, res.getValue());
        } else {
            if (tree.left == null)
                return new AbstractMap.SimpleEntry<>(tree.right, tree);
            Map.Entry<BSTNode, BSTNode> res = rmMaxOrMin(tree.left, false);
            tree.left = res.getKey();
            return new AbstractMap.SimpleEntry<>(tree, res.getValue());
        }
    }

    private BSTNode rmRoot(BSTNode tree) {
        if (tree.left != null) {
            Map.Entry<BSTNode, BSTNode> res = rmMaxOrMin(tree.left, true);
            BSTNode ret = res.getValue();
            ret.left = res.getKey();
            ret.right = tree.right;
            return ret;
        } else if (tree.right != null) {
            Map.Entry<BSTNode, BSTNode> res = rmMaxOrMin(tree.right, false);
            BSTNode ret = res.getValue();
            ret.right = res.getKey();
            ret.left = tree.left;
            return ret;
        }

        return null;
    }

    private BSTNode rmBetweenRecursor(BSTNode tree, long timeLo, long timeHi) {
        if (tree == null)
            return null;
        tree.left = rmBetweenRecursor(tree.left, timeLo, timeHi);
        tree.right = rmBetweenRecursor(tree.right, timeLo, timeHi);
        if (tree.item.compareTime(timeLo) > 0 && tree.item.compareTime(timeHi) < 0)
            return rmRoot(tree);
        return tree;
    }

    public void removeBetween(long timeLo, long timeHi) {
        root = rmBetweenRecursor(root, timeLo, timeHi);
    }

    public String toString() {
        return root.toString();
    }

    private static class BSTNode {
        public final Email item;
        public BSTNode left, right;

        public BSTNode(Email item, BSTNode left, BSTNode right) {
            this.item = item;
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return (left != null ? left.toString() : "") + item + "\n" +
                   (right != null ? right.toString() : "");
        }
    }
}
