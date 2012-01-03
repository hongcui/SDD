package conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import sdd.AbstractCharacterDefinition;
import sdd.CategoricalCharacter;
import sdd.CharacterLocalStateDef;
import sdd.CharacterSet;
import sdd.CharacterStateSeq;
import sdd.LabelText;
import sdd.ObjectFactory;
import sdd.QuantitativeCharMapping;
import sdd.QuantitativeCharMappingSet;
import sdd.QuantitativeCharacter;
import sdd.Representation;
import sdd.ValueRangeWithClass;
import states.IState;
import states.RangeState;
import states.SingletonState;
import taxonomy.ITaxon;
import tree.TreeNode;
import util.ConversionUtil;
import util.QuantitativeStateDef;
import util.TypeUtil;
import annotationSchema.jaxb.Structure;

/**
 * This class handles the addition of characters from a char name->state mapping
 * to an SDD CharacterSet.
 * 
 * @author Alex
 * 
 */
public class CharacterSetHandler extends Observable implements Handler,
		Observer {

	private static final String STATE_ID_PREFIX = "state_";
	private static ObjectFactory sddFactory = new ObjectFactory();
	
	/** Taxon-by-character matrix. */
	private Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> matrix;
	/** SDD CharacterSet that gets attached to main Dataset. */
	private CharacterSet characterSet;

	/**
	 * Creates a new CharacterSetHandler.  Adds characters from 
	 * ITaxon objects to SDD CharacterSet.
	 */
	public CharacterSetHandler() {
		this.matrix = new HashMap<String, 
				Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>>();
		this.characterSet = sddFactory.createCharacterSet();
	}

	/**
	 * This updates when the DatasetHandler processes a new ITaxon.
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable observable, Object arg) {
		if(observable instanceof DatasetHandler &&
				arg instanceof TreeNode<?>) {
			DatasetHandler handler = (DatasetHandler) observable;
			//attach this character set to the main data set 
			//(happens multiple times, but that's ok)
			handler.getDataset().setCharacters(characterSet);
			TreeNode<ITaxon> node = (TreeNode<ITaxon>) arg;
			addCharactersToCharacterSet(node);
		}
	}

	@SuppressWarnings("rawtypes")
	private void addCharactersToCharacterSet(TreeNode<ITaxon> node) {
		//add this new taxon's name to the matrix
		matrix.put(node.getElement().getName(),
				new HashMap<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>());
		Iterator<TreeNode<Structure>> iterStructure = 
				node.getElement().getStructureTree().iterator();
		//loop over each structure in this ITaxon
		while(iterStructure.hasNext()) {
			TreeNode<Structure> structureNode = iterStructure.next();
			Map<String, IState> charStateMap = 
					structureNode.getElement().getCharStateMap();
			//loop over char->state mappings in this structure
			for(String charString : charStateMap.keySet()) {
				//initialize char and state for matrix
				AbstractCharacterDefinition character = null;
				CharacterLocalStateDef localStateDef = null;
				//resolve full character name
				String fullCharName = 
						ConversionUtil.resolveFullCharacterName(charString, structureNode);
				Representation charRep = ConversionUtil.makeRep(fullCharName);
				///get associated state (and it's value)
				IState state = charStateMap.get(charString);
				Object stateValue = state.getMap().get(SingletonState.KEY);
				if(state instanceof SingletonState) {
					//if it's some categorical character
					if(stateValue instanceof String) {
						character = makeSingleCategoricalCharState(localStateDef,
								fullCharName, stateValue);
					}	//otherwise, it's numeric
					else if(TypeUtil.isNumeric(stateValue)) {
						character = makeSingleQuantitativeCharState(localStateDef,
								fullCharName, state);
					}
				}
				else if(state instanceof RangeState) {
					//could be some kind of text range (i.e. a range of colors)
					if(state.getMap().get(RangeState.KEY_FROM) instanceof String) {
						character = sddFactory.createCategoricalCharacter();
						character.setId(fullCharName);
						((sdd.CategoricalCharacter)character).setStates(
								sddFactory.createCharacterStateSeq());
					}
					else if(TypeUtil.isNumeric(state.getMap().get(RangeState.KEY_FROM))) {
						character = makeRangeQuantitativeCharState(localStateDef,
								fullCharName, state);
					}
					else {	//TODO 
						character = sddFactory.createQuantitativeCharacter();
						character.setId(fullCharName);
					}
				}
				//we use the same char rep in either case
				character.setRepresentation(charRep);
				Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> charStateDescMap = 
						matrix.get(node.getElement().getName());
				if(!charStateDescMap.containsKey(character))
					charStateDescMap.put(character, new HashSet<CharacterLocalStateDef>());
				charStateDescMap.get(character).add(localStateDef);
			}
		}
	}

	/**
	 * Makes a single categorical character state definition.
	 * @param character
	 * @param localStateDef Will get initialized by the time this function returns.
	 * @param fullCharName
	 * @param stateValue
	 * @param stateRep
	 * @return sdd CategoricalCharacter for addition to CharacterSet.
	 */
	private CategoricalCharacter makeSingleCategoricalCharState(
			CharacterLocalStateDef localStateDef, String fullCharName, Object stateValue) {
		Representation stateRep = 
				ConversionUtil.makeRep(stateValue.toString());
		String stateName = stateValue.toString();
		CategoricalCharacter character = sddFactory.createCategoricalCharacter();
		character.setId(fullCharName);
		//need to start with an empty state seq in this categorical char
		CharacterStateSeq stateSeq = sddFactory.createCharacterStateSeq();
		((CategoricalCharacter) character).setStates(stateSeq);
		localStateDef = sddFactory.createCharacterLocalStateDef();
		localStateDef.setId(STATE_ID_PREFIX.concat(stateName));
		localStateDef.setRepresentation(stateRep);
		resolveGlobal(character, localStateDef, stateName);
		return character;
	}
	
	/**
	 * Makes a single quantitative character state definition.
	 * @param character
	 * @param localStateDef Will get initialized by the time function returns.
	 * @param fullCharName
	 * @param state
	 */
	@SuppressWarnings("rawtypes")
	private QuantitativeCharacter makeSingleQuantitativeCharState(
			CharacterLocalStateDef localStateDef, String fullCharName, IState state) {
		QuantitativeCharacter character = sddFactory.createQuantitativeCharacter();
		character.setId(fullCharName);
		putMappingAndRangeOnQuantitativeCharacter(character, state);
		localStateDef = new QuantitativeStateDef(character.getId(), character);
		return character;
	}
	
	/**
	 * Makes a ranged quantitative character state definition.
	 * @param localStateDef
	 * @param fullCharName
	 * @param state
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private AbstractCharacterDefinition makeRangeQuantitativeCharState(
			CharacterLocalStateDef localStateDef, String fullCharName,
			IState state) {
		QuantitativeCharacter character = sddFactory.createQuantitativeCharacter();
		character.setId(fullCharName);
		putMappingAndRangeOnQuantitativeCharacter(character, state);
		localStateDef = new QuantitativeStateDef(character.getId(), character);
		return character;
	}

	private void resolveGlobal(AbstractCharacterDefinition character,
			CharacterLocalStateDef localStateDef, String stateName) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Adds a mapping set (mapping and value range) to a QuantitativeCharacter.
	 * @param character
	 * @param state
	 */
	private void putMappingAndRangeOnQuantitativeCharacter(
			QuantitativeCharacter character, IState state) {
		QuantitativeCharMappingSet mappingSet = sddFactory.createQuantitativeCharMappingSet();
		if (state instanceof SingletonState) {
			QuantitativeCharMapping mapping = sddFactory.createQuantitativeCharMapping();
			ValueRangeWithClass range = sddFactory.createValueRangeWithClass();
			range.setLower((Double) state.getMap().get("value"));
			range.setUpper((Double) state.getMap().get("value"));
			mapping.setFrom(range);
			if (!mappingSet.getMapping().contains(mapping))
				mappingSet.getMapping().add(mapping);
			QuantitativeCharacter.MeasurementUnit measurementUnit = new QuantitativeCharacter.MeasurementUnit();
			LabelText unitLabelText = sddFactory.createLabelText();
			unitLabelText.setValue(state.getFromUnit());
			if(state.getFromUnit() == null || state.getFromUnit().isEmpty())
				unitLabelText.setValue("counted occurences");
			unitLabelText.setRole(new QName("http://rs.tdwg.org/UBIF/2006/", "Abbrev"));
			measurementUnit.getLabel().add(unitLabelText);
			((sdd.QuantitativeCharacter) character).setMappings(mappingSet);
			((sdd.QuantitativeCharacter) character)
					.setMeasurementUnit(measurementUnit);
		}
		else if (state instanceof RangeState) {
			QuantitativeCharMapping mapping = sddFactory.createQuantitativeCharMapping();
			ValueRangeWithClass range = sddFactory.createValueRangeWithClass();
			range.setLower((Double) state.getMap().get("from value"));
			range.setUpper((Double) state.getMap().get("to value"));
			mapping.setFrom(range);
			if (!mappingSet.getMapping().contains(mapping))
				mappingSet.getMapping().add(mapping);
			QuantitativeCharacter.MeasurementUnit measurementUnit = new QuantitativeCharacter.MeasurementUnit();
			LabelText unitLabelText = sddFactory.createLabelText();
			unitLabelText.setValue(state.getFromUnit());
			if(state.getFromUnit() == null || state.getFromUnit().isEmpty())
				unitLabelText.setValue("counted occurences");
			unitLabelText.setRole(new QName("http://rs.tdwg.org/UBIF/2006/", "Abbrev"));
			measurementUnit.getLabel().add(unitLabelText);
			((sdd.QuantitativeCharacter) character).setMappings(mappingSet);
			((sdd.QuantitativeCharacter) character)
					.setMeasurementUnit(measurementUnit);
		}
		
	}

	/**
	 * Adds all of the characters stored in this objects taxon-by-character
	 * matrix to the SDD CharacterSet, which is attached to the main
	 * SDD Dataset.
	 * @see conversion.Handler#handle()
	 */
	@Override
	public void handle() {
		//make this a TreeMap to get characters listed alphabetically.
		Map<String, AbstractCharacterDefinition> charsToAdd =
				new TreeMap<String, AbstractCharacterDefinition>();
		for(String taxon : matrix.keySet()) {
			Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> charStateMap = matrix.get(taxon);
			for(AbstractCharacterDefinition charDef : charStateMap.keySet()) {
				charsToAdd.put(charDef.getId(), charDef);
			}
		}
		characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter().
			addAll(charsToAdd.values());
	}

	/**
	 * Gets the taxon-by-char matrix used for building coded description.
	 * @return the matrix
	 */
	public Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> getMatrix() {
		return matrix;
	}

}
