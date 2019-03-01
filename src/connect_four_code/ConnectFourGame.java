/*
 * @author isaiah.cruz
 */

package connect_four_code;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

import game_framework.*;

public class ConnectFourGame extends Thread implements ActionListener{

    //  Private instance variables:
    private ArrayList<Integer> moves;
    private Board board;
    private ConnectFourGUI GUI;

    private final boolean DEBUG = false;
    private String recordingFilename = ""; // To be used in the future for recording (and possibly replaying) steps in a game previously played
    private boolean isFirstPlayerTurn;
    private boolean finished;
    final boolean testPhase;
    private boolean stop = false;
    private static Tree<Move> memory;
    private final ArrayList<Player> humanPlayers;
    private final ArrayList<ConnectFourMachine> AIPlayers;
    private final ArrayList<Player> curPlayerPool;
    private int curPlayerPos;
    private final Timer AITimer;


    public ConnectFourGame(boolean isTest, Tree<Move> a_memory) {
        testPhase = isTest;
        memory = a_memory;
        board = new Board();
        moves = new ArrayList<Integer>();
        humanPlayers = new ArrayList<Player>();
        AIPlayers = new ArrayList<ConnectFourMachine>();
        curPlayerPool = new ArrayList<Player>();
	AITimer = new Timer(0, this);
        Player firstPlayer = new Player();
        Player secondPlayer = new Player();
        firstPlayer.setIdentifier("name", "red");
        secondPlayer.setIdentifier("name", "black");
        firstPlayer.setIdentifier("color", Color.red);
        secondPlayer.setIdentifier("color", Color.black);
        humanPlayers.add(firstPlayer);
        humanPlayers.add(secondPlayer);
	GUI = new ConnectFourGUI(board, this);
        ConnectFourMachine alphaPlayer = new ConnectFourMachine(board, isFirstPlayerTurn);
	ConnectFourMachine betaPlayer = new ConnectFourMachine(board, !isFirstPlayerTurn);
        if(testPhase) {
            curPlayerPool.add(alphaPlayer);
            curPlayerPool.add(betaPlayer);
        } else {
            curPlayerPool.add(firstPlayer);
            curPlayerPool.add(secondPlayer);
        }
        curPlayerPos = 0;
	startGame();
	GUI.repaint();
    }
	
    public Board getBoard() {
	return board;
    }
	
    public Player currentPlayer() {
	return curPlayerPool.get(0);
    }
	
    public Tree<Move> getMemory() {
       	return memory;
    }
	
    public boolean isFinished() {
        return finished;
    }
	
    public boolean isFirstPlayerTurn() {
	return isFirstPlayerTurn;
    }
	
    // Implemented, but not used at the moment
    public void getCode() {
	recordingFilename = "ConnectFour-"
	    + randomWithRange(1, 9) 
	    + randomWithRange(0, 9)
	    + randomWithRange(0, 9)
	    + getRandomLetter() 
	    + getRandomLetter() 
	    + getRandomLetter() 
	    + getRandomLetter() 
	    + getRandomLetter()
	    + ".con";
    }
	
    // Utilized by getCode()
    public String getRandomLetter() {
	int randomNum = randomWithRange(0, 25);
	String alphabet = "abcdefghijklmnopqrstuvwxyz";
        return alphabet.substring(randomNum, randomNum + 1);
    }
	
    public int randomWithRange(int min, int max) {
	int range = (max - min) + 1;     
        return (int)(Math.random() * range) + min;
    }
	
    public void printMoves() {
        int len = moves.size();
	int k;
	
	try {
	    File dir = new File("moves");
	    dir.mkdirs();
	    FileWriter writer = new FileWriter(new File(dir, recordingFilename));
	    writer.write(isFirstPlayerTurn + "\n");
	    for (k = 0; k < len; k++) {
	    writer.write(moves.get(k) + "\n");
	    }

	    writer.close();
	} catch (IOException exception) {
	    System.out.println("Error processing file: " + exception);
	}
    }


    /*
     *  Method for attempting to drop a Connect Four piece at position
     *  xPos.
     *
     *  TODO: Rewrite method to accomodate more than two players
     */
    public void drop(int xPos, boolean isFirstPlayer) {
	System.out.println(testPhase);
	boolean validDrop = board.addPiece(xPos, isFirstPlayer);
	boolean outcome = false;
        if(!validDrop) {
            if(!checkAITurn(alphaPlayer) && !checkAITurn(betaPlayer)) {
        	JOptionPane.showMessageDialog(null, "There is no more space in this column!", "Hey!", JOptionPane.ERROR_MESSAGE);
            }
    	} else {
            moves.add(xPos);
            GUI.repaint(true);
            outcome = board.checkWinner(isFirstPlayer);

	    if(outcome) {
                String msg = "";
	        if(isFirstPlayerTurn) {
	            if(firstPlayer.getIdentifier("name").equals(GUI.colorToString((Color) firstPlayer.getIdentifier("color")))) {
	                msg += "The "+ firstPlayer.getIdentifier("name") +" player won";
	            } else {
	                msg += "The player "+ firstPlayer.getIdentifier("name") +" won";
	            }
	        } else {
	            if(secondPlayer.getIdentifier("name").equals(GUI.colorToString((Color) secondPlayer.getIdentifier("color")))) {
	                msg += "The "+ secondPlayer.getIdentifier("name") +" player won";
	            } else {
	                msg += "The player "+ secondPlayer.getIdentifier("name") +" won";
	            }
	        }

		if(!testPhase) {
	            JOptionPane.showMessageDialog(null, msg, "We have a winner!", JOptionPane.INFORMATION_MESSAGE);
	        }

	        finished = true;
	        if(DEBUG) {
	            printMoves();
	        }
	    } else if(board.fullBoardCheck()) {
	        String msg = "There is no more space left on the board!";
	        if(!checkAITurn(alphaPlayer) && !checkAITurn(betaPlayer)) {
	            JOptionPane.showMessageDialog(null, msg, "It's a draw!", JOptionPane.INFORMATION_MESSAGE);
	        }

		finished = true;
	        if(DEBUG) {
	            printMoves();
	        }
	    }   

	    if(!finished) {
	        if(isFirstPlayerTurn) {
		    isFirstPlayerTurn = false;
		} else {
		    isFirstPlayerTurn = true;
		}
	    }
        }
		
        if(alphaPlayer.isActivated()) {
    	    alphaPlayer.processMove(isFirstPlayerTurn, validDrop, outcome);
    	}
    	if(betaPlayer.isActivated()) {
    	    betaPlayer.processMove(isFirstPlayerTurn, validDrop, outcome);
    	}
	
    	GUI.repaint();
    }
	
    public boolean checkAIActivated() {
	if(alphaPlayer.isActivated() || betaPlayer.isActivated()) {
    	    return true;
	} else {
	    return false;
	}
    }
	
    public boolean checkAITurn(ConnectFourMachine AI) {
        if(AI.isCurrentPlayer(isFirstPlayerTurn) && AI.isActivated() && !finished) {
	    return true;
	} else {
	    return false;
	}
    }
	
    public boolean checkAITurn() {
        if(checkAITurn(alphaPlayer) || checkAITurn(betaPlayer)) {
	    return true;
	} else {
	    return false;
	}
    }
	
    public void playAITurn(ConnectFourMachine AI) {
        AI.nextNode(!testPhase, isFirstPlayerTurn);
        drop(AI.getCurrentNode().getValue().getPosition(), isFirstPlayerTurn);
        GUI.repaint();
    }
	
    public void startGame() {
        if(randomWithRange(0, 1) == 0) {
	    isFirstPlayerTurn = true;
	} else {
	    isFirstPlayerTurn = false;
	}

	finished = false;
	board.clearBoard();
        getCode();
	moves.clear();
	if(alphaPlayer.isActivated()) {
	    alphaPlayer.newGame(isFirstPlayerTurn, memory);
	}
	if(betaPlayer.isActivated()) {
	    betaPlayer.newGame(isFirstPlayerTurn, memory);
	}
	    
	if(betaPlayer.isActivated() && alphaPlayer.isActivated()) {
	    AITimer.start();
	}
	    GUI.repaint();
    }
    
    public void terminate() {
        stop = true;
        this.interrupt();
    }
	
    // Respond to a button click in the game
    public void actionPerformed(ActionEvent e) {
        if(!stop) {
	    if(checkAITurn(alphaPlayer) && !GUI.isAnimating()) {
	        playAITurn(alphaPlayer);
	    	GUI.repaint();
	    } else if(checkAITurn(betaPlayer) && !GUI.isAnimating()) {
	        playAITurn(betaPlayer);
	        GUI.repaint();
	    } else if(finished && testPhase) {
	        startGame();
	    } else if(!testPhase) {
	        GUI.setVisible(true);
	    }
	}
    }
}
