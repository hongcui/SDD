package conversion;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import sdd.AbstractCharacterDefinition;
import sdd.AbstractCharacterMarkup;
import sdd.AbstractRef;
import sdd.CatSummaryData;
import sdd.CategoricalCharacter;
import sdd.CategoricalMarkup;
import sdd.CharTreeCharacter;
import sdd.CharTreeNode;
import sdd.CharTreeNodeRef;
import sdd.CharTreeNodeSeq;
import sdd.CharacterLocalStateDef;
import sdd.CharacterRef;
import sdd.CharacterSet;
import sdd.CharacterTree;
import sdd.CharacterTreeSet;
import sdd.CodedDescription;
import sdd.ConceptMarkup;
import sdd.Dataset;
import sdd.Datasets;
import sdd.DescriptiveConcept;
import sdd.DescriptiveConceptRef;
import sdd.DescriptiveConceptSet;
import sdd.DetailText;
import sdd.DocumentGenerator;
import sdd.LabelText;
import sdd.MarkupText;
import sdd.NaturalLanguageDescription;
import sdd.NaturalLanguageDescriptionSet;
import sdd.NaturalLanguageMarkup;
import sdd.ObjectFactory;
import sdd.QuantSummaryData;
import sdd.QuantitativeCharMapping;
import sdd.QuantitativeCharMappingSet;
import sdd.QuantitativeCharacter;
import sdd.QuantitativeMarkup;
import sdd.Representation;
import sdd.StateData;
import sdd.StateMarkup;
import sdd.TaxonNameCore;
import sdd.TaxonNameRef;
import sdd.TaxonomicRank;
import sdd.TechnicalMetadata;
import sdd.UnivarSimpleStatMeasureData;
import sdd.ValueMarkup;
import sdd.ValueRangeWithClass;
import states.IState;
import states.RangeState;
import states.SingletonState;
import taxonomy.ITaxon;
import taxonomy.TaxonHierarchy;
import tree.TreeNode;
import util.TypeUtil;
import util.XMLGregorianCalendarConverter;
import annotationSchema.jaxb.Structure;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Class for converting an RDF model into SDD.
 * @author Alex
 *
 */
public class SDDConverter {
	
	private Model model;
	private JAXBContext sddContext;
	private ObjectFactory sddFactory;
	
	/** This maps state names to State References.  For use when a state is defined previously and is a valid state for a different, later
	 * character. 
	 */
	private Map<String, sdd.AbstractRef> refs;
	
	/** This maps taxon names to a map from CharacterDefinitions to Sets of state definitions.  This is basically holding the taxon-by-character
	 * matrix information for building a CodedDescription.
	 */
	private Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> taxonNameToCharState;
	
	private Map<String, DescriptiveConcept> dcsToAdd;
	Map<String, AbstractCharacterDefinition> charsToAdd;
	
	/**
	 * Create a new converter object from a single taxon model.
	 * @param taxonModel
	 */
	public SDDConverter(Model taxonModel) {
		this.model = taxonModel;
		try {
			this.sddContext = JAXBContext.newInstance(sdd.ObjectFactory.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		this.sddFactory = new ObjectFactory();
		this.refs = new HashMap<String, sdd.AbstractRef>();
		this.taxonNameToCharState = new HashMap<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>>();
	}
	
	public SDDConverter() {
		try {
			this.sddContext = JAXBContext.newInstance(sdd.ObjectFactory.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		this.sddFactory = new ObjectFactory();
		this.refs = new HashMap<String, sdd.AbstractRef>();
		this.taxonNameToCharState = new HashMap<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>>();
		dcsToAdd = new HashMap<String, DescriptiveConcept>();
		charsToAdd = new HashMap<String, AbstractCharacterDefinition>();
	}

	/**
	 * Transform the model for a single taxon into an SDD document.
	 * @param filename Name of the xml document.
	 */
	public void taxonToSDD(ITaxon taxon, String filename) {
		try {
			Marshaller marshaller = sddContext.createMarshaller();
			Datasets root = sddFactory.createDatasets();
			addMetadata(root);
			addDataset(root, taxon);
			marshaller.marshal(root, new File(filename));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts a TaxonHierarchy into an associated SDD XML file.  A Datasets SDD object is created
	 * and set as the root of the XML file.
	 * @param hierarchy
	 * @param filename
	 */
	public void taxonHierarchyToSDD(TaxonHierarchy hierarchy, String filename) {
		try {
			Marshaller marshaller = sddContext.createMarshaller();
			Datasets root = sddFactory.createDatasets();
			addMetadata(root);
			addDataset(root, hierarchy);
			marshaller.marshal(root, new File(filename));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a Dataset to the root Datasets object, using data from all of the taxa in the hierarchy.
	 * @param root
	 * @param hierarchy
	 */
	protected void addDataset(Datasets root, TaxonHierarchy hierarchy) {
		Dataset dataset = sddFactory.createDataset();
		dataset.setLang("en-us");
		addRepresentationToDataset(dataset, hierarchy.getHierarchy().getRoot().getElement());
		Iterator<TreeNode<ITaxon>> iter = hierarchy.getHierarchy().iterator();
		while(iter.hasNext()) {
			TreeNode<ITaxon> node = iter.next();
			ITaxon taxon = node.getElement();
			addTaxonNameToDataset(dataset, taxon);
			addDescriptiveConceptsToDataset(dataset, taxon);
			addCodedDescriptionToDataset(dataset, taxon);
		}
		DescriptiveConceptSet dcSet = sddFactory.createDescriptiveConceptSet();
		dcSet.getDescriptiveConcept().addAll(dcsToAdd.values());
		dataset.setDescriptiveConcepts(dcSet);
		CharacterSet characterSet = sddFactory.createCharacterSet();
		characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter().addAll(charsToAdd.values());
		dataset.setCharacters(characterSet);
		root.getDataset().add(dataset);
		
	}

	/**
	 * Adds a Dataset to the root Datasets object, using data from taxon object.
	 * @param root The root object of the SDD document.
	 * @param taxon Object to retrieve data from.
	 */
	protected void addDataset(Datasets root, ITaxon taxon) {
		Dataset dataset = sddFactory.createDataset();
		dataset.setLang("en-us");
		addTaxonNameToDataset(dataset, taxon);
		addRepresentationToDataset(dataset, taxon);
		addDescriptiveConceptsToDataset(dataset, taxon);
		addCodedDescriptionToDataset(dataset, taxon);
		root.getDataset().add(dataset);
	}

	/**
	 * Add a taxon name and rank to a dataset.
	 * @param dataset
	 * @param taxon
	 */
	protected void addTaxonNameToDataset(Dataset dataset, ITaxon taxon) {
		TaxonNameCore taxonNameCore = sddFactory.createTaxonNameCore();
		taxonNameCore.setId(taxon.getName());
		TaxonomicRank taxonomicRank = sddFactory.createTaxonomicRank();
		String rank = taxon.getTaxonRank().toString().toLowerCase();
		taxonomicRank.setLiteral(rank);
		Representation rep = sddFactory.createRepresentation();
		LabelText labelText = sddFactory.createLabelText();
		labelText.setValue(rank.concat(" ").concat(taxon.getName()));
		rep.getRepresentationGroup().add(labelText);
		taxonNameCore.setRepresentation(rep);
		taxonNameCore.setRank(taxonomicRank);
		if(dataset.getTaxonNames() == null)
			dataset.setTaxonNames(sddFactory.createTaxonNameSet());
		dataset.getTaxonNames().getTaxonName().add(taxonNameCore);
	}

	/**
	 * Adds a set of descriptive concepts (based on structures from annotation schema).  This is really adding the bulk of the DescriptiveTerminology,
	 * since it also calls the methods to build a CharacterSet and CharacterTrees.
	 * to a dataset.
	 * @param dataset
	 * @param taxon
	 */
	protected void addDescriptiveConceptsToDataset(Dataset dataset, ITaxon taxon) {
		DescriptiveConceptSet dcSet = null;
		if(dataset.getDescriptiveConcepts() == null)
			dcSet = sddFactory.createDescriptiveConceptSet();
		else
			dcSet = dataset.getDescriptiveConcepts();
		CharacterSet characterSet = null;
		if(dataset.getCharacters() == null)
			characterSet = sddFactory.createCharacterSet();
		else
			characterSet = dataset.getCharacters();
		CharacterTreeSet characterTreeSet = null;
		if(dataset.getCharacterTrees() == null)
			characterTreeSet = sddFactory.createCharacterTreeSet();
		else
			characterTreeSet = dataset.getCharacterTrees();
		CharacterTree characterTree = sddFactory.createCharacterTree();
		Representation ctRep = sddFactory.createRepresentation();
		LabelText ctLabelText = sddFactory.createLabelText();
		ctLabelText.setValue("Structural Character tree");
		ctRep.getRepresentationGroup().add(ctLabelText);
		characterTree.setRepresentation(ctRep);
		
		Iterator<TreeNode<Structure>> iter = taxon.getStructureTree().iterator();		
		while(iter.hasNext()) {
			TreeNode<Structure> node = iter.next();
			Structure s = node.getElement();
			DescriptiveConcept dc = sddFactory.createDescriptiveConcept();
			dc.setId(s.getId());
			Representation rep = sddFactory.createRepresentation();
			LabelText labelText = sddFactory.createLabelText();
			labelText.setValue(s.getName());
			rep.getRepresentationGroup().add(labelText);
			dc.setRepresentation(rep);
			dcsToAdd.put(s.getName(), dc);
			List<AbstractCharacterDefinition> charsForCharacterTree = addCharactersToCharacterSet(charsToAdd, s.getCharStateMap(), node, taxon.getName());
			addToCharacterTree(characterTree, dc, charsForCharacterTree, node);
		}
		//This needs to be done Globally, not in the method!
//		dcSet.getDescriptiveConcept().addAll(dcsToAdd.values());
//		dataset.setDescriptiveConcepts(dcSet);
//		characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter().addAll(charsToAdd.values());
//		dataset.setCharacters(characterSet);
		characterTreeSet.getCharacterTree().add(characterTree);
		dataset.setCharacterTrees(characterTreeSet);
	}

	/**
	 * This method first adds a Node to a character tree that points to the descriptive concept object.  It then adds some character nodes 
	 * as references that point to the descriptive concept.
	 * @param characterTree
	 * @param dc
	 * @param charsForCharacterTree
	 * @param node
	 */
	protected void addToCharacterTree(CharacterTree characterTree,
			DescriptiveConcept dc,
			List<AbstractCharacterDefinition> charsForCharacterTree,
			TreeNode<Structure> node) {
		//make a new DC node for the tree, set it's reference to the id of the actual Descriptive Concept, set
		//the ID to be the id of the DC object with 'dc_node' in front, and point to the parent of the DC object.
		CharTreeNode dcNode = sddFactory.createCharTreeNode();
		DescriptiveConceptRef dcRef = sddFactory.createDescriptiveConceptRef();
		dcRef.setRef(dc.getId());
		dcNode.setDescriptiveConcept(dcRef);
		dcNode.setId("dc_node_"+dc.getId());
		if(node.getParent() != null) {
			Structure parentStructure = node.getParent().getElement();
			CharTreeNodeRef ctNodeRef = sddFactory.createCharTreeNodeRef();
			ctNodeRef.setRef("dc_node_"+parentStructure.getId());
			dcNode.setParent(ctNodeRef);
		}
		//Now, for each character in the character list, make a CharNode for the tree, and point to the descriptive
		//concept node as the parent.
		for(AbstractCharacterDefinition character : charsForCharacterTree) {
			CharTreeCharacter ctCharNode = sddFactory.createCharTreeCharacter();
			CharacterRef charRef = sddFactory.createCharacterRef();
			charRef.setRef(character.getId());
			ctCharNode.setCharacter(charRef);
			CharTreeNodeRef ctNodeRef = sddFactory.createCharTreeNodeRef();
			ctNodeRef.setRef(dcNode.getId());
			ctCharNode.setParent(ctNodeRef);
			if(characterTree.getNodes() == null) {
				CharTreeNodeSeq ctNodeSeq = sddFactory.createCharTreeNodeSeq();
				characterTree.setNodes(ctNodeSeq);
			}
			characterTree.getNodes().getNodeOrCharNode().add(ctCharNode);
		}
		characterTree.getNodes().getNodeOrCharNode().add(dcNode);
	}

	/**
	 * Adds characters from a map between character names and states to an SDD Character Set
	 * @param characterMap This map will maintain pairs from character names to SDD Characters.  Passed on to mergeCharacters method and back to the 
	 * addDescriptiveConceptsToDataset method, helping to maintain the Character Terminology.
	 * @param charStateMap This is a mapping from character names to IState objects as provided by the ITaxon.
	 * @param node Node from the Structure tree of a taxon, from which a full character name will be derived.
	 * @param taxonName 
	 * @return A list of SDD characters to be used as CharNodes in the character tree.
	 */
	protected List<AbstractCharacterDefinition> addCharactersToCharacterSet(Map<String, AbstractCharacterDefinition> characterMap,
			Map<String, IState> charStateMap, TreeNode<Structure> node, String taxonName) {
		List<AbstractCharacterDefinition> characters = new ArrayList<AbstractCharacterDefinition>();
		if(!taxonNameToCharState.containsKey(taxonName))
			taxonNameToCharState.put(taxonName, new HashMap<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>());
		
		Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> charStateDescMap = taxonNameToCharState.get(taxonName);
		
		for(String s : charStateMap.keySet()) {
			AbstractCharacterDefinition character = null;
			CharacterLocalStateDef localStateDef = null;
			IState state = charStateMap.get(s);
			String fullCharacterName = util.ConversionUtil.resolveFullCharacterName(s, node);
			if(state instanceof SingletonState) {
				Representation rep = sddFactory.createRepresentation();
				LabelText labelText = sddFactory.createLabelText();
				labelText.setValue(fullCharacterName);
				rep.getRepresentationGroup().add(labelText);
				Representation stateRep = sddFactory.createRepresentation();
				LabelText stateLabelText = sddFactory.createLabelText();
				stateLabelText.setValue(state.getMap().get("value").toString());
				stateRep.getRepresentationGroup().add(stateLabelText);
				
				if(state.getMap().get("value") instanceof String) {
					String stateName = (String) state.getMap().get("value");
					character = sddFactory.createCategoricalCharacter();
					character.setId(fullCharacterName);
					((sdd.CategoricalCharacter)character).setStates(sddFactory.createCharacterStateSeq());
					localStateDef = sddFactory.createCharacterLocalStateDef();
					localStateDef.setId(stateName);
					localStateDef.setRepresentation(stateRep);
					localStateDef.setId("state_".concat(stateName));
					AbstractRef stateRef;
					if(!refs.containsKey(stateName)) {	//if we're seeing this state for the first time, add it to character as a definition
						((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(localStateDef);
						//add reference to refs map for future use.
						stateRef = sddFactory.createConceptStateRef();
						stateRef.setRef(localStateDef.getId());
						refs.put(stateName, stateRef);
					}
					else {	//we've seen this state before and it's not a duplicate for this character, get a Concept ref. from refs Map
						if(charStateDescMap.containsKey(character) && !charStateDescMap.get(character).contains(localStateDef)) {
							stateRef = refs.get(stateName);
							((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(stateRef);
						}
						else if(!charStateDescMap.containsKey(character)) {
							stateRef = refs.get(stateName);
							((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(stateRef);
						}
					}
				}
				else if(TypeUtil.isNumeric(state.getMap().get("value"))) {
					character = sddFactory.createQuantitativeCharacter();
					character.setId(fullCharacterName);
					QuantitativeCharMappingSet mappingSet = sddFactory.createQuantitativeCharMappingSet();
					putMappingAndRangeOnQuanChar(character, state, mappingSet);
				}
				character.setRepresentation(rep);
			}
			else if(state instanceof RangeState){
				Representation rep = sddFactory.createRepresentation();
				LabelText labelText = sddFactory.createLabelText();
				labelText.setValue(fullCharacterName);
				rep.getRepresentationGroup().add(labelText);
				
				if(state.getMap().get("from value") instanceof String) {
					character = sddFactory.createCategoricalCharacter();
					character.setId(fullCharacterName);
					((sdd.CategoricalCharacter)character).setStates(sddFactory.createCharacterStateSeq());
				}
				else if(TypeUtil.isNumeric(state.getMap().get("from value"))) {
					character = sddFactory.createQuantitativeCharacter();
					character.setId(fullCharacterName);
					QuantitativeCharMappingSet mappingSet = sddFactory.createQuantitativeCharMappingSet();
					putMappingAndRangeOnQuanChar(character, state, mappingSet);
				}
				else {
					character = sddFactory.createQuantitativeCharacter();
					character.setId(fullCharacterName);
				}
				character.setRepresentation(rep);
			}
			characters.add(character);
			mergeNewCharacter(characterMap, character, taxonName);
			if(!charStateDescMap.containsKey(character))
				charStateDescMap.put(character, new HashSet<CharacterLocalStateDef>());
			charStateDescMap.get(character).add(localStateDef);
		}
		return characters;		
	}

	/**
	 * Adds a mapping and value range to the mapping set of a QuantitativeCharacter.
	 * @param character
	 * @param state
	 */
	protected void putMappingAndRangeOnQuanChar(
			AbstractCharacterDefinition character, IState state, QuantitativeCharMappingSet mappingSet) {
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
			unitLabelText.setRole(new QName("http://rs.tdwg.org/UBIF/2006/", "Abbrev"));
			measurementUnit.getLabel().add(unitLabelText);
			((sdd.QuantitativeCharacter) character).setMappings(mappingSet);
			((sdd.QuantitativeCharacter) character)
					.setMeasurementUnit(measurementUnit);
		}
	}

	/**
	 * Add or merge character into existing set (really a map) of characters in the dataset.
	 * @param characterMap
	 * @param character
	 */
	protected void mergeNewCharacter(
			Map<String, AbstractCharacterDefinition> characterMap,
			AbstractCharacterDefinition character, String taxonName) {
		String charName = character.getId();
		if(!characterMap.containsKey(charName))
			characterMap.put(charName, character);
		else {
			AbstractCharacterDefinition presentChar = characterMap.get(charName);
//			System.out.println("<Debug>Merging characters for taxon="+taxonName+"\nnew character="+character.toString() +"\npresent character="+presentChar.toString());
			if(presentChar instanceof sdd.CategoricalCharacter) {
				if(character instanceof sdd.CategoricalCharacter) {
					//then just merge states of the two
					((sdd.CategoricalCharacter)presentChar).getStates().getStateDefinitionOrStateReference().
						addAll(
								((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference());
					System.out.println("<Debug>Merged new into present:"+presentChar.toString());
					Set stateSet = new HashSet();
					stateSet.addAll(((CategoricalCharacter) presentChar).getStates().getStateDefinitionOrStateReference());
					((CategoricalCharacter) presentChar).getStates().getStateDefinitionOrStateReference().clear();
					((CategoricalCharacter) presentChar).getStates().getStateDefinitionOrStateReference().addAll(stateSet);
				}
				else if(character instanceof sdd.QuantitativeCharacter) {
					String q = "q_".concat(character.getId());
					character.setId(q);
					characterMap.put(q, character);
				}
			}
			else if(presentChar instanceof sdd.QuantitativeCharacter) {
				if(character instanceof sdd.CategoricalCharacter) {
					String q = "q_".concat(presentChar.getId());
					presentChar.setId(q);
					characterMap.put(q, presentChar);
					characterMap.put(charName, character);
				}
			}
		}
	}
	
	/**
	 * Adds a Coded Description to the set of Coded Descriptions for a Dataset.
	 * @param dataset
	 * @param taxon
	 */
	protected void addCodedDescriptionToDataset(Dataset dataset, ITaxon taxon) {
		if(dataset.getCodedDescriptions() == null)
			dataset.setCodedDescriptions(sddFactory.createCodedDescriptionSet());
		CodedDescription description = sddFactory.createCodedDescription();
		Representation descRep = sddFactory.createRepresentation();
		LabelText descLabel = sddFactory.createLabelText();
		descLabel.setValue("Coded description for " + taxon.getName());
		descRep.getRepresentationGroup().add(descLabel);
		description.setRepresentation(descRep);
		//indicate the taxon scope
		TaxonNameRef taxonNameRef = sddFactory.createTaxonNameRef();
		taxonNameRef.setRef(taxon.getName());
		description.setScope(sddFactory.createDescriptionScopeSet());
		description.getScope().getTaxonName().add(taxonNameRef);
		//now add in the summary data
		description.setSummaryData(sddFactory.createSummaryDataSet());
		addSummaryDataToCodedDescription(description, taxon);
		
		//and finally, add this description to the Coded Description Set
		dataset.getCodedDescriptions().getCodedDescription().add(description);
	}

	/**
	 * Use references to previously-defined DCs and characters to build a coded description for this taxon.
	 * @param description
	 * @param taxon
	 */
	protected void addSummaryDataToCodedDescription(CodedDescription description,
			ITaxon taxon) {
		Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> map = this.taxonNameToCharState.get(taxon.getName());
		for(AbstractCharacterDefinition character : map.keySet()) {
			if(character instanceof sdd.CategoricalCharacter) {
				CatSummaryData summaryData = sddFactory.createCatSummaryData();
				summaryData.setRef(character.getId());
				for(CharacterLocalStateDef stateDef : map.get(character)) {
					if (stateDef != null) {
						StateData stateData = sddFactory.createStateData();
						stateData.setRef(stateDef.getId());
						summaryData.getState().add(stateData);
					}
				}
				JAXBElement<CatSummaryData> dataElement =
					new JAXBElement<CatSummaryData>(new QName("http://rs.tdwg.org/UBIF/2006/", "Categorical"), CatSummaryData.class, CatSummaryData.class, (CatSummaryData) summaryData);
				description.getSummaryData().getCategoricalOrQuantitativeOrSequence().add(dataElement);
			}
			if(character instanceof sdd.QuantitativeCharacter) {
				QuantSummaryData summaryData = sddFactory.createQuantSummaryData();
				summaryData.setRef(character.getId());
				UnivarSimpleStatMeasureData low = sddFactory.createUnivarSimpleStatMeasureData();
				low.setType(new QName("http://rs.tdwg.org/UBIF/2006/", "Low"));
				low.setValue(((sdd.QuantitativeCharacter)character).getMappings().getMapping().get(0).getFrom().getLower());
				summaryData.getMeasureOrPMeasure().add(low);
				UnivarSimpleStatMeasureData high = sddFactory.createUnivarSimpleStatMeasureData();
				high.setType(new QName("http://rs.tdwg.org/UBIF/2006/", "High"));
				high.setValue(((sdd.QuantitativeCharacter)character).getMappings().getMapping().get(0).getFrom().getUpper());
				summaryData.getMeasureOrPMeasure().add(high);
				JAXBElement<QuantSummaryData> dataElement =
					new JAXBElement<QuantSummaryData>(new QName("http://rs.tdwg.org/UBIF/2006/", "Quantitative"), QuantSummaryData.class, QuantSummaryData.class, (QuantSummaryData) summaryData);
				description.getSummaryData().getCategoricalOrQuantitativeOrSequence().add(dataElement);
			}
		}
	}

	/**
	 * Add a represenation element to a Dataset.
	 * @param dataset
	 * @param taxon
	 */
	protected void addRepresentationToDataset(Dataset dataset, ITaxon taxon) {
		Representation rep = sddFactory.createRepresentation();
		LabelText labelText = sddFactory.createLabelText();
		String taxonRank = taxon.getTaxonRank().toString();
		String label = "The " + taxonRank + " " + taxon.getName();
		labelText.setValue(label);
		DetailText detailText = sddFactory.createDetailText();
		detailText.setValue("Generated from Hong's mark-up of FNA document");
		rep.getRepresentationGroup().add(labelText);
		rep.getRepresentationGroup().add(detailText);
		dataset.setRepresentation(rep);
	}

	/**
	 * Add a set of natural language descriptions to a dataset.
	 * @param dataset
	 * @param taxon
	 * @deprecated 
	 */
	private void addNaturalLanguageDescriptions(Dataset dataset, ITaxon taxon) {
		NaturalLanguageDescriptionSet descriptionSet = sddFactory.createNaturalLanguageDescriptionSet();		
		Map<String, NaturalLanguageDescription> statementIdToDescription = 
			new HashMap<String, NaturalLanguageDescription>();
		for(String id : taxon.getStatementTextMap().keySet()) {
			NaturalLanguageDescription description = sddFactory.createNaturalLanguageDescription();
			NaturalLanguageMarkup data = sddFactory.createNaturalLanguageMarkup();
			MarkupText markupText = sddFactory.createMarkupText();
			markupText.setValue(taxon.getStatementTextMap().get(id));
			data.getMarkupGroup().add(markupText);
			description.setNaturalLanguageData(data);
			addRepresentationToNaturalLanguageDescription(description, taxon);
			statementIdToDescription.put(id, description);
		}
		Iterator<TreeNode<Structure>> iter = taxon.getStructureTree().iterator();
		while(iter.hasNext()) {
			TreeNode<Structure> node = iter.next();
			Structure structure = node.getElement();
			NaturalLanguageDescription description = 
				statementIdToDescription.get(structure.getStatementId());
			addConceptToNaturalLanguageDescription(
					description.getNaturalLanguageData(), node);
		}
		descriptionSet.getNaturalLanguageDescription().addAll(statementIdToDescription.values());
		dataset.setNaturalLanguageDescriptions(descriptionSet);
	}

	/**
	 * Adds a ConceptMarkup element to a piece of NaturalLanguageMarkup/data.
	 * @param data
	 * @param node
	 * @deprecated
	 */
	private void addConceptToNaturalLanguageDescription(
			NaturalLanguageMarkup data, TreeNode<Structure> node) {
		Structure structure = node.getElement();
		ConceptMarkup concept = sddFactory.createConceptMarkup();
		concept.setRef(structure.getName());
		for(String charName : structure.getCharStateMap().keySet()) {
			addCharacterToConcept(concept, 
					charName, structure.getCharStateMap().get(charName));
		}
		data.getMarkupGroup().add(concept);
	}

	/**
	 * 
	 * @param concept
	 * @param charName
	 * @param state
	 * @deprecated
	 */
	private void addCharacterToConcept(ConceptMarkup concept, String charName,
			IState state) {
		if(state instanceof RangeState) {
			if(TypeUtil.isNumeric(state.getMap().get("from value"))) {
				QuantitativeMarkup qFrom = sddFactory.createQuantitativeMarkup();
				qFrom.setLabel(charName.concat("_from"));
				ValueMarkup vFrom = sddFactory.createValueMarkup();
				vFrom.setValue((Double)state.getMap().get("from value"));
				JAXBElement<ValueMarkup> eleFrom =
					new JAXBElement<ValueMarkup>(new QName("ValueMarkup"), ValueMarkup.class, vFrom);
				qFrom.getStatusOrModifierOrMeasure().add(eleFrom);
				QuantitativeMarkup qTo = sddFactory.createQuantitativeMarkup();
				qTo.setLabel(charName.concat("_to"));
				ValueMarkup vTo = sddFactory.createValueMarkup();
				vTo.setValue((Double)state.getMap().get("to value"));
				JAXBElement<ValueMarkup> eleTo =
					new JAXBElement<ValueMarkup>(new QName("ValueMarkup"), ValueMarkup.class, vTo);
				qTo.getStatusOrModifierOrMeasure().add(eleTo);
				if(state.getModifier() != null) {
					addModifierToCharacterMarkup(qFrom, state);
					addModifierToCharacterMarkup(qTo, state);
				}
				concept.getMarkupGroup().add(qFrom);
				concept.getMarkupGroup().add(qTo);
			}
			else {
				CategoricalMarkup cFrom = sddFactory.createCategoricalMarkup();
				cFrom.setLabel(charName.concat("_from"));
				StateMarkup stateFrom = sddFactory.createStateMarkup();
				stateFrom.setLabel(state.getMap().get("from value").toString());
				JAXBElement<StateMarkup> eleFrom =
					new JAXBElement<StateMarkup>(new QName("StateMarkup"), StateMarkup.class, stateFrom);
				cFrom.getStatusOrModifierOrState().add(eleFrom);
				CategoricalMarkup cTo = sddFactory.createCategoricalMarkup();
				cTo.setLabel(charName.concat("_to"));
				StateMarkup stateTo = sddFactory.createStateMarkup();
				stateTo.setLabel(state.getMap().get("to value").toString());
				JAXBElement<StateMarkup> eleTo =
					new JAXBElement<StateMarkup>(new QName("StateMarkup"), StateMarkup.class, stateTo);
				cTo.getStatusOrModifierOrState().add(eleTo);
				if (state.getModifier() != null) {
					addModifierToCharacterMarkup(cFrom, state);
					addModifierToCharacterMarkup(cTo, state);
				}
				concept.getMarkupGroup().add(cFrom);
				concept.getMarkupGroup().add(cTo);
			}
		} else {
			if(TypeUtil.isNumeric(state.getMap().get("value"))) {
				QuantitativeMarkup qm = sddFactory.createQuantitativeMarkup();
				qm.setLabel(charName);
				ValueMarkup value = sddFactory.createValueMarkup();
				value.setValue((Double)state.getMap().get("value"));
				JAXBElement<ValueMarkup> ele = 
					new JAXBElement<ValueMarkup>(new QName("ValueMarkup"), ValueMarkup.class, value);
				qm.getStatusOrModifierOrMeasure().add(ele);
				if(state.getModifier() != null) {
					addModifierToCharacterMarkup(qm, state);
				}
				concept.getMarkupGroup().add(qm);
			}
			else {
				CategoricalMarkup cm = sddFactory.createCategoricalMarkup();
				cm.setLabel(charName);
				StateMarkup stateVal = sddFactory.createStateMarkup();
				stateVal.setLabel(state.getMap().get(("value")).toString());
				JAXBElement<StateMarkup> ele =
					new JAXBElement<StateMarkup>(new QName("StateMarkup"), StateMarkup.class, stateVal);
				cm.getStatusOrModifierOrState().add(ele);
				if(state.getModifier() != null) {
					addModifierToCharacterMarkup(cm, state);
				}
				concept.getMarkupGroup().add(cm);
			}
		}
		
	}

	private void addModifierToCharacterMarkup(AbstractCharacterMarkup cFrom,
			IState state) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Add a representation element to a natural language description.
	 * @param description
	 * @param taxon
	 * @deprecated
	 */
	private void addRepresentationToNaturalLanguageDescription(
			NaturalLanguageDescription description, ITaxon taxon) {
		Representation rep = sddFactory.createRepresentation();
		LabelText labelText = sddFactory.createLabelText();
		labelText.setValue("placeholder");
		DetailText detailText = sddFactory.createDetailText();
		detailText.setValue("placeholder");
		rep.getRepresentationGroup().add(labelText);
		rep.getRepresentationGroup().add(detailText);
		description.setRepresentation(rep);
	}

	/**
	 * Adds metadata to the root Datasets object.  Throws on the current time as the "created" field
	 * and a Generator named "RDF to SDD conversion tool."
	 * @param root
	 */
	protected void addMetadata(Datasets root) {
		TechnicalMetadata metadata = sddFactory.createTechnicalMetadata();
		XMLGregorianCalendar xgcNow = XMLGregorianCalendarConverter.asXMLGregorianCalendar(new Date(System.currentTimeMillis()));
		metadata.setCreated(xgcNow);
		DocumentGenerator gen = sddFactory.createDocumentGenerator();
		gen.setName("RDF to SDD conversion tool.");
		gen.setVersion("0.1");
		metadata.setGenerator(gen);
		root.setTechnicalMetadata(metadata);
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

}
