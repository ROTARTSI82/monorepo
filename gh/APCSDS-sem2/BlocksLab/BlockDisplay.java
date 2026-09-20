import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
/**
 * @author Anu Datar
 * 
 * Changed block size and added a split panel display for next block and Score
 * Used to display the contents of a game board
 * 
 * @author Ryan Adolf
 * @version 1.0
 * 
 * Fixed the lag issue with block rendering 
 * Removed the JPanel
 */
public class BlockDisplay extends JComponent implements KeyListener
{
    private final char[] types = {'I', 'T', 'O', 'L', 'J', 'S', 'Z'};

    /**
     * The state of the display, indicating
     * what screen the display is displaying.
     */
    enum State
    {
        TETRIS,
        GAME_OVER
    }

    /**
     * State of the display, either playing
     * tetris or showing the game over screen
     */
    public State state = State.TETRIS;

    private static final Color BACKGROUND = Color.BLACK;
    private static final Color BORDER = Color.BLACK;

    private static final int OUTLINE = 2;
    private static final int BLOCKSIZE = 20;

    private final MyBoundedGrid<Block> board;
    private JFrame frame;
    private ArrowListener listener;
    private Tetris tetris;

    /**
     * Constructs a new display for displaying the given board
     * @param board The grid to display for the tetris area.
     */
    public BlockDisplay(MyBoundedGrid<Block> board)
    {
        this.board = board;


        try
        {
            Clip clip = AudioSystem.getClip();
            // getAudioInputStream() also accepts a File or InputStream
            AudioInputStream ais = AudioSystem.
                    getAudioInputStream(new File("./BlocksLab/dogsong.wav"));
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (Throwable e)
        {
            System.err.println("Failed to load music");
            e.printStackTrace();
        }


        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    createAndShowGUI();
                }
            });

        //Wait until display has been drawn
        try
        {
            while (frame == null || !frame.isVisible())
                Thread.sleep(1);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Updates the state of the display
     * to show the game over screen.
     */
    public void gameOver()
    {
        state = State.GAME_OVER;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI()
    {
        //Create and set up the window.
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.addKeyListener(this);

        //Display the window.
        this.setPreferredSize(new Dimension(
                BLOCKSIZE * (board.getNumCols() + 10),
                BLOCKSIZE * (board.getNumRows() - 2)
            ));

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Repaints the screen with a new frame
     * @param g Graphics object to send graphics to
     */
    public void paintComponent(Graphics g)
    {

        if (state == State.GAME_OVER)
        {
            g.setColor(BACKGROUND);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);

            g.setFont(new Font(null, Font.BOLD, 14));
            g.drawString("Game Over. [ENTER] to Retry", getWidth() / 2 - 90, getHeight() / 2);
            g.drawString("Score: " + tetris.score, getWidth() / 2 - 90, getHeight() / 2 + 16);
            g.drawString("Lines Cleared: " + tetris.linesCleared, getWidth() / 2 - 90, getHeight() / 2 + 16 * 2);
            g.drawString("Level: " + tetris.level, getWidth() / 2 - 90, getHeight() / 2 + 16 * 3);

            return;
        }


        g.setColor(BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(BORDER);
        g.fillRect(0, 0, BLOCKSIZE * board.getNumCols() + OUTLINE, BLOCKSIZE * board.getNumRows());

        g.setColor(Color.WHITE);
        g.fillRect(BLOCKSIZE * board.getNumCols(), 0, BLOCKSIZE * 16, getHeight());

        g.setColor(Color.BLACK);
        g.drawString("Score: " + tetris.score, BLOCKSIZE * board.getNumCols() + 16, 24);
        g.drawString("Level: " + tetris.level, BLOCKSIZE * board.getNumCols() + 16, 24 * 2);
        g.drawString("Lines Cleared: " + tetris.linesCleared,
                BLOCKSIZE * board.getNumCols() + 16, 24 * 3);

        for (int i = 0; i < 7; i++)
        {
            g.drawString(types[i] + "-s seen: " + tetris.stats[i],
                    BLOCKSIZE * board.getNumCols() + 16, 24 * (4 + i));
        }

        g.drawString("NEXT", BLOCKSIZE * board.getNumCols() + 16, 24 * 11);

        g.setColor(Color.BLACK);
        int w = BLOCKSIZE * board.getNumCols() + 32;
        int h = 24 * 12;
        g.fillRect(w - BLOCKSIZE / 2, h - BLOCKSIZE / 2, BLOCKSIZE * 4, BLOCKSIZE * 5);
        for (int r = 0; r < 4; r++)
        {
            for (int c = 0; c < 4; c++)
            {
                for (int i = 0; i < 4; i++)
                {
                    if (Tetrad.LOCS[tetris.next][2 * i] == r &&
                            Tetrad.LOCS[tetris.next][2 * i + 1] == c)
                    {
                        g.setColor(Tetrad.COLS[tetris.next]);
                        g.fillRect( w + r * BLOCKSIZE + OUTLINE/2, h + c * BLOCKSIZE + OUTLINE/2,
                                BLOCKSIZE - OUTLINE, BLOCKSIZE - OUTLINE);
                    }
                }
            }
        }

        for (int row = 0; row < board.getNumRows() - 2; row++)
            for (int col = 0; col < board.getNumCols(); col++)
            {
                Location loc = new Location(row + 2, col);

                Block square = board.get(loc);

                if (square == null)
                    g.setColor(BACKGROUND);
                else
                    g.setColor(square.getColor());

                g.fillRect(col * BLOCKSIZE + OUTLINE/2, row * BLOCKSIZE + OUTLINE/2,
                        BLOCKSIZE - OUTLINE, BLOCKSIZE - OUTLINE);
            }

    }

    /**
     * Redraws the board to include the pieces and border colors.
     */
    public void showBlocks()
    {
        repaint();
    }

    /**
     * Sets the title of the window.
     * @param title New title for the window
     */
    public void setTitle(String title)
    {
        frame.setTitle(title);
    }

    /**
     * Key typed callback for KeyListener
     * @param e Event info
     */
    public void keyTyped(KeyEvent e)
    {
    }

    /**
     * Key released callback for KeyListener
     * @param e Event info
     */
    public void keyReleased(KeyEvent e)
    {
    }

    /**
     * Key typed pressed for KeyListener
     * @param e Event info
     */
    public void keyPressed(KeyEvent e)
    {
        if (listener == null)
            return;

        int code = e.getKeyCode();

        if (state == State.TETRIS)
        {
            if (code == KeyEvent.VK_LEFT)
                listener.leftPressed();
            else if (code == KeyEvent.VK_RIGHT)
                listener.rightPressed();
            else if (code == KeyEvent.VK_DOWN)
                listener.downPressed();
            else if (code == KeyEvent.VK_A)
                listener.upPressed();
            else if (code == KeyEvent.VK_R)
                listener.rPressed();
            else if (code == KeyEvent.VK_SPACE)
                listener.instaDrop();
        }
        else
        {
            if (code == KeyEvent.VK_ENTER)
            {
                tetris.reset();
                state = State.TETRIS;
            }
        }
    }

    /**
     * Sets the ArrowListener object to send the
     * keyPressed events to.
     * @param l New Listener object to call
     */
    public void setArrowListener(ArrowListener l)
    {
        this.listener = l;
    }

    /**
     * Sets the tetris game logic object to use to draw the score,
     * lines cleared, etc.
     * @param t Tetris object to display
     */
    public void setTetris(Tetris t)
    {
        tetris = t;
        setArrowListener(t);
    }
}
