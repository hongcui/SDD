package conversion;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import sdd.AbstractCharacterDefinition;
import sdd.AbstractCharacterMarkup;
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
import sdd.ConceptMarkup;
import sdd.ConceptStateRef;
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
import sdd.QuantitativeMarkup;
import sdd.Representation;
import sdd.StateMarkup;
import sdd.TechnicalMetadata;
import sdd.ValueMarkup;
import states.IState;
import states.RangeState;
import states.SingletonState;
import taxonomy.ITaxon;
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
	private Map<String, sdd.AbstractRef> refs;
	
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
	 * Adds a Dataset to the root Datasets object, using data from taxon object.
	 * @param root The root object of the SDD document.
	 * @param taxon Object to retrieve data from.
	 */
	private void addDataset(Datasets root, ITaxon taxon) {
		Dataset dataset = sddFactory.createDataset();
		dataset.setLang("en-us");
		addRepresentationToDataset(dataset, taxon);
		addDescriptiveConceptsToDataset(dataset, taxon);
		root.getDataset().add(dataset);
	}

	/**
	 * Adds a set of descriptive concepts (based on structures from annotation schema)
	 * to a dataset.
	 * @param dataset
	 * @param taxon
	 */
	private void addDescriptiveConceptsToDataset(Dataset dataset, ITaxon taxon) {
		DescriptiveConceptSet dcSet = sddFactory.createDescriptiveConceptSet();
		CharacterSet characterSet = sddFactory.createCharacterSet();
		Map<String, AbstractCharacterDefinition> charsToAdd = new HashMap<String, AbstractCharacterDefinition>();
		CharacterTreeSet characterTreeSet = sddFactory.createCharacterTreeSet();
		CharacterTree characterTree = sddFactory.createCharacterTree();
		Representation ctRep = sddFactory.createRepresentation();
		LabelText ctLabelText = sddFactory.createLabelText();
		ctLabelText.setValue("Structural Character tree");
		ctRep.getRepresentationGroup().add(ctLabelText);
		characterTree.setRepresentation(ctRep);
		
		Iterator<TreeNode<Structure>> iter = taxon.getStructureTree().iterator();
		Map<String, DescriptiveConcept> dcsToAdd = new HashMap<String, DescriptiveConcept>();
		System.out.print(taxon.getStructureTree().toString());
		
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
			dcsToAdd.put(s.getId(), dc);
			List<AbstractCharacterDefinition> charsForCharacterTree = addCharactersToCharacterSet(charsToAdd, s.getCharStateMap());
			addToCharacterTree(characterTree, dc, charsForCharacterTree, node);
		}
		dcSet.getDescriptiveConcept().addAll(dcsToAdd.values());
		dataset.setDescriptiveConcepts(dcSet);
		characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter().addAll(charsToAdd.values());
		dataset.setCharacters(characterSet);
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
	private void addToCharacterTree(CharacterTree characterTree,
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
	 * @param characterSet
	 * @param charStateMap
	 * @return A list of SDD characters to be used as CharNodes in the character tree.
	 */
	private List<AbstractCharacterDefinition> addCharactersToCharacterSet(Map<String, AbstractCharacterDefinition> characterMap,
			Map<String, IState> charStateMap) {
		List<AbstractCharacterDefinition> characters = new ArrayList<AbstractCharacterDefinition>();
		for(String s : charStateMap.keySet()) {
			AbstractCharacterDefinition character = null;
			IState state = charStateMap.get(s);
			if(state instanceof SingletonState) {
				Representation rep = sddFactory.createRepresentation();
				LabelText labelText = sddFactory.createLabelText();
				labelText.setValue(s);
				rep.getRepresentationGroup().add(labelText);
				Representation stateRep = sddFactory.createRepresentation();
				LabelText stateLabelText = sddFactory.createLabelText();
				stateLabelText.setValue((String) state.getMap().get("value"));
				stateRep.getRepresentationGroup().add(stateLabelText);
				
				if(state.getMap().get("value") instanceof String) {
					String stateName = (String) state.getMap().get("value");
					character = sddFactory.createCategoricalCharacter();
					((sdd.CategoricalCharacter)character).setStates(sddFactory.createCharacterStateSeq());
					if(!refs.containsKey(stateName)) {
						CharacterLocalStateDef localStateDef = sddFactory.createCharacterLocalStateDef();
						localStateDef.setId(stateName);
						localStateDef.setRepresentation(stateRep);
						localStateDef.setId("state_".concat(stateName));
						((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(localStateDef);
						//add reference to refs map for future use.
						ConceptStateRef stateRef = sddFactory.createConceptStateRef();
						stateRef.setRef(localStateDef.getId());
						refs.put(stateName, stateRef);
					}
					else {
						((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(refs.get(stateName));
					}
				}
				else {
					character = sddFactory.createQuantitativeCharacter();
				}
				character.setRepresentation(rep);
			}
			else if(state instanceof RangeState){
				Representation rep = sddFactory.createRepresentation();
				LabelText labelText = sddFactory.createLabelText();
				labelText.setValue(s);
				rep.getRepresentationGroup().add(labelText);
				
				if(state.getMap().get("from value") instanceof String) {
					character = sddFactory.createCategoricalCharacter();
					((sdd.CategoricalCharacter)character).setStates(sddFactory.createCharacterStateSeq());
				}
				else {
					character = sddFactory.createQuantitativeCharacter();
				}
				character.setRepresentation(rep);
			}
			character.setId(s);
			characters.add(character);
			mergeNewCharacter(characterMap, character);
		}
		return characters;		
	}

	private void mergeNewCharacter(
			Map<String, AbstractCharacterDefinition> characterMap,
			AbstractCharacterDefinition character) {
		String charName = character.getId();
		if(!characterMap.containsKey(charName))
			characterMap.put(charName, character);
		else {
			AbstractCharacterDefinition presentChar = characterMap.get(charName);
			if(presentChar instanceof sdd.CategoricalCharacter) {
				if(character instanceof sdd.CategoricalCharacter) {
					//then just merge states of the two
					((sdd.CategoricalCharacter)presentChar).getStates().getStateDefinitionOrStateReference().
						addAll(
								((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference());
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
	 * Add a represenation element to a Dataset.
	 * @param dataset
	 * @param taxon
	 */
	private void addRepresentationToDataset(Dataset dataset, ITaxon taxon) {
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
	private void addMetadata(Datasets root) {
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
