/**
 * @author Grant Yang
 * @version 2025.09.25
 * CSE 123
 * C0: Media
 * TA: Benoit Le
 */

import java.util.*;

/**
 * An implementation of the Media interface for representing Books,
 * including content, metadata (like title and author), and managing user ratings for the book.
 */
public class Book implements Media, Comparable<Book> {

    private String title;
    private List<String> authors;
    private List<String> content;

    private double ratingsAccum;
    private int numRatings;

    /**
     * Constructs a new book with the specified metadata and content.
     * @param title The title of the book. This string must not be null.
     * @param authors The authors of the book. This list may be empty, but
     *                the list must not be null, and it must not contain
     *                any null elements.
     * @param sc The Scanner to read the contents of the book from.
     *           All tokens will be consumed from the scanner, and getContent()
     *           can be used to retrieve a list of the tokens.
     * 
     * Postcondition: All tokens are exhausted from Scanner sc!
     * @throws IllegalArgumentException If any argument to the constructor is null,
     *                                  or if any element of `authors` is null.
     */
    public Book(String title, List<String> authors, Scanner sc) {
        if (title == null || authors == null || sc == null)
            throw new IllegalArgumentException("no parameter to the Book constructor may be null");
        
        for (String author : authors)
            if (author == null)
                throw new IllegalArgumentException(
                    "authors must not contain null elements in Book ctor");

        ratingsAccum = 0;
        numRatings = 0;

        content = new ArrayList<String>();
        while (sc.hasNext())
            content.add(sc.next());
        
        this.authors = authors;
        this.title = title;
    }

    /**
     * Converts this Book object to a human-readable string. This function is pure.
     * @return If no ratings have been added, the string is of the form "{title} by [{authors}]" 
     *         where {authors} is a comma-separated list of author names.
     *         If there is at least one rating, the string will be of the form
     *         "{title} by {authors}: {average rating} ({number of ratings} ratings)"
     *         where the average rating is rounded to 2 decimal places.
     */
    @Override
    public String toString() {
        String authorStr = "";
        for (String author : authors)
            authorStr += ", " + author;

        if (!authorStr.isEmpty())
            authorStr = authorStr.substring(2);
        
        String ratingsExtras = "";
        if (getNumRatings() > 0)
            ratingsExtras = String.format(": %.2f (%d ratings)", 
                                          getAverageRating(), getNumRatings());

        return title + " by [" + authorStr + "]" + ratingsExtras;
    }

    /**
     * Getter for the title of the book.
     * @return The book title as a String.
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the artists responsible for this book (i.e. authors).
     * @return A list of String author names. This list may be empty.
     */
    @Override
    public List<String> getArtists() {
        return authors;
    }

    /**
     * Tries to adds a numeric rating for this book. The statistics calculated by 
     * getNumRatings() and getAverageRating() are updated if successful.
     * @param score A non-negative integer rating for the book. Any rating scale
     *              may be used by the client, and all ratings are weighted equally.
     *              However if the score is negative, this function does nothing.
     */
    @Override
    public void addRating(int score) {
        if (score >= 0) {
            ratingsAccum += score;
            numRatings++;
        }
    }

    /**
     * Getter for the number of ratings that have been added.
     * @return A non-negative integer number for the number of times
     *         addRating(int) has been called on this book.
     */
    @Override
    public int getNumRatings() {
        return numRatings;
    }

    /**
     * Calculates the precise average rating of the book.
     * All ratings are weighted equally. This function is pure.
     * @return A double-precision float for the average rating.
     *         This function will return 0.0 if the book has no ratings.
     */
    @Override
    public double getAverageRating() {
        return getNumRatings() > 0 ? ratingsAccum / getNumRatings() : 0;
    }

    /**
     * Getter for the content of the book.
     * @return A list of tokens found in the book, most likely a list of the words.
     *         This list may be empty.
     */
    @Override
    public List<String> getContent() {
        return content;
    }

    /**
     * Utility function that compares a list of Comparable elements using
     * lexicographical order. Effectively implements compareTo on List<T extends Comparable<T>>.
     * We look at the lowest index at which the lists differ and use the result of 
     * compareTo on the elements. If there is no index at which the lists differ 
     * (i.e. one list is the prefix of another), the longer list is considered greater.
     * This total ordering is consistent with equals().
     * 
     * @param lhs Left hand side of the comparison
     * @param rhs Right hand side of the comparison
     * @return Positive integer if lhs > rhs, negative intger if lhs < rhs,
     *         and 0 if lhs.equals(rhs).
     */
    private <T extends Comparable<T>> int listCompareTo(List<T> lhs, List<T> rhs) {
        int size = Math.max(lhs.size(), rhs.size());
        for (int i = 0; i < size; i++) {
            if (i >= lhs.size())
                return -1;
            else if (i >= rhs.size())
                return 1;
            
            int cmp = lhs.get(i).compareTo(rhs.get(i));
            if (cmp != 0)
                return cmp;
        }

        return 0;
    }

    /**
     * Compares two books by the negative of their average rating first, then by 
     * the negative of the number of ratings, lexicographically by title, by authors, 
     * and then by content. We choose to reverse the usual ordering for average rating
     * and number of ratings so that the highest rated books with the most ratings 
     * will appear first in our collection, but all other fields use the normal ordering. 
     * This total ordering is compatible with equals().
     * 
     * @param o The other book to compare to.
     * @throws NullPointerException If o is null.
     * @return Returns 0 if this.equals(o). Otherwise, returns a
     *         positive integer if this book is "greater than" the other book,
     *         a negative integer if it is "less than".
     */
    @Override
    public int compareTo(Book o) {
        if (o == null)
            throw new NullPointerException("Book.compareTo(null) called");
    
        // this seems inefficient but isn't too bad as we can stop comparing
        // as soon as we notice a difference in each field.
        int[] comparisons = new int[]{
            -((Double) getAverageRating()).compareTo(o.getAverageRating()),
            -((Integer) getNumRatings()).compareTo(o.getNumRatings()),
            title.compareTo(o.title),
            listCompareTo(authors, o.authors),
            listCompareTo(content, o.content)
        };

        for (int i : comparisons)
            if (i != 0)
                return i;
        return 0;
    }
}