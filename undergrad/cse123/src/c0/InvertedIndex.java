/**
 * @author Grant Yang
 * @version 2025.09.25
 * CSE 123
 * C0: Inverted Index
 * TA: Benoit Le
 */

import java.util.*;

/**
 * Utility class for building an inverted index.
 * See the static function createIndex(List<Media>).
 */
public class InvertedIndex {
    public static void main(String[] args) {
        List<Media> docs = List.of(
            new Book("Mistborn", List.of("Brandon Sanderson"),
                     new Scanner("Epic fantasy worldbuildling content")),
            new Book("Farenheit 451", List.of("Ray Bradbury"),
                     new Scanner("Realistic \"sci-fi\" content")),
            new Book("The Hobbit", List.of("J.R.R. Tolkein"),
                     new Scanner("Epic fantasy quest content"))
        );
        
        Map<String, Set<Media>> result = createIndex(docs);
        System.out.println(docs);
        System.out.println();
        System.out.println(result);
    }

    /**
     * Builds an inverted index that allows us to quickly look up
     * all the Media documents that a specific keyword appears in.
     * This function treats keywords in a case-insensitive manner
     * and strips leading and trailing whitespace.
     * 
     * I think this runs in something like O(m log m) time
     * where m is the combined number of keywords in the documents? idk tho.
     * 
     * @param docs The set of documents to index. If the list contains any null
     *             elements, they will be ignored.
     * @throws NullPointerException If docs is null.
     * @return An index for all tokens appearing in the corpus, a map from
     *         lowercase tokens to the set of all documents that the word appears in.
     */
    public static Map<String, Set<Media>> createIndex(List<Media> docs) {
        if (docs == null)
            throw new NullPointerException("InvertedIndex.createIndex(null) called");

        Map<String, Set<Media>> index = new TreeMap<String, Set<Media>>();
        
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
}
