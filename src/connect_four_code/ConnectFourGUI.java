/*
 *
 * @author isaiah.cruz
 */

package connect_four_code;

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
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.File;
import javax.swing.*;

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
	private JButton AIButton;
	
	private JLayeredPane layer;
	
	private int currentSpeedSetting;
	private int[] settingNums;
	
	private boolean[] arrowLocs;
	private int currentArrowLoc;
	
	private Board board;
	private ConnectFourGame game;
	
	private final int SCALE = 50;
	private final int CELL_WIDTH = SCALE;
	private final int CELL_HEIGHT = SCALE;
	private final int CELL_LEN = SCALE;
	private final int GRID_OFFSET = 51;
	
	private final int BUTTON_BOARD_DIST = 30;
	private final int BUTTON_DIST = 180;
	
	private boolean animating = false;
	private boolean placed = true;
	
	private final boolean DEBUG = false;
	
	private JDialog chooseColors;
	private ColorLabel[][] colors;
	
	private Timer pieceTimer;
	
	public ConnectFourGUI(Board a_board, ConnectFourGame a_game) {
		init(a_board, a_game);
	    repaint();
	}
	
	public void init(Board a_board, ConnectFourGame a_game) {
		if(DEBUG) {
			System.out.println("File path: " + file);
		}
	    
		board = a_board;
		game = a_game;
		setVisible(!game.testPhase);
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
	    
	    setResizable(false);
	    
	    KeyListener listener = new MyKeyListener();
	    
	    setTitle("Connect Four");
	    setSize(board.getWidth() + (BUTTON_BOARD_DIST * 2) + 50, board.getHeight() + 51);
	    
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
	        String cardImageFileName = "../UI/arrow.gif";
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
	    String cardImageFileName = "../UI/question.gif";
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
	    
	    AIButton = new JButton();
	    AIButton.setText("Add in CPU player(s)");
	    AIButton.setFocusable(false);
	    outside.add(AIButton);
	    AIButton.setBounds(board.getWidth() + BUTTON_BOARD_DIST, 160, 150, 30);
	    AIButton.addActionListener(this);
	    
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
	    
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    pack();
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
	
	public void addAI() {
		Object[] choices = {"First Player", "Second Player", "Both"};
		int questionOutcome = JOptionPane.showOptionDialog
				(
					null, 
					"Which players do you want to convert to CPUs?", 
					"Do you want to play with CPUs?", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.INFORMATION_MESSAGE, 
					null, 
					choices, 
					0
				);
		if(questionOutcome == 0) {
			game.alphaPlayer.setActivation(true);
		}
		else if(questionOutcome == 1) {
			game.betaPlayer.setActivation(true);
		}
		else {
			game.alphaPlayer.setActivation(true);
			game.betaPlayer.setActivation(true);
		}
	}
	
	public void changeNames() {
		Object[] choices = {"Yes", "No"};
		int questionOutcome = 1;
		if(!(game.checkAIActivated() && game.checkAITurn())) {
			questionOutcome = JOptionPane.showOptionDialog
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
		    	while(true) {
		        	String toChange = JOptionPane.showInputDialog
		            					(
		            						null, 
		            						"What's your name? Maximum of 12 characters."
		            					);
		            if(toChange == null)
		                break;
		            else if(toChange.length() <= 12) {
		            	game.getFirstPlayer().setIdentifier("name", toChange);
		                break;
		            }
		        }
		        if(!game.isFinished()) {
		            repaint();
		        }
		    }
		}
		if(!(game.checkAIActivated())) {
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
		    	while(true) {
		        	String toChange = JOptionPane.showInputDialog(null, "What's your name? Maximum of 12 characters.");
		            if(toChange == null) {
		                break;
		            }
		            else if(toChange.length() <= 12)
		                {game.getSecondPlayer().setIdentifier("name", toChange);
		                break;
		            }
		        }
		        
		        if(!game.isFinished()) {
		            repaint();
		    	}
		    }
	    }
	}
	

	public String colorToString(Color colorName) {
		if(colorName.equals(Color.white)) {
			return "white";
		}
	    else if(colorName.equals(Color.black)) {
	    	return "black";
	    }
	    else if(colorName.equals(Color.red)) {
	    	return "red";   
	    }
	    else if(colorName.equals(Color.green)) {
	    	return "green";
	    }
	    else if(colorName.equals(Color.blue)) {
	    	return "blue"; 
	    }
	    else if(colorName.equals(Color.orange)) {
	    	return "orange";
	    }
	    else if(colorName.equals(Color.yellow)) {
	    	return "yellow";
	    }
	    else if(colorName.equals(Color.magenta)){
	    	return "magenta";
	    }
	    else {
	    	return null;
	    }
	}
	
	public void repaint(boolean pieceDropped) {
		placed = false;
		repaint();
	}
	
	@Override
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
	    	game.startGame();
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
	    else if(e.getSource().equals(AIButton)) {
	    	addAI();
	    }
	}
	
	public boolean isAnimating() {
		return animating;
	}
	
	private class MyMouseListener implements MouseListener {
	
	            // Handle a mouse click on a cell of the board
	            public void mouseClicked(MouseEvent e) {
	            	if(!game.isFinished()) {
	            		if(animating) {
	            			JOptionPane.showMessageDialog(null, "A piece is dropping!", "Hey!", JOptionPane.ERROR_MESSAGE);
	                    }
	                    else {
	                    	for(int k = 0; k < board.getPieces().length; k++) {
	                        	for(int j = 0; j < board.getPieces()[k].length; j++) {
	                        		if(e.getSource().equals(pieces[k][j])) {
	                        			if(!game.checkAITurn()) {
	                        				game.drop(k, game.isFirstPlayerTurn());
	                        			}
	                        			break;
	                                }
	                            }
	                        }
	                    	repaint();
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
	        	if(!game.isFinished()) {
	        		if(animating) {
	        			JOptionPane.showMessageDialog(null, "A piece is dropping!", "Hey!", JOptionPane.ERROR_MESSAGE);
	                }
	                else {
	                	if(!game.checkAITurn()) {
	                		game.drop(currentArrowLoc, game.isFirstPlayerTurn());
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
		            	Color pieceColor = (Color) game.getSecondPlayer().getIdentifier("color");
		            	if(board.getPiece(k, j).isRed()) {
		                	pieceColor = (Color) game.getFirstPlayer().getIdentifier("color");
		                }
		                if(k != board.getLastX() || j != board.getLastY()) {
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
		                	Color pieceColor = (Color) game.getSecondPlayer().getIdentifier("color");
		                    g2D.setColor(Color.black);
		                    
		                    if(board.getPiece(k, j).isRed()) {
		                    	pieceColor = (Color) game.getFirstPlayer().getIdentifier("color");
		                    }
		
		                    if(k == board.getLastX() && j == board.getLastY()) {
		                    	g2D.drawRect(k * SCALE, j * SCALE, CELL_LEN, CELL_LEN);
		                        isRed = board.getPiece(k, j).isRed();
		                        if(!placed && (currentSpeedSetting == 0 || currentSpeedSetting == 1)) {
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
		        if(isRed) {
		        	g2D.setColor((Color) game.getFirstPlayer().getIdentifier("color"));
		        }
		        else {
		        	g2D.setColor((Color) game.getSecondPlayer().getIdentifier("color"));
		        }
		        g2D.fillOval((lastX * SCALE) + 2, pieceY - 1, CIRCLE_LEN, CIRCLE_LEN);
		        g2D.setColor(Color.black);
		        g2D.drawOval((lastX * SCALE) + 2, pieceY - 1, CIRCLE_LEN, CIRCLE_LEN);
		    }
		}
		
		@Override
		 public void actionPerformed(ActionEvent e) {
			if(pieceY >= (lastY * SCALE) + 3) {
				animating = false;
				placed = true;
		        pieceY = -27;
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
		    if(!game.isFinished()) {
		    	prefix += "Turn: \n";
		    }
		    else {
		    	prefix += "Winner: \n";
		    }
		    if(game.isFirstPlayerTurn()) {
		    	player += "\nPlayer 1 | " + ((String) game.getFirstPlayer().getIdentifier("name")).toUpperCase();
		    }
		    else {
		    	player += "\nPlayer 2 | " + ((String) game.getSecondPlayer().getIdentifier("name")).toUpperCase();
		    }
		    
		    g2D.setColor(Color.black);
		    g2D.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
		    g2D.drawString(prefix, turnOrWinner.getX(), turnOrWinner.getY());
		    if(game.isFirstPlayerTurn()) {
		    	g2D.setColor((Color) game.getFirstPlayer().getIdentifier("color"));
		    }
		    else {
		    	g2D.setColor((Color) game.getSecondPlayer().getIdentifier("color"));
		    }
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
		
		public Color getColor() {
			return labelColor;
		}
	}// end of ColorLabel
	
	private class ColorPanel extends JPanel {
		private boolean isFirstPlayerPickingColor;
		
		public ColorPanel() {
			init();
	    }
	
		public void init() {
			colors = new ColorLabel[2][4];
		    isFirstPlayerPickingColor = true;
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
	            	String firstPlayerName = (String) game.getFirstPlayer().getIdentifier("name");
	            	Color firstPlayerColor = (Color) game.getFirstPlayer().getIdentifier("color");
	            	String secondPlayerName = (String) game.getFirstPlayer().getIdentifier("name");
	            	Color secondPlayerColor = (Color) game.getFirstPlayer().getIdentifier("color");
	            	
	            	for(int k = 0; k < colors.length; k++) {
	            		for(int j = 0; j < colors[k].length; j++) {
	            			if(e.getSource().equals(colors[k][j])) {
	            				if(isFirstPlayerPickingColor) {
	            					System.out.println("cat");
	            					game.getFirstPlayer().setIdentifier("color", colors[k][j].getColor());
	                                if(firstPlayerName.equals(colorToString(firstPlayerColor))) {
	                                	game.getFirstPlayer().setIdentifier("name", colorToString((Color) game.getFirstPlayer().getIdentifier("color")));
	                                }
	                                System.out.println("isFirstPlayerPickingColor: " + isFirstPlayerPickingColor);
	                                isFirstPlayerPickingColor = !isFirstPlayerPickingColor;
	                                System.out.println("isFirstPlayerPickingColor: " + isFirstPlayerPickingColor);
	                                if(!game.isFinished()) {
	                                	repaint();
	                                }
	                                chooseColors.setTitle("Pick a color for the second player!");
	                            }
	                            else {
	                            	if(!colors[k][j].getColor().equals(firstPlayerColor)) {
	                            		System.out.println("dog");
	                            		game.getSecondPlayer().setIdentifier("color", colors[k][j].getColor());
		                                if(secondPlayerName.equals(colorToString(secondPlayerColor))) {
		                                	game.getSecondPlayer().setIdentifier("name", colorToString((Color) game.getSecondPlayer().getIdentifier("color")));
		                                }
		                                System.out.println("isFirstPlayerPickingColor: " + isFirstPlayerPickingColor);
	                                    isFirstPlayerPickingColor = !isFirstPlayerPickingColor;
	                                    System.out.println("isFirstPlayerPickingColor: " + isFirstPlayerPickingColor);
	                                    if(!game.isFinished()) {
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
		    String cardImageFileName_hand = "../UI/smaller_hand.gif";
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
		    String cardImageFileName_wasd = "../UI/wasd.gif";
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
