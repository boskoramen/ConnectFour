package game_framework;

public class ConnectFourTree extends Tree<Move>{
	private Node<Move> root;
	
	public ConnectFourTree(Board board) {
		root = new Node<Move>(null, null);
		root.addChild(new Node<Move>(root, new Move(0, board, 0)));
		root.addChild(new Node<Move>(root, new Move(1, board, 0)));
	}
	
	public Node<Move> getRoot() {
		return root;
	}
}
