package conversion;

import sdd.DescriptiveConcept;
import taxonomy.ITaxon;
import tree.TreeNode;
import annotationSchema.jaxb.Structure;

public class TaxonConceptStructureTriple {

	private TreeNode<ITaxon> taxonNode;
	private DescriptiveConcept descriptiveConcept;
	private TreeNode<Structure> structureNode;
	
	/**
	 * A single pair mapping a specific taxon node object to some
	 * descriptive concept associated with that taxon.
	 * @param node
	 * @param dc
	 */
	public TaxonConceptStructureTriple(TreeNode<ITaxon> node, 
			DescriptiveConcept dc, TreeNode<Structure> structureNode) {
		this.taxonNode = node;
		this.descriptiveConcept = dc;
		this.structureNode = structureNode; 
	}

	/**
	 * @return the taxonNode
	 */
	public TreeNode<ITaxon> getTaxonNode() {
		return taxonNode;
	}

	/**
	 * @return the descriptiveConcept
	 */
	public DescriptiveConcept getDescriptiveConcept() {
		return descriptiveConcept;
	}

	/**
	 * @return the structureNode
	 */
	public TreeNode<Structure> getStructureNode() {
		return structureNode;
	}
}
