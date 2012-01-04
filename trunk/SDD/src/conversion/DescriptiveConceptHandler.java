package conversion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import sdd.AbstractRef;
import sdd.ConceptStateDef;
import sdd.ConceptStateSeq;
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
	
	public static final String DC_PREFIX = "dc_";
	public static final String GLOBAL_STATE_ID = "dc_global_states";
	/**
	 * 
	 */
	public DescriptiveConceptHandler() {
		this.dcSet = sddFactory.createDescriptiveConceptSet();
		this.dcsToAdd = new HashMap<String, DescriptiveConcept>();
		DescriptiveConcept dcGlobalStates = sddFactory.createDescriptiveConcept();
		dcGlobalStates.setId(GLOBAL_STATE_ID);
		Representation rep = ConversionUtil.makeRep("Descriptive concept describing states used globally.");
		dcGlobalStates.setRepresentation(rep);
		ConceptStateSeq conceptStates = sddFactory.createConceptStateSeq();
		dcGlobalStates.setConceptStates(conceptStates);
		this.dcsToAdd.put(GLOBAL_STATE_ID, dcGlobalStates);
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
		else if(o instanceof CharacterSetHandler && 
				arg instanceof AbstractRef) {
			addToGlobalStates((AbstractRef)arg);
		}
	}

	/**
	 * Adds a new global state to the descriptive concepts.
	 * @param ref
	 */
	private void addToGlobalStates(AbstractRef ref) {
		ConceptStateDef conceptStateDef = sddFactory.createConceptStateDef();
		conceptStateDef.setId(ref.getRef());
		Representation rep = 
				ConversionUtil.makeRep(ref.getRef().replace(CharacterSetHandler.STATE_ID_PREFIX, ""));
		conceptStateDef.setRepresentation(rep);
		List<ConceptStateDef> currentConceptStates = dcsToAdd.get(GLOBAL_STATE_ID).getConceptStates().getStateDefinition();
		if(!currentConceptStates.contains(conceptStateDef))
			currentConceptStates.add(conceptStateDef);
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
			dc.setId(DC_PREFIX.concat(structure.getName()));
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
