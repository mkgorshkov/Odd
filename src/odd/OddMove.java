package odd;

import boardgame.Move;

/**
 * Represent a move in the Odd game. This class does
 * no error checking with respect to the validity of moves.
 * @author gcoman (based on code written by rwest)
 */
public class OddMove extends Move {
    
    protected int player;
    protected OddBoard.Piece color;
    protected int destCol;
    protected int destRow;

    /**
     * player:  1 or 2
     * destRow: row number of the position to which you want to move the piece
     * destCol: column number of that position
     */
    public OddMove(int player, OddBoard.Piece color, int destRow, int destCol)
            throws IllegalArgumentException {
        this.player = player;
        this.color = color;
        this.destCol = destCol;
        this.destRow = destRow;
    }

    public OddMove(String str)
            throws NumberFormatException, IllegalArgumentException {
        fromString(str);
    }

    public int getPlayerID() {
        return player;
    }

    public static String toPrettyString(int player, OddBoard.Piece color, int destRow, int destCol) {
        StringBuffer s = new StringBuffer("Player" + player + ": " + 
        		((color == OddBoard.Piece.WP) ? "WHITE" : "BLACK") + " on ");
        s.append(destRow + " " + destCol);        
        return s.toString();
    }

    public String toPrettyString() {
        return toPrettyString(player, color, destRow, destCol);
    }

    public String toTransportable() {
    	return player + " " + ((color == OddBoard.Piece.WP) ? "WHITE" : "BLACK") + 
    			" " + destRow + " " + destCol;
    }

    public void fromString(String str)
            throws IllegalArgumentException{
        String[] tokens = str.split(" ");
        if (tokens.length < 4) {
            throw new IllegalArgumentException("Input string is not a legal move encoding: must be \"player destRow destCol\".\n");
        }
        player = Integer.parseInt(tokens[0]);
        color = tokens[1].equals("WHITE") ? OddBoard.Piece.WP : OddBoard.Piece.BP;
        destRow = Integer.parseInt(tokens[2]);
        destCol = Integer.parseInt(tokens[3]);
    }
}
