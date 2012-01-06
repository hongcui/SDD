package conversion.datapassing;

import com.sun.net.httpserver.Filter.Chain;

import sdd.AbstractCharacterDefinition;
import taxonomy.ITaxon;
import tree.TreeNode;
import annotationSchema.jaxb.Structure;

/**
 * Triple used for passing data between CharacterSetHandler and
 * CharacterTreeHandler.
 * @author Alex
 *
 */
public class TaxonCharacterStructureTriple {
	
	private TreeNode<ITaxon> taxonNode;
	private AbstractCharacterDefinition character;
	private TreeNode<Structure> structureNode;
	
	/**
	 * Creates a new triple containing a taxon node, character, and
	 * structure node.  Used for passing data between CharacterSetHandler
	 * and CharacterTreeHandler.
	 * @param taxonNode
	 * @param character
	 * @param structureNode
	 */
	public TaxonCharacterStructureTriple(TreeNode<ITaxon> taxonNode,
			AbstractCharacterDefinition character,
			TreeNode<Structure> structureNode) {
		this.taxonNode = taxonNode;
		this.character = character;
		this.structureNode = structureNode;
	}

	/**
	 * @return the taxonNode
	 */
	public TreeNode<ITaxon> getTaxonNode() {
		return taxonNode;
	}

	/**
	 * @return the character
	 */
	public AbstractCharacterDefinition getCharacter() {
		return character;
	}

	/**
	 * @return the structureNode
	 */
	public TreeNode<Structure> getStructureNode() {
		return structureNode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((character == null) ? 0 : character.hashCode());
		result = prime * result
				+ ((structureNode == null) ? 0 : structureNode.hashCode());
		result = prime * result
				+ ((taxonNode == null) ? 0 : taxonNode.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TaxonCharacterStructureTriple)) {
			return false;
		}
		TaxonCharacterStructureTriple other = (TaxonCharacterStructureTriple) obj;
		if (character == null) {
			if (other.character != null) {
				return false;
			}
		} else if (!character.equals(other.character)) {
			return false;
		}
		if (structureNode == null) {
			if (other.structureNode != null) {
				return false;
			}
		} else if (!structureNode.equals(other.structureNode)) {
			return false;
		}
		if (taxonNode == null) {
			if (other.taxonNode != null) {
				return false;
			}
		} else if (!taxonNode.equals(other.taxonNode)) {
			return false;
		}
		return true;
	}

}
