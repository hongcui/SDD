package conversion;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

import sdd.DetailText;
import sdd.LabelText;
import sdd.Representation;
import taxonomy.ITaxon;
import tree.TreeNode;

/**
 * Handles an SDD Dataset object's creation. Namely, this creates a new 
 * sdd.Dataset object, adds a representation to it, and processes the 
 * TaxonHierarchy we want a Dataset for.
 * @author alex
 *
 */
public class DatasetHandler extends Observable implements Handler {

	private sdd.Dataset dataset;
	private sdd.ObjectFactory sddFactory;
	private taxonomy.TaxonHierarchy hierarchy;
	private Deque<ITaxon> taxa;
	
	/**
	 * Create a new DatasetHandler object.
	 * @param hierarchy The TaxonHierarchy for which a DatasetHandler is to
	 * be created.
	 */
	public DatasetHandler(taxonomy.TaxonHierarchy hierarchy) {
		this.hierarchy = hierarchy;
		this.sddFactory = new sdd.ObjectFactory();
		this.dataset = sddFactory.createDataset();
		this.taxa = new LinkedList<ITaxon>();
	}
	
	/**
	 * Called when one of the properties has changed.
	 */
	public void publish(Object arg) {
		this.setChanged();
		this.notifyObservers(arg);
	}
	
	/**
	 * Adds a representation for the dataset based on the root Taxon of 
	 * the hierarchy.  Then calls processHierarchy, which notifies 
	 * observers of each of the ITaxon TreeNodes in the hierarchy.
	 */
	@Override
	public void handle() {
		//Adds some representation based on the root Taxon of the hierarchy
		addRepresentationToDataset(this.hierarchy.getHierarchy().getRoot().getElement());
		processHierarchy();
	}
	
	/**
	 * Add a represenation element to the Dataset.
	 * @param dataset
	 * @param taxon
	 */
	protected void addRepresentationToDataset(ITaxon taxon) {
		Representation rep = sddFactory.createRepresentation();
		LabelText labelText = sddFactory.createLabelText();
		String taxonRank = taxon.getTaxonRank().toString();
		String label = "The " + taxonRank + " " + taxon.getName();
		labelText.setValue(label);
		DetailText detailText = sddFactory.createDetailText();
		detailText.setValue("Generated from Hong's mark-up of FNA document");
		rep.getRepresentationGroup().add(labelText);
		rep.getRepresentationGroup().add(detailText);
		this.dataset.setRepresentation(rep);
	}
	
	/**
	 * Loop through the ITaxon nodes of the hierarchy, publishing to Observers
	 * with each pass.
	 */
	protected void processHierarchy() {
		Iterator<TreeNode<ITaxon>> iter = hierarchy.getHierarchy().iterator();
		while(iter.hasNext()) {
			TreeNode<ITaxon> node = iter.next();
			ITaxon taxon = node.getElement();
			this.taxa.addFirst(taxon);
			publish(node);
		}
	}
	
	public sdd.Dataset getDataset() {
		return this.dataset;
	}
	
	public Deque<ITaxon> getTaxa() {
		return this.taxa;
	}

}
