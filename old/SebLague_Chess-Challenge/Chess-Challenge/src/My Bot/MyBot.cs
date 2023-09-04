using System;
using System.Numerics;
using ChessChallenge.API;

public class MyBot : IChessBot
{
    int score(Board b, int alpha, int beta, int d)
    {
        if (b.IsInCheckmate()) return -4096;
        if (b.IsDraw()) return 0;

        Move[] moves = b.GetLegalMoves();
        if (d <= 0 || moves.Length == 0)
        {
            var count = new Func<PieceType, int>((type) =>
                BitOperations.PopCount(b.GetPieceBitboard(type, b.IsWhiteToMove)) -
                BitOperations.PopCount(b.GetPieceBitboard(type, b.IsWhiteToMove ^ true)));
            
            return moves.Length + 10 * count(PieceType.Pawn) + 30 * (count(PieceType.Bishop) + count(PieceType.Knight)) + 90 * count(PieceType.Queen) + 50 * count(PieceType.Rook);
        }

        foreach (Move m in moves)
        {
            b.MakeMove(m);
            alpha = Math.Max(alpha, -score(b, -beta, -alpha, d - 1));
            b.UndoMove(m);
            if (alpha >= beta)
                return alpha;
        }

        return alpha;
    }
    
    public Move Think(Board board, Timer timer)
    {
        Move[] moves = board.GetLegalMoves();

        int best = -4096;
        Move sel = moves[0];
        foreach (Move m in moves)
        {
            board.MakeMove(m);
            int s = -score(board, -4096, -best, 2);
            if (s > best)
            {
                sel = m;
                best = s;
            }
            
            board.UndoMove(m);
        }
        
        return sel;
    }
}