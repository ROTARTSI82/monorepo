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
 * including content, metadata (like title and author), and user ratings for the book.
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
     * Adds a numeric rating for this book. The statistics calculated by 
     * getNumRatings() and getAverageRating() are updated accordingly.
     * @param score The integer rating for the book. Any rating scale
     *              may be used by the client, and all ratings are weighted equally.
     */
    @Override
    public void addRating(int score) {
        ratingsAccum += score;
        numRatings++;
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
     * Compares two books by their average rating.
     * The number of ratings is not considered if the books have at least
     * one rating, but a book with zero ratings is considered less than any
     * book with one or more ratings and equal to other books with zero ratings.
     * 
     * @param o The other book to compare to.
     * @throws NullPointerException If o is null.
     * @return A positive integer if this book is higher rated than the other book,
     *         zero if they are rated equally, and a negative integer if the
     *         other book is higher rated.
     */
    @Override
    public int compareTo(Book o) {
        if (o == null)
            throw new NullPointerException("Book.compareTo(null) called");
    
        if (getNumRatings() == 0)
            return -o.getNumRatings();
        else if (o.getNumRatings() == 0)
            return 1;
        
        double diff = getAverageRating() - o.getAverageRating();
        return (int) (diff >= 0 ? Math.ceil(diff) : Math.floor(diff));
    }
}