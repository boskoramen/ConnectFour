/**
 *
 * @author isaiah.cruz
 */

package connect_four_code;

import game_framework.*;

public class ConnectFourMachine extends AIPlayer {
	
    private final boolean isFirstPlayer;
    private Node<Move> currentNode;
    private Node<Move> lastNode;
    private final Board board;

    public ConnectFourMachine(Board a_board, boolean redPlayer) {
        isFirstPlayer = redPlayer;
        board = a_board;
    }
    
    @Override
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
        Node<Move> nextNode = new Node<>(currentNode, nextMove);
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

    public int logicalPosition() {
            int losePosition = -1;
            for(int i = 0; i < board.getXSIZE(); i++) {
                Board tempBoard = board.clone(false);
                if(tempBoard.addPiece(i, isFirstPlayerTurn)) { 
                    boolean hasWinner = tempBoard.checkWinner(isFirstPlayerTurn);
                    if(hasWinner) {
                        if(!(isFirstPlayerTurn^isFirstPlayer)) {
                            return i;
                        }
                        else {
                            losePosition = i;
                        }
                    }
                    else {
                        tempBoard.undo();
                        tempBoard.addPiece(i, !isFirstPlayerTurn);
                        hasWinner = tempBoard.checkWinner(!isFirstPlayerTurn);
                        if(hasWinner) {
                            if(!(isFirstPlayerTurn^isFirstPlayer)) {
                                losePosition = i;
                            }
                            else {
                                return i;
                            }
                        }
                    }
                }
            }
        return losePosition;
    }

    public void nextNode(boolean testPhase) {
            int logicalPos = logicalPosition(isFirstPlayerTurn);
            if(logicalPos == -1) {
                    if(testPhase) {
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
                                    nextKnownNode();
                            }
                    }
            }
            else {
                    Node<Move> nextNode = new Node<>(currentNode, new Move(logicalPos, currentNode.getValue()));
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
}
