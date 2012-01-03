package conversion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import sdd.DescriptiveConcept;
import sdd.DescriptiveConceptSet;
import sdd.ObjectFactory;
import sdd.Representation;
import taxonomy.ITaxon;
import tree.TreeNode;
import util.ConversionUtil;
import annotationSchema.jaxb.Structure;

/**
 * This class handles all of the DescriptiveConcepts that need
 * to be added to the dataset.
 * @author Alex
 *
 */
public class DescriptiveConceptHandler extends Observable implements Handler, Observer {

	private static ObjectFactory sddFactory = new ObjectFactory();
	private DescriptiveConceptSet dcSet;
	private Map<String, DescriptiveConcept> dcsToAdd;
	/**
	 * 
	 */
	public DescriptiveConceptHandler() {
		this.dcSet = sddFactory.createDescriptiveConceptSet();
		dcsToAdd = new HashMap<String, DescriptiveConcept>();
	}

	/**
	 * Updates when seeing a new ITaxon object from the DatasetHandler.
	 * @param o Should be an DatasetHandler object (ignore otherwise).
	 * @param arg Should be an ITaxon object.
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof DatasetHandler && arg instanceof TreeNode<?>) {
			DatasetHandler datasetHandler = (DatasetHandler) o;
			datasetHandler.getDataset().setDescriptiveConcepts(dcSet);
			TreeNode<ITaxon> node = (TreeNode<ITaxon>) arg;
			getDescriptiveConceptsFromStructures(node);
		}
		else if(o instanceof ModifierHandler &&
				arg instanceof DescriptiveConcept) {
			DescriptiveConcept dcModifiers = (DescriptiveConcept) arg;
			//Update the modifier DC (which will be added to set later).
			dcsToAdd.put(dcModifiers.getId(), dcModifiers);
		}
	}

	/**
	 * Iterates over the structure tree of an ITaxon, creating new
	 * descriptive concepts for structures (if required).
	 * @param node
	 */
	private void getDescriptiveConceptsFromStructures(TreeNode<ITaxon> node) {
		Iterator<TreeNode<Structure>> iter = node.getElement().
				getStructureTree().iterator();
		while(iter.hasNext()) {
			TreeNode<Structure> structureNode = iter.next();
			Structure structure = structureNode.getElement();
			DescriptiveConcept dc = sddFactory.createDescriptiveConcept();
			dc.setId(structure.getName());
			Representation rep = ConversionUtil.makeRep(structure.getName());
			dc.setRepresentation(rep);
			dcsToAdd.put(dc.getId(), dc);
			publish(structure);	//Tell ModifierHandler about this structure.
		}
	}
	
	/**
	 * This should notify the ModifierHandler when a new DescriptiveConcept
	 * has been encountered.
	 * @param structure The structure from which modifiers may be extracted.
	 */
	public void publish(Structure structure) {
		this.setChanged();
		this.notifyObservers(structure);
	}

	/** 
	 * Takes everything in the dcsToAdd set and adds them to the
	 * dcSet.
	 * @see conversion.Handler#handle()
	 */
	@Override
	public void handle() {
		this.dcSet.getDescriptiveConcept().addAll(dcsToAdd.values());

	}

}
