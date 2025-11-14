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
     *
     * @param in
     * @return
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
     *
     * @param input
     */
    public Classifier(Scanner input) {
        if (input == null)
            throw new IllegalArgumentException("input Scanner may not be null");
        root = readTree(input);
        if (root == null)
            throw new IllegalStateException("tree read from Scanner is empty");
    }

    /**
     *
     * @param example
     * @param label
     * @param cur
     * @return
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
     *
     * @param data
     * @param labels
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
     *
     * @param block
     * @param cur
     * @return
     */
    private ClassifierNode classifyTraverse(TextBlock block, ClassifierNode cur) {
        if (cur.left == null)
            return cur;
        if (block.get(cur.feat) < cur.threshold)
            return classifyTraverse(block, cur.left);
        return classifyTraverse(block, cur.right);
    }

    /**
     *
     * @param input
     * @return
     */
    public String classify(TextBlock input) {
        if (input == null)
            throw new IllegalArgumentException("input may not be null");
        return classifyTraverse(input, root).classification;
    }

    /**
     *
     * @param out
     * @param node
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
     *
     * @param output
     */
    public void save(PrintStream output) {
        if (output == null)
            throw new IllegalArgumentException("output PrintStream may not be null");
        saveTraverse(output, root);
    }

    /**
     *
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
         *
         * @param decider
         * @param klass
         */
        public ClassifierNode(TextBlock decider, String klass) {
            left = null;
            this.decider = decider;
            classification = klass;
            threshold = 0;
            feat = null;
        }

        /**
         *
         * @param feat
         * @param threshold
         * @param left
         * @param right
         */
        public ClassifierNode(String feat, double threshold,
                              ClassifierNode left, ClassifierNode right) {
            this.feat = feat;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
            decider = null;
            classification = null;
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
