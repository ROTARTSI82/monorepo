/**
 * @author Grant Yang
 * @version 2025.09.25
 * CSE 123
 * C0: Search Engine
 * TA: Benoit Le
 */

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class Testing {

    @Test
    @DisplayName("Book string, list constructor")
    public void testBookStringList() {
        Book book = new Book("Title", List.of("Author 1", "Author 2"), new Scanner("Content"));

        assertEquals("Title", book.getTitle());
        assertEquals(List.of("Author 1", "Author 2"), book.getArtists());
        assertEquals("Title by [Author 1, Author 2]", book.toString());
        assertEquals(List.of("Content"), book.getContent());
    }

    @Test
    @DisplayName("getNumRatings")
    public void testNumRatings() {
        Book book = new Book("Title", List.of("Author"), new Scanner("Content"));
        
        assertEquals(0, book.getNumRatings());

        book.addRating(1);
        assertEquals(1, book.getNumRatings());


        book.addRating(1);
        assertEquals(2, book.getNumRatings());
        
    }

    @Test
    @DisplayName("getAvgRating")
    public void testAvgRatings() {
        Book book = new Book("Title", List.of("Author"), new Scanner("Content"));
        assertEquals(0, book.getAverageRating());
        

        book.addRating(4);
        assertEquals(4, book.getAverageRating());


        book.addRating(5);
        assertEquals(4.5, book.getAverageRating());
        
    }

    @Test
    @DisplayName("createIndex tests")
    public void testInvertedIndex() {
        Book mistborn = new Book("Mistborn", List.of("Brandon Sanderson"),
                                 new Scanner("Epic fantasy worldbuildling content"));
        Book farenheit = new Book("Farenheit 451", List.of("Ray Bradbury"),
                                  new Scanner("Realistic \"sci-fi\" content"));
        Book hobbit = new Book("The Hobbit", List.of("J.R.R. Tolkein"),
                               new Scanner("Epic fantasy quest content"));
        
        List<Media> books = List.of(mistborn, farenheit, hobbit);
        Map<String, Set<Media>> index = SearchClient.createIndex(books);

        assertFalse(index.containsKey("sci-fi"));
        assertTrue(index.containsKey("\"sci-fi\""));
        assertEquals(mistborn.compareTo(mistborn), 0);
        assertTrue(mistborn.compareTo(farenheit) > 0);

        Set<Book> expected = Set.of(mistborn, hobbit);
        assertEquals(expected, index.get("fantasy"));
    }
}
