package conversion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
import annotationSchema.jaxb.ObjectFactory;
import annotationSchema.jaxb.Relation;
import annotationSchema.jaxb.Statement;
import annotationSchema.jaxb.Structure;
import annotationSchema.jaxb.Treatment;
import dao.FilenameTaxonDao;

public class DescriptionParser {

	private static final String WHOLE_ORGANISM_STRUCTURE_ID = "whole_organism";
	private Properties props;
	private JAXBContext annotationContext;
	private String taxonName;
	private TaxonRank taxonRank;
	private FilenameTaxonDao filenameTaxonDao;
	private String filename;
	private HashMap sigPluMap;
	
	/**
	 * Creates a new DescriptionParser object (loads a jaxb context
	 */
	public DescriptionParser(String taxonName, TaxonRank taxonRank, FilenameTaxonDao dao,HashMap sigPluMap) {
		setTaxonName(taxonName);
		setTaxonRank(taxonRank);
		this.props = new DescriptionProperties();
	//	filenameTaxonDao = new FilenameTaxonDao();
		this.filenameTaxonDao = dao;
		this.sigPluMap = sigPluMap;
		try {
			annotationContext = JAXBContext.newInstance(annotationSchema.jaxb.ObjectFactory.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/**added by Jing Liu
	 * Creates a new DescriptionParser object (loads a jaxb context
	 */
	public DescriptionParser(String taxonName, TaxonRank taxonRank, String filename, FilenameTaxonDao dao,HashMap sigPluMap) {
		setTaxonName(taxonName);
		setTaxonRank(taxonRank);
		setFilename(filename);
		this.props = new DescriptionProperties();
	//	filenameTaxonDao = new FilenameTaxonDao();
		filenameTaxonDao = dao;
		this.filenameTaxonDao = dao;
		this.sigPluMap = sigPluMap;
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
			this.filename = filenameTaxonDao.getFilenameForDescription(taxonRank, taxonName);
			if (filename != "") {   //added by Jing Liu
				String path = props.getProperty("input.path") + filename;
				Treatment treatment = (Treatment) unmarshaller
						.unmarshal(new File(path));
				Description description = treatment.getDescription();
				if (description != null) {
					List<Statement> statementList = description.getStatement();
					buildStructureTree(taxon, statementList);
					for (Statement statement : statementList) {
						taxon.addStatementTextEntry(statement.getId(),
								statement.getText());
					}
					taxon.normalizeAllNames(sigPluMap);
				}else{
					return null;
				}

			}
		} catch(JAXBException e) {
			e.printStackTrace();
		}
		return taxon;
	}
	
	/** added by Jing Liu
	 * Parses the file of the taxon.
	 */
	public ITaxon parseTaxon(String filename) {
		ITaxon taxon = TaxonFactory.getTaxonObject(taxonRank, taxonName);
		try {
			Unmarshaller unmarshaller = annotationContext.createUnmarshaller();
			//this.filename = filenameTaxonDao.getFilenameForDescription(taxonRank, taxonName);
			if (filename != "") {   //added by Jing Liu
				String path = props.getProperty("input.path") + filename;
				Treatment treatment = (Treatment) unmarshaller
						.unmarshal(new File(path));
				Description description = treatment.getDescription();
				if (description != null) {
					List<Statement> statementList = description.getStatement();
					if (statementList.size() != 0) {
						buildStructureTree(taxon, statementList);
						for (Statement statement : statementList) {
							taxon.addStatementTextEntry(statement.getId(),
									statement.getText());
						}
						taxon.normalizeAllNames(sigPluMap);
					} //else {
					//	return null;
				//	}
			//	}else{
			//		return null;
				}

			}
		} catch(JAXBException e) {
			e.printStackTrace();
		}
		return taxon;
	}
	
	
	/**added by Jing Liu
	 * Parses the file of the taxon.
	 */
	public ITaxon parsePeudoTaxon() {
		ITaxon taxon = TaxonFactory.getTaxonObject(taxonRank, taxonName);
		return taxon;
	}
	
	/**
	 * Goes through structures and the relations between them to build a structure tree for the taxon.
	 * @param taxon
	 * @param statements
	 */
	private void buildStructureTree(ITaxon taxon, List<Statement> statements) {
		
		for(Statement statement : statements) {
			if (!statement.getRelationOrStructure().isEmpty()) {
				List<Structure> localStructPool = new ArrayList<Structure>();
				List<Relation> relations = new ArrayList<Relation>();
				for (Object o : statement.getRelationOrStructure()) {
					if (o instanceof Structure) {
						Structure structure = (Structure) o;
						structure.setStatementId(statement.getId());
						processCharacters(structure);
						//the whole organism structure should come first
						if (structure.getName().equals(WHOLE_ORGANISM_STRUCTURE_ID))
							taxon.getStructureTree().setRoot(
									new TreeNode<Structure>(structure));
						else
							localStructPool.add(structure);
					} else { //RELATIONS
						relations.add((Relation) o);
					}
				} //end relation/structure list
				if (relations.isEmpty()) {
					checkForNullRoot(taxon);
					try {
						taxon.getStructureTree().getRoot()
								.addChildren(localStructPool);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(taxon.getStructureTree().getRoot());
						System.out.println(localStructPool.toString());
					}
				}
				else
					placeStructuresAccordingToRelations(taxon, localStructPool,
							relations);
			}
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
		//first, make sure this taxon has a non-null root.
		checkForNullRoot(taxon);
		List<TreeNode<Structure>> branches = new ArrayList<TreeNode<Structure>>();
		for(Relation relation : relations) {
			try {
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
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(relation.toString());
			}
		}
		for(TreeNode<Structure> branch : branches)
			taxon.getStructureTree().getRoot().addChild(branch);	
		for(Structure s : localStructPool) {
			TreeNode<Structure> inTree = taxon.getStructureTree().contains(s);
			if(inTree == null)
				taxon.getStructureTree().getRoot().addChild(new TreeNode<Structure>(s));
		}
	}
	
	/**
	 * Checks if this taxon has a null root.  If it does, we create a kind of
	 * "dummy" root node, which has a Structure as an element, named according
	 * to WHOLE_ORGANISM_STRUCTURE_ID.
	 * @param taxon
	 */
	private void checkForNullRoot(ITaxon taxon) {
		if (taxon.getStructureTree().getRoot() == null) {
			ObjectFactory fact = new ObjectFactory();
			Structure structure = fact.createStructure();
			structure.setName(WHOLE_ORGANISM_STRUCTURE_ID);
			structure.setId("dummy_id");
			TreeNode<Structure> node = new TreeNode<Structure>(structure);
			taxon.getStructureTree().setRoot(node);
		}
		
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

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
