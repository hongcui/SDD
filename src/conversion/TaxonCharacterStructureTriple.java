package conversion;

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

}
