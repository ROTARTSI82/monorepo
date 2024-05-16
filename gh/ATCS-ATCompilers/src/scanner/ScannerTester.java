package scanner;

import ast.Program;
import codegen.Emitter;
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
            Program prog = parse.parseProgram();

            Emitter emit = new Emitter(file + ".asm", prog);
            prog.compile(emit);
            emit.close();
//            prog.exec();
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
                "5procedures/recurse.txt",
                "7codegen/parserTest9.2.txt",
                "7codegen/recurse2.txt",
                "7codegen/double.txt",
                "7codegen/simple.txt",
                "7codegen/calcpi.txt"
        ).forEach(ScannerTester::go);
    }
}