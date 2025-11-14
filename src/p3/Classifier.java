import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class Classifier {

    // Add fields here

    private ClassifierNode root;

    private ClassifierNode readTree(Scanner in) {
        if (!in.hasNext())
            return null;

        String line = in.nextLine();
        if (line.equals("Spam") || line.equals("Ham"))
            return new ClassifierNode(line.equals("Spam"));

        String feat = line.substring("Feature: ".length());
        double threshold = Double.parseDouble(in.nextLine().substring("Threshold: ".length()));
        return new ClassifierNode(feat, threshold, readTree(in), readTree(in));
    }

    public Classifier(Scanner input) {
        if (input == null)
            throw new IllegalArgumentException("input Scanner may not be null");
        root = readTree(input);
        if (root == null)
            throw new IllegalStateException("tree read from Scanner is empty");
    }

    private ClassifierNode learn(TextBlock example, boolean truth, ClassifierNode cur) {
        if (cur.left == null) {
            if (cur.spam ^ truth) {
                // we will ignore the possibility of findBiggestDifference returning null
                String feat = cur.decider.findBiggestDifference(example);
                double exampleFeat = example.get(feat);
                double threshold = midpoint(cur.decider.get(feat), exampleFeat);
                ClassifierNode newLeaf = new ClassifierNode(example, truth);
                return new ClassifierNode(feat, threshold,
                        exampleFeat <= threshold ? newLeaf : cur,
                        exampleFeat <= threshold ? cur : newLeaf);
            }
        } else {
            if (!example.containsFeature(cur.feat) || example.get(cur.feat) <= cur.threshold)
                cur.left = learn(example, truth, cur.left);
            else
                cur.right = learn(example, truth, cur.right);
        }

        return cur;
    }

    public Classifier(List<TextBlock> data, List<String> labels) {
        if (data == null || labels == null || labels.isEmpty() || data.size() != labels.size())
            throw new IllegalArgumentException(
                    "data and labels may not be null or empty and must be of the same length");
        root = new ClassifierNode(data.getFirst(), labels.getFirst().equals("Spam"));

        for (int i = 1; i < data.size(); i++) {
            TextBlock example = data.get(i);
            ClassifierNode res = classifyTraverse(example, root);
            boolean truth = labels.get(i).equals("Spam");
            if (res.spam ^ truth) {
                res.feat = res.decider.findBiggestDifference(example);
                res.threshold = midpoint(res.decider.get(res.feat), example.get(res.feat));
                if (example.get(res.feat) <= res.threshold) {
                    res.left = new ClassifierNode(example, truth);
                    res.right = new ClassifierNode(res.decider, res.spam);
                } else {
                    res.right = new ClassifierNode(example, truth);
                    res.left = new ClassifierNode(res.decider, res.spam);
                }
            }
        }
    }

    private ClassifierNode classifyTraverse(TextBlock block, ClassifierNode cur) {
        if (cur.left == null)
            return cur;
        if (!block.containsFeature(cur.feat) || block.get(cur.feat) <= cur.threshold)
            return classifyTraverse(block, cur.left);
        return classifyTraverse(block, cur.right);
    }

    public String classify(TextBlock input) {
        return classifyTraverse(input, root).spam ? "Spam" : "Ham";
    }

    private void saveTraverse(PrintStream out, ClassifierNode node) {
        if (node != null) {
            if (node.left == null) {
                out.println(node.spam ? "Spam" : "Ham");
            } else {
                out.println("Feature: " + node.feat);
                out.println("Threshold: " + node.threshold);
                saveTraverse(out, node.left);
                saveTraverse(out, node.right);
            }
        }
    }

    public void save(PrintStream output) {
        if (output == null)
            throw new IllegalArgumentException("output PrintStream may not be null");
        saveTraverse(output, root);
    }

    private static class ClassifierNode {
        // branch case
        ClassifierNode left;
        ClassifierNode right;
        String feat;
        double threshold;

        // leaf case (decider is only used during training)
        TextBlock decider;
        boolean spam;

        ClassifierNode(boolean isSpam) {
            left = null;
            spam = isSpam;
        }

        ClassifierNode(TextBlock decider, boolean isSpam) {
            left = null;
            this.decider = decider;
            spam = isSpam;
        }

        ClassifierNode(String feat, double threshold,
                       ClassifierNode left, ClassifierNode right) {
            this.feat = feat;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
        }

        void setDecider(TextBlock decider, boolean isSpam) {
            this.decider = decider;

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
