/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author isaiah.cruz
 */
public class Board {
private final int XSIZE = 7;
private final int YSIZE = 6;
private final int SCALE = 50;

private GamePiece[][] pieces = new GamePiece[XSIZE][YSIZE];

private int lastX;
private int lastY;

private final boolean DEBUG = false;

public Board()
    {for(int k = 0; k < pieces.length; k++)
        {for(int j = 0; j < pieces[k].length; j++)
            {pieces[k][j] = null;
            }
        }
    }

public int addPiece(int xPos, boolean isRed)
    {if(pieces[xPos][0] != null) return 2;
    else
        {if(DEBUG) if(DEBUG) System.out.println("PIECE ADDED");
        if(DEBUG) if(DEBUG) System.out.println("\nisRed: " + isRed + "\n");
        for(int j = YSIZE - 1; j >= 0; j--)
            {if(pieces[xPos][j] == null)
                {pieces[xPos][j] = new GamePiece(isRed);
                lastX = xPos;
                lastY = j;
                j = -1;
                }                
            }
        }
    if(checkWinner(isRed))
        return 1;
    return 0;
    }

public void clearBoard()
    {for(int k = 0; k < pieces.length; k++)
        {for(int j = 0; j < pieces[k].length; j++)
            {pieces[k][j] = null;
            }
        }
    }

public int getLastX()
    {return lastX;
    }

public int getLastY()
    {return lastY;
    }

public GamePiece[][] getPieces()
    {return pieces;
    }

public GamePiece getPiece(int x, int y)
    {return pieces[x][y];
    }

public int getSize()
    {return YSIZE * XSIZE;
    }

public int getXSIZE()
    {return XSIZE;
    }

public int getYSIZE()
    {return YSIZE;
    }

public int getWidth()
    {return XSIZE * SCALE;
    }

public int getHeight()
    {return YSIZE * SCALE;
    }

public boolean fullBoardCheck()
    {for(int k = 0; k < pieces.length; k++)
        {for(int j = 0; j < pieces[k].length; j++)
            {if(pieces[k][j] == null) return false;
            }
        }
    return true;
    }

public boolean checkWinner(boolean redPlayer)
    {boolean hasWon = false;
    int count = 0;
    
    //VERTICALS
    for(int k = 0; k < XSIZE; k++)
        {int j = lastY;
        if(pieces[k][j] != null && pieces[k][j].isRed() == redPlayer)
            {count++;
            }
        else count = 0;
        if(count == 4) 
            {j = YSIZE;
            k = XSIZE;
            return true;
            }
        }
    
    //HORIZONTALS
    if(!hasWon)
        {count = 0;
        for(int j = 0; j < YSIZE; j++)
            {int k = lastX;
            if(pieces[k][j] != null && pieces[k][j].isRed() == redPlayer)
                {count++;
                }
            else count = 0;
            if(count == 4) 
                {j = YSIZE;
                k = XSIZE;
                return true;
                }
            }
        }

    //DIAGONALS 1
    if(!hasWon)
        {if(DEBUG) System.out.println("CHECKING DIAGONALS");  
        count = 0;
        int diagX = 0;
        int diagY = 0;
        if(DEBUG) System.out.println("\nlastX: "+lastX+"  lastY: "+lastY);
        boolean done = false;
        
        int k = lastX;
        int j = lastY;
        
        while(!done)
            {if(DEBUG) System.out.println("\nk: "+k+"  diagX: "+diagX);
            diagX = k;
            diagY = j;
            if(diagY == 0 || diagX == 0 || diagX == XSIZE - 1)
                {if(DEBUG) System.out.println("DONE");
                done = true;
                }
            k--;
            j--;
            }
        
        done = false;
        
        if(DEBUG) System.out.println("\ndiagX: "+diagX+"  diagY: "+diagY);
        k = diagX;
        j = diagY;
        while(!done)
            {if(pieces[k][j] != null && pieces[k][j].isRed() == redPlayer)
                {count++;
                }
            else count = 0;
            if(count == 4) 
                {j = YSIZE;
                k = XSIZE;
                return true;
                }
            
            if(j == YSIZE - 1 || k == XSIZE - 1)
                {if(DEBUG) System.out.println("DONE");
                done = true;
                }
            
            k++;
            j++;
            }
        }
    
    //DIAGONALS 2
    if(!hasWon)
        {if(DEBUG) System.out.println("CHECKING DIAGONALS");  
        count = 0;
        int diagX = 0;
        int diagY = 0;
        if(DEBUG) System.out.println("\nlastX: "+lastX+"  lastY: "+lastY);
        boolean done = false;
        
        int k = lastX;
        int j = lastY;
        
        while(!done)
            {diagX = k;
            if(DEBUG) System.out.println("\nk: "+k+"  diagX: "+diagX);
            diagY = j;
            if(diagY == 0 || diagX == 0 || diagX == XSIZE - 1)
                {if(DEBUG) System.out.println("DONE");
                done = true;
                }
            k++;
            j--;
            }
        
        done = false;
        
        if(DEBUG) System.out.println("\ndiagX: "+diagX+"  diagY: "+diagY);
        k = diagX;
        j = diagY;
        while(!done)
            {if(pieces[k][j] != null && pieces[k][j].isRed() == redPlayer)
                {count++;
                }
            else count = 0;
            if(count == 4) 
                {return true;
                }
            
            if(j == YSIZE - 1 || k == 0)
                {if(DEBUG) System.out.println("DONE");
                done = true;
                }
            
            k--;
            j++;
            }
        }
    
    return false;
    }
}
