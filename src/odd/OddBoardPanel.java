package odd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


import odd.OddBoard.Piece;
import boardgame.BoardPanel;

/**
 * A board panel for display and input for the Odd game.
 * @author gcoman (based on code written by rwest)
 */
public class OddBoardPanel extends BoardPanel
        implements MouseListener, MouseMotionListener, ComponentListener {

	private static final long serialVersionUID = 1L;
	
	public static final int SCALE = 2;
	public static final int SRAD = 10; //sin 30 degrees * radius
 	public static final int CRAD = 17; //cos 30 descrees * radius
	
	// Board location of piece playing 
    BoardPanelListener list = null; // Who needs a move input ?

    public OddBoardPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addComponentListener(this);
    }

    protected void requestMove(BoardPanelListener l) {
        list = l;
    }

    protected void cancelMoveRequest() {
        list = null;
    }

    public void mouseClicked(MouseEvent arg0) {
    	OddBoard.Piece moveType = OddBoard.Piece.WP; 
        switch (arg0.getButton()) {
            // BUTTON1 is left, BUTTON3 is right:
            case MouseEvent.BUTTON1:
                break;
            case MouseEvent.BUTTON3:
                moveType = OddBoard.Piece.BP;
                break;
            default:
                return;
        }
    	
    	OddBoard board = (OddBoard) getCurrentBoard();
        Point p = getCross(arg0.getX(), arg0.getY());
        OddMove mm = new OddMove(board.getTurn(), moveType, p.x, p.y);
        if (list != null && board.isLegal(mm)) {
            list.moveEntered(mm);
            cancelMoveRequest();
            // Request dirty area to be redrawn
            repaint();
        }
    }

    /** Paint the board to the offscreen buffer. This does the painting
     * of the actual board, but not the pieces being moved by the user.*/
    public void drawBoard(Graphics g) {
    	//Draw background 
    	Rectangle clip = g.getClipBounds();
        g.setColor(new Color(222, 184, 135));
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
                
        Color line_color = Color.BLACK;
        //Draw board
        OddBoard bd = (OddBoard) getCurrentBoard();
        for (int i = -OddBoard.SIZE; i <= OddBoard.SIZE; i++)
        	for (int j = -OddBoard.SIZE; j <= OddBoard.SIZE; j++) {
        		if(bd.getPieceAt(i, j) == Piece.INVALID) continue;
        		int[] xs = hex_x(i,j),
       			      ys = hex_y(j);
        		
				switch(bd.getPieceAt(i, j)) {    				
				case WP:
					g.setColor(Color.WHITE);    					
					break;
				case BP:
					g.setColor(Color.DARK_GRAY);    				
					break;
				case WP_CLUST:
					g.setColor(Color.YELLOW);    				
					break;
				case BP_CLUST:
					g.setColor(Color.BLUE);    				
					break;
				default:
					break;
				}
				//draw inside cell
				if(bd.getPieceAt(i, j) != OddBoard.Piece.EMPTY) g.fillPolygon(xs, ys, 6);
    			g.setColor(line_color); 
				g.drawPolygon(xs,ys, 6); //draw border	
    		}
        //draw coordinate lines
		g.setColor(line_color);
		g.drawLine(- SCALE * CRAD, SCALE * SRAD * (OddBoard.SIZE * 3 + 2), 
				SCALE * CRAD * ( 4 * OddBoard.SIZE + 3), SCALE * SRAD * (OddBoard.SIZE * 3 + 2));
		g.drawLine(SCALE * CRAD * (OddBoard.SIZE), - SCALE * SRAD, 
					SCALE * CRAD * (OddBoard.SIZE * 3 + 2), SCALE * SRAD * ( 6 * OddBoard.SIZE + 5));
    }
    
    /**
     * Returns the x-coords of the hexagon corresponding to the cell (i,j)
     */
    private int[] hex_x(int i,int j) {
    	int[] toRet = new int[6];
		toRet[0] = CRAD * 2 * (i + OddBoard.SIZE) - CRAD * j  ;
		toRet[1] = toRet[0] + CRAD;
		toRet[2] = toRet[1] + CRAD;
		toRet[3] = toRet[2];
		toRet[4] = toRet[1];
		toRet[5] = toRet[0];
		for (int k = 0; k < toRet.length; k++) {
			toRet[k] *= SCALE;			
		}
		return toRet;
    }
    
    /**
     * Returns the y-coords of the hexagon corresponding to the cell (i,j)
     */
    private int[] hex_y(int j) {
    	int[] toRet = new int[6];
		toRet[0] = SRAD + (OddBoard.SIZE - j) * SRAD * 3;
		toRet[1] = toRet[0] - SRAD;
		toRet[2] = toRet[0];
		toRet[3] = toRet[2] + SRAD * 2;
		toRet[4] = toRet[3] + SRAD;
		toRet[5] = toRet[3];
		for (int k = 0; k < toRet.length; k++) {
			toRet[k] *= SCALE;			
		}
		return toRet;
    }
    
    /** We use the double-buffering provided by the superclass, but draw
     *  the "transient" elements in the paint() method. */
    public void paint(Graphics g) {
        // Paint the board as usual, this will used the offscreen buffer
        super.paint(g);
    }

    public void componentResized(ComponentEvent arg0) {
    }

    /**
     * Return the cell corresponding to the clicked region
     */
    private Point getCross(int px, int py) {
    	// get y
    	int y = py / (SRAD * 3 * SCALE); 
    	y = OddBoard.SIZE - y;
    	
    	// get x
    	int px_adjust = px + CRAD * y * SCALE;
		int x = px_adjust / (CRAD * 2 * SCALE);
		x = x - OddBoard.SIZE;
		
    	if(py % (SRAD * 3 * SCALE) < SRAD * SCALE && y < OddBoard.SIZE) {
    		// upper border
    		// get center of current cell (x_1,y_1)
    		double x_1 = (x + OddBoard.SIZE) * CRAD * 2 + CRAD;
    		x_1 =  x_1 - CRAD * y; //adjust
    		double y_1 = (OddBoard.SIZE - y) * SRAD * 3 + SRAD * 2;
    		
    		// compare to the input point
    		double dx = px - x_1 * SCALE;
    		double dy = py - y_1 * SCALE;  		
    		if( dx * dx + dy * dy > 
    				(CRAD - dx) * (CRAD - dx) + (3 * SRAD - dy) * (3 * SRAD - dy) ) {
    			if( px_adjust % (CRAD * 2 * SCALE) > CRAD * SCALE) x++; //move to the right
    			y++; // move up    			
    		}
    	}
    	return new Point(x,y);
    }
  
    /* Don't use these interface methods */
    public void mouseReleased(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mouseMoved(MouseEvent arg0) {
    }

    public void componentMoved(ComponentEvent arg0) {
    }

    public void componentShown(ComponentEvent arg0) {
    }

    public void componentHidden(ComponentEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseDragged(MouseEvent arg0) {
    }
}
