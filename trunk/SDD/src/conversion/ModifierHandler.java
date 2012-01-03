package conversion;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import sdd.DescriptiveConcept;
import sdd.LabelText;
import sdd.ModifierDef;
import sdd.ModifierSeq;
import sdd.Representation;
import states.IState;
import util.ConversionUtil;
import annotationSchema.jaxb.Structure;

/**
 * Class that handles the modifiers DescriptiveConcept.
 * @author Alex
 *
 */
public class ModifierHandler extends Observable implements Handler, Observer {
	
	private static final String MODIFIERS_ID = "modifiers";
	private static final String LABEL_TEXT = "Descriptive Concept for holding modifiers.";
	private static final String ID_PREFIX = "mod_";
	private DescriptiveConcept dcModifiers;
	private static sdd.ObjectFactory sddFactory = new sdd.ObjectFactory();
	private Set<String> seenModifiers;
	
	public ModifierHandler() {
		this.dcModifiers = sddFactory.createDescriptiveConcept();
		this.seenModifiers = new HashSet<String>();
		ModifierSeq modifierSeq = sddFactory.createModifierSeq();
		dcModifiers.setModifiers(modifierSeq);
		Representation repModifiers = sddFactory.createRepresentation();
		LabelText labelTextModifiers = sddFactory.createLabelText();
		dcModifiers.setId(MODIFIERS_ID);
		labelTextModifiers.setValue(LABEL_TEXT);
		repModifiers.getRepresentationGroup().add(labelTextModifiers);
		dcModifiers.setRepresentation(repModifiers);
	}

	/** 
	 * This handler gets modifiers of structures processed by the 
	 * DescriptiveConceptHandler.
	 * @param observable DCHandler that published event.
	 * @param arg The new structure.
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object arg) {
		if(observable instanceof DescriptiveConceptHandler && 
				arg instanceof Structure) {
			Structure structure = (Structure) arg;
			//loop over each character name in the structure's char->state map
			for(String charName : structure.getCharStateMap().keySet()) {
				IState state = structure.getCharStateMap().get(charName);
				if(state.getModifier() != null) {
					ModifierDef modifierDef;
					if(! seenModifiers.contains(state.getModifier())) {
						modifierDef = sddFactory.createModifierDef();
						modifierDef.setId(ID_PREFIX.concat(
								state.getModifier().replace(" ", "_")));
						Representation rep = 
								ConversionUtil.makeRep(state.getModifier());
						modifierDef.setRepresentation(rep);
						//add to modifier sequence
						dcModifiers.getModifiers().getModifier().add(modifierDef);
						seenModifiers.add(state.getModifier());
						publish(dcModifiers);
					}
				}
			}
		}
	}
	
	/**
	 * Publishes to the DCHandler when the dcModifiers object has a 
	 * new modifier added to it.
	 * @param dc
	 */
	public void publish(DescriptiveConcept dc) {
		this.setChanged();
		this.notifyObservers(dc);
	}

	/**
	 * Does nothing.
	 * @see conversion.Handler#handle()
	 */
	@Override
	public void handle() {

	}

}
