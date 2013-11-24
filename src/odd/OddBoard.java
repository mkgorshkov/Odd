package odd;

import boardgame.Board;
import boardgame.BoardPanel;
import boardgame.Move;
import java.util.LinkedList;


/**
 * Odd Game board representation. The board is represented as a
 * two-dimensional array with a coord in the i-th row ( where a row is parallel to the x-axis) 
 * and j-th column ( where a column is at 120 degrees angle from the x-axis) 
 * Index [0,0] is the center of the board 
 * @author gcoman (based on code written by rwest)
 */
public class OddBoard extends Board {

    /**Minimal cluster size */
	public static final int MIN_CLUSTER_SIZE = 5;
	
	/** Possible states of a board cell */
	public enum Piece {WP,BP,EMPTY,INVALID, WP_CLUST, BP_CLUST};

	/** Board size */
	public static final int SIZE = 4;
	private static final int SIZE_DATA = SIZE*2+1;
	
	/** The board is indexed matrix: first row, then col): */
    private final Piece data[][] = new Piece[SIZE_DATA][SIZE_DATA];
    
	
    /** Players are denoted by the ints 1 and 2 (1 starts the game): */
    // number of empty positions (one has the ball):
    private int numEmpty = SIZE_DATA * SIZE_DATA - SIZE * (SIZE + 1);
    private int moveCount = 0;
    private int turn = 1;
    private int winner = NOBODY;
    private int num_clusters = 0; // number of large clusters
    
    
    /** Returns a board in the starting position. */
    public OddBoard() {
    	for (int row = 0; row < SIZE_DATA; row++)
    		for (int col = 0; col < SIZE_DATA; col++)
    			if( row - col < -SIZE  || row - col > SIZE) 
    				data[row][col] = Piece.INVALID;
    			else data[row][col] = Piece.EMPTY;
    }
    
    /** Returns a deep copy of another board. */
    public OddBoard(OddBoard pb) {
        for (int i = 0; i < SIZE_DATA; i++)
            System.arraycopy(pb.data[i], 0, this.data[i], 0, SIZE_DATA);
        this.turn = pb.turn;
        this.winner = pb.winner;
        this.numEmpty = pb.numEmpty;
    }

    /** Get the array containing the board data directly */
    public Piece[][] getBoardData() {
        return data;
    }

    /** Get the value at a position: return PLAYER1 / PLAYER 2 / INVALID / EMPTY.
     * This uses 0-based coordinates. */
    public Piece getPieceAt(int row, int col) {
    	
    	if(row < -SIZE || row > SIZE || col < -SIZE || col > SIZE ) 
    		return Piece.INVALID;
        return data[row + SIZE][col + SIZE];
    }

    /** Return winner number (1 or 2) or NOBODY if no winner yet. */
    public int getWinner() {
        return winner;
    }

    /** Set a winner without finishing the game. */
    public void forceWinner(int win) {
        if (win != 1 && win != 2) {
            throw new IllegalArgumentException(
                    "Illegal argument: Winner must be 1 or 2.");
        }
        this.winner = (win == 1) ? 1 : 2;
    }

    /** Return the next player. */
    public int getTurn() {
    	return turn;
    }
    
    /** Get the number of empty board positions. */
    public int countEmptyPositions() {
        return numEmpty;
    }

    /** Verify legality of a move. This depends on the current turn. */
    public boolean isLegal(Move mm) {
    	OddMove m = (OddMove) mm;
        return getPieceAt(m.destRow, m.destCol) == Piece.EMPTY;
    }

    /** Execute a move. */
    public void move(Move mm) throws IllegalArgumentException {
    	OddMove m = (OddMove) mm;
    	if (!isLegal(m))
            throw new IllegalArgumentException("Illegal move: " + mm.toTransportable());
    	
        data[m.destRow + SIZE][m.destCol + SIZE] = 
        	(m.color == Piece.WP) ? Piece.WP : Piece.BP;
        
        if(--numEmpty == 0) determineWinner();
        else turn = (turn == 1) ? 2 : 1;        
    }

    /** Get the number of turns played. */
    public int getTurnsPlayed() {
        return moveCount;
    }
    
    
    public void determineWinner() {
    	// build the graph of connected components: array that holds the parent. 
    	// If then node x is root, the value will be negative |parent[x]| = size of component of x    	
    	int[] parent = new int[SIZE_DATA * SIZE_DATA];
    	for (int k = 0; k < parent.length; k++) parent[k] = -1;
    	
    	int crt_index = 0;
    	for (int j = -SIZE; j <= SIZE; j++)
			for (int i = -SIZE; i <= SIZE; i++, crt_index++) {
				Piece crt_piece = getPieceAt(i, j);
				if(crt_piece == Piece.INVALID || crt_piece == Piece.EMPTY) continue;
				
				int connection_code = 0; // used to know which neighbours are connected		
				if(getPieceAt(i - 1, j - 1) == crt_piece) connection_code++;
				if(getPieceAt(i, j - 1) == crt_piece) connection_code += 2;
				if(getPieceAt(i - 1, j) == crt_piece) connection_code += 4;
				
				switch (connection_code) {
				case 1: 
				case 2:
				case 3: // one connection, have to look for root
					//find root
					int y = crt_index - SIZE_DATA - ((connection_code == 1) ? 1 : 0);
					do{
						int tmp = y;
						y = parent[y];
						parent[tmp] = crt_index; 
					}while (y >= 0);
					parent[crt_index] += y;
					break;
				case 6: // two connections
					//find root
					y = crt_index - SIZE_DATA; int z=y;
					do{
						z = y;
						y = parent[y];
						parent[z] = crt_index; 
					}while (y >= 0);					
					// if root not the same, then we have to connect the second piece
					parent[crt_index] += y;
					if( z == crt_index - 1) break;
				default:  //one connection to its closest neighbour
					parent[crt_index] += parent[crt_index-1];
					parent[crt_index-1] = crt_index;
					break;					
				case 0: // no neighbours
					break;
				}
			}
    	num_clusters = 0;
		for (int k = 0; k < parent.length; k++) {
			if(parent[k] <= -MIN_CLUSTER_SIZE) num_clusters++;
			int y = k;
			while (y >= 0) y = parent[y];
			if(y <= -MIN_CLUSTER_SIZE) 
				data[k % SIZE_DATA][k / SIZE_DATA] = 
					( data[k % SIZE_DATA][k / SIZE_DATA] == Piece.WP ) ?
							Piece.WP_CLUST : Piece.BP_CLUST;
		}
    	winner = (num_clusters % 2 == 1) ? 1 : 2;
    }

    /** Return a copy of this board */
    public Object clone() {
        return new OddBoard(this);
    }

    /** String representation of a board */
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        if(winner == NOBODY) {
        	b.append("There are " + numEmpty + " empty spaces left. " + getNameForID(turn) + " to play.\n");
        }else{
        	b.append("The winner is " + (getNameForID(winner)) + "! Number of pieces is " + num_clusters +".\n");
        }
        for (int j = SIZE; j >= -SIZE; j--) {
        	for (int i = 0; i < -j; i++) b.append(" ");
        	for (int i = -SIZE; i <= SIZE; i++) {
        		switch (getPieceAt(i, j)) {
				case EMPTY:
					b.append("+ ");
					break;
				case WP:
					b.append("# ");
					break;
				case BP:
					b.append("- ");
					break;
				case WP_CLUST:
					b.append("@ ");
					break;
				case BP_CLUST:
					b.append("= ");
					break;
				default:
					b.append(" ");
				}
        	}
        	b.append("\n");
		}
        return b.toString();	        
    }

    public int getNumPieces() {
    	return num_clusters;
    }
    
    
    public String getNameForID(Piece p) {
        switch (p) {
            case WP:
                return "Player1";
            case BP:
                return "Player2";
            default:
                throw new IllegalArgumentException(
                        "Valid player IDs are 1 and 2. You passed " + p + ".");
        }

    }
    
    
    public String getNameForID(int p) {
        switch (p) {
        case 1:
            return "Player1";
        case 2:
            return "Player2";
        default:
            throw new IllegalArgumentException(
                    "Valid player IDs are 1 and 2. You passed " + p + ".");
        }        
    }

    public int getIDForName(String s) {
        if (s.equals("Player1")) {
            return 1;
        }
        if (s.equals("Player2")) {
            return 2;
        }
        throw new IllegalArgumentException(
                "Valid player names are Player1 and Player2. You passed '" + s + "'.");
    }

    public int getNumberOfPlayers() {
        return 2;
    }

    public Move parseMove(String str)
            throws NumberFormatException, IllegalArgumentException {
        return new OddMove(str);
    }

    /** Return a custom BoardPanel so that we can accept user input */
    @Override
    public BoardPanel createBoardPanel() {
        return new OddBoardPanel();
    }
    
    public LinkedList<OddMove> getValidMoves() {
    	LinkedList<OddMove> moves = new LinkedList<OddMove>(); 
		for (int i = -SIZE; i <= SIZE; i++) {
    		for (int j = -SIZE; j <= SIZE; j++) {
				if (getPieceAt(i, j) == Piece.EMPTY) {
					moves.add(new OddMove(getTurn(), Piece.WP, i, j));
					moves.add(new OddMove(getTurn(), Piece.BP, i, j));
				}
			}
		}
    	return moves;
    }
    

} // End class Board