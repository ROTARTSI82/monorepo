/**
 * @author Grant Yang
 * @version 2025.09.25
 * CSE 123
 * C0: Search Engine
 * TA: Benoit Le
 */

import java.io.*;
import java.util.*;

// This class allows users to find and rate books within BOOK_DIRECTORY
// containing certain terms
public class SearchClient {
    public static final String BOOK_DIRECTORY = "./books";
    private static final Random RAND = new Random();

    // Some class constants you can play around with to give random ratings to the uploaded books!
    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 5;
    public static final int MIN_NUM_RATINGS = 1;
    public static final int MAX_NUM_RATINGS = 100;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        List<Media> media = new ArrayList<>(loadBooks());

        Map<String, Set<Media>> index = createIndex(media);

        System.out.println("Welcome to the CSE 123 Search Engine!");
        String command = "";
        while (!command.equalsIgnoreCase("quit")) {
            System.out.println("What would you like to do? [Search, Rate, Quit]");
            System.out.print("> ");
            command = console.nextLine();

            if (command.equalsIgnoreCase("search")) {
                searchQuery(console, index);
            } else if (command.equalsIgnoreCase("rate")) {
                addRating(console, media);
            } else if (!command.equalsIgnoreCase("quit")) {
                System.out.println("Invalid command, please try again.");
            }
        }
        System.out.println("See you next time!");
    }

    /**
     * Builds an inverted index that allows us to quickly look up
     * all the Media documents that a specific keyword appears in.
     * This function treats keywords in a case-insensitive manner
     * and strips leading and trailing whitespace.
     * 
     * @param docs The set of documents to index. If the list contains any null
     *             elements, they will be ignored. This parameter must not be null.
     * @throws NullPointerException If `docs` is null.
     * @return An index for all tokens appearing in the corpus, a map from
     *         lowercase tokens to the set of all documents that the word appears in.
     */
    public static Map<String, Set<Media>> createIndex(List<Media> docs) {
        if (docs == null)
            throw new NullPointerException("InvertedIndex.createIndex(null) called");

        Map<String, Set<Media>> index = new TreeMap<String, Set<Media>>();
        
        // I think this runs in something like O(m log m) time
        // where m is the combined number of keywords in the documents? idk tho
        for (Media doc : docs) {
            if (doc != null) {
                for (String tok : doc.getContent()) {
                    String sanitized = tok.trim().toLowerCase();
                    if (!index.containsKey(sanitized))
                        index.put(sanitized, new HashSet<Media>());
                    index.get(sanitized).add(doc);
                }
            }
        }

        return index;
    }

    /**
     * Performs a search for documents by how often they contain words from the query.
     * The document(s) containing the highest number of the keywords are returned,
     * with multiple documents being returned if there is a tie. 
     * An empty set is returned if none of the query keywords appear in any documents.
     *
     * This search does consider the frequency of the keywords in the query, but
     * it does NOT consider the frequency of the keywords within the documents being searched. 
     * 
     * The query is case-insensitive, and we ignore whitespace.
     * 
     * @param index The search index for your corpus, built by createIndex(List<Media>).
     * @param query A simple string of keywords to look for literally.
     * @return A set of documents which contain the maximal proportion of words in the query string.
     * @throws NullPointerException Can throw if `index` or `query` are null, 
     *                              or if `index` contains null values.
     */
    public static Set<Media> search(Map<String, Set<Media>> index, String query) {
        Scanner sc = new Scanner(query);
        List<String> toks = new ArrayList<String>();
        while (sc.hasNext())
            toks.add(sc.next().trim().toLowerCase());
        sc.close();

        Map<Media, Integer> counts = new HashMap<Media, Integer>();
        Set<Media> winners = new TreeSet<Media>();
        int maxOcc = -1;
        for (String tok : toks) {
            if (index.containsKey(tok)) {
                for (Media doc : index.get(tok)) {
                    int occ = 1 + (counts.containsKey(doc) ? counts.get(doc) : 0);
                    counts.put(doc, occ);

                    if (occ > maxOcc) {
                        maxOcc = occ;
                        winners.clear();
                        winners.add(doc);
                    } else if (occ == maxOcc) {
                        winners.add(doc);
                    }
                }
            }
        }

        return winners;
    }
    
    // Allows the user to search a specific query using the provided 'index' to find appropraite
    //  Media entries.
    //
    // Parameters:
    //   console - the Scanner to get user input from. Should be non-null
    //   index - an inverted index mapping terms to the Set of media containing those terms.
    //           Should be non-null
    public static void searchQuery(Scanner console, Map<String, Set<Media>> index) {
        System.out.println("Enter query:");
        System.out.print("> ");
        String query = console.nextLine();

        Set<Media> result = search(index, query);
        
        if (result.isEmpty()) {
            System.out.println("\tNo results!");
        } else {
            for (Media m : result) {
                System.out.println("\t" + m.toString());
            }
        }
    }

    // Allows the user to add a rating to one of the options wthin 'media'
    //
    // Parameters:
    //   console - the Scanner to get user input from. Should be non-null.
    //   media - list of all media options loaded into the search engine. Should be non-null.
    public static void addRating(Scanner console, List<Media> media) {
        for (int i = 0; i < media.size(); i++) {
            System.out.println("\t" + i + ": " + media.get(i).toString());
        }
        System.out.println("What would you like to rate (enter index)?");
        System.out.print("> ");
        int choice = Integer.parseInt(console.nextLine());
        if (choice < 0 || choice >= media.size()) {
            System.out.println("Invalid choice");
        } else {
            System.out.println("Rating [" + media.get(choice).getTitle() + "]");
            System.out.println("What rating would you give?");
            System.out.print("> ");
            int rating = Integer.parseInt(console.nextLine());
            media.get(choice).addRating(rating);
        }
    }

    // Loads all books from BOOK_DIRECTORY. Assumes that each book starts with two lines -
    //      "Title: " which is followed by the book's title
    //      "Author: " which is followed by the book's author
    //
    // Returns:
    //   A list of all book objects corresponding to the ones located in BOOK_DIRECTORY
    public static List<Media> loadBooks() throws FileNotFoundException {
        List<Media> ret = new ArrayList<>();
        
        File dir = new File(BOOK_DIRECTORY);
        for (File f : dir.listFiles()) {
            Scanner sc = new Scanner(f, "utf-8");
            String title = sc.nextLine().substring("Title: ".length());
            List<String> author = List.of(sc.nextLine().substring("Author: ".length()));

            Media book = new Book(title, author, sc);

            // Adds random ratings to 'book' based on the class constants. 
            // Feel free to comment this out.
            int minRating = RAND.nextInt(MAX_RATING - MIN_RATING + 1) + MIN_RATING;
            addRatings(minRating, Math.min(MAX_RATING,RAND.nextInt(MAX_RATING - minRating + 1) + minRating),
                        RAND.nextInt(MAX_NUM_RATINGS - MIN_NUM_RATINGS) + MIN_NUM_RATINGS, book);
            ret.add(book);
        }

        return ret;
    }

    // Adds ratings to the provided media numRatings amount of times. Each rating is a random int
    // between minRating and maxRating (inclusive).
    private static void addRatings(int minRating, int maxRating, int numRatings, Media media) {
        for (int i = 0; i < numRatings; i++) {
            media.addRating(RAND.nextInt(maxRating - minRating + 1) + minRating);
        }
    }
}
