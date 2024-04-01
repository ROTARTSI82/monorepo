package scanner;

import ast.Environment;
import ast.Statement;
import parser.Parser;

import java.io.*;

/**
 * ScannerTester.java
 * @author Grant Yang
 * @version 2024.01.26
 * Tester class that uses the Scanner class to scan the two input test files
 * and prints out the resulting stream of tokens.
 */
public class ScannerTester
{
    /**
     * Entry point into the tester
     * @param args Command-line arguments
     * @throws FileNotFoundException If either of the two test cases cannot be read
     */
    public static void main(String[] args) throws IOException
    {
        Scanner scan2 = new Scanner(new FileInputStream("ast/fib.txt"));
        Environment env = new Environment();
        Parser parse = new Parser(scan2);
        parse.parseStatement().exec(env);
    }
}