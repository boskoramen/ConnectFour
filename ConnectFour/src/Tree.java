/**
 *
 * @author isaiah.cruz
 */

public class Tree {
		
		private Node startNode;
		
		public Tree(Board a_board) {
			startNode = new Node(null, 0, a_board);
			startNode.addChild(0);
			startNode.addChild(1);
		}
		
		public Node getStartNode() {
			return startNode;
		}
	}
