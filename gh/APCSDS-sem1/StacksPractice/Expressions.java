import java.util.Scanner;
import java.util.Stack;

/**
 * Expressions class providing static util methods
 * to 1) convert infix to postfix
 *    2) evaluate a postfix expression
 * or 3) check if parens are balanced
 * 
 * @author Anu Datar
 * @author Grant Yang
 * @version 10/27/2017
 * 2021.11.08
 */

public class Expressions
{

    /**
     * Check if two parens correspond
     * @param opener Single-char string of the opening paren
     * @param closer Single-char string of the closing paren
     * @return True if the opener matches the closer
     */
    private static boolean corresponds(String opener, String closer)
    {
        return opener.equals("[") && closer.equals("]") || opener.equals("(") && closer.equals(")")
                || opener.equals("{") && closer.equals("}");
    }

    /**
     * Checks if a single-character string is a valid binary operator
     * @param c String to check
     * @return True if c is one of + - * / %
     */
    private static boolean isOperator(String c)
    {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("%");
    }

    /**
     * Checks if a single character string is an opening paren
     * @param c String to check
     * @return true if c is one of ( [ {
     */
    private static boolean isOpenParen(String c)
    {
        return c.equals("(") || c.equals("[") || c.equals("{");
    }

    /**
     * Checks if a single character string is a closing paren
     * @param c String to check
     * @return True if c is one of ) ] }
     */
    private static boolean isCloseParen(String c)
    {
        return c.equals(")") || c.equals("]") || c.equals("}");
    }

    /**
     * Gets the precedence of an operator
     * @param op A valid operator from isOperator() or an opening paren
     * @return 1 for + -, 2 for * / %, and -999 for an open paren
     */
    private static int precedence(String op)
    {
        if (op.equals("+") || op.equals("-")) return 1;
        else if (isOpenParen(op)) return -999;
        else return 2;
    }

    /**
     * parenthesis matching : An expression is said to be balanced if
     * every opener has a corresponding closer, in the right order
     * {, [ or ( are the only types of brackets allowed
     * @param expression Expression containing operands, operators,
     *                   and any of the 3 supported brackets
     * @return true is the parenthesis are balanced
     *         false otherwise
     */
    public static boolean matchParenthesis(String expression)
    {
        Stack<String> parens = new Stack<String>();

        for (int i = 0; i < expression.length(); i++)
        {
            String c = expression.substring(i, i + 1);
            if (isOpenParen(c))
                parens.push(c);
            else if ((c.equals(")") || c.equals("]") || c.equals("}"))
                    && (parens.isEmpty() || !corresponds(parens.pop(), c)))
            {
                return false;
            }
        }

        // Write code here
        return parens.isEmpty();
    }

    /**
     * Queries if a single-character string is a numerical digit
     * @param s String to check
     * @return True if s is one of 0123456789
     */
    private static boolean isDigit(String s)
    {
        return s.equals("0") || s.equals("1") || s.equals("2") || s.equals("3")
                || s.equals("4") || s.equals("5") || s.equals("6") || s.equals("7")
                || s.equals("8") || s.equals("9");
    }

    /**
     * returns a string in postfix form
     * if given an expression in infix form as a parameter
     * does this conversion using a Stack
     * @precondition The input string is a valid infix expression containing
     *               only binary plus, binary minus, multiplication, division, or
     *               modulo. Only base10 decimal numbers are supported
     * @param expr valid expression in infix form
     * @return equivalent expression in postfix form
     */
    public static String infixToPostfix(String expr)
    {
        Stack<String> operators = new Stack<String>();
        String builder = "";

        boolean previousCharWasDigit = false;

        for (int i = 0; i < expr.length(); i++)
        {
            String c = expr.substring(i, i + 1);
            if (isOpenParen(c))
            {
                operators.push(c);
                previousCharWasDigit = false;
            }
            else if (isCloseParen(c))
            {
                while (!operators.isEmpty() && !isOpenParen(operators.peek()))
                {
                    builder += " ";
                    builder += operators.pop();
                }

                if (!operators.isEmpty()) operators.pop(); // pop the openParen
                previousCharWasDigit = false;
            }
            else if (isOperator(c))
            {
                previousCharWasDigit = false;
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c))
                {
                    builder += " ";
                    builder += operators.pop();
                }

                operators.push(c);
            }
            else if (isDigit(c) || c.equals("."))
            {
                if (!previousCharWasDigit)
                    builder += " ";
                builder += c;
                previousCharWasDigit = true;
            }
            else
            {
                previousCharWasDigit = false;
            }
        }

        while (!operators.isEmpty())
        {
            builder += " ";
            builder += operators.pop();
        }
        
        // trim leading spaces. (:
        while (builder.length() > 0 && builder.substring(0, 1).equals(" "))
            builder = builder.substring(1);

        return builder;
    }

    /**
     * Converts a single-character string to it's value as a double
     * such that '1' -> 1
     * @param c Single character digit string
     * @return The number the character represents
     */
    private static double valueOf(String c)
    {
        if (c.equals("0")) return 0;
        else if (c.equals("1")) return 1;
        else if (c.equals("2")) return 2;
        else if (c.equals("3")) return 3;
        else if (c.equals("4")) return 4;
        else if (c.equals("5")) return 5;
        else if (c.equals("6")) return 6;
        else if (c.equals("7")) return 7;
        else if (c.equals("8")) return 8;
        else return 9;
    }

    /**
     * returns the value of an expression in postfix form
     * does this computation using a Stack.
     *
     * For this implementation, operands and operators need not be separated by spaces!
     *
     * @param expr valid expression in postfix form
     * @return value of the expression
     * @precondition postfix expression
     *               contains numbers and operators + - * / and %
     */
    public static double evalPostfix(String expr) throws Exception
    {
        Stack<Double> operands = new Stack<Double>();

        boolean prevWasDigit = false;
        boolean afterDecimal = false;
        double place = 0.1;

        for (int i = 0; i < expr.length(); i++)
        {
            String c = expr.substring(i, i+1);

            if (c.equals("+"))
            {
                operands.push(operands.pop() + operands.pop());
                prevWasDigit = false;
            }
            else if (c.equals("-"))
            {
                double top = operands.pop();
                operands.push(operands.pop() - top);
                prevWasDigit = false;
            }
            else if (c.equals("*"))
            {
                operands.push(operands.pop() * operands.pop());
                prevWasDigit = false;
            }
            else if (c.equals("%"))
            {
                double top = operands.pop();
                operands.push(operands.pop() % top);
                prevWasDigit = false;
            }
            else if (c.equals("/"))
            {
                double top = operands.pop();
                operands.push(operands.pop() / top);
                prevWasDigit = false;
            }
            else
            {
                if (prevWasDigit && !afterDecimal)
                {
                    if (isDigit(c))
                    {
                        operands.push(operands.pop() * 10 + valueOf(c));
                    }
                    else if (c.equals("."))
                    {
                        afterDecimal = true;
                    }
                    else
                    {
                        prevWasDigit = false;
                    }
                }
                else if (prevWasDigit && isDigit(c))
                {
                    operands.push(operands.pop() + valueOf(c) * place);
                    place /= 10;
                }
                else
                {
                    place = 0.1;
                    afterDecimal = false;
                    if (isDigit(c))
                    {
                        operands.push(valueOf(c));
                        prevWasDigit = true;
                    }
                    else
                    {
                        prevWasDigit = false;
                    }
                }
            }
        }

        if (operands.size() != 1) throw new Exception("What the heck are you doing you idiot");
        return operands.pop();
    }

    /**
     * Tester to check if infix to postfix and evaluate postfix work well
     * @param args Command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        String exp = "2 + 3 * 4";
        test(exp, 14);

        exp = "8 * 12 / 2";
        test(exp, 48);

        exp = "5 % 2 + 3 * 2 - 4 / 2";
        test(exp, 5);

        test("3 * ([2] + {8})", 30);

        // test balanced expressions
        testBalanced("{ 2 + 3 } * ( 4 + 3 )", true);
        testBalanced("} 4 + 4 { * ( 4 + 3 )", false);
        testBalanced("[ [ [ ] ]", false);
        testBalanced("{ ( } )", false);
        testBalanced("( ( ( ) ) )", true);

//        System.out.print(">");
//        Scanner scan = new Scanner(System.in);
//        while (scan.hasNext())
//        {
//            String next = scan.nextLine();
//            String post = infixToPostfix(next);
//            System.out.printf("Matches: %d\nPostfix: %s\nValue: %f\n>",
//                    matchParenthesis(next) ? 1 : 0, post, evalPostfix(post));
//        }
    }

    /**
     * Tests an infix expression, expecting it to have a certain value
     * @param expr Infix expression
     * @param expect Expected value
     */
    public static void test(String expr, double expect) throws Exception
    {
        String post = infixToPostfix(expr);        
        double val = evalPostfix(post);

        System.out.println("Infix: " + expr);
        System.out.println("Postfix: " + post);
        System.out.println("Value: " + val);
        if (val == expect)
        {
            System.out.print("** Success! Great Job **");
        }
        else
        {
            System.out.print("** Oops! Something went wrong. ");
            System.out.println("Check your postfix and eval methods **");
        }
    }

    /**
     * Tests matchParenthesis() on a string with an expected value
     * @param ex Expression to test
     * @param expected Expected return value (true if parens are matched)
     */
    public static void testBalanced(String ex, boolean expected)
    {
        boolean act = matchParenthesis(ex);
        if (act == expected)
            System.out.println("** Success!: matchParenthesis(" + ex + ") returned " + act);
        else
        {
            System.out.print("** Oops! Something went wrong check : matchParen(" + ex + ")");
            System.out.println(" returned " + act + " but should have returned " + expected);
        }
    }
}
