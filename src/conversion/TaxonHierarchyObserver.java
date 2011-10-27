/**
 * 
 */
package conversion;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.xml.namespace.QName;

import sdd.Dataset;
import sdd.LabelText;
import sdd.Representation;
import sdd.TaxonHierarchyCore;
import sdd.TaxonHierarchyNode;
import sdd.TaxonHierarchyNodeRef;
import sdd.TaxonHierarchyNodeSeq;
import sdd.TaxonNameCore;
import sdd.TaxonNameRef;
import taxonomy.ITaxon;
import tree.TreeNode;

/**
 * This class subscribes to a TaxonNameHandler.  Every time that handler
 * publishes, we update an SDD TaxonHierarchy with the newly added taxon.
 * The Hierarchy is added to the main SDD Dataset via the TaxonNameHandler, which
 * references the DatasetHandler in which the Dataset lies.
 * @author alex
 *
 */
public class TaxonHierarchyObserver implements Observer {
	
	private static final String NODE_PREFIX = "th_node_";
	private sdd.ObjectFactory sddFactory;
	private sdd.TaxonHierarchySet taxonHierarchySet;
	private sdd.TaxonHierarchyCore taxonHierarchyCore;
	private sdd.TaxonHierarchyNodeSeq taxonHierarchyNodeSeq;
	private TaxonNameHandler taxonNameHandler;
	
	public TaxonHierarchyObserver(TaxonNameHandler handler) {
		this.taxonNameHandler = handler;
		this.sddFactory = new sdd.ObjectFactory();
		this.taxonHierarchySet = this.sddFactory.createTaxonHierarchySet();
		this.taxonHierarchyCore = sddFactory.createTaxonHierarchyCore();
		this.taxonHierarchyNodeSeq = sddFactory.createTaxonHierarchyNodeSeq();
		addRepToTaxonHierarchyCore();
		taxonHierarchyCore.setNodes(taxonHierarchyNodeSeq);
		taxonHierarchySet.getTaxonHierarchy().add(taxonHierarchyCore);
	}
	
	/**
	 * Update the TaxonHierarchy
	 */
	@Override
	public void update(Observable obs, Object arg) {
		if(obs instanceof TaxonNameHandler) {
			TaxonNameHandler handler = (TaxonNameHandler)obs;
			if(arg instanceof TreeNode) {
				TreeNode<ITaxon> node = (TreeNode<ITaxon>)arg;
				addTaxonHierarchyToDataset(
						handler.getDatasetHandler().getDataset(),
						node);
			}
		}
	}
	
	/**
	 * Places a Taxon Hierarchy in the dataset according to the Hierarchy defined
	 * under the TaxonHierarchy object.
	 * The Taxon Names to which the hierarchy nodes refer are defined by 
	 * {@link SDDConverter#addTaxonNameToDataset(Dataset, ITaxon)}.
	 * @param dataset
	 * @param treeNode The TaxonHiearchy tree node to add to the SDD TaxonHierarchy.
	 */
	protected void addTaxonHierarchyToDataset(Dataset dataset, TreeNode<ITaxon> treeNode) {
		ITaxon taxon = treeNode.getElement();		
		TaxonNameCore taxonName = lookupTaxonNameCore(taxon);
		TaxonHierarchyNode taxonNode = sddFactory.createTaxonHierarchyNode();
		taxonNode.setId(NODE_PREFIX.concat(taxon.getName()));
		TaxonNameRef ref = sddFactory.createTaxonNameRef();
		ref.setRef(taxonName.getId());
		taxonNode.setTaxonName(ref);
		
		TreeNode<ITaxon> parentTreeNode = treeNode.getParent();
		if(parentTreeNode != null) {
			ITaxon parentTaxon = parentTreeNode.getElement();
			TaxonHierarchyNodeRef parentNodeRef = sddFactory.createTaxonHierarchyNodeRef();
			parentNodeRef.setRef(NODE_PREFIX.concat(parentTaxon.getName()));
			taxonNode.setParent(parentNodeRef);
		}
		//update the sequence of taxon hierarchy nodes
		this.taxonHierarchyNodeSeq.getNode().add(taxonNode);
		//update the core's taxon node sequence
		this.taxonHierarchyCore.setNodes(taxonHierarchyNodeSeq);
		dataset.setTaxonHierarchies(taxonHierarchySet);
	}

	/**
	 * Just adds a simple rep to the TaxonHierarchy core.  Called once, from
	 * Constructor.
	 */
	private void addRepToTaxonHierarchyCore() {
		Representation rep = sddFactory.createRepresentation();
		LabelText labelText = sddFactory.createLabelText();
		labelText.setValue("Taxon hierarchy defined by this collection of descriptions.");
		rep.getRepresentationGroup().add(labelText);
		this.taxonHierarchyCore.setRepresentation(rep);
		this.taxonHierarchyCore.setTaxonHierarchyType(
				new QName("http://rs.tdwg.org/UBIF/2006/", "PhylogeneticTaxonomy"));
	}

	/**
	 * Lookup a Taxon's TaxonNameCore in the SDD Dataset.
	 * @param taxon
	 * @return
	 */
	private TaxonNameCore lookupTaxonNameCore (ITaxon taxon) {
		List<TaxonNameCore> coreNames = 
				((DatasetHandler)this.taxonNameHandler.getDatasetHandler()).
					getDataset().getTaxonNames().getTaxonName();
		for(TaxonNameCore name : coreNames) {
			if(taxon.getName().equals(name.getId()))
				return name;
		}
		return null;
	}

}
