package connect_four_code;
import game_framework.Node;
import game_framework.Tree;

/**
 *
 * @author isaiah.cruz
 */

public class ConnectFourMachine {
	
	private boolean isFirstPlayer;
	private Node currentNode;
	private Node lastNode;
	private boolean activated;
	private int totalVictories;
	private Board board;
	
	public ConnectFourMachine(Board a_board, boolean redPlayer, Tree a_tree) {
		isFirstPlayer = redPlayer;
		totalVictories = 0;
		board = a_board;
	}
	
	public Node getCurrentNode() {
		return currentNode;
	}
	
	public void setCurrentNode(Node a_node) {
		lastNode = currentNode;
		currentNode = a_node;
	}
	
	public Node getLastNode() {
		return lastNode;
	}
	
	public void nextNode(boolean toWin, boolean isFirstPlayerTurn) {
		int logicalPos = logicalPosition(isFirstPlayerTurn);
		if(logicalPos == -1) {
			if(toWin) {
				if(getCurrentNode().getVictoryChildren().size() + getCurrentNode().getChildren().size() > 0) {
					nextKnownNode();
				}
				else {
					nextUnknownNode();
				}
			}
			else {
				if(getCurrentNode().getUncheckedPositions().size() > 0) {
					nextUnknownNode();
				}
				else {
					System.out.println("not random");
					nextKnownNode();
				}
			}
		}
		else {
			getCurrentNode().addChild(logicalPos);
			setCurrentNode(getCurrentNode().getLastChild());
		}
	}
	
	public void nextUnknownNode() {
		int range = getCurrentNode().getUncheckedPositions().size();
		int nextPos = (int)(Math.random() * range);
		getCurrentNode().addChild(getCurrentNode().getUncheckedPositions().get(nextPos));
		setCurrentNode(getCurrentNode().getLastChild());
	}
	
	public void nextKnownNode() {
		if(getCurrentNode().getVictoryChildren().size() > 0) {
			int range = getCurrentNode().getVictoryChildren().size();
			int nextPos = (int)(Math.random() * range);
			setCurrentNode(getCurrentNode().getVictoryChildren().get(nextPos));
		}
		else {
			int range = getCurrentNode().getChildren().size();
			int nextPos = (int)(Math.random() * range);
			setCurrentNode(getCurrentNode().getChildren().get(nextPos));
		}
	}
	
	public int logicalPosition(boolean isFirstPlayerTurn) {
		int losePosition = -1;
		for(int i = 0; i < board.getXSIZE(); i++) {
			Board tempBoard = board.clone(false);
			if(tempBoard.addPiece(i, isFirstPlayer)) { 
				boolean hasWinner = board.checkWinner(isFirstPlayerTurn);
				if(hasWinner && !(isFirstPlayerTurn^isFirstPlayer)) {
					return i;
				}
				else {
					tempBoard.undo();
					tempBoard.addPiece(i, !isFirstPlayer);
					hasWinner = board.checkWinner(isFirstPlayerTurn);
					if(hasWinner && !(isFirstPlayerTurn^isFirstPlayer)) {
						losePosition = i;
					}
				}
			}
		}
		return losePosition;
	}
	
	/*
	 * Outcomes:
	 * 0 = draw or continue
	 * 1 = someone won
	 * 2 = invalid move
	 */
	public void processMove(boolean isFirstPlayerTurn, boolean validDrop, boolean outcome) {
		if(!(isFirstPlayer^isFirstPlayerTurn)) { // Checks if the AI is the current player when processMove was called
			if(!validDrop) {
				lastNode.removeChild(currentNode);
				currentNode = lastNode;
				lastNode = currentNode.getParent();
			}
			else if(outcome) {
				Node currentTempNode = getCurrentNode();
				Node lastTempNode = getLastNode();
				while(lastTempNode.getParent() != null) {
					lastTempNode.addVictoryChild(currentTempNode);
					currentTempNode = lastTempNode;
					lastTempNode = lastTempNode.getParent();
				}
				totalVictories = currentTempNode.getVictoryChildren().size();
			}
			else if(!outcome) {
				getLastNode().addChild(getCurrentNode());
			}
		}
		else {
			if(!outcome) {
				getLastNode().addChild(getCurrentNode());
			}
		}
	}
	
	public void newGame(boolean isFirstPlayerTurn, Tree memory) {
		currentNode = memory.getStartNode();
		if(!isFirstPlayer^isFirstPlayerTurn) {
			setCurrentNode(getCurrentNode().getChildren().get(0));
		}
		else {
			setCurrentNode(getCurrentNode().getChildren().get(1));
		}
	}
	
	public int getTotalVictories() {
		return totalVictories;
	}
	
	public boolean isCurrentPlayer(boolean isFirstPlayerTurn) {
		return !(isFirstPlayer^isFirstPlayerTurn);
	}
	
	public void setActivation(boolean activate) {
		activated = activate;
	}
	
	public boolean isActivated() {
		return activated;
	}
}
