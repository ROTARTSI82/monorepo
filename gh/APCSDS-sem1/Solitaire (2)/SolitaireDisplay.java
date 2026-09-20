import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalTime;
import java.util.*;
import javax.swing.Timer;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * Display driver for the solitaire application
 * @author Grant Yang
 * @version 2021.11.03
 */
public class SolitaireDisplay extends JComponent implements MouseListener, KeyListener
{
    private static final int CARD_WIDTH = 73;
    private static final int CARD_HEIGHT = 97;
    private static final int SPACING = 5;  //distance between cards
    private static final int FACE_UP_OFFSET = 15;  //distance for cascading face-up cards
    private static final int FACE_DOWN_OFFSET = 5;  //distance for cascading face-down cards
    private static final Random RAND = new Random();
    private static final Font WIN = new Font("Comic Sans", Font.BOLD, 32);
    private static final Font INSTRUCTIONS = new Font("Comic Sans", Font.BOLD, 16);

    private final JFrame frame;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private final Solitaire game;

    private boolean winProcessed = false;

    // index 0 - yay.wav
    private final AudioInputStream[] soundFx = new AudioInputStream[2];

    private final Timer timer;
    private LocalTime endTime;

    /**
     * Constructs a display driver for a Solitaire game object.
     * @param game Game object to use
     */
    public SolitaireDisplay(Solitaire game)
    {
        this.game = game;

        frame = new JFrame("Solitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);

        setFocusable(true);
        this.setPreferredSize(new Dimension(CARD_WIDTH * 7 + SPACING * 8,
                CARD_HEIGHT * 2 + SPACING * 3 + FACE_DOWN_OFFSET * 7 + 13 * FACE_UP_OFFSET));
        this.addMouseListener(this);
        this.addKeyListener(this);

        frame.pack();
        frame.setVisible(true);

        try
        {
            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(Objects.requireNonNull(SolitaireDisplay.class.
                    getClassLoader().getResource("tmpsec.wav")));


            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            System.out.println("Prepared to be soothed by the dulcet tones of"
                    + " Paul McCartney's 'Temporary Secretary'");
            FloatControl gainControl = (FloatControl) clip
                    .getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(100.0f);
        }
        catch (Exception e)
        {
            System.err.println("'Tis but a tragedy that you may not be soothed by the dulcet"+
                    " tones of Paul McCartney's 'Temporary Secretary': " + e);
        }

        timer = new Timer(1000, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                repaint();
            }
        });
        timer.start();
    }

    /**
     * Plays a sound effect
     * @param path File name of the sound to play
     */
    public void playSound(String path)
    {
        try
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Objects.requireNonNull(Solitaire.class
                    .getClassLoader().getResource(path))));
            clip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Draw the state of the game to the screen
     * @param g AWT graphics objects to draw with.
     */
    public void paintComponent(Graphics g)
    {
        if (game.hasWon())
        {
            if (!winProcessed)
            {
                playSound("yay.wav");
                winProcessed = true;
            }

            g.setColor(new Color(RAND.nextInt(256), RAND.nextInt(256), RAND.nextInt(256)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(RAND.nextInt(256), RAND.nextInt(256), RAND.nextInt(256)));
            g.setFont(WIN);
            g.drawString(String.format("You scored %d in %s", game.getScore(),
                            game.timeElapsed(endTime)), 10, 100);
            g.setFont(INSTRUCTIONS);
            g.drawString("Press ESC to play again", 100, 150);
            return;
        }

        winProcessed = false;
        endTime = LocalTime.now();

        //background
        g.setColor(new Color(0, 128, 0));
        g.fillRect(0, 0, getWidth(), getHeight());

        //face down
        drawCard(g, game.getStockCard(), SPACING, SPACING);

        //stock
        drawCard(g, game.getWasteCard(), SPACING * 2 + CARD_WIDTH, SPACING);
        if (selectedRow == 0 && selectedCol == 1)
            drawBorder(g, SPACING * 2 + CARD_WIDTH, SPACING);

        //aces
        for (int i = 0; i < 4; i++)
        {
            drawCard(g, game.getFoundationCard(i), SPACING * (4 + i) + CARD_WIDTH * (3 + i),
                    SPACING);
            if (selectedRow == 0 && selectedCol - 2 == i)
            {
                drawBorder(g, SPACING + (CARD_WIDTH + SPACING) * (i + 3), 5);
            }
        }


        //piles
        for (int i = 0; i < 7; i++)
        {
            Stack<Card> pile = game.getPile(i);
            int offset = 0;
            for (int j = 0; j < pile.size(); j++)
            {
                drawCard(g, pile.get(j), SPACING + (CARD_WIDTH + SPACING) * i,
                        CARD_HEIGHT + 2 * SPACING + offset);
                if (selectedRow == 1 && selectedCol == i && j == pile.size() - 1)
                    drawBorder(g, SPACING + (CARD_WIDTH + SPACING) * i,
                            CARD_HEIGHT + 2 * SPACING + offset);

                if (pile.get(j).isFaceUp())
                    offset += FACE_UP_OFFSET;
                else
                    offset += FACE_DOWN_OFFSET;
            }
        }

        g.setFont(INSTRUCTIONS);
        g.setColor(new Color(0, 0, 0));
        g.drawString("Time: " + game.timeElapsed(endTime), getWidth() - 300, getHeight() - 75);
        g.drawString("BACKSPACE - Undo Move", getWidth() - 300, getHeight() - 50);
        g.drawString("SHIFT - Send cards to foundation", getWidth() - 300, getHeight() - 30);
        g.drawString("ESC - Reset game", getWidth() - 300, getHeight() - 10);

        g.drawString("ENTER - Run solitaire AI", 10, getHeight() - 10);
        g.drawString("Score: " + game.getScore(), 10, getHeight() - 30);
    }

    private void drawCard(Graphics g, Card card, int x, int y)
    {
        if (card == null)
        {
            g.setColor(Color.BLACK);
            g.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        }
        else
        {
            Image image = new ImageIcon(card.getFileName()).getImage();
            g.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
        //none selected previously
        int col = e.getX() / (SPACING + CARD_WIDTH);
        int row = e.getY() / (SPACING + CARD_HEIGHT);
        if (row > 1)
            row = 1;
        if (col > 6)
            col = 6;

        if (row == 0 && col == 0)
            game.stockClicked();
        else if (row == 0 && col == 1)
            game.wasteClicked();
        else if (row == 0 && col >= 3)
            game.foundationClicked(col - 3);
        else if (row == 1)
            game.pileClicked(col);
        repaint();
    }

    private void drawBorder(Graphics g, int x, int y)
    {
        g.setColor(Color.YELLOW);
        g.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        g.drawRect(x + 1, y + 1, CARD_WIDTH - 2, CARD_HEIGHT - 2);
        g.drawRect(x + 2, y + 2, CARD_WIDTH - 4, CARD_HEIGHT - 4);
    }

    /**
     * Unselects everything
     */
    public void unselect()
    {
        selectedRow = -1;
        selectedCol = -1;
    }

    /**
     * Queries if the waste is currently selected
     * @return True if the waste is selected
     */
    public boolean isWasteSelected()
    {
        return selectedRow == 0 && selectedCol == 1;
    }

    /**
     * Selects the waste
     */
    public void selectWaste()
    {
        selectedRow = 0;
        selectedCol = 1;
    }

    /**
     * Queries if a pile is selected
     * @return True if any pile is selected
     */
    public boolean isPileSelected()
    {
        return selectedRow == 1;
    }

    /**
     * Queries the pile selected
     * @precondition isPileSelected()
     * @return Index of the pile selected, -1 otherwise.
     */
    public int selectedPile()
    {
        if (selectedRow == 1)
            return selectedCol;
        else
            return -1;
    }

    /**
     * Selects a certain pile
     * @param index Pile to select
     */
    public void selectPile(int index)
    {
        selectedRow = 1;
        selectedCol = index;
    }

    /**
     * Selects a certain foundation
     * @param index Foundation to select
     */
    public void selectFoundation(int index)
    {
        selectedRow = 0;
        selectedCol = 2 + index;
    }

    /**
     * Queries if a foundation is selected
     * @return True if any foundation is selected
     */
    public boolean isFoundationSelected()
    {
        return selectedRow == 0 && selectedCol > 1;
    }

    /**
     * Gets the selected foundation
     * @precondition isFoundationSelected()
     * @return Index of the selected foundation, a negative number if invalid.
     */
    public int selectedFoundation()
    {
        return selectedRow == 0 ? selectedCol - 2 : -1;
    }

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            autoIterate();
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            game.autoFoundation(true);
        }
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            game.reset();
        else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
            game.undoMove();
        }

        this.repaint();
    }

    private void autoIterate()
    {
        game.savesEnabled = false;
        unselect();
        game.stockClicked();

        for (int i = 0; i < 16; i++)
        {
            game.autoFoundation(false);
            game.savesEnabled = false;

            revealAllPiles();

            if (i % 2 == 0)
            {
                // try moving between piles
                for (int j : shuffledRange(game.pileLength()))
                {
                    for (int k : shuffledRange(game.pileLength()))
                    {
                        unselect();
                        game.pileClicked(j);
                        game.pileClicked(k);
                    }
                }
                revealAllPiles();
            }
            else
            {
                // try moving from waste onto piles
                for (int j : shuffledRange(game.pileLength()))
                {
                    unselect();
                    game.wasteClicked();
                    game.pileClicked(j);
                }
            }
        }
        game.savesEnabled = true;
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    /**
     * Generate a random permutation of values from 0 to max (exclusive)
     * @param max Number of elements in the array
     * @return A shuffled array
     */
    public int[] shuffledRange(int max)
    {
        int[] ret = new int[max];
        for (int i = 0; i < max; i++)
            ret[i] = i;

        Random r = new Random();
        for (int i = 0; i < max - 1; i++)
        {
            int swapWith = i + r.nextInt(max - i);
            int tmp = ret[i];
            ret[i] = ret[swapWith];
            ret[swapWith] = tmp;
        }
        return ret;
    }

    /**
     * Tries to reveal the top card of any pile that doesn't have the
     * top card revealed.
     * @postcondition No pile has a face-down top card
     */
    public void revealAllPiles()
    {
        for (int i : shuffledRange(game.pileLength()))
        {
            unselect();
            game.pileClicked(i);
        }
    }
}