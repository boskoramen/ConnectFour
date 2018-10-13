package game_framework;
/*
 *
 * @author isaiah.cruz
 */

public class Board {
	private final int XSIZE = 7;
	private final int YSIZE = 6;
	private final int SCALE = 50;
	
	private ConnectFourPiece[][] pieces = new ConnectFourPiece[XSIZE][YSIZE];
	
	private int lastX;
	private int lastY;
	
	public Board() {
		for(int k = 0; k < pieces.length; k++) {
			for(int j = 0; j < pieces[k].length; j++) {
				pieces[k][j] = null;
	        }
	    }
	}
	
	/*
	 * Adds a game piece for player of isRed (true = player 1; false = player 2) 
	 * and simulates dropping a piece in Connect Four at position @xPos. 
	 * Checks whether it is a valid position to drop a piece and, 
	 * if the position is valid, whether it led to a win or loss
	 * 
	 * @param	xPos	The position on the board (0 to 6) where you are dropping the Connect Four Game Piece
	 * @param	isRed	Indicates whether it is currently the player 1's turn 
	 * @return			2 if position is invalid, 1 if the person dropping the piece won, and 0 if the piece was dropped but the player dropping the piece did not win
	 */
	public boolean addPiece(int xPos, boolean isRed) {
		if(pieces[xPos][0] != null) {
			return false;
		}
	    else {
	        for(int j = YSIZE - 1; j >= 0; j--) {
	        	if(pieces[xPos][j] == null) {
	        		pieces[xPos][j] = new ConnectFourPiece(isRed);
	                lastX = xPos;
	                lastY = j;
	                j = -1;
	                }                
	            }
        }
	    return true;
	    }
	
	/*
	 * Adds a game piece for player of isRed (true = player 1; false = player 2)
	 * at a position (@xPos, @yPos) of the board.
	 * <p>
	 * Meant to be used for the cloning method
	 * 
	 * @param	xPos	The position on the x-axis of the board (0 to 6) where you are placing the Connect Four Game Piece
	 * @param	yPos	The position on the y-axis of the board (0 to 6) where you are placing the Connect Four Game Piece
	 * @param	isRed	The position on the board board (0 to 6) where you are placing the Connect Four Game Piece
	 */
	public boolean addPiece(int xPos, int yPos, boolean isRed) {
		if(pieces[xPos][yPos] != null) {
			return false;
		}
		pieces[xPos][yPos] = new ConnectFourPiece(isRed);
		return true;
	    }
	
	public void clearBoard()
	    {for(int k = 0; k < pieces.length; k++)
	        {for(int j = 0; j < pieces[k].length; j++)
	            {pieces[k][j] = null;
	            }
	        }
	    }
	
	public Board clone(boolean debug) {
		Board tempBoard = new Board();
		for(int k = 0; k < pieces.length; k++) {
			for(int j = 0; j < pieces[k].length; j++) {
		    	if(pieces[k][j] != null) {
					tempBoard.addPiece(k, j,pieces[k][j].isRed());
		    	}
		    }
		}
		return tempBoard;
	}
	
	public boolean fullBoardCheck() {
		for(int k = 0; k < pieces.length; k++) {
	    	for(int j = 0; j < pieces[k].length; j++) {
	        	if(pieces[k][j] == null) return false;
            }
        }
	    return true;
    }
	
	public void undo() {
		pieces[lastX][lastY] = null;
    }
	
	public boolean checkWinner(boolean redPlayer) {
		boolean hasWon = false;
	    int count = 0;
	    
	    //HORIZONTALS
	    for(int k = 0; k < XSIZE; k++) {
	    	int j = lastY;
	    	if(pieces[k][j] != null && pieces[k][j].isRed() == redPlayer) {
	        	count++;
            }
	        else {
	        	count = 0;
	        }
	        if(count == 4) {
	        	return true;
            }
        }
	    
	    //VERTICALS
	    if(!hasWon) {
	    	count = 0;
	        for(int j = 0; j < YSIZE; j++) {
	        	int k = lastX;
	            if(pieces[k][j] != null && pieces[k][j].isRed() == redPlayer) {
	            	count++;
                }
	            else {
	            	count = 0;
	            }
	            if(count == 4) {
	            	return true;
                }
            }
        }
	
	    //DIAGONALS 1
	    if(!hasWon) {
	    	count = 0;
	        int diagX = 0;
	        int diagY = 0;
	        boolean done = false;
	        
	        int k = lastX;
	        int j = lastY;
	        
	        while(!done) {
	        	diagX = k;
	            diagY = j;
	            if(diagY == 0 || diagX == 0 || diagX == XSIZE - 1) {
	            	done = true;
                }
	            k--;
	            j--;
            }
	        
	        done = false;
	        
	        k = diagX;
	        j = diagY;
	        while(!done) {
	        	if(pieces[k][j] != null && pieces[k][j].isRed() == redPlayer) {
	            	count++;
                }
	            else count = 0;
	            if(count == 4) {
	            	return true;
                }
	            
	            if(j == YSIZE - 1 || k == XSIZE - 1) {
	            	done = true;
                }
	            
	            k++;
	            j++;
            }
        }
	    
	    //DIAGONALS 2
	    if(!hasWon) {
	    	count = 0;
	        int diagX = 0;
	        int diagY = 0;
	        boolean done = false;
	        
	        int k = lastX;
	        int j = lastY;
	        
	        while(!done) {
	        	diagX = k;
	            diagY = j;
	            if(diagY == 0 || diagX == 0 || diagX == XSIZE - 1) {
	            	done = true;
                }
	            k++;
	            j--;
            }
	        
	        done = false;
	        
	        k = diagX;
	        j = diagY;
	        while(!done) {
	        	if(pieces[k][j] != null && pieces[k][j].isRed() == redPlayer) {
	        		count++;
                }
	            else {
	            	count = 0;
	            }
	            if(count == 4) {
	            	return true;
                }
	            
	            if(j == YSIZE - 1 || k == 0) {
	            	done = true;
                }
	            
	            k--;
	            j++;
            }
        }
	    
	    return false;
    }
	
	public int getLastX() {
		return lastX;
    }
	
	public int getLastY() {
		return lastY;
    }
	
	public ConnectFourPiece[][] getPieces() {
		return pieces;
    }
	
	public ConnectFourPiece getPiece(int x, int y) {
		return pieces[x][y];
    }
	
	public int getSize() {
		return YSIZE * XSIZE;
    }
	
	public int getXSIZE() {
		return XSIZE;
    }
	
	public int getYSIZE() {
		return YSIZE;
    }
	
	public int getWidth() {
		return XSIZE * SCALE;
    }
	
	public int getHeight() {
		return YSIZE * SCALE;
    }
}
