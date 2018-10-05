/**
 *
 * @author isaiah.cruz
 */

public class Tree {
		
		private Node startNode;
		int count;
		
		public Tree(Board a_board) {
			count = 0;
			startNode = new Node(null, 0, a_board, this);
			startNode.addChild(0);
			startNode.addChild(1);
		}
		
		public Node getStartNode() {
			return startNode;
		}
	}
