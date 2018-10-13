/**
 *
 * @author isaiah.cruz
 */

package game_framework;

import java.util.ArrayList;

public class Node<E> {
	private Node<E> parent;
	private final E value;
	private ArrayList<Node<E>> children;
	
	public Node (Node<E> node, E val) {
		parent = node;
		value = val;
		children = new ArrayList<Node<E>>();
	}
	
	public boolean addChild(Node<E> node) {
		if(!children.contains(node)) {
			children.add(node);
			return true;
		}
		return false;
	}
	public boolean removeChild(Node<E> node) {
		if(!children.contains(node)) {
			return false;
		}
		children.remove(node);
		return true;
	}
	public Node<E> getParent() {
		return parent;
	}
	public E getValue() {
		return value;
	}
	public int getIndex() {
		return parent.getChildren().indexOf(this);
	}
	public ArrayList<Node<E>> getChildren() {
		return children;
	}
}
