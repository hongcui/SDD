package conversion;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import sdd.AbstractCharacterDefinition;
import sdd.CharacterLocalStateDef;
import sdd.DescriptiveConcept;
import sdd.LabelText;
import sdd.ModifierDef;
import sdd.ModifierSeq;
import sdd.Representation;
import states.IState;
import util.ConversionUtil;
import annotationSchema.jaxb.Structure;
import conversion.datapassing.TaxonCharacterStateTriple;

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
	private Map<String, ModifierDef> seenModifiers;
	/** Maps matrix taxon-character-state "triples" to modifier defs. */
	private Map<TaxonCharacterStateTriple, ModifierDef> matrixModifiers;
	
	public ModifierHandler() {
		this.dcModifiers = sddFactory.createDescriptiveConcept();
		this.seenModifiers = new HashMap<String, ModifierDef>();
		this.matrixModifiers = 
				new HashMap<TaxonCharacterStateTriple, ModifierDef>();
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void update(Observable observable, Object arg) {
		if(observable instanceof DescriptiveConceptHandler && 
				arg instanceof Structure) {
			Structure structure = (Structure) arg;
			//loop over each character name in the structure's char->state map
			for(String charName : structure.getCharStateMap().keySet()) {
				for(IState state : structure.getCharStateMap().get(charName)) {
					if(state.getModifier() != null) {
						ModifierDef modifierDef;
						if(! seenModifiers.containsKey(state.getModifier())) {
							modifierDef = sddFactory.createModifierDef();
							modifierDef.setId(ID_PREFIX.concat(
									state.getModifier().replace(" ", "_").
									replace(";", "")));
							Representation rep = 
									ConversionUtil.makeRep(state.getModifier());
							modifierDef.setRepresentation(rep);
							//add to modifier sequence
							dcModifiers.getModifiers().getModifier().add(modifierDef);
							seenModifiers.put(state.getModifier(), modifierDef);
							publish(dcModifiers);
						}
					}
				}
			}
		}
		else if(observable instanceof CharacterSetHandler &&
				arg instanceof Map) {
			//then we're getting passed a triple->IState map
			IState state =
					((Map<TaxonCharacterStateTriple, IState>) arg).values().iterator().next();
			if(state.getModifier() != null) {
				TaxonCharacterStateTriple triple =
						((Map<TaxonCharacterStateTriple, IState>) arg).keySet().iterator().next();
				ModifierDef modDef = seenModifiers.get(state.getModifier());
				matrixModifiers.put(triple, modDef);
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

	/**
	 * @return the seenModifiers
	 */
	public Map<String, ModifierDef> getSeenModifiers() {
		return seenModifiers;
	}

	/**
	 * @return the matrixModifiers
	 */
	public Map<TaxonCharacterStateTriple, ModifierDef> getMatrixModifiers() {
		return matrixModifiers;
	}

}
