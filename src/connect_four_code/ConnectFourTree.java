package connect_four_code;

import game_framework.Move;
import game_framework.Node;
import game_framework.Tree;

public class ConnectFourTree extends Tree<Move>{
	private Node<Move> root;
	int victoryMoves;
	
	public ConnectFourTree(Board board) {
		root = new Node<Move>(null, new Move(0, board, 0));
		root.addChild(new Node<Move>(root, new Move(0, board, 1)));
		root.addChild(new Node<Move>(root, new Move(1, board, 1)));
	}
	
	public Node<Move> getRoot() {
		return root;
	}
}
