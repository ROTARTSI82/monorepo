/**
 * @author Grant Yang
 * @version 2025.11.14
 * CSE 123
 * P3: Cornbear's Classifier
 * TA: Benoit Le
 */

import java.io.*;
import java.util.*;

/**
 * A simple model for the classification of text into arbitrary classes.
 * Supports saving and loading models, training a model from data,
 * and running classifications on new data.
 */
public class Classifier {
    private ClassifierNode root;

    /**
     * Loads a decision tree from a Scanner. The tree is represented in a pre-order traversal,
     * where branches are two lines: one containing the word to look for (`Feature: {string}`) and
     * the other containing the threshold with which to make decisions (`Threshold: {double}`).
     * Leaves are a single line consisting of the class for that point in the tree, and must not
     * have `Feature :` as a prefix.
     * @param in The Scanner to read the tree from.
     * @return The root node of the tree read from the scanner, or null if the scanner is empty.
     */
    private ClassifierNode readTree(Scanner in) {
        if (!in.hasNext())
            return null;

        String line = in.nextLine();
        if (line.startsWith("Feature: ")) {
            String feat = line.substring("Feature: ".length());
            double threshold = Double.parseDouble(in.nextLine().substring("Threshold: ".length()));
            return new ClassifierNode(feat, threshold, readTree(in), readTree(in));
        }

        return new ClassifierNode(null, line);
    }

    /**
     * Loads a classifier model by reading a previously-saved model from the given scanner.
     * @param input The scanner to read the model from.
     *              Lines will be read from the scanner until the entire model is loaded.
     * @throws IllegalArgumentException if the input scanner is null.
     * @throws IllegalStateException if the model read from the scanner is empty.
     */
    public Classifier(Scanner input) {
        if (input == null)
            throw new IllegalArgumentException("input Scanner may not be null");
        root = readTree(input);
        if (root == null)
            throw new IllegalStateException("tree read from Scanner is empty");
    }

    /**
     * Performs training on a decision tree with a single new example.
     * We descend to the leaf node that the model will classify the example as.
     * If that leaf node correctly classifies the example, no changes are made.
     * Otherwise, the leaf node is replaced with a new branch that correctly classifies
     * the example. The feature we look at is the one that has the greatest difference between
     * our new example and the datapoint the old misclassifying leaf corresponds to, and
     * we set the threshold to be the midpoint.
     * @param example The training data point.
     * @param label The correct label for the training data.
     * @param cur The decision tree to train.
     * @return The updated root for the decision tree after learning from the example.
     */
    private ClassifierNode learn(TextBlock example, String label, ClassifierNode cur) {
        if (cur.left == null) {
            if (!cur.classification.equals(label)) {
                String feat = cur.decider.findBiggestDifference(example);
                ClassifierNode newLeaf = new ClassifierNode(example, label);
                double exampleFeat = example.get(feat);
                double threshold = midpoint(cur.decider.get(feat), exampleFeat);
                return new ClassifierNode(feat, threshold,
                        exampleFeat < threshold ? newLeaf : cur,
                        exampleFeat < threshold ? cur : newLeaf);
            }
        } else {
            if (example.get(cur.feat) < cur.threshold)
                cur.left = learn(example, label, cur.left);
            else
                cur.right = learn(example, label, cur.right);
        }

        return cur;
    }

    /**
     * Trains a new classifier model from the given training data and labels.
     * @param data A list of TextBlock examples for training.
     *             There should be no duplicate data points with conflicting labels,
     *             and there should be no empty or null TextBlocks.
     * @param labels A list of the (non-null) corresponding labels for the training data.
     * @throws IllegalArgumentException if data or labels are null, if labels is empty, or if data
     *                                  and labels have different sizes.
     */
    public Classifier(List<TextBlock> data, List<String> labels) {
        if (data == null || labels == null || labels.isEmpty() || data.size() != labels.size())
            throw new IllegalArgumentException(
                    "data and labels may not be null or empty and must be of the same length");

        root = new ClassifierNode(data.getFirst(), labels.getFirst());
        for (int i = 1; i < data.size(); i++)
            root = learn(data.get(i), labels.get(i), root);
    }

    /**
     * Uses the given decision tree to classify the TextBlock, traversing the
     * tree until we reach a leaf node (a classification).
     * @param block The TextBlock to classify.
     * @param cur The root of the decision tree to use.
     * @return The leaf node containing the classification for the given TextBlock.
     */
    private ClassifierNode classifyTraverse(TextBlock block, ClassifierNode cur) {
        if (cur.left == null)
            return cur;
        if (block.get(cur.feat) < cur.threshold)
            return classifyTraverse(block, cur.left);
        return classifyTraverse(block, cur.right);
    }

    /**
     * Classifies a given TextBlock, returning the predicted class label as a String.
     * @param input The TextBlock to be classified.
     * @return The predicted class label for the input TextBlock.
     * @throws IllegalArgumentException if the input TextBlock is null.
     */
    public String classify(TextBlock input) {
        if (input == null)
            throw new IllegalArgumentException("input may not be null");
        return classifyTraverse(input, root).classification;
    }

    /**
     * Writes out a pre-order traversal of the given decision tree to the PrintStream.
     * Branch nodes will be written as two lines, the first being `Feature: {string}`
     * and the second being `Threshold: {double}`. Leaf nodes will simply write their
     * class as a single line.
     * @param out The PrintStream to write the decision tree to.
     * @param node The root of the decision tree to write out.
     */
    private void saveTraverse(PrintStream out, ClassifierNode node) {
        if (node != null) {
            if (node.left == null) {
                out.println(node.classification);
            } else {
                out.println("Feature: " + node.feat);
                out.println("Threshold: " + node.threshold);
                saveTraverse(out, node.left);
                saveTraverse(out, node.right);
            }
        }
    }

    /**
     * Writes the current state of the classifier model to the given PrintStream
     * in a format such that it can be loaded again later.
     * @param output The PrintStream to save the model to.
     * @throws IllegalArgumentException if the output PrintStream is null.
     */
    public void save(PrintStream output) {
        if (output == null)
            throw new IllegalArgumentException("output PrintStream may not be null");
        saveTraverse(output, root);
    }

    /**
     * Represents a node in the decision tree. Can be either a branch node with a feature and
     * threshold to decide which child to descend into, or a leaf node with a classification.
     */
    private static class ClassifierNode {
        // branch case
        public ClassifierNode left;
        public ClassifierNode right;
        public final String feat;
        public final double threshold;

        // leaf case
        public final String classification;
        public final TextBlock decider; // only used during training

        /**
         * Constructs a new leaf node.
         * @param decider The TextBlock data point that this leaf node corresponds to.
         *                This is used during training to track what TextBlock lead to this leaf
         *                being created, and is null otherwise.
         * @param klass The classification for this leaf.
         */
        public ClassifierNode(TextBlock decider, String klass) {
            this.left = null;
            this.right = null;
            this.decider = decider;
            this.classification = klass;
            this.threshold = 0;
            this.feat = null;
        }

        /**
         * Constructs a new branch node.
         * @param feat The feature used for splitting at this node.
         * @param threshold The threshold value for the feature.
         * @param left The root of the left subtree for values strictly less than the threshold.
         * @param right The right subtree for values greater than or equal to the threshold.
         */
        public ClassifierNode(String feat, double threshold,
                              ClassifierNode left, ClassifierNode right) {
            this.feat = feat;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
            this.decider = null;
            this.classification = null;
        }
    }


    ////////////////////////////////////////////////////////////////////
    // PROVIDED METHODS - **DO NOT MODIFY ANYTHING BELOW THIS LINE!** //
    ////////////////////////////////////////////////////////////////////

    // Helper method to calculate the midpoint of two provided doubles.
    private static double midpoint(double one, double two) {
        return Math.min(one, two) + (Math.abs(one - two) / 2.0);
    }

    // Behavior: Calculates the accuracy of this model on provided Lists of 
    //           testing 'data' and corresponding 'labels'. The label for a 
    //           datapoint at an index within 'data' should be found at the 
    //           same index within 'labels'.
    // Exceptions: IllegalArgumentException if the number of datapoints doesn't match the number 
    //             of provided labels
    // Returns: a map storing the classification accuracy for each of the encountered labels when
    //          classifying
    // Parameters: data - the list of TextBlock objects to classify. Should be non-null.
    //             labels - the list of expected labels for each TextBlock object. 
    //             Should be non-null.
    public Map<String, Double> calculateAccuracy(List<TextBlock> data, List<String> labels) {
        // Check to make sure the lists have the same size (each datapoint has an expected label)
        if (data.size() != labels.size()) {
            throw new IllegalArgumentException(
                    String.format("Length of provided data [%d] " +
                                    "doesn't match provided labels [%d]", 
                            data.size(), labels.size()));
        }

        // Create our total and correct maps for average calculation
        Map<String, Integer> labelToTotal = new HashMap<>();
        Map<String, Double> labelToCorrect = new HashMap<>();
        labelToTotal.put("Overall", 0);
        labelToCorrect.put("Overall", 0.0);

        for (int i = 0; i < data.size(); i++) {
            String result = classify(data.get(i));
            String label = labels.get(i);

            // Increment totals depending on resultant label
            labelToTotal.put(label, labelToTotal.getOrDefault(label, 0) + 1);
            labelToTotal.put("Overall", labelToTotal.get("Overall") + 1);
            if (result.equals(label)) {
                labelToCorrect.put(result, labelToCorrect.getOrDefault(result, 0.0) + 1);
                labelToCorrect.put("Overall", labelToCorrect.get("Overall") + 1);
            }
        }

        // Turn totals into accuracy percentage
        for (String label : labelToCorrect.keySet()) {
            labelToCorrect.put(label, labelToCorrect.get(label) / labelToTotal.get(label));
        }

        return labelToCorrect;
    }
}
