package taxonomy;

import java.util.Map;
import java.util.TreeMap;

import states.IState;
import tree.Tree;
import annotationSchema.jaxb.Character;
import annotationSchema.jaxb.Structure;

/**
 * Base class for all Taxa.  Every taxa has a scientific name, a rank (Family, Genus, species etc.), 
 * a Tree of sub-taxa, a mapping of characters to states at this taxonomical rank, and a tree describing
 * the structure of the organism at this taxonomical rank.
 * @author Alex
 *
 */
public class TaxonBase implements ITaxon {

	protected String name;
	protected TaxonRank taxonRank;
	protected Map<Character, IState> charMap;
	protected Tree<Structure> structureTree;
	
	/**
	 * Create a new Taxon with a given rank.
	 * @param rank
	 */
	public TaxonBase(TaxonRank rank) {
		this.taxonRank = rank;
		this.charMap = new TreeMap<Character, IState>();
		this.structureTree = new Tree<Structure>();
	}
	
	/**
	 * Create a new Taxon.
	 * @param rank The taxonomical rank.
	 * @param name The name of the taxon.
	 */
	public TaxonBase(TaxonRank rank, String name) {
		this.taxonRank = rank;
		this.name = name;
		this.charMap = new TreeMap<Character, IState>();
		this.structureTree = new Tree<Structure>();
	}
	
	/* (non-Javadoc)
	 * @see taxonomy.ITaxon#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see taxonomy.ITaxon#getCharMap()
	 */
	@Override
	public Map<Character, IState> getCharMap() {
		return this.charMap;
	}

	@Override
	public Tree<Structure> getStructureTree() {
		return this.structureTree;
	}

	@Override
	public TaxonRank getTaxonRank() {
		return this.taxonRank;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((charMap == null) ? 0 : charMap.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((structureTree == null) ? 0 : structureTree.hashCode());
		result = prime * result
				+ ((taxonRank == null) ? 0 : taxonRank.hashCode());
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
		if (!(obj instanceof TaxonBase)) {
			return false;
		}
		TaxonBase other = (TaxonBase) obj;
		if (charMap == null) {
			if (other.charMap != null) {
				return false;
			}
		} else if (!charMap.equals(other.charMap)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (structureTree == null) {
			if (other.structureTree != null) {
				return false;
			}
		} else if (!structureTree.equals(other.structureTree)) {
			return false;
		}
		if (taxonRank != other.taxonRank) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + "[name=" + name
				+ ", taxonRank=" + taxonRank + ", charMap=" + charMap
				+ ", \nstructureTree=" + structureTree + "]";
	}
	
}
