/**
 * 
 */
package taxonomy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import states.IState;
import tree.Tree;
import annotationSchema.jaxb.Relation;
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
	public Map<Structure, Map<annotationSchema.jaxb.Character, IState>> getCharMap();
	
	/**
	 * @return The structure tree of this taxon.
	 */
	public Tree<Structure> getStructureTree();
	
	/**
	 * @return The rank of this taxon.
	 */
	public TaxonRank getTaxonRank();
	
	/**
	 * Add a relation between structures to the list of relations for this taxon.
	 * @param r The relation to add.
	 */
	public void addRelation(Relation r);
	
	/**
	 * Gets the list of relations between structures for this taxon.
	 * @return
	 */
	public List<Relation> getRelations();
	
	/**
	 * Normalize the names of all the structures, relations and characters
	 * of this taxon.
	 */
	public void normalizeAllNames(HashMap sigPluMap);
	
	/**
	 * Returns a map from statement ids to the text contained therein.
	 * @return
	 */
	public Map<String, String> getStatementTextMap();
	
	/**
	 * Adds a statementId->text entry to the map for this taxon.
	 * @param statementId
	 * @param text
	 */
	public void addStatementTextEntry(String statementId, String text);
}
