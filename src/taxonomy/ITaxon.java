/**
 * 
 */
package taxonomy;

import java.util.Map;

import states.IState;
import tree.Tree;
import annotationSchema.jaxb.Structure;

/**
 * All level of taxa implement this base interface.
 * @author Alex
 *
 */
public interface ITaxon {

	/**
	 * Get the name of this taxon.
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the character-state map for this taxon.
	 * @return
	 */
	public Map<annotationSchema.jaxb.Character, IState> getCharMap();
	
	/**
	 * @return The structure tree of this taxon.
	 */
	public Tree<Structure> getStructureTree();
	
	/**
	 * @return The rank of this taxon.
	 */
	public TaxonRank getTaxonRank();
}
