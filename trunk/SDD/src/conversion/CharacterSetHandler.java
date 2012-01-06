package conversion;

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
import sdd.AbstractRef;
import sdd.CategoricalCharacter;
import sdd.CharacterLocalStateDef;
import sdd.CharacterSet;
import sdd.CharacterStateSeq;
import sdd.ConceptStateDef;
import sdd.ConceptStateRef;
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
import conversion.datapassing.TaxonCharacterStateTriple;
import conversion.datapassing.TaxonCharacterStructureTriple;

/**
 * This class handles the addition of characters from a char name->state mapping
 * to an SDD CharacterSet.
 * 
 * @author Alex
 * 
 */
public class CharacterSetHandler extends Observable implements Handler,
		Observer {

	public static final String STATE_ID_PREFIX = "state_";
	private static ObjectFactory sddFactory = new ObjectFactory();
	
	/** Taxon-by-character matrix. */
	private Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> matrix;
	/** SDD CharacterSet that gets attached to main Dataset. */
	private CharacterSet characterSet;
	/** Keeps track of state references to be used for later characters. */
	private Map<String, AbstractRef> stateRefs;
	/** Keeps track of global states from Descriptive Concept. */
	private Map<String, ConceptStateRef> globalStates;

	/**
	 * Creates a new CharacterSetHandler.  Adds characters from 
	 * ITaxon objects to SDD CharacterSet.
	 */
	public CharacterSetHandler() {
		this.matrix = new HashMap<String, 
				Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>>();
		this.characterSet = sddFactory.createCharacterSet();
		this.stateRefs = new TreeMap<String, AbstractRef>();
		this.globalStates = new HashMap<String, ConceptStateRef>();
	}
	
	/**
	 * Publishes to the descriptive concept handler to notify it 
	 * of encountering a global state.  Also publishes to the 
	 * Modifier handler.
	 */
	private void publish(AbstractRef stateRef) {
		this.setChanged();
		this.notifyObservers(stateRef);
	}
	
	/**
	 * Notify CharacterTreeHandler of character and associated data.
	 * @param node
	 * @param character
	 * @param structureNode
	 */
	private void publish(TreeNode<ITaxon> node,
			AbstractCharacterDefinition character,
			TreeNode<Structure> structureNode) {
		this.setChanged();
		this.notifyObservers(new TaxonCharacterStructureTriple(node, character, structureNode));
	}
	
	/**
	 * Notify the ModifierHandler of any states requiring a modifier.
	 * @param name
	 * @param character
	 * @param localStateDef
	 */
	private void publish(String name, AbstractCharacterDefinition character,
			CharacterLocalStateDef localStateDef, IState state) {
		TaxonCharacterStateTriple triple = 
				new TaxonCharacterStateTriple(name, character, localStateDef);
		Map<TaxonCharacterStateTriple, IState> possibleModifier =
				new HashMap<TaxonCharacterStateTriple, IState>();
		possibleModifier.put(triple, state);
		this.setChanged();
		this.notifyObservers(possibleModifier);
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
		if(observable instanceof DescriptiveConceptHandler &&
				arg instanceof ConceptStateDef) {
			//getting passed back a global state from DCHandler
			//add it to the global states mapping
			ConceptStateDef globalStateDef = (ConceptStateDef) arg;
			ConceptStateRef globalStateRef = sddFactory.createConceptStateRef();
			globalStateRef.setRef(globalStateDef.getId());
			globalStates.put(globalStateDef.getId(), globalStateRef);
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
			Map<String, List<IState>> charStateMap = 
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
				///get each associated state (and it's value)
				List<IState> stateList = charStateMap.get(charString);
				for(IState state : stateList) {		
					if(state instanceof SingletonState) {
						//if it's some categorical character
						Object stateValue = state.getMap().get(SingletonState.KEY);
						String stateName = stateValue.toString();
						if(stateValue instanceof String) {
							if(character == null)
								character = makeSingleCategoricalCharacter(fullCharName);
							localStateDef = makeSingleCategoricalState(stateValue);
							attachStateCategorical((CategoricalCharacter) character, 
									localStateDef, stateName);
						}	//otherwise, it's numeric
						else if(TypeUtil.isNumeric(stateValue)) {
							if(character == null)
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
							if(character == null)
								character = makeRangeQuantitativeCharState(localStateDef,
										fullCharName, state);
						}
						else {	//TODO 
							character = sddFactory.createQuantitativeCharacter();
							character.setId(fullCharName);
						}
					}
					//we use the same char rep in either case (for all states of that char)
					character.setRepresentation(charRep);
					Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> charStateDescMap = 
							matrix.get(node.getElement().getName());
					if(!charStateDescMap.containsKey(character))
						charStateDescMap.put(character, new HashSet<CharacterLocalStateDef>());
					charStateDescMap.get(character).add(localStateDef);
					publish(node.getElement().getName(), character, localStateDef, state);
				}
				//publish to CharacterTreeHandler
				publish(node, character, structureNode);
			}
		}
	}

	/**
	 * Makes a single categorical character.
	 * @param fullCharName
	 * @return sdd CategoricalCharacter for addition to CharacterSet.
	 */
	private CategoricalCharacter makeSingleCategoricalCharacter(String fullCharName) {
		CategoricalCharacter character = sddFactory.createCategoricalCharacter();
		character.setId(fullCharName);
		//need to start with an empty state seq in this categorical char
		CharacterStateSeq stateSeq = sddFactory.createCharacterStateSeq();
		character.setStates(stateSeq);
		return character;
	}
	
	/**
	 * Makes a single local state definition for a categorical character.
	 * @param stateValue
	 * @return
	 */
	private CharacterLocalStateDef makeSingleCategoricalState(Object stateValue) {
		Representation stateRep = 
				ConversionUtil.makeRep(stateValue.toString());
		String stateName = stateValue.toString();
		CharacterLocalStateDef localStateDef = sddFactory.createCharacterLocalStateDef();
		localStateDef.setId(STATE_ID_PREFIX.concat(stateName));
		localStateDef.setRepresentation(stateRep);
		return localStateDef;
	}
	
	/**
	 * 
	 * @param character The CategoricalCharacter to which a state will be attached.
	 * @param localStateDef The local State definition (might end up being a State Reference).
	 * @param stateName The name of the state.
	 */
	private void attachStateCategorical(CategoricalCharacter character,
			CharacterLocalStateDef localStateDef, String stateName) {
		//We first have to check if there's another character that uses this
		//same state.  If we remove this character from consideration, is there 
		//another one that still maps to the state in question?  If so,
		//we need this state globally.
		boolean flaggedGlobal = false;
		for(Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> taxonMap 
				: this.matrix.values()) {
			for(AbstractCharacterDefinition presentChar : taxonMap.keySet()) {
				Set<CharacterLocalStateDef> stateSet = taxonMap.get(presentChar);
				if(!flaggedGlobal && stateSet.contains(localStateDef) &&
						!presentChar.equals(character)) {
					//then we need this state globally
					AbstractRef stateRef = stateRefs.get(stateName);
					List<Object> currentStates = 
							character.getStates().getStateDefinitionOrStateReference();
					if(!currentStates.contains(stateRef))
						currentStates.add(stateRef);
					flaggedGlobal = true;
					publish(stateRef);	//publish to DCHandler
					break;
				}
			}
		}
		List<Object> currentStates = 
				character.getStates().getStateDefinitionOrStateReference();
		if(!flaggedGlobal && !stateRefs.containsKey(stateName)) {
			//haven't seen this before, stays as a local state def.
			if(!currentStates.contains(localStateDef))
				currentStates.add(localStateDef);
			//make a ref for this in case we see it later
			AbstractRef stateRef = sddFactory.createConceptStateRef();
			stateRef.setRef(localStateDef.getId());
			stateRefs.put(stateName, stateRef);
		}
		else if(!flaggedGlobal){
			if(!currentStates.contains(stateRefs.get(stateName)))
				currentStates.add(stateRefs.get(stateName));
		}
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
				if(!charsToAdd.containsKey(charDef.getId()))
						charsToAdd.put(charDef.getId(), charDef);
				else {	//merge categorical characters
					AbstractCharacterDefinition character = 
							charsToAdd.get(charDef.getId());
					if(character instanceof CategoricalCharacter) {
						CategoricalCharacter tempChar = (CategoricalCharacter) charDef;
						mergeWithoutDuplicates((CategoricalCharacter) character, tempChar);
					}
					else if(character instanceof QuantitativeCharacter) {
						QuantitativeCharacter tempChar = (QuantitativeCharacter) charDef;
						mergeWithoutDuplicates((QuantitativeCharacter) character, tempChar);
					}
				}
				//now resolve global states of Categorical Characters
				AbstractCharacterDefinition character =
						charsToAdd.get(charDef.getId());
				if(character instanceof CategoricalCharacter)
					resolveGlobalStates((CategoricalCharacter)character);
			}
		}
		characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter().
			addAll(charsToAdd.values());
	}

	/**
	 * Merges a temp character's states into a "keeper" character's states,
	 * disallowing duplicate refs or duplicate local state defs (task of removing
	 * defs from state set when appropriate state reference exists belongs to
	 * different function).
	 * @param keeper
	 * @param temp
	 */
	private void mergeWithoutDuplicates(CategoricalCharacter keeper, CategoricalCharacter temp) {
		List<Object> statesToKeep = keeper.getStates().getStateDefinitionOrStateReference();
		List<Object> tempStates = temp.getStates().getStateDefinitionOrStateReference();
		for(Object tempState : tempStates) {
			if(!statesToKeep.contains(tempState))
				statesToKeep.add(tempState);
		}
	}
	
	/**
	 * Merges a temp quant. character's state into the keeper quant. characters.
	 * Does this by expanding lower and upper range as far as necessary for
	 * both characters.
	 * @param keeper
	 * @param temp
	 */
	private void mergeWithoutDuplicates(QuantitativeCharacter keeper, QuantitativeCharacter temp) {
		List<QuantitativeCharMapping> keeperMappings = keeper.getMappings().getMapping();
		List<QuantitativeCharMapping> tempMappings = temp.getMappings().getMapping();
		Double min = Double.POSITIVE_INFINITY;
		Double max = Double.NEGATIVE_INFINITY;
		for(QuantitativeCharMapping mapping : keeperMappings) {
			if(mapping.getFrom().getLower() < min)
				min = mapping.getFrom().getLower();
			if(mapping.getFrom().getUpper() > max)
				max = mapping.getFrom().getUpper();
		}
		for(QuantitativeCharMapping mapping : tempMappings) {
			if(mapping.getFrom().getLower() < min)
				min = mapping.getFrom().getLower();
			if(mapping.getFrom().getUpper() > max)
				max = mapping.getFrom().getUpper();
		}
		keeperMappings.get(0).getFrom().setLower(min);
		keeperMappings.get(0).getFrom().setUpper(max);
	}
	
	/**
	 * 
	 * @param character
	 */
	private void resolveGlobalStates(CategoricalCharacter character) {
		List<Object> states = character.getStates().
				getStateDefinitionOrStateReference();
		Set<Object> statesToKeep = new HashSet<Object>();
		for(Object state : states) {
			if(state instanceof CharacterLocalStateDef) {
				CharacterLocalStateDef stateDef = (CharacterLocalStateDef) state;
				if(globalStates.containsKey(stateDef.getId()))
					statesToKeep.add(globalStates.get(stateDef.getId()));
				else
					statesToKeep.add(stateDef);
			}
			else if(state instanceof ConceptStateRef) {
				statesToKeep.add(state);
			}
		}
		states.clear();
		states.addAll(statesToKeep);
	}

	/**
	 * Gets the taxon-by-char matrix used for building coded description.
	 * @return the matrix
	 */
	public Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> getMatrix() {
		return matrix;
	}

}
