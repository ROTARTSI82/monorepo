import java.time.LocalTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Class to simulate a solitaire game with a user interface
 * @author Grant Yang
 * @version 2021.11.03
 */
public class Solitaire
{
    /**
     * Entry point
     * @param args Command line arguments
     */
    public static void main(String[] args)
    {
        new Solitaire();
    }

    private Stack<Card> stock;
    private Stack<Card> waste;
    private Stack<Card>[] foundations;
    private Stack<Card>[] piles;
    private final SolitaireDisplay display;

    private static final int MOVE_TO_FOUNDATION = 56;
    private static final int NEW_DEAL = 3;
    private static final int RESET_STOCK = 56;
    private int score = 0;

    private LocalTime start;
    private final Stack<SaveState> saveStates = new Stack<>();

    /**
     * Public flag for if moves will be saved by saveState for undoing
     */
    public boolean savesEnabled = true;

    /**
     * Gets the number of piles there are
     * @return 7
     */
    public int pileLength()
    {
        return piles.length;
    }

    private static final int NUM_DEALT_FROM_STOCK = 1;

    static private class SaveState
    {
        public Stack<Card> stock;
        public Stack<Card> waste;
        public Stack<Card>[] foundations;
        public Stack<Card>[] piles;
        public int no;

        public Stack<Card> cloneStack(Stack<Card> in)
        {
            Stack<Card> tmp = new Stack<>();
            while (!in.isEmpty()) tmp.push(in.pop());

            Stack<Card> ret = new Stack<>();
            while (!tmp.isEmpty())
            {
                ret.push(tmp.peek().copy());
                in.push(tmp.pop());
            }

            return ret;
        }

        public SaveState(Stack<Card> s, Stack<Card> w, Stack<Card>[] f, Stack<Card>[] p, int tno)
        {
            no = tno;
            stock = cloneStack(s); // vector implements clonable
            waste = cloneStack(w);
            foundations = new Stack[f.length];
            for (int i = 0; i < f.length; i++)
                foundations[i] = cloneStack(f[i]);

            piles = new Stack[p.length];
            for (int i = 0; i < p.length; i++)
                piles[i] = cloneStack(p[i]);
        }

        @Override
        public String toString()
        {
            return String.format("State no %d", no);
        }
    }

    /**
     * Queries if the player has won
     * @return True iff all foundations have kings as their top card.
     */
    public boolean hasWon()
    {
        boolean ret = true;
        for (Stack<Card> foundation : foundations)
            ret &= (!foundation.isEmpty() && foundation.peek().getRank() == 13);
        return ret;
    }

    /**
     * Get the player's current score
     * @return Player score
     */
    public int getScore()
    {
        return score;
    }

    private void saveState()
    {
        if (savesEnabled) display.playSound("some.wav");
        saveStates.push(new SaveState(stock, waste, foundations, piles, 0));
    }

    /**
     * Undo the last move
     * @precondition There is a previous state to undo to
     * @postcondition The state of the game has returned to the state before the previous move
     */
    public void undoMove()
    {
        if (!saveStates.isEmpty())
        {
            SaveState s = saveStates.pop();
            foundations = s.foundations;
            piles = s.piles;
            stock = s.stock;
            waste = s.waste;
        }
    }

    /**
     * Gets the time elapsed since the game started as a string
     * @return Human readable time elapsed
     * @param end The time to stop timing at
     */
    public String timeElapsed(LocalTime end)
    {
        return String.format("%02d:%02d", MINUTES.between(start, end), SECONDS.between(start, end) % 60);
    }

    /**
     * Resets the game to a new starting position,
     * creating a random deal.
     */
    public void reset()
    {
        score = 0;
//        saveStates.clear();
        start = LocalTime.now();
        waste = new Stack<Card>();
        stock = new Stack<Card>();

        ArrayList<Card> allCards = new ArrayList<>();
        for (int suit = 0; suit < 4; suit++)
        {
            for (int rank = 1; rank <= 13; rank++)
            {
                allCards.add(new Card(rank, suit));
            }
        }

        Random rng = new Random();
        for (int cursor = 0; cursor < allCards.size(); cursor++)
        {
            int swap = cursor + rng.nextInt(allCards.size() - cursor);
            Card tmp = allCards.get(swap);
            allCards.set(swap, allCards.get(cursor));
            allCards.set(cursor, tmp);
        }

        foundations = new Stack[4];
        piles = new Stack[7];

        for (int i = 0; i < foundations.length; i++)
            foundations[i] = new Stack<>();

        for (int i = 0; i < piles.length; i++)
        {
            piles[i] = new Stack<>();
            for (int j = 0; j < i + 1; j++)
            {
                piles[i].push(allCards.get(0));
                allCards.remove(0);
            }
            piles[i].peek().turnUp();
        }

        while (allCards.size() > 0)
        {
            stock.push(allCards.get(0));
            allCards.remove(0);
        }
    }

    /**
     * Constructs a new game of solitaire!
     */
    public Solitaire()
    {
        reset();
        display = new SolitaireDisplay(this);
    }

    /**
     * Peek at the top of the stock
     * @return The card on top of the stock,
     *         or null if the stock is empty
     */
    public Card getStockCard()
    {
        return stock.isEmpty() ? null : stock.peek();
    }

    /**
     * Peek at the top of the waste
     * @return returns the card on top of the waste,
     *         or null if the waste is empty
     */
    public Card getWasteCard()
    {
        return waste.isEmpty() ? null : waste.peek();
    }

    /**
     * Peeks at the top of a foundation
     * @precondition:  0 <= index < 4
     * @postcondition returns the card on top of the given
     *                foundation, or null if the foundation
     *                is empty
     * @param index The number of the foundation to peek at
     * @return Top card of the foundation or null
     */
    public Card getFoundationCard(int index)
    {
        return foundations[index].isEmpty() ? null : foundations[index].peek();
    }

    /**
     * Access a certain pile
     * @precondition  0 <= index < 7
     * @postcondition returns a reference to the given pile
     * @param index Which pile to access
     * @return A reference to the stack representing a certain pile.
     */
    public Stack<Card> getPile(int index)
    {
        return piles[index];
    }

    /**
     * Handles user input for when the stock is clicked.
     * @postcondition Moves up to NUM_DEALT_FROM_STOCK cards from the stock
     *                to the waste or reset the stock depending on if it is empty.
     */
    public void stockClicked()
    {
        if (hasWon() || display.isWasteSelected() || display.isPileSelected()) return;

        saveState();
        if (!stock.empty())
        {
            for (int i = 0; i < NUM_DEALT_FROM_STOCK && !stock.isEmpty(); i++)
            {
                waste.push(stock.pop());
                waste.peek().turnUp();
            }
            score -= NEW_DEAL;
        }
        else
        {
            while (!waste.isEmpty())
            {
                stock.push(waste.pop());
                stock.peek().turnDown();
            }
            score -= RESET_STOCK;
        }
    }

    /**
     * Handles user input for when the waste is clicked.
     * @postcondition Selects or unselects the waste depending on
     *                if it was selected to begin with.
     */
    public void wasteClicked()
    {
        if (hasWon()) return;

        if (display.isWasteSelected())
        {
            for (int i = 0; i < foundations.length; i++)
            {
                if (!waste.isEmpty() && canAddToFoundation(waste.peek(), i))
                {
                    saveState();
                    foundations[i].push(waste.pop());
                    score += MOVE_TO_FOUNDATION;
                    break;
                }
            }
            display.unselect();
        }
        else if (!waste.isEmpty() && !display.isPileSelected() && !display.isPileSelected())
        {
            display.selectWaste();
        }
    }

    /**
     * Handles user input when a specific foundation is clicked
     * @precondition 0 <= index < 4
     * @param index The foundation clicked
     * @postcondition Selects the foundation if nothing is selected,
     *                unselects it if it is already selected,
     *                or tries moving a card to the foundation if the waste or a pile is selected.
     */
    public void foundationClicked(int index)
    {
        if (hasWon()) return;
        Stack<Card> p = display.isPileSelected() ? piles[display.selectedPile()] : null;

        if (p != null && !p.isEmpty() && canAddToFoundation(p.peek(), index))
        {
            saveState();
            foundations[index].push(p.pop());
            score += MOVE_TO_FOUNDATION;
            display.unselect();
        }
        else if (display.isWasteSelected() && !waste.isEmpty() &&
                canAddToFoundation(waste.peek(), index))
        {
            saveState();
            foundations[index].push(waste.pop());
            score += MOVE_TO_FOUNDATION;
            display.unselect();
        }
        else if (!display.isWasteSelected() && !display.isPileSelected()
                && !display.isFoundationSelected())
        {
            display.selectFoundation(index);
        }
        else if (display.isFoundationSelected())
        {
            display.unselect();
        }

    }

    /**
     * Handles user input when a certain pile is clicked
     * @precondition 0 <= index < 7
     * @param index The pile that was clicked
     * @postcondition Selects or unselects a pile depending on what was selected.
     *                Tries moving between piles if a different pile was selected.
     *                Tries moving a card from the waste or foundation depending on
     *                what was selected. Flips up the top card if it is face-down.
     */
    public void pileClicked(int index)
    {
        if (hasWon()) return;

        if (display.isWasteSelected())
        {
            if (canAddToPile(waste.peek(), index))
            {
                saveState();
                piles[index].push(waste.pop());
            }
            display.unselect();
        }
        else if (display.isFoundationSelected())
        {
            Stack<Card> f = foundations[display.selectedFoundation()];

            if (!f.isEmpty() && canAddToPile(f.peek(), index))
            {
                saveState();
                piles[index].push(f.pop());
                display.unselect();
            }
        }
        else if (!display.isPileSelected() && !piles[index].isEmpty())
        {
            if (piles[index].peek().isFaceUp()) display.selectPile(index);
            else piles[index].peek().turnUp();
        }
        else if (display.isPileSelected() && display.selectedPile() == index)
        {
            for (int i = 0; i < foundations.length; i++)
            {
                if (!piles[index].isEmpty() && canAddToFoundation(piles[index].peek(), i))
                {
                    saveState();
                    foundations[i].push(piles[index].pop());
                    score += MOVE_TO_FOUNDATION;
                    break;
                }
            }
            display.unselect();
        }
        else if (display.isPileSelected())
        {
            attemptMove(display.selectedPile(), index, 9999);
            display.unselect();
        }
    }

    //precondition: 0 <= index < 4
    //postcondition: Returns true if the given card can be
    // legally moved to the top of the given
    // foundation
    private boolean canAddToFoundation(Card card, int index)
    {
        return (card.getRank() == 1 && foundations[index].isEmpty()) ||
                (!foundations[index].isEmpty() &&
                        card.getRank() == foundations[index].peek().getRank() + 1 &&
                        foundations[index].peek().getSuit().equals(card.getSuit()));
    }

    private void attemptMove(int pfrom, int pto, int max)
    {
        Stack<Card> toAdd = removeFaceUpCards(pfrom, max);
        if (!toAdd.isEmpty() && canAddToPile(toAdd.peek(), pto))
        {
            // add it
            saveState();
            addToPile(toAdd, pto);
        }
        else
        {
            max = toAdd.size() - 1;
            addToPile(toAdd, pfrom); // put it back
            if (max > 0) attemptMove(pfrom, pto, max);
        }
    }

    /**
     * Try moving all cards that can be moved to the foundation to the foundation.
     * @postcondition All accessible cards that can be moved are moved to the foundation.
     * @param doSave If true, state will be saved with saveState() for undoing
     */
    public void autoFoundation(boolean doSave)
    {
        if (doSave) saveState();
        savesEnabled = false;

        // try moving from waste to foundation
        for (int j : display.shuffledRange(foundations.length))
        {
            display.unselect();
            wasteClicked();
            foundationClicked(j);
        }

        // try moving from piles to foundations
        for (int j : display.shuffledRange(piles.length))
        {
            for (int k : display.shuffledRange(foundations.length))
            {
                display.unselect();
                pileClicked(j);
                foundationClicked(k);
            }
        }
        savesEnabled = true;
    }

    //precondition: 0 <= index < 7
    //postcondition: Removes all face-up cards on the top of
    // the given pile; returns a stack
    // containing these cards
    private Stack<Card> removeFaceUpCards(int index, int max)
    {
        Stack<Card> ret = new Stack<>();
        int i = 0;
        while (!piles[index].isEmpty() && piles[index].peek().isFaceUp() && i++ < max)
            ret.push(piles[index].pop());

        return ret;
    }

    //precondition: 0 <= index < 7
    //postcondition: Removes elements from cards, and adds
    // them to the given pile.
    private void addToPile(Stack<Card> cards, int index)
    {
        while (!cards.isEmpty())
            piles[index].push(cards.pop());
    }

    //precondition: 0 <= index < 7
    //postcondition: Returns true if the given card can be
    // legally moved to the top of the given pile
    private boolean canAddToPile(Card c, int index)
    {
        return (piles[index].isEmpty() && c.getRank() == 13) ||
                (!piles[index].isEmpty() && piles[index].peek().isFaceUp() &&
                (c.isRed() ^ piles[index].peek().isRed()) &&
                c.getRank() == piles[index].peek().getRank() - 1);
    }
}