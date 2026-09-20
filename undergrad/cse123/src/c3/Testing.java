/**
 * @author Grant Yang
 * @version 2025.11.30
 * CSE 123
 * C3: B(e)ST of the B(e)ST
 * TA: Benoit Le
 */

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Testing {
    private static final List<Email> EMAIL_SET = Arrays.asList(
            new Email(1478328181, 98,
                      "Market Indicator Gives Trump An 86% Chance Of Winning The Election",
                      "Richard Kahn", "Jeffrey Epstein [jeevacation@gmail.com]"),
            new Email(1498814596, 51,
                      "Funny dog video of a dog gnawing at Donald Trump's hair.",
                      "Soon Yi Previn [asoonyiprevin@gmail.com]", "Soon Yi Previn"),
            new Email(1514540700, -90, "Re: Kuhn - Breakthroughs (and Trump)",
                      "Robert Kuhn <III>", "jeffrey E. <jeevacation@gmail.com>"),
            new Email(1520453874, 98, "Re: What's up in trump World?",
                      "jeffrey E. [jeevacation@gmail.com]", "LHS"),
            new Email(1521646328, -99, "RE: hey", "Mark L. Epstein",
                      "jeffrey E. [jeevacation@gmail.com]"),
            new Email(1525888632, 99, "ny times freudian",
                      "jeffrey E. [jeevacation@gmail.com]", "Steve Bannon"));

    private static final boolean[][] CONTAINMENT = new boolean[][]{
            new boolean[]{false, false, true},
            new boolean[]{true, false, false},
            new boolean[]{false, false, false},
            new boolean[]{false, false, false},
            new boolean[]{false, false, false},
            new boolean[]{false, false, false}};

    void testBSTProperty(CollectionManager cm) {
        // tests the toString() and BST property at the same time
        // as the toString() is just an inorder traversal.
        Scanner repr = new Scanner(cm.toString());
        Email prev = null;
        while (repr.hasNext()) {
            Email cur = Email.rawParse(repr);
            assertNotNull(cur);
            assertTrue(prev == null || cur.compareTo(prev) > 0);
            prev = cur;
        }
    }

    @Test
    void loadSaveTest() throws IOException {
        // tests that loading and immediately saving does not change anything.
        // also tests specific items were loaded correctly.
        for (int i = 1; i < 4; i++) {
            String in1 = new String(Files.readAllBytes(Paths.get("input" + i + ".txt")));
            CollectionManager cm = new CollectionManager(new Scanner(in1));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(baos);

            testBSTProperty(cm);
            for (int j = 0; j < EMAIL_SET.size(); j++)
                assertEquals(CONTAINMENT[j][i - 1], cm.contains(EMAIL_SET.get(j)));

            cm.save(out);
            out.close();
            assertEquals(in1, baos.toString());
        }
    }

    @Test
    void addContainsRemoveTest() {
        CollectionManager cm = new CollectionManager();
        ArrayList<Email> emails = new ArrayList<>(EMAIL_SET);
        Collections.shuffle(emails);

        Random rand = new Random();
        boolean[] decisions = new boolean[emails.size()];

        // adds a random subset in a random order and tests add() and contains()
        // behave as expected.
        for (int i = 0; i < emails.size(); i++) {
            // tests that default constructor is empty at first
            assertFalse(cm.contains(emails.get(i)));
            decisions[i] = rand.nextBoolean();
            if (decisions[i])
                cm.add(emails.get(i));
            testBSTProperty(cm);
        }

        for (int i = 0; i < emails.size(); i++)
            assertEquals(cm.contains(emails.get(i)), decisions[i]);

        // adds all items, tests adding items that were already in.
        for (Email email : emails)
            cm.add(email);
        testBSTProperty(cm);

        // this should remove emails at index 3 & 4 in EMAIL_SET
        cm.removeBetween(1514540700, 1525888632);
        testBSTProperty(cm);
        System.out.println(cm);
        for (int i = 0; i < EMAIL_SET.size(); i++)
            assertEquals(cm.contains(EMAIL_SET.get(i)), i != 3 && i != 4);

        // remove all
        cm.removeBetween(Long.MIN_VALUE, Long.MAX_VALUE);
        testBSTProperty(cm);
        assertEquals("", cm.toString());
    }
}