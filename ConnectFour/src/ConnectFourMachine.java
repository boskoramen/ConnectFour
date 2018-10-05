/**
 *
 * @author isaiah.cruz
 */

public class ConnectFourMachine {
	
	private boolean isRedPlayer;
	private Node currentNode;
	private Node lastNode;
	private boolean activated;
	private int totalVictories;
	private Board board;
	private int randMoveCount = 0;
	
	public ConnectFourMachine(Board a_board, boolean redPlayer, Tree a_tree) {
		isRedPlayer = redPlayer;
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
	
	public void nextNode(boolean toWin, boolean isRed) {
		int logicalPos = logicalPosition(isRed);
		if(logicalPos == -1) {
			if(toWin) {
				if(getCurrentNode().getVictoryChildren().size() + getCurrentNode().getChildren().size() > 0) {
					nextKnownNode();
				}
				else {
					nextUnknownNode(isRed);
				}
			}
			else {
				if(getCurrentNode().getUncheckedPositions().size() > 0) {
					nextUnknownNode(isRed);
				}
				else {
					nextKnownNode();
				}
			}
		}
		else {
			getCurrentNode().addChild(logicalPos);
			setCurrentNode(getCurrentNode().getLastChild());
		}
	}
	
	public void nextUnknownNode(boolean isRed) {
		int range = getCurrentNode().getUncheckedPositions().size();
		int nextPos = (int)(Math.random() * range);
		getCurrentNode().addChild(getCurrentNode().getUncheckedPositions().get(nextPos));
		setCurrentNode(getCurrentNode().getLastChild());
		randMoveCount++;
		System.out.println("Random move: " + randMoveCount);
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
	
	public int logicalPosition(boolean isRed) {
		for(int i = 0; i < board.getXSIZE(); i++) {
			Board tempBoard = board.clone(false);
			int outcome = tempBoard.addPiece(i, isRedPlayer);
			if(outcome == 1 && !(isRed^isRedPlayer)) {
				return i;
			}
			else {
				if(outcome != 2) {
					tempBoard.undo();
				}
				outcome = tempBoard.addPiece(i, !isRedPlayer);
				if(outcome == 1 && !(isRed^isRedPlayer)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/*
	 * Outcomes:
	 * 0 = draw or continue
	 * 1 = someone won
	 * 2 = invalid move
	 */
	public void processMove(boolean redPlayer, int outcome) {
		if(!(isRedPlayer^redPlayer)) { // Checks if the AI is the current player when processMove was called
			if(outcome == 1) {
				Node currentTempNode = getCurrentNode();
				Node lastTempNode = getLastNode();
				while(lastTempNode.getParent() != null) {
					lastTempNode.addVictoryChild(currentTempNode);
					currentTempNode = lastTempNode;
					lastTempNode = lastTempNode.getParent();
				}
				totalVictories = currentTempNode.getVictoryChildren().size();
			}
			else if(outcome == 0) {
				getLastNode().addChild(getCurrentNode());
			}
			else if(outcome == 2) {
				lastNode.removeChild(currentNode);
				currentNode = lastNode;
				lastNode = currentNode.getParent();
			}
		}
		else {
			if(outcome == 0) {
				getLastNode().addChild(getCurrentNode());
			}
		}
	}
	
	public void newGame(boolean redPlayer, Tree memory) {
		currentNode = memory.getStartNode();
		if(!isRedPlayer^redPlayer) {
			setCurrentNode(getCurrentNode().getChildren().get(0));
		}
		else {
			setCurrentNode(getCurrentNode().getChildren().get(1));
		}
	}
	
	public int getTotalVictories() {
		return totalVictories;
	}
	
	public boolean isCurrentPlayer(boolean redPlayer) {
		return !(isRedPlayer^redPlayer);
	}
	
	public void setActivation(boolean activate) {
		activated = activate;
	}
	
	public boolean isActivated() {
		return activated;
	}
}
