package conversion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import states.IState;
import states.StateFactory;
import taxonomy.ITaxon;
import taxonomy.TaxonFactory;
import taxonomy.TaxonRank;
import tree.TreeNode;
import annotationSchema.jaxb.Description;
import annotationSchema.jaxb.Relation;
import annotationSchema.jaxb.Statement;
import annotationSchema.jaxb.Structure;
import annotationSchema.jaxb.Treatment;
import dao.FilenameTaxonDao;

public class DescriptionParser {

	private Properties props;
	private JAXBContext annotationContext;
	private String taxonName;
	private TaxonRank taxonRank;
	private FilenameTaxonDao filenameTaxonDao;
	
	/**
	 * Creates a new DescriptionParser object (loads a jaxb context
	 */
	public DescriptionParser(String taxonName, TaxonRank taxonRank) {
		setTaxonName(taxonName);
		setTaxonRank(taxonRank);
		this.props = new DescriptionProperties();
		filenameTaxonDao = new FilenameTaxonDao();
		try {
			annotationContext = JAXBContext.newInstance(annotationSchema.jaxb.ObjectFactory.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the file of the taxon.
	 */
	public ITaxon parseTaxon() {
		ITaxon taxon = TaxonFactory.getTaxonObject(taxonRank, taxonName);
		try {
			Unmarshaller unmarshaller = annotationContext.createUnmarshaller();
			String filename = filenameTaxonDao.getFilenameForDescription(taxonRank, taxonName);
			String path = props.getProperty("input.path") + filename;
			Treatment treatment = (Treatment) unmarshaller.unmarshal(new File(path));
			Description description = treatment.getDescription();
			List<Statement> statementList = description.getStatement();
			buildStructureTree(taxon, statementList);
			for(Statement statement : statementList) {
				taxon.addStatementTextEntry(statement.getId(), statement.getText());
			}
		} catch(JAXBException e) {
			e.printStackTrace();
		}
		taxon.normalizeAllNames();
		return taxon;
	}
	
	/**
	 * Goes through structures and the relations between them to build a structure tree for the taxon.
	 * @param taxon
	 * @param statements
	 */
	private void buildStructureTree(ITaxon taxon, List<Statement> statements) {
		
		for(Statement statement : statements) {
			List<Structure> localStructPool = new ArrayList<Structure>();
			List<Relation> relations = new ArrayList<Relation>();
			for(Object o : statement.getRelationOrStructure()) {
				if(o instanceof Structure) {
					Structure structure = (Structure) o;
					processCharacters(structure);
					//the whole organism structure should come first
					if(structure.getName().equals("whole_organism"))
						taxon.getStructureTree().setRoot(new TreeNode<Structure>(structure));
					else
						localStructPool.add(structure);
				}
				else {	//RELATIONS
					relations.add((Relation) o);
				}
			}	//end relation/structure list
			if(relations.isEmpty())
				taxon.getStructureTree().getRoot().addChildren(localStructPool);
			else
				placeStructuresAccordingToRelations(taxon, localStructPool, relations);
		}
	}

	/**
	 * Process each character into a map entry character name->state for the structure's
	 * character-state map.
	 * @param structure
	 */
	@SuppressWarnings("rawtypes")
	private void processCharacters(Structure structure) {
		List<annotationSchema.jaxb.Character> characters = structure.getCharacter();
		for(annotationSchema.jaxb.Character c : characters) {
			IState state = StateFactory.getStateObject(c);
			structure.addMapping(c.getName(), state);
			String modifier = c.getModifier();
			if(modifier != null)
				structure.addModifierToCharName(c.getName(), modifier);
		}
	}

	/**
	 * Place structures into structure tree in a manner consistent
	 * with the given relations between the structures.
	 * @param taxon
	 * @param localStructPool
	 * @param relations
	 */
	private void placeStructuresAccordingToRelations(ITaxon taxon,
			List<Structure> localStructPool, List<Relation> relations) {
		List<TreeNode<Structure>> branches = new ArrayList<TreeNode<Structure>>();
		for(Relation relation : relations) {
			Structure to = (Structure) relation.getTo().get(0);
			Structure from = (Structure) relation.getFrom().get(0);
			TreeNode<Structure> inTree = taxon.getStructureTree().contains(to);
			if(inTree != null && relation.getName().equals("part_of"))
				inTree.addChild(new TreeNode<Structure>(from));
			else if(relation.getName().equals("part_of")){
				TreeNode<Structure> branch = containsElement(branches, to);
				if(branch != null)
					branch.addChild(new TreeNode<Structure>(from));
				else {
					branch = new TreeNode<Structure>(to);
					branch.addChild(new TreeNode<Structure>(from));
					branches.add(branch);
				}
			}
			else {
				taxon.addRelation(relation);
				TreeNode<Structure> containsFrom = containsElement(branches, from);
				if(containsFrom == null)
					branches.add(new TreeNode<Structure>(from));
				TreeNode<Structure> containsTo = containsElement(branches, to);
				if(containsTo == null)
					branches.add(new TreeNode<Structure>(to));
			}
		}
		for(TreeNode<Structure> branch : branches)
			taxon.getStructureTree().getRoot().addChild(branch);		
	}
	
	/**
	 * Looks through a list of nodes for a node having the given Structure as the element and
	 * returns that node if present, null otherwise.
	 * @param nodes
	 * @param parent
	 * @return
	 */
	private TreeNode<Structure> containsElement(List<TreeNode<Structure>> nodes, Structure parent) {
		for(TreeNode<Structure> node : nodes) {
			if(node.getElement().equals(parent))
				return node;
		}
		return null;
	}

	/**
	 * @param taxonName the taxon name to set
	 */
	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}

	/**
	 * @return the taxon name.
	 */
	public String getTaxonName() {
		return taxonName;
	}

	public TaxonRank getTaxonRank() {
		return taxonRank;
	}

	public void setTaxonRank(TaxonRank taxonRank) {
		this.taxonRank = taxonRank;
	}
}
