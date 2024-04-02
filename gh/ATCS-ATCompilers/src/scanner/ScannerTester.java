package scanner;

import ast.Environment;
import ast.Program;
import ast.Statement;
import parser.Parser;

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * ScannerTester.java
 * @author Grant Yang
 * @version 2024.01.26
 * Tester class that uses the Scanner class to scan the two input test files
 * and prints out the resulting stream of tokens.
 */
public class ScannerTester
{
    public static void go(String file)
    {
        try
        {
            System.out.println(" ================ [ " + file + " ] =================");
            Scanner scan2 = new Scanner(new FileInputStream(file));
            Parser parse = new Parser(scan2);
            parse.parseProgram().exec();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Entry point into the tester
     * @param args Command-line arguments
     * @throws FileNotFoundException If either of the two test cases cannot be read
     */
    public static void main(String[] args) throws IOException
    {
        Stream.of(
                "5procedures/parserTest7.txt",
                "5procedures/parserTest8.txt",
                "5procedures/parserTest8_5.txt", "5procedures/recurse.txt"
        ).forEach(ScannerTester::go);
    }
}