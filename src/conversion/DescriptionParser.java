package conversion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import taxonomy.ITaxon;
import taxonomy.TaxonFactory;
import taxonomy.TaxonRank;
import tree.Tree;
import tree.TreeNode;
import annotationSchema.jaxb.Description;
import annotationSchema.jaxb.Relation;
import annotationSchema.jaxb.Statement;
import annotationSchema.jaxb.Structure;
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
	 * Parses the filename of the taxon.
	 */
	public ITaxon parseTaxon() {
		ITaxon taxon = TaxonFactory.getTaxonObject(taxonRank, taxonName);
		try {
			Unmarshaller unmarshaller = annotationContext.createUnmarshaller();
			String filename = filenameTaxonDao.getFilenameForDescription(taxonRank, taxonName);
			String path = props.getProperty("input.path") + filename;
			Description description = (Description) unmarshaller.unmarshal(new File(path));
			List<Statement> statementList = description.getStatement();
			buildStructureTree(taxon, statementList);
		} catch(JAXBException e) {
			e.printStackTrace();
		}
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

	private void placeStructuresAccordingToRelations(ITaxon taxon,
			List<Structure> localStructPool, List<Relation> relations) {
		List<TreeNode<Structure>> branches = new ArrayList<TreeNode<Structure>>();
		for(Relation relation : relations) {
			Structure to = (Structure) relation.getTo().get(0);
			int fromIndex = localStructPool.indexOf(to);
			Structure from = (Structure) relation.getFrom().get(0);
			int toIndex = localStructPool.indexOf(from);
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