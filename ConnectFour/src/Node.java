/**
 *
 * @author isaiah.cruz
 */

import java.util.ArrayList;

public class Node{
		private Node parent;
		private Node lastChild;
		private Board board;
		private int position;
		private ArrayList<Node> children;
		private ArrayList<Node> victoryChildren;
		private ArrayList<Integer> uncheckedPositions;
		private final int label;
		
		private final Tree SOURCE;
		
		public Node(Node a_parent, int a_position, Board a_board, Tree a_tree) {
			parent = a_parent;
			position = a_position;
			board = a_board;
			
			children = new ArrayList<Node>();
			victoryChildren = new ArrayList<Node>();
			uncheckedPositions = new ArrayList<Integer>();
			
			for(int i = 0; i < board.getXSIZE(); i++) {
				uncheckedPositions.add(i);
			}
			
			SOURCE = a_tree;
			label = SOURCE.count;
		}
		public void addChild(int position) {
			Node child = new Node(this, position, board, SOURCE);
			SOURCE.count++;
			children.add(child);
			lastChild = child;
			uncheckedPositions.remove((Integer) position);
		}
		public void addChild(Node node) {
			children.add(node);
			lastChild = node;
			uncheckedPositions.remove((Integer)node.getPosition());
		}
		public void addVictoryChild(Node node) {
			victoryChildren.add(node);
		}
		public void removeChild(Node node) {
			children.remove(node);
		}
		public int getPosition() {
			return position;
		}
		public Node getParent() {
			return parent;
		}
		public Node getLastChild() {
			return lastChild;
		}
		public ArrayList<Node> getChildren() {
			return children;
		}
		public ArrayList<Node> getVictoryChildren() {
			return victoryChildren;
		}
		public ArrayList<Integer> getUncheckedPositions() {
			return uncheckedPositions;
		}
		public int getLabel() {
			return label;
		}
	}