package conversion.datapassing;

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((descriptiveConcept == null) ? 0 : descriptiveConcept
						.hashCode());
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
		if (!(obj instanceof TaxonConceptStructureTriple)) {
			return false;
		}
		TaxonConceptStructureTriple other = (TaxonConceptStructureTriple) obj;
		if (descriptiveConcept == null) {
			if (other.descriptiveConcept != null) {
				return false;
			}
		} else if (!descriptiveConcept.equals(other.descriptiveConcept)) {
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
