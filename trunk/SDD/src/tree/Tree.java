package tree;

import java.util.Deque;
import java.util.LinkedList;

public class Tree<T> {
	
	private TreeNode<T> root;
	
	/**
	 * Default no-arg constructor.  Creates an empty tree.
	 */
	public Tree() {
		
	}
	
	/**
	 * Creates a new Tree with the given root.
	 * @param root
	 */
	public Tree(TreeNode<T> root) {
		this.root = root;
	}

	/**
	 * @return the root
	 */
	public TreeNode<T> getRoot() {
		return root;
	}
	
	/**
	 * Set the root of this tree.
	 * @param root
	 */
	public void setRoot(TreeNode<T> root) {
		this.root = root;
	}
	
	/**
	 * Looks for an element in this tree, and returns it if found.  Returns null otherwise.
	 * @param element The element to look for.
	 * @return
	 */
	public T contains(T element) {
		Deque<TreeNode<T>> queue = new LinkedList<TreeNode<T>>();
		queue.add(root);
		while(!queue.isEmpty()) {
			TreeNode<T> node = queue.poll();
			if(node.getElement().equals(element))
				return node.getElement();
			for(TreeNode<T> child : node.getChildren())
				queue.add(child);
		}
		return null;
	}
	
	/**
	 * Returns true if this tree is empty, false otherwise.
	 * @return
	 */
	public boolean isEmpty() {
		return (this.root == null);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Tree)) {
			return false;
		}
		Tree other = (Tree) obj;
		if (root == null) {
			if (other.root != null) {
				return false;
			}
		} else if (!root.equals(other.root)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tree [root=" + root + "]";
	}
}
