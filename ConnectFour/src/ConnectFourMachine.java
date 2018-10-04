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
	
	public ConnectFourMachine(Board a_board, boolean redPlayer, Tree a_tree) {
		isRedPlayer = redPlayer;
		totalVictories = 0;
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
	
	public void nextNode(boolean toWin) {
		if(!toWin) {
			nextNode();
		}
		else {
			if(getCurrentNode().getVictoryChildren().size() + getCurrentNode().getChildren().size() > 0) {
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
			else {
				int range = getCurrentNode().getUncheckedPositions().size();
				int nextPos = (int)(Math.random() * range);
				getCurrentNode().addChild(getCurrentNode().getUncheckedPositions().get(nextPos));
				setCurrentNode(getCurrentNode().getLastChild());
			}
		}
	}
	
	public void nextNode() {
		if(getCurrentNode().getUncheckedPositions().size() > 0) {
			int range = getCurrentNode().getUncheckedPositions().size();
			int nextPos = (int)(Math.random() * range);
			getCurrentNode().addChild(getCurrentNode().getUncheckedPositions().get(nextPos));
			setCurrentNode(getCurrentNode().getLastChild());
		}
		else {
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
	}
	
	/*
	 * Outcomes:
	 * 0 = draw or continue
	 * 1 = someone won
	 * 2 = invalid move
	 */
	public void processMove(boolean redPlayer, int outcome) {
		if(outcome == 2) {
			currentNode = lastNode;
			lastNode = currentNode.getParent();
		}
		else {
			if(!isRedPlayer^redPlayer) { // Checks if the AI is the current player when processMove was called
				if(outcome == 1) {
					Node currentTempNode = getCurrentNode();
					Node lastTempNode = getLastNode();
					while(lastTempNode.getParent() != null) {
						lastTempNode.addVictoryChild(currentTempNode);
						currentTempNode = lastTempNode;
						lastTempNode = lastTempNode.getParent();
					}
					System.out.println("Victory size:" + currentTempNode.getVictoryChildren().size());
					totalVictories++;
				}
				else if(outcome == 0) {
					getLastNode().addChild(getCurrentNode());
				}
			}
			else {
				if(outcome == 0) {
					getLastNode().addChild(getCurrentNode());
				}
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
