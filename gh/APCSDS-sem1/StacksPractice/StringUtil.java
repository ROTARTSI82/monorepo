import java.util.Stack;

/**
 * String utilities to reverse strings and
 * check for palindromes with stacks
 * 
 * @author Anu Datar
 * @author Grant Yang
 * @version 10/27/2017
 * 2021.11.08
 */
public class StringUtil
{
    //reverse a string using a Stack and substring not charAt

    /**
     * Reverses a string with a stack
     * @param str A string to reverse
     * @return The reverse of the string such that "abc" -> "cba"
     */
    public static String reverseString(String str)
    {
        Stack<String> stack = new Stack<String>();
        for (int i = 0; i < str.length(); i++)
            stack.push(str.substring(i, i+1));

        String ret = "";
        while (!stack.isEmpty())
            ret += stack.pop();

        return ret;
    }

    /**
     * Checks for a palindrome by testing for equality
     * with a string's reverse.
     * @param s String to check for
     * @return True if s is a palindrome, false otherwise.
     */
    public static boolean isPalindrome(String s)
    {
        return reverseString(s).equals(s);
    }

    /** 
     * The tester for checking that reverse and isPalindrome work well.
     * @param args Command line args
     */
    public static void main(String[] args)
    {
        String test =  "racecar";
        String test2 = "notapalindrome";

        if ( !("".equalsIgnoreCase(reverseString(""))) )
            System.out.println("** Oops Something went wrong. Check your reverse method **");

        if ( !("a".equalsIgnoreCase(reverseString("a"))) )
            System.out.println("** Oops Something went wrong. Check your reverse method **");

        if (!test.equalsIgnoreCase(reverseString(test)))
            System.out.println("** Oops Something went wrong. Check your reverse method **");
        else
            System.out.println("Success " + test + " matched " + reverseString(test));
            
        if (test2.equalsIgnoreCase(reverseString(test2)))
            System.out.println("** Oops Something went wrong. Check your reverse method **");

    }
}
