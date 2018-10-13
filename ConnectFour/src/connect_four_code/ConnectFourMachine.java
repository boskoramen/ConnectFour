/**
 *
 * @author isaiah.cruz
 */

package connect_four_code;

import game_framework.*;

public class ConnectFourMachine {
	
	private boolean isFirstPlayer;
	private Node<Move> currentNode;
	private Node<Move> lastNode;
	private boolean activated;
	private int totalVictories;
	private Board board;
	
	public ConnectFourMachine(Board a_board, boolean redPlayer) {
		isFirstPlayer = redPlayer;
		totalVictories = 0;
		board = a_board;
	}
	
	public Node<Move> getCurrentNode() {
		return currentNode;
	}
	
	public void setCurrentNode(Node<Move> a_node) {
		lastNode = currentNode;
		currentNode = a_node;
	}
	
	public Node<Move> getLastNode() {
		return lastNode;
	}
	
	public void nextUnknownNode() {
		int range = currentNode.getValue().getUncheckedMoves().size();
		int nextPos = (int)(Math.random() * range);
		
		Move nextMove = new Move(currentNode.getValue().getUncheckedMoves().get(nextPos), board, currentNode.getValue().getTier() + 1);
		Node<Move> nextNode = new Node<Move>(currentNode, nextMove);
		currentNode.addChild(nextNode);
		setCurrentNode(currentNode.getChildren().get(currentNode.getChildren().size() - 1)); //Sets currentNode to the last added child of the last currentNode
	}
	
	public void nextKnownNode() {
		if(currentNode.getValue().getVictoryMoves().size() > 0) {
			int range = currentNode.getValue().getVictoryMoves().size();
			int nextPos = (int)(Math.random() * range);
			int victoryPos = currentNode.getValue().getVictoryMoves().get(nextPos);
			int moveIndex = 0;
			for(int i = 0; i < currentNode.getChildren().size(); i++) {
				if(currentNode.getChildren().get(i).getValue().getPosition() == victoryPos) {
					moveIndex = i;
					break;
				}
			}
			
			setCurrentNode(currentNode.getChildren().get(moveIndex));
		}
		else {
			int range = currentNode.getChildren().size();
			int nextPos = (int)(Math.random() * range);
			setCurrentNode(currentNode.getChildren().get(nextPos));
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
	
	public void nextNode(boolean toWin, boolean isFirstPlayerTurn) {
		int logicalPos = logicalPosition(isFirstPlayerTurn);
		if(logicalPos == -1) {
			if(toWin) {
				if(currentNode.getValue().getVictoryMoves().size() + currentNode.getChildren().size() > 0) {
					nextKnownNode();
				}
				else {
					nextUnknownNode();
				}
			}
			else {
				if(currentNode.getValue().getUncheckedMoves().size() > 0) {
					nextUnknownNode();
				}
				else {
					System.out.println("not random");
					nextKnownNode();
				}
			}
		}
		else {
			System.out.println("not logical pos");
			Node<Move> nextNode = new Node<Move>(currentNode, new Move(logicalPos, currentNode.getValue()));
			currentNode.addChild(nextNode);
			setCurrentNode(currentNode.getChildren().get(currentNode.getChildren().size() - 1)); //Sets currentNode to the last added child of the last currentNode
		}
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
				Node<Move> currentTempNode = currentNode;
				Node<Move> lastTempNode = getLastNode();
				while(lastTempNode.getParent() != null) {
					lastTempNode.getValue().addVictoryMove(currentTempNode.getValue().getPosition());
					currentTempNode = lastTempNode;
					lastTempNode = lastTempNode.getParent();
				}
				totalVictories = currentTempNode.getValue().getVictoryMoves().size();
			}
			else if(!outcome) {
				getLastNode().addChild(currentNode);
			}
		}
		else {
			if(!outcome) {
				getLastNode().addChild(currentNode);
			}
		}
	}
	
	public void newGame(boolean isFirstPlayerTurn, Tree<Move> memory) {
		currentNode = memory.getRoot();
		if(!isFirstPlayer^isFirstPlayerTurn) {
			setCurrentNode(currentNode.getChildren().get(0));
		}
		else {
			setCurrentNode(currentNode.getChildren().get(1));
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
