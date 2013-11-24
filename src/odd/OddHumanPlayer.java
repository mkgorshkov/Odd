package odd;

import boardgame.*;

/** Human Odd game client with GUI.
 *
 *  To run, set a large timeout on the server so the 
 *  human player has time to enter a move.
 */
public class OddHumanPlayer extends HumanPlayer {
    public OddHumanPlayer() { super( new OddBoard() ); } 
}

