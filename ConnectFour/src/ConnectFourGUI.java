/*
 *
 * @author isaiah.cruz
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FontMetrics;
import java.net.URL;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.File;
import javax.swing.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class ConnectFourGUI extends JFrame implements ActionListener {
    
	final File file = new File(ConnectFourGUI.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	    
	private JLabel question;
	private JPanel instructions;
	
	private JLabel[] arrows;
	private JLabel turnOrWinner;
	private JLabel errorMessage;
	private JLabel[][] pieces;  
	private InsidePanel inside;
	private OutsidePanel outside;
	private JButton startGameButton;
	private JButton endButton;
	private JButton setSpeedButton;
	private JButton changeColorButton;
	private JButton renameButton;
	
	private JLayeredPane layer;
	
	private int currentSpeedSetting;
	private int[] settingNums;
	
	private boolean isRedPlayer;
	private boolean finished;
	private boolean[] arrowLocs;
	private int currentArrowLoc;
	
	private ArrayList<Integer> moves;
	
	private Board board;
	
	private final int SCALE = 50;
	private final int CELL_WIDTH = SCALE;
	private final int CELL_HEIGHT = SCALE;
	private final int CELL_LEN = SCALE;
	private final int GRID_OFFSET = 51;
	
	private final int BUTTON_BOARD_DIST = 30;
	private final int BUTTON_DIST = 150 + BUTTON_BOARD_DIST;
	
	private boolean animating = false;
	
	private int newX = 500000;
	private int newY = 500000;
	
	private final boolean DEBUG = false;
	
	// To be used in the future for recording (and possibly replaying) steps in a game previously played
	private String recordingFilename = "";
	
	private ConnectFourMachine alphaPlayer;
	private ConnectFourMachine betaPlayer;
	private boolean playToWin;
	
	private JDialog chooseColors;
	
	private ColorLabel[][] colors;
	
	private Color firstPlayerColor;
	private Color secondPlayerColor;
	
	private String firstPlayerColorName;
	private String secondPlayerColorName;
	
	private boolean firstPlayerNameChanged;
	private boolean secondPlayerNameChanged;
	
	private boolean isFirstPlayer = true;
	
	private static Tree memory;
	
	private Timer pieceTimer;
	private Timer AITimer;
	
	public ConnectFourGUI() {
		board = new Board(false);
	    init();
	    repaint();
	}
	
	public void init() {
		if(DEBUG) {
			System.out.println("File path: " + file);
		}
	    
	    inside = new InsidePanel();
	    outside = new OutsidePanel();
	    
	    layer = new JLayeredPane();
	    this.setContentPane(layer);
	    
	    outside.setLayout(null);
	    outside.setPreferredSize(new Dimension((board.getWidth()) + (200 * 2) + 50, board.getHeight() + 51));
	    outside.setBounds(new Rectangle((board.getWidth()) + (200 * 2) + 50, board.getHeight() + 51));
	    
	    inside.setLayout(null);
	    inside.setPreferredSize(new Dimension(board.getWidth(), board.getHeight()));
	    inside.setBounds(0, GRID_OFFSET, board.getWidth(), board.getHeight());
	    
	    layer.setPreferredSize(new Dimension((board.getWidth()) + (200 * 2) + 50, board.getHeight() + 51));
	    
	    inside.setFocusable(true);
	    inside.requestFocusInWindow();
	    
	    currentSpeedSetting = 2;
	    
	    settingNums = new int[3];
	    settingNums[0] = 30;
	    settingNums[1] = 15;
	    settingNums[2] = 0;
	    
	    setVisible(true);
	    setResizable(false);
	    
	    KeyListener listener = new MyKeyListener();
	    
	    setTitle("Connect Four");
	    setSize(board.getWidth() + (BUTTON_BOARD_DIST * 2) + 50, board.getHeight() + 51);
	    
	    moves = new ArrayList<Integer>();
	    
	    pieces = new JLabel[board.getXSIZE()][board.getYSIZE()];
	    for(int k = 0; k < pieces.length; k++) {
	    	for(int j = 0; j < pieces[k].length; j++) {
	        	pieces[k][j] = new JLabel();
	            inside.add(pieces[k][j]);
	            pieces[k][j].setBounds(k * SCALE, j * SCALE, CELL_WIDTH, CELL_HEIGHT);
	            pieces[k][j].addMouseListener(new MyMouseListener());
	            pieces[k][j].setVisible(true);
	        }
	    }
	    inside.addKeyListener(listener);
	    inside.addMouseListener(new MyMouseListener());
	    currentArrowLoc = 0;
	    arrowLocs = new boolean[board.getXSIZE()];
	    arrows = new JLabel[board.getXSIZE()];
	    for(int k = 0; k < arrows.length; k++) {
	    	arrows[k] = new JLabel();
	        arrowLocs[k] = false;
	        outside.add(arrows[k]);
	        arrows[k].setBounds(k * SCALE, 0, CELL_WIDTH, CELL_HEIGHT);
	        String cardImageFileName = "UI/arrow.gif";
	        URL imageURL = getClass().getResource(cardImageFileName);
	        if (imageURL != null) {
	        	ImageIcon icon = new ImageIcon(imageURL);
	            arrows[k].setIcon(icon);
	        } 
	        else {
	           	throw new RuntimeException("Card image not found: \"" + cardImageFileName + "\"");
	        }
	        arrows[k].setVisible(false);
	    }        
	    
	    turnOrWinner = new JLabel();
	    turnOrWinner.setBounds(board.getWidth() + BUTTON_BOARD_DIST, 200, 150, 150);
	    
	    errorMessage = new JLabel();
	    errorMessage.setBounds(board.getWidth()+ BUTTON_BOARD_DIST, 250, 150, 150);
	    
	    arrowLocs[currentArrowLoc] = true;
	    
	    question = new JLabel();
	    question.setBounds(outside.getWidth() - 30, 0, 30, 30);
	    String cardImageFileName = "UI/question.gif";
	        URL questionImageURL = getClass().getResource(cardImageFileName);
	        if (questionImageURL != null) {
	        	ImageIcon icon = new ImageIcon(questionImageURL);
	            question.setIcon(icon);
	        } 
	        else {
	           	throw new RuntimeException("Card image not found: \"" + cardImageFileName + "\"");
	        }
	    question.addMouseListener(new MyMouseListener());
	    outside.add(question);
	        
	    instructions = new InstructionsPanel();
	    instructions.setBounds(0, 0, board.getWidth(), outside.getHeight());
	        
	    repaint();
	    
	    startGameButton = new JButton();
	    startGameButton.setText("Start a new game");
	    startGameButton.setFocusable(false);
	    outside.add(startGameButton);
	    startGameButton.setBounds(board.getWidth()+ BUTTON_BOARD_DIST, 40, 150, 30);
	    startGameButton.addActionListener(this);
	        
	    renameButton = new JButton();
	    renameButton.setText("Rename players");
	    renameButton.setFocusable(false);
	    outside.add(renameButton);
	    renameButton.setBounds(board.getWidth() + BUTTON_BOARD_DIST, 100, 150, 30);
	    renameButton.addActionListener(this);
	    
	    endButton = new JButton();
	    endButton.setText("Quit");
	    endButton.setFocusable(false);
	    outside.add(endButton);
	    endButton.setBounds(board.getWidth() + BUTTON_BOARD_DIST + BUTTON_DIST, board.getHeight() - 40, 150, 30);
	    endButton.addActionListener(this);
	    
	    setSpeedButton = new JButton();
	    setSpeedButton.setText("Change the speed");
	    setSpeedButton.setFocusable(false);
	    outside.add(setSpeedButton);
	    setSpeedButton.setBounds(board.getWidth()+ BUTTON_BOARD_DIST + BUTTON_DIST, 40, 150, 30);
	    setSpeedButton.addActionListener(this);
	    
	    changeColorButton = new JButton();
	    changeColorButton.setText("Change the colors");
	    changeColorButton.setFocusable(false);
	    outside.add(changeColorButton);
	    changeColorButton.setBounds(board.getWidth() + BUTTON_BOARD_DIST + BUTTON_DIST, 100, 150, 30);
	    changeColorButton.addActionListener(this);
	    
	    pieceTimer = new Timer(settingNums[currentSpeedSetting], inside);
	    AITimer = new Timer(0 , this);
	    
	    firstPlayerColor = Color.red;
	    firstPlayerColorName = "red";
	    
	    secondPlayerColor = Color.black;
	    secondPlayerColorName = "black";
	    
	    alphaPlayer = new ConnectFourMachine(board, isRedPlayer, memory);
	    betaPlayer = new ConnectFourMachine(board, !isRedPlayer, memory);
	    
	    alphaPlayer.setActivation(true);
	    betaPlayer.setActivation(true);
	    
	    playToWin = false;
	    
	    memory = new Tree(board);
	    
	    pack();
	    
	    layer.add(inside);
	    layer.add(outside);
	    layer.add(instructions);
	    instructions.setVisible(false);
	    
	    layer.setLayer(inside, 2);
	    layer.setLayer(outside, 0);
	    layer.setLayer(instructions, 3);
	    
	    inside.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    inside.setVisible(true);
	    outside.setVisible(true);
	    
	    startGame();
	}
	
	// Implemented, but not used at the moment
	public void getCode() {
		recordingFilename = "ConnectFour-"
	    					+  randomWithRange(1, 9) 
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
	        writer.write(isRedPlayer + "\n");
	    	for (k = 0; k < len; k++) {
	    		writer.write(moves.get(k) + "\n");
	        }
	        writer.close();
		} 
	    catch (IOException exception) {
			System.out.println("Error processing file: " + exception);
	    }
	}
	
	public void changeColors() {
		ColorPanel colorPanel = new ColorPanel();
	    chooseColors = new JDialog(this, "Pick a color for the first player!", true);
	    chooseColors.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
	    chooseColors.add(colorPanel);
	    chooseColors.setPreferredSize(colorPanel.getSize());
	    chooseColors.setResizable(false);
	    colorPanel.repaint();
	    chooseColors.pack();
	    chooseColors.setVisible(true);
	}
	
	public void changeNames() {
		Object[] choices = {"Yes", "No"};
	    boolean done;
	    var questionOutcome = JOptionPane.showOptionDialog
	    						(
		    						null, 
		    						"Do you want to rename player one?", 
		    						"Do you want to pick a name?", 
		    						JOptionPane.YES_NO_OPTION, 
		    						JOptionPane.INFORMATION_MESSAGE, 
		    						null, 
		    						choices, 
		    						0
		    					);
	    if(questionOutcome == 0) {
	    	firstPlayerNameChanged = true;
	        done = false;
	        while(!done) {
	        	String toChange = JOptionPane.showInputDialog
	            					(
	            						null, 
	            						"What's your name? Maximum of 12 characters."
	            					);
	            if(toChange == null)
	                done = true;
	            else if(toChange.length() <= 12) {
	            	firstPlayerColorName = toChange;
	                done = true;
	            }
	        }
	        if(!finished) {
	            repaint();
	        }
	    }
	    
	    questionOutcome = JOptionPane.showOptionDialog
							(
								null, 
								"Do you want to rename player two?", 
								"Do you want to pick a name?", 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.INFORMATION_MESSAGE, 
								null, 
								choices, 
								0
							);
	    if(questionOutcome == 0) {
	    	secondPlayerNameChanged = true;
	        done = false;
	        while(!done) {
	        	String toChange = JOptionPane.showInputDialog(null, "What's your name? Maximum of 12 characters.");
	            if(toChange == null) {
	                done = true;
	            }
	            else if(toChange.length() <= 12)
	                {secondPlayerColorName = toChange;
	                done = true;
	            }
	        }
	        
	        if(!finished) {
	            repaint();
	    	}
	    }
	}
	
	public void drop(int xpos, boolean isRed) {
		int result = board.addPiece(xpos, isRed);
		if(result == 2) {
        	if(!checkAITurn(alphaPlayer) && !checkAITurn(betaPlayer)) {
        		JOptionPane.showMessageDialog(null, "There is no more space in this column!", "Hey!", JOptionPane.ERROR_MESSAGE);
        	}
    	}
        else {
        	newX = board.getLastX();
            newY = board.getLastY();
            moves.add(xpos);
            repaint();
            
			if(result == 1) {
	        	String msg = "";
	            if(isRedPlayer) {
	            	if(!firstPlayerNameChanged) {
	                    msg += "The "+ firstPlayerColorName +" player won";
	            	}
	                else {
	                    msg += "The player "+ firstPlayerColorName +" won";
	                }
	            }
	            else {
	            	if(!secondPlayerNameChanged) {
	                    msg += "The "+ secondPlayerColorName +" player won";
	            	}
	                else {
	                    msg += "The player "+ secondPlayerColorName +" won";
	                }
	            }
	            if(!checkAITurn(alphaPlayer) && !checkAITurn(betaPlayer)) {
	            	JOptionPane.showMessageDialog(null, msg, "We have a winner!", JOptionPane.INFORMATION_MESSAGE);
	            }
	            finished = true;
	            if(!DEBUG) {
	            	printMoves();
	            }
	            repaint();
	        }
	        else if(board.fullBoardCheck()) {
	        	String msg = "There is no more space left on the board!";
	        	if(!checkAITurn(alphaPlayer) && !checkAITurn(betaPlayer)) {
	        		JOptionPane.showMessageDialog(null, msg, "It's a draw!", JOptionPane.INFORMATION_MESSAGE);
	        	}
	            finished = true;
	            if(!DEBUG) {
	            	printMoves();
	            }
	            repaint();
	        }   
	        if(!finished) {
	        	if(isRedPlayer) isRedPlayer = false;
	            else isRedPlayer = true;
	        }
        }
		
        if(alphaPlayer.isActivated()) {
    		alphaPlayer.processMove(isRedPlayer, result);
    	}
    	if(betaPlayer.isActivated()) {
    		betaPlayer.processMove(isRedPlayer, result);
    	}
    	 
    	if(finished && (alphaPlayer.getTotalVictories() >= 1000 || betaPlayer.getTotalVictories() >= 1000)) {
    		betaPlayer.setActivation(false);
    		playToWin = true;
    		AITimer.setDelay(500);
    		System.out.println("TEST: " + memory.getStartNode().getChildren().get(0).getVictoryChildren().size());
    	}
	}
	
	public void repaint() {
		inside.repaint();
		outside.repaint();
	    for(int k = 0; k < arrowLocs.length; k++) {
	    	if(arrowLocs[k]) {
	    		arrows[k].setVisible(true);
	    	}
	        else {
	        	arrows[k].setVisible(false);
	        }
	    }
	    turnOrWinner.repaint();
	    pack();
	}
	
	public boolean checkAITurn(ConnectFourMachine AI) {
		if(AI.isCurrentPlayer(isRedPlayer) && AI.isActivated() && !finished) {
	    	return true;
	    }
	    else {
	    	return false;
	    }
	}
	
	public void playAITurn(ConnectFourMachine AI) {
		AI.nextNode(playToWin, isRedPlayer);
		drop(AI.getCurrentNode().getPosition(), isRedPlayer);
	}
	
	public void startGame() {
		if(randomWithRange(0, 1) == 0) {
			isRedPlayer = true;
		}
	    else {
	    	isRedPlayer = false;
		}
	    finished = false;
	    board.clearBoard();
	    getCode();
	    moves.clear();
	    if(alphaPlayer.isActivated()) {
	    	alphaPlayer.newGame(isRedPlayer, memory);
	    }
	    if(betaPlayer.isActivated()) {
	    	betaPlayer.newGame(isRedPlayer, memory);
	    }
	    
	    if(betaPlayer.isActivated() && alphaPlayer.isActivated()) {
	    	AITimer.start();
	    }
	    repaint();
	}
	
	// Run the game
	public void displayGame() {
	       java.awt.EventQueue.invokeLater(new Runnable() {
	               public void run() {
	                       setVisible(true);
	               }
	       });
	}
	
	// Respond to a button click in the game
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource().equals(startGameButton)) {
	    	startGame();
	    }
	    else if(e.getSource().equals(endButton)) {
	    	System.exit(0);
	    }
	    else if(e.getSource().equals(setSpeedButton)) {
	    	String[] choices = {"Normal", "Fast", "Instant"};
	        int choice = JOptionPane.showOptionDialog(null, "Pick one of the speeds", "Setting speed...", 
	                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
	        currentSpeedSetting = choice;
	        pieceTimer.setDelay(settingNums[currentSpeedSetting]);
	    }
	    else if(e.getSource().equals(changeColorButton)) {
	    	changeColors();
	    }
	    else if(e.getSource().equals(renameButton)) {
	    	changeNames();
	    }
	    else {
	    	if(checkAITurn(alphaPlayer) && !animating) {
	    		playAITurn(alphaPlayer);
	    		repaint();
	    	}
	    	else if(checkAITurn(betaPlayer) && !animating) {
	    		playAITurn(betaPlayer);
	    		repaint();
	    	}
	    	else if(finished) {
	    		startGame();
	    	}
	    }
	}
	
	private class MyMouseListener implements MouseListener {
	
	            // Handle a mouse click on a cell of the board
	            public void mouseClicked(MouseEvent e) {
	            	if(!finished) {
	            		if(animating) {
	            			JOptionPane.showMessageDialog(null, "A piece is dropping!", "Hey!", JOptionPane.ERROR_MESSAGE);
	                    }
	                    else {
	                    	for(int k = 0; k < board.getPieces().length; k++) {
	                        	for(int j = 0; j < board.getPieces()[k].length; j++) {
	                        		if(e.getSource().equals(pieces[k][j])) {
	                        			if(!checkAITurn(alphaPlayer) && !checkAITurn(betaPlayer)) {
	                        				drop(k, isRedPlayer);
	                        			}
	                        			break;
	                                }
	                            }
	                        }
	                    }
	                }
	                else {
	                	JOptionPane.showMessageDialog(null, "This game has already finished!", "Hey!", JOptionPane.ERROR_MESSAGE);
	                }
	            }
	
	            // Checks when mouse leaves the question label
	            public void mouseExited(MouseEvent e) {
	            if(e.getSource().equals(question)) {
	            		instructions.setVisible(false);
	                    }
	            }
	
	            // Not used
	            public void mouseReleased(MouseEvent e) {
	            }
	
	            // Checks when mouse enters the question label
	            public void mouseEntered(MouseEvent e) 
	                {if(e.getSource().equals(question)) {
	                	instructions.setVisible(true);
	                    }
	                }
	
	            // Not used
	            public void mousePressed(MouseEvent e) {
	            }
	    }
	
	private class MyKeyListener implements KeyListener {
	
	    public void keyReleased (KeyEvent e) {
	    }
	          
	    public void keyPressed (KeyEvent e) {
	    	int keyCode = e.getKeyCode();
	        if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
	        	arrowLocs[currentArrowLoc] = false;
	            if(currentArrowLoc == arrowLocs.length - 1) {
	            	currentArrowLoc = 0;
            	}
	            else {
	            	currentArrowLoc++;
            	}
	            arrowLocs[currentArrowLoc] = true;
	            repaint();
            }
	        else if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
	        	arrowLocs[currentArrowLoc] = false;
	            if(currentArrowLoc == 0) {
	            	currentArrowLoc = arrowLocs.length - 1;
            	}
	            else {
	            	currentArrowLoc--;
            	}
	            arrowLocs[currentArrowLoc] = true;
	            repaint();
            }
	        else if(keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER) {
	        	if(!finished) {
	        		if(animating) {
	        			JOptionPane.showMessageDialog(null, "A piece is dropping!", "Hey!", JOptionPane.ERROR_MESSAGE);
	                }
	                else {
	                	if(!checkAITurn(alphaPlayer) && !checkAITurn(betaPlayer)) {
	                		drop(currentArrowLoc, isRedPlayer);
	                	}
	                }
	            }
	            else {
	            	JOptionPane.showMessageDialog(null, "This game has already finished!", "Hey!", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    }
	    
	    public void keyTyped (KeyEvent e) {
	    }
	}
	
	private class InsidePanel extends JPanel implements ActionListener {
		
		private final int CIRCLE_LEN = SCALE - 4;
		
		private int pieceY = -27;
		private boolean isRed = false;
		
		private int lastX;
		private int lastY;
		
		public InsidePanel() {
		}
		
		private void redrawGrid (Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
		    g2D.setColor(Color.black);
		    for(int k = 0; k < board.getPieces().length; k++) {
		    	for(int j = 0; j < board.getPieces()[k].length; j++) {
		        	g2D.drawRect(k * SCALE, j * SCALE, CELL_LEN, CELL_LEN);
		        }
		    }
		}
		
		private void paintPieces (Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
		    for(int k = 0; k < board.getPieces().length; k++) {
		    	for(int j = 0; j < board.getPieces()[k].length; j++) {
		    		g2D.setColor(Color.black);
		            if(board.getPiece(k, j) != null) {
		            	Color pieceColor = secondPlayerColor;
		                if(board.getPiece(k, j).isRed()) {
		                	pieceColor = firstPlayerColor;
		                }
		                if(k != newX || j != newY) {
		                	g2D.setColor(pieceColor);
		                    g2D.fillOval((k * SCALE) + 2, (j * SCALE) + 2, CIRCLE_LEN, CIRCLE_LEN);
		                    g2D.setColor(Color.black);
		                    g2D.drawOval((k * SCALE) + 2, (j * SCALE) + 2, CIRCLE_LEN, CIRCLE_LEN);
		                }
		            }
		        }
		    }
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		    Graphics2D g2D = (Graphics2D) g;
		    g2D.setColor(Color.WHITE);
		    g2D.fillRect(0, 0, board.getWidth(), board.getHeight());
		    if(!animating) {
		    	for(int k = 0; k < board.getPieces().length; k++) {
		        	for(int j = board.getPieces()[k].length - 1; j >= 0; j--) {
		            	g2D.setColor(Color.black);
		                if(board.getPiece(k, j) == null) {
		                	g2D.drawRect(k * SCALE, j * SCALE, CELL_LEN, CELL_LEN);
		                }
		                else {
		                	Color pieceColor = secondPlayerColor;
		                    g2D.setColor(Color.black);
		                    
		                    if(board.getPiece(k, j).isRed()) {
		                    	pieceColor = firstPlayerColor;
		                    }
		
		                    if(k == newX && j == newY) {
		                    	g2D.drawRect(k * SCALE, j * SCALE, CELL_LEN, CELL_LEN);
		                        isRed = board.getPiece(k, j).isRed();
		                        
		                        if(currentSpeedSetting == 0 || currentSpeedSetting == 1) {
		                        	animating = true;
		                            lastX = k;
		                            lastY = j;
		                            pieceTimer.start();
		                        }
		                        else {
		                        	g2D.setColor(pieceColor);
		                            g2D.fillOval((k * SCALE) + 2, (j * SCALE) + 2, CIRCLE_LEN, CIRCLE_LEN);
		                            g2D.setColor(Color.black);
		                            g2D.drawOval((k * SCALE) + 2, (j * SCALE) + 2, CIRCLE_LEN, CIRCLE_LEN);
		                        }
		                    }
		                    else {
		                    	g2D.drawRect(k * SCALE, j * SCALE, CELL_LEN, CELL_LEN);
		                        g2D.setColor(pieceColor);
		                        g2D.fillOval((k * SCALE) + 2, (j * SCALE) + 2, CIRCLE_LEN, CIRCLE_LEN);
		                        g2D.setColor(Color.black);
		                        g2D.drawOval((k * SCALE) + 2, (j * SCALE) + 2, CIRCLE_LEN, CIRCLE_LEN);
		                    }
		                }
		            }
		        }
		    }
		    else {
		    	g2D.setColor(Color.white);
		        g2D.fillOval((lastX * SCALE) + 2, pieceY - 2, CIRCLE_LEN, CIRCLE_LEN);
		        redrawGrid(g);
		        paintPieces(g);
		        if(isRed) g2D.setColor(firstPlayerColor);
		        else g2D.setColor(secondPlayerColor);
		        g2D.fillOval((lastX * SCALE) + 2, pieceY - 1, CIRCLE_LEN, CIRCLE_LEN);
		        g2D.setColor(Color.black);
		        g2D.drawOval((lastX * SCALE) + 2, pieceY - 1, CIRCLE_LEN, CIRCLE_LEN);
		    }
		}
		
		@Override
		 public void actionPerformed(ActionEvent e) {
			if(pieceY >= (lastY * SCALE) + 3) {
				animating = false;
		        pieceY = -27;
		        newX = 500000000;
		        newY = 500000000;
		        pieceTimer.stop();
		        try {
		        	Thread.sleep(100);
		        }
		        catch(InterruptedException x) {
		    	}
		    }
		    else {
		    	repaint();
		    	pieceY += 10;
		    }
		}
	}// end of InsidePanel
	
	
	private class OutsidePanel extends JPanel {
	
		public OutsidePanel() {
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		    Graphics2D g2D = (Graphics2D) g;
		    BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER); 
		    BasicStroke blankStroke = new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER); 
		    
		    g2D.setColor(Color.black);
		    g2D.drawRect(0, GRID_OFFSET, board.getWidth(), board.getHeight());
		    
		    g2D.setColor(Color.LIGHT_GRAY);
		    g2D.fillRect(0, 0, this.getWidth(), GRID_OFFSET);
		    g2D.fillRect(board.getWidth(), 0, this.getWidth() - board.getWidth(), this.getHeight());
		    String prefix = "";
		    String player = "";
		    if(!finished) prefix += "Turn: \n";
		    else prefix += "Winner: \n";
		    if(isRedPlayer) player += "\n Player 1 | " + firstPlayerColorName.toUpperCase();
		    else player += "\nPlayer 2 | " + secondPlayerColorName.toUpperCase();
		    
		    g2D.setColor(Color.black);
		    g2D.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
		    g2D.drawString(prefix, turnOrWinner.getX(), turnOrWinner.getY());
		    if(isRedPlayer) g2D.setColor(firstPlayerColor);
		    else g2D.setColor(secondPlayerColor);
		    g2D.setFont(new Font("TimesRoman", Font.PLAIN, 30)); 
		    g2D.setStroke(stroke);
		    g2D.drawString(player, turnOrWinner.getX(), turnOrWinner.getY() + 30);
		    g2D.setStroke(blankStroke);
	    }
	}// end of OutsidePanel
	
	
	private class ColorLabel extends JLabel {
	
		private Color labelColor;
		
		public ColorLabel(Color color) {
			labelColor = color;
		}
		
		public String getColorName() {
			if(labelColor.equals(Color.white)) return "white";
		    else if(labelColor.equals(Color.black)) return "black";
		    else if(labelColor.equals(Color.red)) return "red";   
		    else if(labelColor.equals(Color.green)) return "green";
		    else if(labelColor.equals(Color.blue)) return "blue"; 
		    else if(labelColor.equals(Color.orange)) return "orange";
		    else if(labelColor.equals(Color.yellow)) return "yellow";
		    else return "magenta";
		}
		
		public Color getColor() {
			return labelColor;
		}
	}// end of ColorLabel
	
	private class ColorPanel extends JPanel {
	
		public ColorPanel() {
			init();
	    }
	
		public void init() {
			colors = new ColorLabel[2][4];
		    setSize(290, 190);
		    for(int k = 0; k < colors.length; k++) {
		    	for(int j = 0; j < colors[k].length; j++) {
		        	if(k == 0 && j == 0) {
		            	colors[k][j] = new ColorLabel(Color.white);
	                }
		            else if(k == 0 && j == 1) {
		            	colors[k][j] = new ColorLabel(Color.black);
	                }
		            else if(k == 0 && j == 2) {
		            	colors[k][j] = new ColorLabel(Color.red);   
	                }
		            else if(k == 0 && j == 3) {
		            	colors[k][j] = new ColorLabel(Color.green);  
	                }
		            else if(k == 1 && j == 0) {
		            	colors[k][j] = new ColorLabel(Color.blue);    
	                }
		            else if(k == 1 && j == 1) {
		            	colors[k][j] = new ColorLabel(Color.orange);
	                }
		            else if(k == 1 && j == 2) {
		            	colors[k][j] = new ColorLabel(Color.yellow);
	                }
		            else {
		            	colors[k][j] = new ColorLabel(Color.magenta);
	                }
		            colors[k][j].addMouseListener(new ColorLabelListener());
		            add(colors[k][j]);
	            }
	        }
		}
	
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		    Graphics2D g2D = (Graphics2D) g;
		    for(int k = 0; k < colors.length; k++) {
		    	for(int j = 0; j < colors[k].length; j++) {
		    		int yOffset = 0;
		            if(k > 0) {
		            	yOffset = 1;
		            }
		            g2D.setColor(colors[k][j].getColor());
		            g2D.fillRect(20 + (60 * j)
		                    , 20 + (50 * k) + (10 * yOffset)
		                    , 50, 50);
		            g2D.setColor(Color.black);
		            g2D.drawRect(20 + (60 * j)
		                    , 20 + (50 * k) + (10 * yOffset)
		                    , 50, 50);
		            colors[k][j].setBounds(20 + (60 * j)
		                    , 20 + (50 * k) + (10 * yOffset)
		                    , 50, 50);
		        }
		    }
		}
	
	private class ColorLabelListener implements MouseListener {
	            
	            // Checks when mouse is clicked for the "choose colors" pop-up
	            public void mouseClicked(MouseEvent e) {
	            	for(int k = 0; k < colors.length; k++) {
	            		for(int j = 0; j < colors[k].length; j++) {
	            			if(e.getSource().equals(colors[k][j])) {
	            				if(isFirstPlayer) {
	            					firstPlayerColor = colors[k][j].getColor();
	                                if(!firstPlayerNameChanged) {
	                                	firstPlayerColorName = colors[k][j].getColorName();
	                                }
	                                isFirstPlayer = !isFirstPlayer;
	                                if(!finished) {
	                                	repaint();
	                                }
	                                chooseColors.setTitle("Pick a color for the second player!");
	                            }
	                            else {
	                            	if(!colors[k][j].getColor().equals(firstPlayerColor)) {
	                            		secondPlayerColor = colors[k][j].getColor();
	                                    if(!secondPlayerNameChanged) {
	                                    	secondPlayerColorName = colors[k][j].getColorName();
	                                    }
	                                    isFirstPlayer = !isFirstPlayer;
	                                    if(!finished) {
	                                    	outside.repaint();
	                                    	inside.repaint();
	                                    }
	                                    chooseColors.setVisible(false);
	                                }
	                                else {
	                                	JOptionPane.showMessageDialog(null, "This color was already chosen!", "Hey!", JOptionPane.ERROR_MESSAGE);
	                                }
	                            }
	                            break;
	                        }
	                    }
	                }
	            }
	
	            public void mouseExited(MouseEvent e) {
	            }
	
	            // Not used
	            public void mouseReleased(MouseEvent e) {
	            }
	
	            // Not used
	            public void mouseEntered(MouseEvent e) {
	            }
	
	            // Not used
	            public void mousePressed(MouseEvent e) {
	            }
	}
	}// end of ColorPanel
	
	
	private class InstructionsPanel extends JPanel {
	
		private JLabel hand = new JLabel();
		private JLabel wasd = new JLabel();
		    
		public InstructionsPanel() {
		}
	
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		    Graphics2D g2D = (Graphics2D) g;
		    FontMetrics metrics = g2D.getFontMetrics();
		    Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
		    fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		    Font TNR_Bold = new Font("Times New Roman", Font.BOLD, 20).deriveFont(fontAttributes);
		    Font TNR_Plain = new Font("Times New Roman", Font.PLAIN, 14);
		    String str = "";
		
		    g2D.setColor(Color.white);
		    g2D.fillRect(0, 0, board.getWidth(), outside.getHeight());
		
		    g2D.setColor(Color.black);
		    g2D.drawRect(0, 0, board.getWidth() - 1, outside.getHeight() - 1);
		
		
		    g2D.setFont(TNR_Bold);
		    str = "How to place a piece on the board";
		    metrics = g2D.getFontMetrics(TNR_Bold);
		    g2D.drawString(str, (board.getWidth() - metrics.stringWidth(str)) / 2, 25);
		
		    str = "Click on a square on the board with your mouse";
		    g2D.setFont(TNR_Plain);
		    g2D.drawString(str, 25, 50);
		
		    str = "OR";
		    metrics = g2D.getFontMetrics(TNR_Plain);
		    g2D.drawString(str, (board.getWidth() - metrics.stringWidth(str)) / 2, 50 + metrics.getHeight() + 5);
		
		    str = "Press TAB. Then use A and D"; 
		    metrics = g2D.getFontMetrics(TNR_Plain);
		    g2D.drawString(str, (board.getWidth() - metrics.stringWidth(str)) / 2, 50 + 2 * metrics.getHeight() + 2 * 5);
		
		    str = "or the LEFT and RIGHT ARROW KEYS"; 
		    metrics = g2D.getFontMetrics(TNR_Plain);
		    g2D.drawString(str, (board.getWidth() - metrics.stringWidth(str)) / 2, 50 + 3 * metrics.getHeight() + 2 * 5);
		
		    str = "to move the arrow above the board.";
		    g2D.drawString(str, (board.getWidth() - metrics.stringWidth(str)) / 2, 50 + 4 * metrics.getHeight() + 2 * 5);
		
		    str = "Press the SPACE or ENTER keys to drop";
		    g2D.drawString(str, (board.getWidth() - metrics.stringWidth(str)) / 2, 50 + 6 * metrics.getHeight() + 2 * 5);
		
		    str = "a piece where the arrow is located.";
		    g2D.drawString(str, (board.getWidth() - metrics.stringWidth(str)) / 2, 50 + 7 * metrics.getHeight() + 2 * 5);
		
		    add(hand);
		    hand.setBounds(0, 600, 50, 500);
		    String cardImageFileName_hand = "UI/smaller_hand.gif";
		    URL questionImageURL_hand = getClass().getResource(cardImageFileName_hand);
		    if (questionImageURL_hand != null) {
		    	ImageIcon icon = new ImageIcon(questionImageURL_hand);
		        hand.setIcon(icon);
		    } 
		    else {
		    	throw new RuntimeException("Card image not found: \"" + cardImageFileName_hand + "\"");
		    }
		    
		    add(wasd);
		    wasd.setBounds(0, 0, 500, 500);
		    String cardImageFileName_wasd = "UI/wasd.gif";
		    URL questionImageURL_wasd = getClass().getResource(cardImageFileName_wasd);
		    if (questionImageURL_wasd != null) {
		    	ImageIcon icon = new ImageIcon(questionImageURL_wasd);
		        hand.setIcon(icon);
		    } 
		    else {
		    	throw new RuntimeException("Card image not found: \"" + cardImageFileName_wasd + "\"");
		    }
		}
	}// end of InsidePanel

}// end of ConnectFourGUI
