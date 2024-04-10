package scanner;

import parser.Parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * ScannerTester.java
 *
 * @author Grant Yang
 * @version 2024.01.26
 * Tester class that uses the Scanner class to scan the two input test files
 * and prints out the resulting stream of tokens.
 */
public class ScannerTester
{
    /**
     * Scans the file and executes it as a pascal program
     *
     * @param file The file to scan
     */
    private static void go(String file)
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
     *
     * @param args Command-line arguments
     * @throws FileNotFoundException If either of the two test cases cannot be read
     */
    public static void main(String[] args)
    {
        Stream.of(
                "5procedures/parserTest7.txt",
                "5procedures/parserTest8.txt",
                "5procedures/parserTest8_5.txt", "5procedures/recurse.txt",
                "5procedures/hanoi.txt"
        ).forEach(ScannerTester::go);
    }
}