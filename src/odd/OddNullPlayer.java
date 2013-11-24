package odd;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import java.util.LinkedList;

/**
 * This is a dummy dummy dummy player who does nothing but always perform 
 * the move with the "smallest" (x,y) coordinate
 * @author gcoman (based on code written by rwest)
 */
public class OddNullPlayer extends Player {

    public OddNullPlayer() {
        super("NullPlayer");
    }

    public Move chooseMove(Board board) {
        OddBoard pb = (OddBoard) board;
        LinkedList<OddMove> validMoves = pb.getValidMoves();
        //if (validMoves.isEmpty()) return null; 
        //else 
        return validMoves.getFirst(); 
    }

    public Board createBoard() {
        return new OddBoard();
    }
}
