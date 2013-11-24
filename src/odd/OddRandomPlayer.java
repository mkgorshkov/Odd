package odd;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import java.util.LinkedList;
import java.util.Random;

/**
 * This is a dummy dummy dummy player who does nothing but always perform 
 * the move with the "smallest" (x,y) coordinate
 * @author gcoman (based on code written by rwest)
 */
public class OddRandomPlayer extends Player {

	Random R = new Random();
	
    public OddRandomPlayer() {
        super("RandomPlayer");
    }

    public OddRandomPlayer(Random r) {
        super("RandomPlayer");
        R=r;
    }
    public Move chooseMove(Board board) {
        OddBoard pb = (OddBoard) board;
        LinkedList<OddMove> validMoves = pb.getValidMoves();
        //if (validMoves.isEmpty()) return null; 
        //else 
        return validMoves.get(R.nextInt(validMoves.size())); 
    }

    public Board createBoard() {
        return new OddBoard();
    }
}
