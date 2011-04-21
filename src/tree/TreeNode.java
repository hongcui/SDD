package tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A node of a tree.
 * @author Alex
 *
 * @param <T>
 */
public class TreeNode<T> {

	private T element;
	
	private List<TreeNode<T>> children;
	
	/**
	 * Create a new TreeNode with no element.
	 */
	public TreeNode() {
		this.children = new ArrayList<TreeNode<T>>();
	}
	
	/**
	 * Create a new TreeNode with a given element.
	 * @param element
	 */
	public TreeNode(T element) {
		this.element = element;
		this.children = new ArrayList<TreeNode<T>>();
	}
	
	/**
	 * Add a child node to this node.
	 * @param child
	 */
	public void addChild(TreeNode<T> child) {
		this.children.add(child);
	}
	
	/**
	 * Add a collection of children to this node.
	 * @param children
	 */
	public void addChildren(Collection<T> children) {
		for(T child : children)
			this.children.add(new TreeNode<T>(child));
	}

	/**
	 * @return the element
	 */
	public T getElement() {
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(T element) {
		this.element = element;
	}

	/**
	 * @return the children
	 */
	public List<TreeNode<T>> getChildren() {
		return children;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((element == null) ? 0 : element.hashCode());
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
		if (!(obj instanceof TreeNode)) {
			return false;
		}
		TreeNode other = (TreeNode) obj;
		if (children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!children.equals(other.children)) {
			return false;
		}
		if (element == null) {
			if (other.element != null) {
				return false;
			}
		} else if (!element.equals(other.element)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s =  "TreeNode [element=" + element + "[children=";
		for(TreeNode<T> child : children)
			s += ("\n\t" + child);
		s += "]";
		return s;
	} 
}
