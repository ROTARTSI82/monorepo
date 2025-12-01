/**
 * @author Grant Yang
 * @version 2025.11.28
 * CSE 123
 * C3: B(e)ST of the B(e)ST
 * TA: Benoit Le
 */

import java.io.*;
import java.util.*;

/**
 * Manages a collection of Email objects using a Binary Search Tree (BST).
 * Supports adding items, checking containment, saving and loading from a file,
 * and removing all Emails sent within a time range.
 */
public class CollectionManager {
    
    private BSTNode root;

    /**
     * Initializes an empty CollectionManager.
     */
    public CollectionManager() {
        root = null;
    }

    /**
     * Helper method to recursively read a BST from a Scanner.
     * The expected format is a pre-order traversal
     * of the underlying BST, with each line formatted according to the
     * "timestamp ~ sentiment ~ subject ~ from ~ to" format of Email::toString().
     * Lines containing "null" signify empty subtrees.
     * @param in The scanner to read from.
     * @return The constructed BSTNode, or null if the token is "null".
     */
    private BSTNode readRecursor(Scanner in) {
        Email res = Email.rawParse(in);
        if (res == null)
            return null;
        return new BSTNode(res, readRecursor(in), readRecursor(in));
    }

    /**
     * Initializes a CollectionManager by loading a previously-saved
     * collection from the Scanner. The expected format is a pre-order traversal
     * of the underlying BST, with each line formatted according to the
     * "timestamp ~ sentiment ~ subject ~ from ~ to" format of Email::toString().
     * Lines containing "null" signify empty subtrees.
     * @param in The Scanner to read the collection from.
     */
    public CollectionManager(Scanner in) {
        root = readRecursor(in);
    }

    /**
     * Helper method to recursively add an item to the BST while
     * preserving the binary search tree property. This operation is a
     * no-op if the BST already contains the specified item.
     * @param item The Email to add.
     * @param tree The root of the tree to add to.
     * @return The root of the tree with the item added.
     */
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

    /**
     * Tries to add the provided Email to the collection.
     * If the collection already contains this Email, nothing happens.
     * @param item The Email item to add.
     */
    public void add(Email item) {
        root = addRecursor(item, root);
    }

    /**
     * Helper method to recursively check if an item exists in a BST.
     * @param item The Email to look for.
     * @param tree The root of the BST to use.
     * @return true if found, false otherwise.
     */
    private boolean containsRecursor(Email item, BSTNode tree) {
        if (tree == null)
            return false;
        if (item.equals(tree.item))
            return true;
        if (item.compareTo(tree.item) < 0)
            return containsRecursor(item, tree.left);
        return containsRecursor(item, tree.right);
    }

    /**
     * Checks if the collection contains the specified item.
     * @param item The Email item to check for.
     * @return true if the item is in the collection, false otherwise.
     */
    public boolean contains(Email item) {
        return containsRecursor(item, root);
    }

    /**
     * Writes a BST to the given PrintStream.
     * The BST is written out as a pre-order traversal. Each line is a node
     * in the tree, using the "timestamp ~ sentiment ~ subject ~ from ~ to" format
     * of Email::toString(). Lines containing "null" signify empty subtrees.
     * @param out The PrintStream to save the collection to.
     * @param tree The BST to save
     */
    private void saveRecursor(PrintStream out, BSTNode tree) {
        if (tree != null) {
            out.println(tree.item);
            saveRecursor(out, tree.left);
            saveRecursor(out, tree.right);
        } else {
            out.println("null");
        }
    }

    /**
     * Saves the current collection to the given PrintStream
     * such that it can be loaded again later. The collection is saved
     * as a pre-order traversal of the underlying BST, with each line formatted according
     * to the "timestamp ~ sentiment ~ subject ~ from ~ to" format of Email::toString().
     * Lines containing "null" signify empty subtrees.
     * @param output The PrintStream to save the collection to.
     */
    public void save(PrintStream output) {
        saveRecursor(output, root);
    }

    /**
     * Recursively finds and removes the maximum or minimum node in a given BST.
     * Used as a helper for BST removal logic.
     * @param tree The root of the BST to modify
     * @param maximize If true, finds and removes the maximum node.
     *                 Otherwise, finds and removes the minimum.
     * @return A pair consisting of (new root, maximal/minimal node).
     *         The new root of the modified BST is obtained by .getKey() while
     *         the element that was removed can be obtained with .getValue().
     */
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

    /**
     * Removes the root node of a given BST while maintaining the BST property.
     * The root node is removed and replaced with either the maximal element from the
     * left subtree or the minimal element from the right subtree in the cases where
     * there are children to consider.
     * @param tree The root of the subtree from which to remove the root node.
     * @return The new root of the BST after removal. Note that this may be null.
     */
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

    /**
     * Helper method to recursively remove Emails within a specific timestamp range.
     * @param tree The tree to remove Emails from.
     * @param timeLo The lower bound of the timestamp range (exclusive).
     * @param timeHi The upper bound of the timestamp range (exclusive).
     * @return The tree with all specified Emails removed.
     */
    private BSTNode rmBetweenRecursor(BSTNode tree, long timeLo, long timeHi) {
        if (tree == null)
            return null;
        tree.left = rmBetweenRecursor(tree.left, timeLo, timeHi);
        tree.right = rmBetweenRecursor(tree.right, timeLo, timeHi);
        if (tree.item.compareTime(timeLo) > 0 && tree.item.compareTime(timeHi) < 0)
            return rmRoot(tree);
        return tree;
    }

    /**
     * Removes from the collection all Emails that were sent between the specified times.
     * @param timeLo The lower bound for the email's UNIX timestamp (exclusive, seconds).
     * @param timeHi The upper bound for the email's UNIX timestamp (exclusive, seconds).
     */
    public void removeBetween(long timeLo, long timeHi) {
        // ^ this is my creative extension btw
        root = rmBetweenRecursor(root, timeLo, timeHi);
    }

    /**
     * Gets a human-readable string that lists the Emails in this collection.
     * @return A string of the Emails in this collection in order from least to greatest.
     *         Each Email is on a new line, and there is a trailing newline.
     *         If the collection is empty, an empty string is returned.
     *         Emails are formatted as "{timestamp} ~ {sentiment} ~ {subject} ~ {from} ~ {to}".
     */
    public String toString() {
        if (root == null) return "";
        return root.toString();
    }

    /**
     * Represents a node in the Binary Search Tree.
     * Stores an Email item to decide branching and references to left and right subtrees.
     */
    private static class BSTNode {
        public final Email item;
        public BSTNode left, right;

        /**
         * Constructs a new node in the binary search tree
         * @param item The Email item that decides branching at this node
         * @param left The left subtree of Emails strictly less than `item`.
         * @param right The right subtree of Emails greater than or equal to `item`.
         */
        public BSTNode(Email item, BSTNode left, BSTNode right) {
            this.item = item;
            this.left = left;
            this.right = right;
        }

        /**
         * Gets a human-readable string that lists the Emails in this search tree.
         * @return A string of the Emails in this tree in order from least to greatest.
         *         Each Email is on a new line, and there is a trailing newline.
         *         Emails are formatted as "{timestamp} ~ {sentiment} ~ {subject} ~ {from} ~ {to}".
         */
        public String toString() {
            return (left != null ? left.toString() : "") + item + "\n" +
                   (right != null ? right.toString() : "");
        }
    }
}
