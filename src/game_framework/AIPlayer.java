/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_framework;

/**
 *
 * @author Froze
 */
public abstract class AIPlayer extends Player {
    private Node<Move> currentNode;
    private Node<Move> lastNode;
    
    public AIPlayer() {
        super();
    }
    
    public abstract Node<Move> getCurrentNode();
    public abstract void setCurrentNode(Node<Move> a_node);
    public abstract Node<Move> getLastNode();
    public abstract void nextUnknownNode();
    public abstract void nextKnownNode();
    public abstract int logicalPosition(boolean isFirstPlayerTurn);
    
    @Override
    public boolean isAI() {
        return true;
    }
}
