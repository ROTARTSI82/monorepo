import java.awt.*;
import java.util.*;

/**
 * Represents a rectangular game board, containing Piece objects.
 * @author Grant Yang
 * @author Unknown
 * @version 2022.04.03
 */
public class Board extends BoundedGrid<Piece>
{
    private boolean isWhiteTurn;

    /**
     * Constructs a new Board with the given dimensions
     */
    public Board()
    {
        super(8, 8);
    }

    /**
     * Sets whose turn it is
     * @param turn True if it's whites turn, false for black
     */
    public void setIsWhiteTurn(boolean turn)
    {
        isWhiteTurn = turn;
    }

    /**
     * Make a move on the board
     * @param move Which piece to move and where to move it to
     */
    public void executeMove(Move move)
    {
        if (move instanceof Promotion)
        {
            Promotion promotion = (Promotion) move;
            try
            {
                promotion.getType().getConstructor(new Class[]{Color.class})
                        .newInstance(move.getPiece().getColor())
                        .putSelfInGrid(this, move.getDestination());

                move.getPiece().removeSelfFromGrid();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            move.getPiece().moveTo(move.getDestination());

            if (move instanceof Castling)
            {
                Castling castling = (Castling) move;
                executeMove(castling.getRookMove());
            }
        }
    }

    /**
     * Unmake a move on the board
     * Precondition:  move has already been made on the board
     * Postcondition: piece has moved back to its source,
     *                and any captured piece is returned to its location
     * @param move Move that was just made with executeMove()
     */
    public void undoMove(Move move)
    {
        Piece piece = move.getPiece();
        Location source = move.getSource();
        Location dest = move.getDestination();
        Piece victim = move.getVictim();

        if (move instanceof Promotion)
        {
            piece.putSelfInGrid(this, source);
            remove(dest);

            if (victim != null)
                victim.putSelfInGrid(piece.getBoard(), dest);
        }
        else
        {
            piece.moveTo(source);

            if (victim != null)
                victim.putSelfInGrid(piece.getBoard(), dest);

            if (move instanceof Castling)
            {
                Castling castling = (Castling) move;
                undoMove(castling.getRookMove());
            }
        }
    }

    /**
     * Get a list of all possible moves
     * @param color The side to move. Either Color.WHITE or Color.BLACK
     * @return A list of all pseudo-legal moves
     */
    public ArrayList<Move> allMoves(Color color)
    {
        ArrayList<Move> ret = new ArrayList<Move>();
        for (int r = 0; r < getNumRows(); r++)
        {
            for (int c = 0; c < getNumCols(); c++)
            {
                Piece p = get(new Location(r, c));
                if (p != null && p.getColor().equals(color))
                    for (Location d : p.destinations())
                    {
                        if (p instanceof Pawn && (d.getRow() == 0 || d.getRow() == 7))
                            ret.add(new Promotion(p, d, Queen.class));
                        else
                            ret.add(new Move(p, d));
                    }

                if (p instanceof King)
                {
                    int ksdir = Location.EAST;
                    int qsdir = Location.WEST;

                    Location dookKs = p.getLocation().getAdjacentLocation(ksdir);
                    Location dookQs = p.getLocation().getAdjacentLocation(qsdir);

                    Location kingKs = dookKs.getAdjacentLocation(ksdir);
                    Location kingQs = dookQs.getAdjacentLocation(qsdir);

                    ArrayList<Location> ks = new ArrayList<Location>();
                    ArrayList<Location> qs = new ArrayList<Location>();
                    p.sweep(ks, ksdir);
                    p.sweep(qs, qsdir);

                    Location nookQs = kingQs.getAdjacentLocation(qsdir)
                                             .getAdjacentLocation(qsdir);
                    Location nookKs = kingKs.getAdjacentLocation(ksdir);

                    if (qs.size() == 3 && isValid(nookQs) && get(nookQs) instanceof Rook)
                    {
                        Rook rook = (Rook) get(nookQs);
                        if (rook.getColor() == p.getColor())
                            ret.add(new Castling(p, kingQs, new Move(get(nookQs), dookQs)));
                    }
                    if (ks.size() == 2 && isValid(nookKs) && get(nookKs) instanceof Rook)
                    {
                        Rook rook = (Rook) get(nookKs);
                        if (rook.getColor() == p.getColor())
                            ret.add(new Castling(p, kingKs, new Move(get(nookKs), dookKs)));
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Load a partial fen onto the board
     * @param fen The first part of a forsyth-edwards notation string,
     *            omitting side to move, castling rights, en passant targets,
     *            halfmove and fullmove counters, etc.
     */
    public void loadFen(String fen)
    {
        int cur = 63;
        for (char c : fen.toCharArray())
        {
            if ('1' <= c && c <= '9')
            {
                cur -= c - '0';
                continue;
            }

            Color col;
            if (c >= 'a')
            {
                c -= 32;
                col = Color.WHITE;
            }
            else col = Color.BLACK;

            Location l = new Location(cur / 8, cur % 8);
            if (c == 'K')
                new King(col).putSelfInGrid(this, l);
            else if (c == 'Q')
                new Queen(col).putSelfInGrid(this, l);
            else if (c == 'N')
                new Knight(col).putSelfInGrid(this, l);
            else if (c == 'B')
                new Bishop(col).putSelfInGrid(this, l);
            else if (c == 'R')
                new Rook(col).putSelfInGrid(this, l);
            else if (c == 'P')
                new Pawn(col).putSelfInGrid(this, l);
            else
                continue;

            cur--;
        }
    }

    /**
     * Construct a partial fen from the board state
     * @return A forsyth-edwards notation string containing only
     *         board state and side to move, omitting en passant targets,
     *         castling rights, halfmove/fullmove counters, etc.
     */
    public String getFen()
    {
        int inRow = 0;
        StringBuilder b = new StringBuilder();
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Location l = new Location(r, c);
                if (get(l) != null)
                {
                    if (inRow > 0)
                    {
                        b.append(inRow);
                        inRow = 0;
                    }
                    Piece p = get(l);

                    char offset;
                    if (p.getColor().equals(Color.WHITE))
                        offset = '\00';
                    else offset = '\40';

                    if (p instanceof Rook)
                        b.append((char) ('R' + offset));
                    else if (p instanceof King)
                        b.append((char) ('K' + offset));
                    else if (p instanceof Queen)
                        b.append((char) ('Q' + offset));
                    else if (p instanceof Bishop)
                        b.append((char) ('B' + offset));
                    else if (p instanceof Pawn)
                        b.append((char) ('P' + offset));
                    else if (p instanceof Knight)
                        b.append((char) ('N' + offset));
                }
                else
                {
                    inRow++;
                }
            }
            if (inRow > 0)
            {
                b.append(inRow);
                inRow = 0;
            }

            if (r < 7)
                b.append("/");
        }

        b.append(' ');

        if (isWhiteTurn)
            b.append('w');
        else b.append('b');

        return b.toString();
    }
}