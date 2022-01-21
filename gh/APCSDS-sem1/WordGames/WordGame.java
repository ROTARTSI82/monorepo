import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class implementing word games with a GUI to demonstrate binary
 * search on an ordered list of words.
 *
 * Jotto: Try to guess a computer's n-letter word given n guesses
 *        and the number of common letters between the word and your guess.
 * Echo: Look up whether a string is a word or not and if so, what
 *       index it is at in the dictionary.
 *
 * @author Grant Yang
 * @version 2022.01.20
 */
public class WordGame
{
    private final WordGameDisplay display = new WordGameDisplay();
    private final List<String> dictionary = new ArrayList<>();
    private String text;

    /**
     * Construct a new WordGame object, loading the dictionary
     * in "words.txt"
     */
    public WordGame()
    {
        Iterator<String> it = display.loadWords("words.txt");
        while (it.hasNext())
            dictionary.add(it.next());
    }

    /**
     * Play a game of echo forever, looking up the given string in the dictionary
     * and returning its index if it was found.
     */
    public void echo()
    {
        display.setTitle("The Echo Game");

        text = "Enter a word.";
        for (;;)
        {
            display.setText(text);
            String inp = display.getGuess();
            int index = dictionaryIndex(inp);
            text += "\n\"" + inp + "\"";
            if (index == -1)
                text += "is not a word.";
            else
                text += "is word #" + index +".\nEnter another word.";
        }
    }

    /**
     * Plays a game of jotto forever, with the computer starting with a 3-letter word.
     * Each round, the player has n guesses to guess the computer's random n-letter word.
     * The player's guess MUST be at a valid n-letter English word, and the computer
     * tells the player how many letters are shared between their guess and the secret.
     * At the end of each round, n is increased by one. "pass" may be entered to
     * reveal the secret and have the computer choose a new word and restart the round.
     * If the player fails a round, the game restarts from n=3.
     */
    public void jotto()
    {
        int n = 3;
        text = "";

        for (;;)
        {
            text += "Guess my " + n + "-letter word!\n";
            display.setTitle("Jotto (" + n + " letters)");

            String secret = getRandomWord(n);
            String guess = "";

            int guessNum = 1;
            while (!guess.equals(secret) && guessNum <= n)
            {
                display.setText(text);
                guess = display.getGuess();

                if (guess.equals("pass"))
                {
                    guessNum = 1;
                    text += "OK! The word was " + secret + "\nNow guess my new "
                            + n + "-letter word!\n";
                    secret = getRandomWord(n);
                }
                else
                {
                    if (guess.equals("secretcheatcode"))
                        guess = secret;

                    if (guessNum == 1) text = "";

                    if (guess.length() != n)
                        text += guessNum + ".\t" + guess + "\tmust be " + n + " letters\n";
                    else if (dictionaryIndex(guess) == -1)
                        text += guessNum + ".\t" + guess + "\tis not a word.\n";
                    else
                        text += guessNum + ".\t" + guess + "\t" +
                                commonLetters(guess, secret) + "\n";

                    guessNum++;
                }
            }

            if (guess.equals(secret))
            {
                text += "You got it!\n";
                n++;
            }
            else
            {
                text += "It was " + secret + "! Play again!\n";
                n = 3;
            }
        }
    }

    /**
     * Display a menu giving the user the choice to play Jotto or the Echo Game.
     */
    public void menu()
    {
        display.setTitle("Shall we play a game?");
        display.setText("echo\njotto");
        String inp = display.getGuess();

        if (inp.equals("jotto"))
            jotto();
        else if (inp.equals("echo"))
            echo();
        else
            menu();
    }

    /**
     * Determines the number of letters shared between two
     * strings in O(n) with n being the number of letters
     * in both strings combined.
     *
     * @param a The first string
     * @param b The second string
     * @return The number of common letters shared between
     *         the strings. For example, "eye" and "bee" share 2
     *         letters (two 'e's) and so do "bee" and "ebb" ('e' and 'b')
     */
    public int commonLetters(String a, String b)
    {
        int[] letterFrequency = new int[256];
        for (int i = 0; i < 256; i++) letterFrequency[i] = 0;

        for (int i = 0; i < a.length(); i++)
            letterFrequency[a.charAt(i)]++;

        int occ = 0;
        for (int i = 0; i < b.length(); i++)
            if (letterFrequency[b.charAt(i)]-- > 0)
                occ++;

        return occ;
    }

    /**
     * Gets a random word from the dictionary in O(1)
     * @return A word from `dictionary` with equal probability of getting
     *         each word.
     */
    public String getRandomWord()
    {
        return dictionary.get((int) (Math.random() * dictionary.size()));
    }

    /**
     * Gets a random word of a certain length from the dictionary
     * using <a href="https://en.wikipedia.org/wiki/Rejection_sampling">rejection sampling</a>.
     *
     * This method WILL get stuck in an infinite loop if there are no words
     * of the specified length in the dictionary!
     *
     * @param len The desired length of the word
     * @return A random word of that length.
     */
    public String getRandomWord(int len)
    {
        String word = "";
        while (word.length() != len)
            word = getRandomWord();
        return word;
    }

    /**
     * Searches the dictionary for a word using binary search,
     * returning the index if the word is found.
     * This function has a time complexity of O(log n) with n
     * being the number of words in the dictionary.
     *
     * @param str The string to look for
     * @return If the word is not in the dictionary, -1 is returned.
     *         Otherwise, we return the index of the word in the dictionary.
     */
    public int dictionaryIndex(String str)
    {

        int upperBound = dictionary.size() - 1;
        int lowerBound = 0;

        while (lowerBound <= upperBound)
        {
            int midpoint = lowerBound + (upperBound - lowerBound) / 2;

            int cmp = dictionary.get(midpoint).compareTo(str);
            if (cmp == 0)
                return midpoint;
            else if (cmp > 0)
                upperBound = midpoint - 1;
            else
                lowerBound = midpoint + 1;
        }

        return -1;
    }
}
