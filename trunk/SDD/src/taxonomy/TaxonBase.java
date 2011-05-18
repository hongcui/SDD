package taxonomy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import states.IState;
import tree.Tree;
import tree.TreeNode;
import annotationSchema.jaxb.Character;
import annotationSchema.jaxb.Relation;
import annotationSchema.jaxb.Structure;
import dao.SingularPluralDao;

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
	protected Map<Structure, Map<Character, IState>> charMap;
	protected Tree<Structure> structureTree;
	protected List<Relation> relations;
	protected Map<String, String> statementTextMap;
	protected SingularPluralDao singularPluralDao;
	
	/**
	 * Create a new Taxon with a given rank.
	 * @param rank
	 */
	public TaxonBase(TaxonRank rank) {
		this.taxonRank = rank;
		this.charMap = new TreeMap<Structure, Map<Character, IState>>();
		this.structureTree = new Tree<Structure>();
		this.relations = new ArrayList<Relation>();
		this.singularPluralDao = new SingularPluralDao();
		this.statementTextMap = new TreeMap<String, String>();
	}
	
	/**
	 * Create a new Taxon.
	 * @param rank The taxonomical rank.
	 * @param name The name of the taxon.
	 */
	public TaxonBase(TaxonRank rank, String name) {
		this.taxonRank = rank;
		this.name = name;
		this.charMap = new TreeMap<Structure, Map<Character, IState>>();
		this.structureTree = new Tree<Structure>();
		this.relations = new ArrayList<Relation>();
		this.singularPluralDao = new SingularPluralDao();
		this.statementTextMap = new TreeMap<String, String>();
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
	public Map<Structure, Map<Character, IState>> getCharMap() {
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
	
	/**
	 * Adds a relation to the list of relations between structures for this taxon.
	 * @param r The relation to add.
	 */
	@Override
	public void addRelation(Relation r) {
		this.relations.add(r);
	}

	/**
	 * Return the list of relations between structures for this taxon.
	 * @return
	 */
	@Override
	public List<Relation> getRelations() {
		return relations;
	}
	
	/*
	 * (non-Javadoc)
	 * @see taxonomy.ITaxon#normalizeAllNames()
	 */
	@Override
	public void normalizeAllNames() {
		normalizeRelationNames();
		normalizeStructureNames();
	}
	
	/*
	 * (non-Javadoc)
	 * @see taxonomy.ITaxon#getStatementTextMap()
	 */
	@Override
	public Map<String, String> getStatementTextMap() {
		return this.statementTextMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see taxonomy.ITaxon#addStatementTextEntry(java.lang.String, java.lang.String)
	 */
	@Override
	public void addStatementTextEntry(String statementId, String text) {
		this.statementTextMap.put(statementId, text);
	}
	
	private void normalizeCharacterNames(List<annotationSchema.jaxb.Character> chars) {
		for(Character c : chars) {
			String normalized = normalizeString(c.getName());
			List<String> singular = singularPluralDao.getSingularForPlural(normalized);
			if(!singular.isEmpty())
				normalized = singular.get(0);
			c.setName(normalized);
		}
	}

	private void normalizeRelationNames() {
		for(Relation r: this.relations) {
			String normalized = normalizeString(r.getName());
			r.setName(normalized);
		}
	}
	
	private void normalizeStructureNames() {
		Iterator<TreeNode<Structure>> iter = this.structureTree.iterator();
		while(iter.hasNext()) {
			Structure s = iter.next().getElement();
			String normalized = normalizeString(s.getName());
			s.setName(normalized);
			normalizeCharacterNames(s.getCharacter());
			normalizeCharacterNameMaps(s.getCharStateMap(), s.getModifierMap());
		}
	}

	@SuppressWarnings("rawtypes")
	private void normalizeCharacterNameMaps(Map<String, IState> charStateMap,
			Map<String, String> modifierMap) {
		for(String s : charStateMap.keySet()) {
			String normalized = normalizeString(s);
			List<String> singular = singularPluralDao.getSingularForPlural(normalized);
			if(!singular.isEmpty())
				normalized = singular.get(0);
			s = normalized;
		}
		for(String s : modifierMap.keySet()) {
			String normalized = normalizeString(s);
			List<String> singular = singularPluralDao.getSingularForPlural(normalized);
			if(!singular.isEmpty())
				normalized = singular.get(0);
			s = normalized;
		}
		
	}

	private String normalizeString(String name) {
		String normalized = name.replace("{", "");
		normalized = normalized.replace("}", "");
		normalized = normalized.replace(" ", "_");
		return normalized;
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
