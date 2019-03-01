/**
 *
 * @author isaiah.cruz
 */

package game_framework;

public abstract class Tree<E> {
		
		private Node<E> root;
		
		public Tree() {
			root = new Node<E> (null, null);
		}
		public Tree(E value) {
			root = new Node<E> (null, value);
		}
		
		public Node<E> getRoot() {
			return root;
		}
	}
