package connect_four_code;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import game_framework.*;

/**
 *
 * @author isaiah.cruz
 */

public class ConnectFourRunner implements ActionListener{
	private Timer t;
	private Tree<Move> memory;
	private Board board;
	private ConnectFourGame[] testRuns;
	
	public ConnectFourRunner () {
	}
	
	public void init() {
		try {
			board = new Board();
			memory = new ConnectFourTree(board);
			testRuns = new ConnectFourGame[]{
                                new ConnectFourGame(true, memory), 
                                new ConnectFourGame(true, memory), 
                                new ConnectFourGame(true, memory), 
                                new ConnectFourGame(true, memory),
                                new ConnectFourGame(true, memory),
                                new ConnectFourGame(true, memory),
                                new ConnectFourGame(true, memory),
                                new ConnectFourGame(true, memory),
                                new ConnectFourGame(true, memory)
                            };
			t = new Timer(0, this);
			t.start();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		}
	}
	
	public void actionPerformed(ActionEvent e) {
	    if(memory.getRoot().getChildren().get(0).getValue().getNumVictoryChildren() >= 1000) {
	    	t.stop();
	    	for(ConnectFourGame game:testRuns) {
	    		game.terminate();
	    	}
	    	ConnectFourGame game = new ConnectFourGame(false, memory);
    	}
	}
	
	public static void main(String[] args) {
		try {
			ConnectFourRunner self = new ConnectFourRunner();
			self.init();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error");
		}
	}
    
}
