import java.util.*;

/**
 * TreeUtil contains the following methods for manipulating binary trees:
 * Object leftmost(TreeNode t)
 * Object rightmost(TreeNode t)
 * int maxDepth(TreeNode t)
 * TreeNode createRandom(int depth)
 * int countNodes(TreeNode t)
 * int countLeaves(TreeNode t)
 * void preOrder(TreeNode t, TreeDisplay disp)
 * void postOrder(TreeNode t, TreeDisplay disp)
 * void inOrder(TreeNode t, TreeDisplay disp)
 * List<String> fillList(TreeNode t, List<String> list)
 * void saveTree(String fileName, TreeNode t)
 * TreeNode loadTree(String fileName)
 * void twentyQuestions()
 * TreeNode copy(TreeNode t)
 * boolean sameShape(TreeNode t1, TreeNode t2)
 * TreeNode createDecodingTree(TreeDisplay display)
 * String decodeMorse(TreeNode decodingTree, String cipherText, TreeDisplay display)
 * int eval(TreeNode expTree)
 * TreeNode createExpressionTree(String exp)
 * 
 * @author Grant Yang
 * @version 2021.11.18
 */
public class TreeUtil
{
    //used to prompt for command line input
    private static Scanner in = new Scanner(System.in);

    /**
     * Gets the leftmost node in the tree
     * by repeatedly following the left child.
     * @param t Tree to find the node in
     * @return Value of the leftmost node
     */
    public static Object leftmost(TreeNode t)
    {
        return t.getLeft() == null ? t.getValue() : leftmost(t.getLeft());
    }

    /**
     * Gets the rightmost node in the tree
     * by repeatedly following the right child.
     * @param t Tree to find the node in
     * @return Value of the rightmost node
     */
    public static Object rightmost(TreeNode t)
    {
        return t.getRight() == null ? t.getValue() : rightmost(t.getRight());
    }

    /**
     * Finds the height of a tree (the max number of edges between the root and a leaf)
     * @param t The tree to traverse
     * @return The height
     */
    public static int maxDepth(TreeNode t)
    {
        return t == null ? -1 : 1 + Math.max(maxDepth(t.getRight()), maxDepth(t.getLeft()));
    }

    /**
     * create a random tree of the specified depth.  No attempt to balance the tree
     * is provided.
     * @param depth of the tree
     * @return TreeNode object that points to the generated tree
     */
    public static TreeNode createRandom(int depth)
    {
        if (Math.random() * Math.pow(2, depth) < 1)
            return null;
        return new TreeNode(((int)(Math.random() * 10)),
            createRandom(depth - 1),
            createRandom(depth - 1));
    }
    /**
     * Counts the number of nodes in a tree
     * @param t Tree to traverse
     * @return The number of nodes in it
     */
    public static int countNodes(TreeNode t)
    {
        return t == null ? 0 : 1 + countNodes(t.getLeft()) + countNodes(t.getRight());
    }
    /**
     * Counts the leaf nodes in a tree
     * @param t Tree to traverse
     * @return The number of leaf nodes in this tree
     */
    public static int countLeaves(TreeNode t)
    {
        return t == null ? 0 : t.getLeft() == null && t.getRight() == null ? 1 : 
                countLeaves(t.getLeft()) + countLeaves(t.getRight());
    }
    /**
     * Visits nodes in a tree in pre-order with a TreeDisplay
     * @param t Tree to traverse
     * @param display Display to show the traversal with
     */
    public static void preOrder(TreeNode t, TreeDisplay display)
    {
        if (t == null) return;
        display.visit(t);
        preOrder(t.getLeft(), display);
        preOrder(t.getRight(), display);
    }
    /**
     * Visits nodes in a tree in in-order with a TreeDisplay
     * @param t Tree to traverse
     * @param display Display to show the traversal with
     */
    public static void inOrder(TreeNode t, TreeDisplay display)
    {
        if (t == null) return;
        inOrder(t.getLeft(), display);
        display.visit(t);
        inOrder(t.getRight(), display);
    }
    /**
     * Visits nodes in a tree in post-order with a TreeDisplay
     * @param t Tree to traverse
     * @param display Display to show the traversal with
     */
    public static void postOrder(TreeNode t, TreeDisplay display)
    {
        if (t == null) return;
        postOrder(t.getLeft(), display);
        postOrder(t.getRight(), display);
        display.visit(t);
    }

    /**
     * Flattens a binary tree into a list using preorder traversal, with '$'
     * representing null values to denote leaf nodes.
     * @param t Tree to flatten
     * @param list List to add the elements into
     * @return Passes back what was passed into list (which has now been modified)
     */
    public static List<String> fillList(TreeNode t, List<String> list)
    {
        if (t == null)
        {
            list.add("$");
            return list;
        }

        list.add(t.getValue().toString());
        fillList(t.getLeft(), list);
        fillList(t.getRight(), list);
        return list;
    }
    /**
     * saveTree uses the FileUtil utility class to save the tree rooted at t
     * as a file with the given file name
     * @param fileName is the name of the file to create which will hold the data
     *        values in the tree
     * @param t is the root of the tree to save
     */
    public static void saveTree(String fileName, TreeNode t)
    {
        FileUtil.saveFile(fileName, fillList(t, new LinkedList<>()).iterator());
    }
    /**
     * buildTree takes in an iterator which will iterate through a valid description of
     * a binary tree with String values.  Null nodes are indicated by "$" markers
     * @param it the iterator which will iterate over the tree description
     * @return a pointer to the root of the tree built by the iteration
     */
    private static TreeNode buildTree(Iterator<String> it)
    {
        if (!it.hasNext()) 
        {
            System.err.println("buildTree ran out of tokens!");
            return null;
        }

        String n = it.next();

        if (n.equals("$"))
            return null;

        return new TreeNode(n, buildTree(it), buildTree(it));
    }
    /**
     * read a file description of a tree and then build the tree
     * @param fileName is a valid file name for a file that describes a binary tree
     * @return a pointer to the root of the tree
     */
    public static TreeNode loadTree(String fileName)
    {
        return buildTree(FileUtil.loadFile(fileName));
    }
    /**
     * utility method that waits for a user to type text into Std Input and then press enter
     * @return the string entered by the user
     */
    private static String getUserInput()
    {
        return in.nextLine();
    }

    /**
     * plays a single round of 20 questions
     * postcondition:  plays a round of twenty questions, asking the user questions as it
     *                 walks down the given knowledge tree, lighting up the display as it goes;
     *                 modifies the tree to include information learned.
     * @param t a pointer to the root of the game tree
     * @param display which will show the progress of the game
     * @param questionsLeft number of questions we can ask
     */
    private static void twentyQuestionsRound(TreeNode t, TreeDisplay display, int questionsLeft)
    {
        // display.visit(t);
        if (t.getLeft() == null && t.getRight() == null || questionsLeft <= 0) 
        {
            System.out.printf("Hmmm... Is it '%s' [Y/n]? ", t.getValue().toString());
            if (!getUserInput().equals("Y")) 
            {
                System.out.print("What was it? ");
                String is = getUserInput();
                System.out.printf("What describes '%s' but not '%s'? ", is,
                                 t.getValue().toString());
                String diff = getUserInput();
                t.setRight(new TreeNode(t.getValue()));
                t.setLeft(new TreeNode(is));
                t.setValue(diff);
            }
            else
                System.out.println("YAY!");
        } 
        else 
        {
            System.out.printf("Does '%s' describe it [Y/n]? ", t.getValue().toString());
            twentyQuestionsRound(getUserInput().equals("Y") ? t.getLeft() : t.getRight(), display,
                                 questionsLeft - 1);
        }
    }
    /** 
     * plays a game of 20 questions
     * Begins by reading in a starting file and then plays multiple rounds
     * until the user enters "quit".  Then the final tree is saved
     */
    public static void twentyQuestions()
    {
        TreeNode knowledge = loadTree("knowledge.bt.txt");
        boolean run = true;
        while (run)
        {
            twentyQuestionsRound(knowledge, null, 20);

            System.out.print("Continue [Y/n]? ");
            run = getUserInput().equals("Y");
        }

        saveTree("knowledge.bt.txt", knowledge);
    }
    /**
     * copy a binary tree
     * @param t the root of the tree to copy
     * @return a new tree, which is a complete copy
     *         of t with all new TreeNode objects
     *         pointing to the same values as t (in the same order, shape, etc)
     */
    public static TreeNode copy(TreeNode t)
    {
        return t == null ? null : new TreeNode(t.getValue(), copy(t.getLeft()), copy(t.getRight()));
    }

    /**
     * tests to see if two trees have the same shape, but not necessarily the
     * same values.  Two trees have the same shape if they have TreeNode objects
     * in the same locations relative to the root
     * @param t1 pointer to the root of the first tree
     * @param t2 pointer to the root of the second tree
     * @return true if t1 and t2 describe trees having the same shape, false otherwise
     */
    public static boolean sameShape(TreeNode t1, TreeNode t2)
    {
        return t1 == null && t2 == null || (t1.getLeft() == null) == (t2.getLeft() == null) && 
            (t1.getRight() == null) == (t2.getRight() == null) && 
            sameShape(t1.getLeft(), t2.getLeft()) &&
            sameShape(t1.getRight(), t2.getRight());
    }
    /**
     * Generate a tree for decoding Morse code
     * @param display the display that will show the decoding tree
     * @return the decoding tree
     */
    public static TreeNode createDecodingTree(TreeDisplay display)
    {
        TreeNode tree = new TreeNode("Morse Tree");
        display.displayTree(tree);
        insertMorse(tree, "a", ".-", display);
        insertMorse(tree, "b", "-...", display);
        insertMorse(tree, "c", "-.-.", display);
        insertMorse(tree, "d", "-..", display);
        insertMorse(tree, "e", ".", display);
        insertMorse(tree, "f", "..-.", display);
        insertMorse(tree, "g", "--.", display);
        insertMorse(tree, "h", "....", display);
        insertMorse(tree, "i", "..", display);
        insertMorse(tree, "j", ".---", display);
        insertMorse(tree, "k", "-.-", display);
        insertMorse(tree, "l", ".-..", display);
        insertMorse(tree, "m", "--", display);
        insertMorse(tree, "n", "-.", display);
        insertMorse(tree, "o", "---", display);
        insertMorse(tree, "p", ".--.", display);
        insertMorse(tree, "q", "--.-", display);
        insertMorse(tree, "r", ".-.", display);
        insertMorse(tree, "s", "...", display);
        insertMorse(tree, "t", "-", display);
        insertMorse(tree, "u", "..-", display);
        insertMorse(tree, "v", "...-", display);
        insertMorse(tree, "w", ".--", display);
        insertMorse(tree, "x", "-..-", display);
        insertMorse(tree, "y", "-.--", display);
        insertMorse(tree, "z", "--..", display);
        return tree;
    }
    /**
     * helper method for building a Morse code decoding tree.
     * postcondition:  inserts the given letter into the decodingTree,
     *                 in the appropriate position, as determined by
     *                 the given Morse code sequence; lights up the display
     *                 as it walks down the tree
     * @param decodingTree is the partial decoding tree
     * @param letter is the letter to add
     * @param code is the Morse code for letter
     * @param display is the display that will show progress as the method walks 
     *        down the tree
     * 
     * . = left
     * - = right
     */
    private static void insertMorse(TreeNode decodingTree, String letter,
                                    String code, TreeDisplay display)
    {
        // display.visit(decodingTree);
        if (code.isEmpty()) decodingTree.setValue(letter);
        else if (code.substring(0, 1).equals(".")) 
        {
            if (decodingTree.getLeft() == null)
                decodingTree.setLeft(new TreeNode(null));

            insertMorse(decodingTree.getLeft(), letter, code.substring(1), display);
        } 
        else 
        {
            if (decodingTree.getRight() == null)
                decodingTree.setRight(new TreeNode(null));

            insertMorse(decodingTree.getRight(), letter, code.substring(1), display);
        }
    }

    /**
     * Decodes a single-character cipherText encoded in morse code
     * @param decodingTree Tree containing letters for morse sequences, 
     *                     with - going right and . going left.
     * @param cipherText Morse sequence to decode
     * @param d Display to visit nodes with
     * @return Morse character that corresponds to the ciphertext
     */
    private static String decodeSingle(TreeNode decodingTree, String cipherText, TreeDisplay d)
    {
        if (cipherText.isEmpty() || cipherText.substring(0, 1).equals(" "))
        {
            d.visit(decodingTree);
            return decodingTree.getValue().toString();
        }

        return decodeSingle(cipherText.substring(0, 1).equals("-") ? decodingTree.getRight() : 
                decodingTree.getLeft(), cipherText.substring(1), d);
    }

    /**
     * decodes Morse code by walking the decoding tree according to the input code
     * @param decodingTree is the Morse code decoding tree
     * @param cipherText is Morse code consisting of dots, dashes, and spaces
     * @param display is the display object that will show the decoding progress
     * @return the string represented by cipherText
     */
    public static String decodeMorse(TreeNode decodingTree, String cipherText, TreeDisplay display)
    {
        String running = "";
        int begin = 0;
        int end = 0;
        while (end < cipherText.length()) 
        {
            while (end < cipherText.length() && !cipherText.substring(end, ++end).equals(" ")) 
                end=0+end;
            // System.out.println("Decode " + cipherText.substring(begin, end));
            running += decodeSingle(decodingTree, cipherText.substring(begin, end), display);
            begin = end++;
        }

        return running;
    }
    /**
     * optional work. Recursively evaluates an expression tree
     * with integer values and "+-/*"
     * @param expTree Tree to evaluate
     * @return the value of the expression tree
     */
    public static int eval(TreeNode expTree)
    {
        if (expTree.getValue() instanceof Integer)
            return (Integer) expTree.getValue();

        int lhs = eval(expTree.getLeft());
        int rhs = eval(expTree.getRight());
        String e = (String) expTree.getValue();
        if (e.equals("+"))
            return lhs + rhs;
        else if (e.equals("-"))
            return lhs - rhs;
        else if (e.equals("*"))
            return lhs * rhs;
        else // if (e.equals("/"))
            return lhs / rhs;
    }

    /**
     * Retrieves the precedence of an operator
     * @param operator Single character operator to check
     * @return -1 if not an operator, 2 if / or *, and 1 if + or -
     */
    private static int prec(String operator)
    {
        if (operator.equals("+") || operator.equals("-"))
        {
            return 1;
        }
        else if (operator.equals("/") || operator.equals("*"))
        {
            return 2;
        }
        else // returns -1 for NOT AN OPERATOR
        {
            return -1;
        }
    }

    /**
     * optional work. Constructs an expression tree from an expression
     * with parens "()", plus/minus "+-", and multiply/divide "/*"
     * @param exp The expression to parse
     * @return An expression tree representing an expression
     */
    public static TreeNode createExpressionTree(String exp)
    {
        // unwrap parens
        int charsToRemove = 0;
        Stack<Integer> parens = new Stack<>();
        for (int i = 0; i < exp.length(); i++)
        {
            if (exp.substring(i, i+1).equals("("))
            {
                parens.push(i);
            } 
            else if (exp.substring(i, i+1).equals(")"))
            {
                int op = parens.pop();
                if (op + 1 == exp.length() - i)
                {
                    charsToRemove = op + 1;
                    i = Integer.MAX_VALUE; // hacky break
                }
            }
        }
        System.out.print("Remove Parens of '" + exp + "' = ");
        exp = exp.substring(charsToRemove, exp.length() - charsToRemove);
        System.out.println(exp);

        // find the lowest precedence operator
        int i = 0;
        int lowestPrec = 999;
        int lowestPrecIndex = -1;
        while (i < exp.length())
        {
            if (exp.substring(i,i+1).equals("(")) 
                while (i < exp.length() && !exp.substring(i,++i).equals(")")) i=0+i;
            else
            {
                int prec = prec(exp.substring(i,i+1));
                if (prec != -1 && prec <= lowestPrec)
                {
                    lowestPrec = prec;
                    lowestPrecIndex = i;
                }
                i++;
            }
        }

        if (lowestPrecIndex == -1) 
        {
            return new TreeNode(Integer.parseInt(exp.trim()), null, null);
        }

        return new TreeNode(exp.substring(lowestPrecIndex, lowestPrecIndex+1),
            createExpressionTree(exp.substring(0, lowestPrecIndex)), 
            createExpressionTree(exp.substring(lowestPrecIndex + 1)));
    }
}
