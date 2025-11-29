import java.util.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the CSE 123 Collection Manager! " +
                           "To begin, enter your desired mode of operation:");
        System.out.println();
        System.out.println("1) Start with an empty collection manager");
        System.out.println("2) Load collection from file");
        System.out.print("Enter your choice here: ");

        int choice = Integer.parseInt(console.nextLine());
        while (choice != 1 && choice != 2) {
            System.out.println("Invalid choice! Try again");
            choice = Integer.parseInt(console.nextLine());
        }

        CollectionManager collectionManager = null;
        if (choice == 1) {
            collectionManager = new CollectionManager();
        } else { // choice == 2
            System.out.print("Enter file to read: ");
            String inFileName = console.nextLine();
            File inFile = new File(inFileName);
            while (!inFile.exists()) {
                System.out.println("  File does not exist. Please try again.");
                System.out.print("Enter file to read: ");
                inFileName = console.nextLine();
                inFile = new File(inFileName);
            }
    
            collectionManager = new CollectionManager(new Scanner(inFile));
            System.out.println("Collection manager created!");
            System.out.println();
        }

        menu();
        String option = console.nextLine();
        while (!option.equalsIgnoreCase("quit")) {
            System.out.println();

            if (option.equalsIgnoreCase("add")) {
                // TODO: Call your collection manager's add method and 
                // pass in the result of the Item's parse() method
                collectionManager.add(Email.parse(console));
                System.out.println();
            } else if (option.equalsIgnoreCase("contains")) {
                // TODO: Print the result of your collection manager's 
                // contains method and pass in the result of the Item's parse() method
                System.out.println("contains: "
                        + collectionManager.contains(Email.parse(console)));
                System.out.println();
            } else if (option.equalsIgnoreCase("print")) {
                System.out.println(collectionManager.toString());
                System.out.println();
            } else if (option.equalsIgnoreCase("creative")) {
                long lo = Long.parseLong(console.nextLine());
                long hi = Long.parseLong(console.nextLine());
                collectionManager.removeBetween(lo, hi);
                System.out.println();
            } else if (option.equalsIgnoreCase("save")) {
                System.out.print("Enter file to save to: ");
                String outFileName = console.nextLine();
                PrintStream outFile = new PrintStream(new File(outFileName));
                collectionManager.save(outFile);
                System.out.println("Collection Manager exported!");
                System.out.println();
            } else if (!option.equalsIgnoreCase("quit")) {
                System.out.println("  Invalid choice. Please try again.");
                System.out.println();
            }

            menu();
            option = console.nextLine();
        }
    }

    // 
    private static void menu() {
        System.out.println("What would you like to do? Choose an option in brackets.");
        System.out.println("  [add] item");
        System.out.println("  [contains] item");
        System.out.println("  [print] my collection");
        System.out.println("  [save] my collection");
        System.out.println("  [creative] extension");
        System.out.println("  [quit] program");
    }

    public static void generateInputs() throws FileNotFoundException {
        File bigTest = new File("emails.txt");
        Scanner scan = new Scanner(bigTest);
        ArrayList<Email> emails = new ArrayList<>();
        while (scan.hasNextLine())
            emails.add(Email.parse(scan));

        Collections.shuffle(emails);
        for (int i = 1; i < 4; i ++) {
            System.out.println(emails);
            CollectionManager cm = new CollectionManager();
            int start = emails.size() * (i - 1) / 3;
            for (int j = 0; j < 64; j++)
                cm.add(emails.get(start + j));
            cm.save(new PrintStream("input" + i + ".txt"));
        }
    }
}

