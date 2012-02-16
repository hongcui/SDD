//package conversion;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeMap;
//import java.util.TreeSet;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBElement;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.datatype.XMLGregorianCalendar;
//import javax.xml.namespace.QName;
//
//import sdd.AbstractCharSummaryData;
//import sdd.AbstractCharacterDefinition;
//import sdd.AbstractCharacterMarkup;
//import sdd.AbstractRef;
//import sdd.CatSummaryData;
//import sdd.CategoricalCharacter;
//import sdd.CategoricalMarkup;
//import sdd.CharTreeAbstractNode;
//import sdd.CharTreeCharacter;
//import sdd.CharTreeNode;
//import sdd.CharTreeNodeRef;
//import sdd.CharTreeNodeSeq;
//import sdd.CharacterLocalStateDef;
//import sdd.CharacterRef;
//import sdd.CharacterSet;
//import sdd.CharacterTree;
//import sdd.CharacterTreeSet;
//import sdd.CodedDescription;
//import sdd.ConceptMarkup;
//import sdd.ConceptStateDef;
//import sdd.ConceptStateRef;
//import sdd.ConceptStateSeq;
//import sdd.Dataset;
//import sdd.Datasets;
//import sdd.DescriptiveConcept;
//import sdd.DescriptiveConceptRef;
//import sdd.DescriptiveConceptSet;
//import sdd.DetailText;
//import sdd.DocumentGenerator;
//import sdd.LabelText;
//import sdd.MarkupText;
//import sdd.ModifierDef;
//import sdd.ModifierRefWithData;
//import sdd.ModifierSeq;
//import sdd.NaturalLanguageDescription;
//import sdd.NaturalLanguageDescriptionSet;
//import sdd.NaturalLanguageMarkup;
//import sdd.ObjectFactory;
//import sdd.QuantSummaryData;
//import sdd.QuantitativeCharMapping;
//import sdd.QuantitativeCharMappingSet;
//import sdd.QuantitativeCharacter;
//import sdd.QuantitativeMarkup;
//import sdd.Representation;
//import sdd.StateData;
//import sdd.StateMarkup;
//import sdd.TaxonHierarchyCore;
//import sdd.TaxonHierarchyNode;
//import sdd.TaxonHierarchyNodeRef;
//import sdd.TaxonHierarchyNodeSeq;
//import sdd.TaxonHierarchySet;
//import sdd.TaxonNameCore;
//import sdd.TaxonNameRef;
//import sdd.TaxonomicRank;
//import sdd.TaxonomicScopeSet;
//import sdd.TechnicalMetadata;
//import sdd.UnivarSimpleStatMeasureData;
//import sdd.ValueMarkup;
//import sdd.ValueRangeWithClass;
//import states.IState;
//import states.RangeState;
//import states.SingletonState;
//import taxonomy.ITaxon;
//import taxonomy.TaxonHierarchy;
//import tree.TreeNode;
//import util.QuantitativeStateDef;
//import util.TypeUtil;
//import util.XMLGregorianCalendarConverter;
//import annotationSchema.jaxb.Structure;
//
//import com.hp.hpl.jena.rdf.model.Model;
//
///**
// * Class for converting an RDF model into SDD.
// * @author Alex
// *
// */
//public class SDDConverter {
//	
//	private Model model;
//	private JAXBContext sddContext;
//	private ObjectFactory sddFactory;
//	
//	/** This maps state names to State References.  For use when a state is defined previously and is a valid state for a different, later
//	 * character. 
//	 */
//	private Map<String, sdd.AbstractRef> refs;
//	
//	/** This maps taxon names to a map from CharacterDefinitions to Sets of state definitions.  This is basically holding the taxon-by-character
//	 * matrix information for building a CodedDescription.
//	 */
//	private Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> taxonNameToCharState;
//	
//	/** This map maintains the DescriptiveConcepts that will be added to the Dataset.	 */
//	private Map<String, DescriptiveConcept> dcsToAdd;
//	
//	/** This map maintains the Characters that will be added to the Dataset.	 */
//	private Map<String, AbstractCharacterDefinition> charsToAdd;
//	
//	/** Maintains a mapping from ITaxon objects to SDD TaxonNames (used as reference in building SDD TaxonHierarchy).	 */
//	private Map<ITaxon, TaxonNameCore> taxonToTaxonName;
//	
//	/** Used to keep track of which DescriptiveConcepts are pointed to by which CharacterTreeNodes.	 */
//	private Map<DescriptiveConcept, CharTreeNode> dcToCtNode;
//	
//	/** Maps names (Strings) of modifiers to their SDD ModifierDef.	 */
//	private Map<String, ModifierDef> modifiers;
//	
//	/** Maps ITaxon name to the characters that contain modifiers.	 */
//	private Map<String, Map<CharacterLocalStateDef, ModifierDef>> stateToModifier;
//	
//	/** Have a Descriptive Concept holding ConceptStates for global use. */
//	private DescriptiveConcept globalStates;
//	
//	/**
//	 * This map contains ConceptStates that are used by more than one character, and must
//	 * therefore be global ConceptStates, and not just local CharacterDefs.
//	 */
//	private Map<String, ConceptStateRef> mustBeGlobal;
//	
//	/**
//	 * Create a new converter object from a single taxon model.
//	 * @param taxonModel
//	 */
//	public SDDConverter(Model taxonModel) {
//		this.model = taxonModel;
//		try {
//			this.sddContext = JAXBContext.newInstance(sdd.ObjectFactory.class);
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//		this.sddFactory = new ObjectFactory();
//		this.refs = new HashMap<String, sdd.AbstractRef>();
//		this.taxonNameToCharState = new HashMap<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>>();
//	}
//	
//	/**
//	 * Constructor used to convert a whole hierarchy of taxa, and not just a single taxon.
//	 */
//	public SDDConverter() {
//		try {
//			this.sddContext = JAXBContext.newInstance(sdd.ObjectFactory.class);
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//		this.sddFactory = new ObjectFactory();
//		this.refs = new HashMap<String, sdd.AbstractRef>();
//		this.taxonNameToCharState = new HashMap<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>>();
//		dcsToAdd = new HashMap<String, DescriptiveConcept>();
//		charsToAdd = new TreeMap<String, AbstractCharacterDefinition>();
//		this.taxonToTaxonName = new HashMap<ITaxon, TaxonNameCore>();
//		this.dcToCtNode = new HashMap<DescriptiveConcept, CharTreeNode>();
//		this.modifiers = new HashMap<String, ModifierDef>();
//		this.stateToModifier = new HashMap<String, Map<CharacterLocalStateDef, ModifierDef>>();
//		//global Descriptive Concept for global states
//		this.globalStates = sddFactory.createDescriptiveConcept();
//		Representation rep = sddFactory.createRepresentation();
//		LabelText labelText = sddFactory.createLabelText();
//		labelText.setValue("Descriptive Concept for holding global states.");
//		rep.getRepresentationGroup().add(labelText);
//		this.globalStates.setRepresentation(rep);
//		this.globalStates.setId("dc_states");
//		ConceptStateSeq stateSeq = sddFactory.createConceptStateSeq();
//		this.globalStates.setConceptStates(stateSeq);
//		this.mustBeGlobal = new HashMap<String, ConceptStateRef>();
//	}
//
//	/**
//	 * Transform the model for a single taxon into an SDD document.
//	 * @param filename Name of the xml document.
//	 */
//	public void taxonToSDD(ITaxon taxon, String filename) {
//		try {
//			Marshaller marshaller = sddContext.createMarshaller();
//			Datasets root = sddFactory.createDatasets();
//			addMetadata(root);
//			addDataset(root, taxon);
//			marshaller.marshal(root, new File(filename));
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Converts a TaxonHierarchy into an associated SDD XML file.  A Datasets SDD object is created
//	 * and set as the root of the XML file.
//	 * @param hierarchy
//	 * @param filename
//	 */
//	public void taxonHierarchyToSDD(TaxonHierarchy hierarchy, String filename) {
//		try {
//			Marshaller marshaller = sddContext.createMarshaller();
//			Datasets root = sddFactory.createDatasets();
//			addMetadata(root);
//			addDataset(root, hierarchy);
//			marshaller.marshal(root, new File(filename));
//			System.out.println("Modifier map:");
//			for(String s : this.stateToModifier.keySet()) {
//				System.out.println(s);
//				Map<CharacterLocalStateDef, ModifierDef> map = this.stateToModifier.get(s);
//				for(CharacterLocalStateDef state : map.keySet())
//					System.out.println("\t"+state+": "+map.get(state));
//			}
//			System.out.println("End modifier map.");
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Converts a TaxonHierarchy into an associated SDD XML file.  A Datasets SDD object is created
//	 * and set as the root of the XML file. This method only uses the taxa from the hierarchy that
//	 * match taxonRank.
//	 * @param hierarchy
//	 * @param taxonRank
//	 * @param filename
//	 */
//	public void taxonHierarchyToSDD(TaxonHierarchy hierarchy, String taxonRank, String filename) {
//		try {
//			Marshaller marshaller = sddContext.createMarshaller();
//			Datasets root = sddFactory.createDatasets();
//			addMetadata(root);
//			addDataset(root, hierarchy, taxonRank);
//			marshaller.marshal(root, new File(filename));
//			System.out.println("Modifier map:");
//			for(String s : this.stateToModifier.keySet()) {
//				System.out.println(s);
//				Map<CharacterLocalStateDef, ModifierDef> map = this.stateToModifier.get(s);
//				for(CharacterLocalStateDef state : map.keySet())
//					System.out.println("\t"+state+": "+map.get(state));
//			}
//			System.out.println("End modifier map.");
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Adds a Dataset to the root Datasets object, using data from all of the taxa in the hierarchy.
//	 * @param root
//	 * @param hierarchy
//	 */
//	protected void addDataset(Datasets root, TaxonHierarchy hierarchy) {
//		Dataset dataset = sddFactory.createDataset();
//		dataset.setLang("en-us");
//		addRepresentationToDataset(dataset, hierarchy.getHierarchy().getRoot().getElement());
//		Iterator<TreeNode<ITaxon>> iter = hierarchy.getHierarchy().iterator();
//		
//		//Have a Modifier Descriptive Concept for global use.
//		DescriptiveConcept dcModifiers = sddFactory.createDescriptiveConcept();
//		dcModifiers.setId("modifiers");
//		Representation modRep = sddFactory.createRepresentation();
//		LabelText labelTextMod = sddFactory.createLabelText();
//		labelTextMod.setValue("Descriptive Concept for holding modifiers.");
//		modRep.getRepresentationGroup().add(labelTextMod);
//		dcModifiers.setRepresentation(modRep);
//		
//		while(iter.hasNext()) {
//			TreeNode<ITaxon> node = iter.next();
//			ITaxon taxon = node.getElement();
//			addTaxonNameToDataset(dataset, taxon);
//			addDescriptiveConceptsToDataset(dataset, taxon, dcModifiers);
//			addCodedDescriptionToDataset(dataset, taxon);
//		}
//		addTaxonHierarchyToDataset(dataset, hierarchy);
//		dcsToAdd.put("modifiers", dcModifiers);
//		dcsToAdd.put("globalStates", this.globalStates);
//		DescriptiveConceptSet dcSet = sddFactory.createDescriptiveConceptSet();
//		
//		dataset.setDescriptiveConcepts(dcSet);
//		CharacterSet characterSet = sddFactory.createCharacterSet();
//		characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter().addAll(charsToAdd.values());
//		postProcessCharacterSet(characterSet);
//		dcSet.getDescriptiveConcept().addAll(dcsToAdd.values());
//		dataset.setCharacters(characterSet);
//		root.getDataset().add(dataset);
//		
//	}
//	
//	/**
//	 * Adds a Dataset to the root Datasets object, using data from taxa in the hierarchy
//	 * that match the taxonRank specified.
//	 * @param root
//	 * @param hierarchy
//	 * @param taxonRank
//	 */
//	protected void addDataset(Datasets root, TaxonHierarchy hierarchy, String taxonRank) {
//		Dataset dataset = sddFactory.createDataset();
//		dataset.setLang("en-us");
//		addRepresentationToDataset(dataset, hierarchy.getHierarchy().getRoot().getElement());
//		Iterator<TreeNode<ITaxon>> iter = hierarchy.rankIterator(taxonRank);
//		
//		//Have a Modifier Descriptive Concept for global use.
//		DescriptiveConcept dcModifiers = sddFactory.createDescriptiveConcept();
//		dcModifiers.setId("modifiers");
//		Representation modRep = sddFactory.createRepresentation();
//		LabelText labelTextMod = sddFactory.createLabelText();
//		labelTextMod.setValue("Descriptive Concept for holding modifiers.");
//		modRep.getRepresentationGroup().add(labelTextMod);
//		dcModifiers.setRepresentation(modRep);
//		
//		while(iter.hasNext()) {
//			TreeNode<ITaxon> node = iter.next();
//			ITaxon taxon = node.getElement();
//			System.out.println("Processing taxon: " + taxon.toString());
//			addTaxonNameToDataset(dataset, taxon);
//			addDescriptiveConceptsToDataset(dataset, taxon, dcModifiers);
//			addCodedDescriptionToDataset(dataset, taxon);
//		}
//		addTaxonHierarchyToDataset(dataset, hierarchy);
//		dcsToAdd.put("modifiers", dcModifiers);
//		dcsToAdd.put("globalStates", this.globalStates);
//		DescriptiveConceptSet dcSet = sddFactory.createDescriptiveConceptSet();
//		
//		dataset.setDescriptiveConcepts(dcSet);
//		CharacterSet characterSet = sddFactory.createCharacterSet();
//		characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter().addAll(charsToAdd.values());
//		postProcessCharacterSet(characterSet);
//		dcSet.getDescriptiveConcept().addAll(dcsToAdd.values());
//		dataset.setCharacters(characterSet);
//		root.getDataset().add(dataset);
//	}
//
//	/**
//	 * Places a Taxon Hierarchy in the dataset according to the Hierarchy defined under the TaxonHierarchy object.
//	 * The Taxon Names to which the hierarchy nodes refer are defined by {@link SDDConverter#addTaxonNameToDataset(Dataset, ITaxon)}.
//	 * @param dataset
//	 * @param hierarchy The hierarchy object from which an SDD Taxon Hierarchy is induced.
//	 */
//	protected void addTaxonHierarchyToDataset(Dataset dataset, TaxonHierarchy hierarchy) {
//		TaxonHierarchySet thSet = sddFactory.createTaxonHierarchySet();
//		TaxonHierarchyCore core = sddFactory.createTaxonHierarchyCore();
//		Representation rep = sddFactory.createRepresentation();
//		LabelText labelText = sddFactory.createLabelText();
//		labelText.setValue("Taxon hierarchy defined by this collection of descriptions.");
//		rep.getRepresentationGroup().add(labelText);
//		core.setRepresentation(rep);
//		core.setTaxonHierarchyType(new QName("http://rs.tdwg.org/UBIF/2006/", "PhylogeneticTaxonomy"));
//		TaxonHierarchyNodeSeq nodeSeq = sddFactory.createTaxonHierarchyNodeSeq();
//		Iterator<TreeNode<ITaxon>> iter = hierarchy.getHierarchy().iterator();
//		
//		while(iter.hasNext()) {
//			TreeNode<ITaxon> treeNode = iter.next();
//			ITaxon taxon = treeNode.getElement();
//			if(!this.taxonToTaxonName.containsKey(taxon))
//				addTaxonNameToDataset(dataset, taxon);
//			TaxonNameCore taxonName = this.taxonToTaxonName.get(taxon);
//			TaxonHierarchyNode taxonNode = sddFactory.createTaxonHierarchyNode();
//			taxonNode.setId("th_node_".concat(taxon.getName()));
//			TaxonNameRef ref = sddFactory.createTaxonNameRef();
//			ref.setRef(taxonName.getId());
//			taxonNode.setTaxonName(ref);
//			
//			TreeNode<ITaxon> parentTreeNode = treeNode.getParent();
//			if(parentTreeNode != null) {
//				ITaxon parentTaxon = parentTreeNode.getElement();
//				TaxonHierarchyNodeRef parentNodeRef = sddFactory.createTaxonHierarchyNodeRef();
//				parentNodeRef.setRef("th_node_".concat(parentTaxon.getName()));
//				taxonNode.setParent(parentNodeRef);
//			}
//			nodeSeq.getNode().add(taxonNode);
//		}
//		core.setNodes(nodeSeq);
//		thSet.getTaxonHierarchy().add(core);
//		dataset.setTaxonHierarchies(thSet);
//	}
//
//	/**
//	 * Adds a Dataset to the root Datasets object, using data from taxon object.
//	 * @param root The root object of the SDD document.
//	 * @param taxon Object to retrieve data from.
//	 */
//	protected void addDataset(Datasets root, ITaxon taxon) {
//		Dataset dataset = sddFactory.createDataset();
//		dataset.setLang("en-us");
//		addTaxonNameToDataset(dataset, taxon);
//		addRepresentationToDataset(dataset, taxon);
//		addDescriptiveConceptsToDataset(dataset, taxon, null);
//		addCodedDescriptionToDataset(dataset, taxon);
//		root.getDataset().add(dataset);
//	}
//
//	/**
//	 * Add a taxon name and rank to a dataset.
//	 * @param dataset
//	 * @param taxon
//	 */
//	protected void addTaxonNameToDataset(Dataset dataset, ITaxon taxon) {
//		TaxonNameCore taxonNameCore = sddFactory.createTaxonNameCore();
//		taxonNameCore.setId(taxon.getName());
//		TaxonomicRank taxonomicRank = sddFactory.createTaxonomicRank();
//		String rank = taxon.getTaxonRank().toString().toLowerCase();
//		taxonomicRank.setLiteral(rank);
//		Representation rep = sddFactory.createRepresentation();
//		LabelText labelText = sddFactory.createLabelText();
//		labelText.setValue(rank.concat(" ").concat(taxon.getName()));
//		rep.getRepresentationGroup().add(labelText);
//		taxonNameCore.setRepresentation(rep);
//		taxonNameCore.setRank(taxonomicRank);
//		if(dataset.getTaxonNames() == null)
//			dataset.setTaxonNames(sddFactory.createTaxonNameSet());
//		dataset.getTaxonNames().getTaxonName().add(taxonNameCore);
//		taxonToTaxonName.put(taxon, taxonNameCore);
//	}
//
//	/**
//	 * Adds a set of descriptive concepts (based on structures from annotation schema).  This is really adding the bulk of the DescriptiveTerminology,
//	 * since it also calls the methods to build a CharacterSet and CharacterTrees.
//	 * to a dataset.
//	 * @param dataset
//	 * @param taxon
//	 * @param dcModifiers The DescriptiveConcept object that contains modifiers used throughout the document. 
//	 */
//	protected void addDescriptiveConceptsToDataset(Dataset dataset, ITaxon taxon, DescriptiveConcept dcModifiers) {
//		DescriptiveConceptSet dcSet = null;
//		if(dataset.getDescriptiveConcepts() == null)
//			dcSet = sddFactory.createDescriptiveConceptSet();
//		else
//			dcSet = dataset.getDescriptiveConcepts();
//		CharacterSet characterSet = null;
//		if(dataset.getCharacters() == null)
//			characterSet = sddFactory.createCharacterSet();
//		else
//			characterSet = dataset.getCharacters();
//		CharacterTreeSet characterTreeSet = null;
//		if(dataset.getCharacterTrees() == null)
//			characterTreeSet = sddFactory.createCharacterTreeSet();
//		else
//			characterTreeSet = dataset.getCharacterTrees();
//		CharacterTree characterTree = sddFactory.createCharacterTree();
//		Representation ctRep = sddFactory.createRepresentation();
//		LabelText ctLabelText = sddFactory.createLabelText();
//		ctLabelText.setValue("Structural Character tree");
//		ctRep.getRepresentationGroup().add(ctLabelText);
//		characterTree.setRepresentation(ctRep);
//		TaxonomicScopeSet taxonomicScopeSet = sddFactory.createTaxonomicScopeSet();
//		TaxonNameRef taxonNameRef = sddFactory.createTaxonNameRef();
//		taxonNameRef.setRef(taxonToTaxonName.get(taxon).getId());
//		taxonomicScopeSet.getTaxonName().add(taxonNameRef);
//		characterTree.setScope(taxonomicScopeSet);
//		
//		Iterator<TreeNode<Structure>> iter = taxon.getStructureTree().iterator();		
//		while(iter.hasNext()) {
//			TreeNode<Structure> node = iter.next();
//			Structure s = node.getElement();
//			DescriptiveConcept dc = sddFactory.createDescriptiveConcept();
//			dc.setId(s.getName());
//			Representation rep = sddFactory.createRepresentation();
//			LabelText labelText = sddFactory.createLabelText();
//			labelText.setValue(s.getName());
//			rep.getRepresentationGroup().add(labelText);
//			dc.setRepresentation(rep);
//			dcsToAdd.put(s.getName(), dc);
//			List<AbstractCharacterDefinition> charsForCharacterTree = addCharactersToCharacterSet(charsToAdd, s.getCharStateMap(), node, taxon.getName());
//			addToCharacterTree(characterTree, dc, charsForCharacterTree, node);
//			addToModifiersFromDescriptiveConcepts(taxon, dc, dcModifiers, s);
//		}
//		//This needs to be done Globally, not in the method!
////		dcSet.getDescriptiveConcept().addAll(dcsToAdd.values());
////		dataset.setDescriptiveConcepts(dcSet);
////		characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter().addAll(charsToAdd.values());
////		dataset.setCharacters(characterSet);
//		characterTreeSet.getCharacterTree().add(characterTree);
//		dataset.setCharacterTrees(characterTreeSet);
//	}
//
//	/**
//	 * Adds modifier set to Descriptive Concept definition.
//	 * @param taxon Taxon object from which modifiers are being added.
//	 * @param dc
//	 * @param modifiersDc
//	 * @param s Structure containing char-state map to get modifiers from.
//	 */
//	protected void addToModifiersFromDescriptiveConcepts(ITaxon taxon, DescriptiveConcept dc,
//			DescriptiveConcept modifiersDc, Structure s) {
//		ModifierSeq modifierSeq = modifiersDc.getModifiers();
//		if(modifierSeq == null) {
//			modifierSeq = sddFactory.createModifierSeq();
//			modifiersDc.setModifiers(modifierSeq);
//		}
//		for(String charName : s.getCharStateMap().keySet()) {
//			IState state = s.getCharStateMap().get(charName);
//			if(state.getModifier() != null) {
//				ModifierDef modifierDef;
//				if(! this.modifiers.containsKey(state.getModifier())) {
//					modifierDef = sddFactory.createModifierDef();
//					modifierDef.setId("mod_".concat(state.getModifier().replace(" ", "_")));
//					Representation rep = sddFactory.createRepresentation();
//					LabelText labelText = sddFactory.createLabelText();
//					labelText.setValue(state.getModifier());
//					rep.getRepresentationGroup().add(labelText);
//					modifierDef.setRepresentation(rep);
//					modifierSeq.getModifier().add(modifierDef);
//					modifiers.put(state.getModifier(), modifierDef);
//				}
//			}
//		}
//	}
//
//	/**
//	 * This method first adds a Node to a character tree that points to the descriptive concept object.  It then adds some character nodes 
//	 * as references that point to the descriptive concept.
//	 * @param characterTree
//	 * @param dc
//	 * @param charsForCharacterTree
//	 * @param node
//	 */
//	protected void addToCharacterTree(CharacterTree characterTree,
//			DescriptiveConcept dc,
//			List<AbstractCharacterDefinition> charsForCharacterTree,
//			TreeNode<Structure> node) {
//		//make a new DC node for the tree, set it's reference to the id of the actual Descriptive Concept, set
//		//the ID to be the id of the DC object with 'dc_node' in front, and point to the parent of the DC object.
//		Object dcNode = null;
//		String dcNodeId = null;
//		DescriptiveConceptRef dcRef = sddFactory.createDescriptiveConceptRef();
//		dcRef.setRef(dc.getId());
//		if(this.dcToCtNode.containsKey(dc)) {
//			dcNode = sddFactory.createCharTreeNodeRef();
//			((CharTreeNodeRef)dcNode).setRef(this.dcToCtNode.get(dc).getId());
//			dcNodeId = ((CharTreeNodeRef) dcNode).getRef();
//		}
//		else {
//			dcNode = sddFactory.createCharTreeNode();
//			((CharTreeNode) dcNode).setDescriptiveConcept(dcRef);
//			((CharTreeNode) dcNode).setId("dc_node_"+dc.getId());
//			this.dcToCtNode.put(dc, (CharTreeNode) dcNode);
//			dcNodeId = ((CharTreeNode)dcNode).getId();
//		}
//		if(node.getParent() != null) {
//			Structure parentStructure = node.getParent().getElement();
//			CharTreeNodeRef ctNodeRef = sddFactory.createCharTreeNodeRef();
//			ctNodeRef.setRef("dc_node_"+parentStructure.getName());
//			if(dcNode instanceof CharTreeNode)
//				((CharTreeNode) dcNode).setParent(ctNodeRef);
//		}
//		//Now, for each character in the character list, make a CharNode for the tree, and point to the descriptive
//		//concept node as the parent.
//		for(AbstractCharacterDefinition character : charsForCharacterTree) {
//			CharTreeCharacter ctCharNode = sddFactory.createCharTreeCharacter();
//			CharacterRef charRef = sddFactory.createCharacterRef();
//			charRef.setRef(character.getId());
//			ctCharNode.setCharacter(charRef);
//			CharTreeNodeRef ctNodeRef = sddFactory.createCharTreeNodeRef();
//			ctNodeRef.setRef(dcNodeId);
//			ctCharNode.setParent(ctNodeRef);
//			if(characterTree.getNodes() == null) {
//				CharTreeNodeSeq ctNodeSeq = sddFactory.createCharTreeNodeSeq();
//				characterTree.setNodes(ctNodeSeq);
//			}
//			if(!characterTree.getNodes().getNodeOrCharNode().contains(ctCharNode))
//				characterTree.getNodes().getNodeOrCharNode().add(ctCharNode);
//		}
//		if(dcNode instanceof CharTreeAbstractNode)
//			characterTree.getNodes().getNodeOrCharNode().add((CharTreeAbstractNode) dcNode);
//	}
//
//	/**
//	 * Adds characters from a map between character names and states to an SDD Character Set
//	 * @param characterMap This map will maintain pairs from character names to SDD Characters.  Passed on to mergeCharacters method and back to the 
//	 * addDescriptiveConceptsToDataset method, helping to maintain the Character Terminology.
//	 * @param charStateMap This is a mapping from character names to IState objects as provided by the ITaxon.
//	 * @param node Node from the Structure tree of a taxon, from which a full character name will be derived.
//	 * @param taxonName 
//	 * @return A list of SDD characters to be used as CharNodes in the character tree.
//	 */
//	protected List<AbstractCharacterDefinition> addCharactersToCharacterSet(Map<String, AbstractCharacterDefinition> characterMap,
//			Map<String, IState> charStateMap, TreeNode<Structure> node, String taxonName) {
//		List<AbstractCharacterDefinition> characters = new ArrayList<AbstractCharacterDefinition>();
//		if(!taxonNameToCharState.containsKey(taxonName))
//			taxonNameToCharState.put(taxonName, new HashMap<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>());
//		
//		Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> charStateDescMap = taxonNameToCharState.get(taxonName);
//		
//		for(String s : charStateMap.keySet()) {
//			AbstractCharacterDefinition character = null;
//			CharacterLocalStateDef localStateDef = null;
//			IState state = charStateMap.get(s);
//			String fullCharacterName = util.ConversionUtil.resolveFullCharacterName(s, node);
//			System.out.println(fullCharacterName +": " +state.toString());
//			if(state instanceof SingletonState) {
//				Representation characterRep = sddFactory.createRepresentation();
//				LabelText labelText = sddFactory.createLabelText();
//				labelText.setValue(fullCharacterName);
//				characterRep.getRepresentationGroup().add(labelText);
//				Representation stateRep = sddFactory.createRepresentation();
//				LabelText stateLabelText = sddFactory.createLabelText();
//				stateLabelText.setValue(state.getMap().get("value").toString());
//				stateRep.getRepresentationGroup().add(stateLabelText);
//				
//				if(state.getMap().get("value") instanceof String) {
//					String stateName = (String) state.getMap().get("value");
//					character = sddFactory.createCategoricalCharacter();
//					character.setId(fullCharacterName);
//					//Debugging...
//					if(character.getId().equals("stem_architecture_or_shape") ||
//							character.getId().equals("stem_architecture"))
//						System.out.println("Start Debugging the stem_architecture_or_shape bug...");
//					((sdd.CategoricalCharacter)character).setStates(sddFactory.createCharacterStateSeq());
//					localStateDef = sddFactory.createCharacterLocalStateDef();
//					localStateDef.setId(stateName);
//					localStateDef.setRepresentation(stateRep);
//					localStateDef.setId("state_".concat(stateName));
//					AbstractRef stateRef;
////					if(!refs.containsKey(stateName)) {	//if we're seeing this state for the first time, add it to character as a definition
////						ConceptStateDef conceptStateDef = sddFactory.createConceptStateDef();
////						conceptStateDef.setId(localStateDef.getId());
////						conceptStateDef.setRepresentation(localStateDef.getRepresentation());
////						this.globalStates.getConceptStates().getStateDefinition().add(conceptStateDef);
////						((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(localStateDef);
////						//add reference to refs map for future use.
////						stateRef = sddFactory.createConceptStateRef();
////						stateRef.setRef(localStateDef.getId());
////						refs.put(stateName, stateRef);
////					}
////					else {	//we've seen this state before and it's not a duplicate for this character, get a Concept ref. from refs Map
////						if((charStateDescMap.containsKey(character) && !charStateDescMap.get(character).contains(localStateDef))
////								|| !charStateDescMap.containsKey(character)) {
////							stateRef = refs.get(stateName);
////							((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(stateRef);
////							this.mustBeGlobal.put(stateRef.getRef(), (ConceptStateRef) stateRef);
////						}
//					//if we remove this character from the picture, is there another character that
//					//still maps to the state in question? if so, then we need it globally. Otherwise,
//					//we can leave it as a StateDefinition
//					Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> copy = 
//						new HashMap<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>>();
//					copy.putAll(this.taxonNameToCharState);
//					boolean flaggedGlobal = false;
//					for(Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> taxonMap : copy.values()) {
//						taxonMap.remove(character);	//remove from each taxon character->state map
//						for(Set<CharacterLocalStateDef> set : taxonMap.values()) {
//							if(!flaggedGlobal && set.contains(localStateDef)) {
//								stateRef = refs.get(stateName);
////								System.out.println("***Found global state for character:\n" + character.toString() 
////										+ "\n" + stateRef.toString() + "\n***");
//								((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(stateRef);
//								this.mustBeGlobal.put(stateRef.getRef(), (ConceptStateRef) stateRef);
//								flaggedGlobal = true;
//								break;
//							}
//						}
//					}					
//					if(!flaggedGlobal && !refs.containsKey(stateName)) {
//						ConceptStateDef conceptStateDef = sddFactory.createConceptStateDef();
//						conceptStateDef.setId(localStateDef.getId());
//						conceptStateDef.setRepresentation(localStateDef.getRepresentation());
//						this.globalStates.getConceptStates().getStateDefinition().add(conceptStateDef);
//						((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(localStateDef);
//						//add reference to refs map for future use.
//						stateRef = sddFactory.createConceptStateRef();
//						stateRef.setRef(localStateDef.getId());
//						refs.put(stateName, stateRef);
//					}
//					else if(!flaggedGlobal && refs.containsKey(stateName)) {
//						((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().add(refs.get(stateName));
//					}
//				}
//				else if(TypeUtil.isNumeric(state.getMap().get("value"))) {
//					character = sddFactory.createQuantitativeCharacter();
//					character.setId(fullCharacterName);
//					QuantitativeCharMappingSet mappingSet = sddFactory.createQuantitativeCharMappingSet();
//					putMappingAndRangeOnQuanChar(character, state, mappingSet);
//					localStateDef = new QuantitativeStateDef(character.getId(), character);
//				}
//				character.setRepresentation(characterRep);
//			}
//			else if(state instanceof RangeState){
//				Representation rep = sddFactory.createRepresentation();
//				LabelText labelText = sddFactory.createLabelText();
//				labelText.setValue(fullCharacterName);
//				rep.getRepresentationGroup().add(labelText);
//				
//				if(state.getMap().get("from value") instanceof String) {
//					character = sddFactory.createCategoricalCharacter();
//					character.setId(fullCharacterName);
//					((sdd.CategoricalCharacter)character).setStates(sddFactory.createCharacterStateSeq());
//				}
//				else if(TypeUtil.isNumeric(state.getMap().get("from value"))) {
//					character = sddFactory.createQuantitativeCharacter();
//					character.setId(fullCharacterName);
//					QuantitativeCharMappingSet mappingSet = sddFactory.createQuantitativeCharMappingSet();
//					putMappingAndRangeOnQuanChar(character, state, mappingSet);
//					localStateDef = new QuantitativeStateDef(character.getId(), character);
//				}
//				else {
//					character = sddFactory.createQuantitativeCharacter();
//					character.setId(fullCharacterName);
//				}
//				character.setRepresentation(rep);
//			}
//			characters.add(character);
//			mergeNewCharacter(characterMap, character, taxonName);
//			if(!charStateDescMap.containsKey(character))
//				charStateDescMap.put(character, new HashSet<CharacterLocalStateDef>());
//			charStateDescMap.get(character).add(localStateDef);
//			//map the taxon to the char/state modifier, if modifier is not null
//			String modName = state.getModifier();
//			if(modName != null) {
//				ModifierDef modDef = this.modifiers.get(modName);
//				if(this.stateToModifier.get(taxonName) == null)
//					this.stateToModifier.put(taxonName, new HashMap<CharacterLocalStateDef, ModifierDef>());
//				this.stateToModifier.get(taxonName).put(localStateDef, modDef);
//			}
//		}
//		return characters;		
//	}
//
//	/**
//	 * Adds a mapping and value range to the mapping set of a QuantitativeCharacter.
//	 * @param character
//	 * @param state
//	 */
//	protected void putMappingAndRangeOnQuanChar(
//			AbstractCharacterDefinition character, IState state, QuantitativeCharMappingSet mappingSet) {
//		if (state instanceof SingletonState) {
//			QuantitativeCharMapping mapping = sddFactory.createQuantitativeCharMapping();
//			ValueRangeWithClass range = sddFactory.createValueRangeWithClass();
//			range.setLower((Double) state.getMap().get("value"));
//			range.setUpper((Double) state.getMap().get("value"));
//			mapping.setFrom(range);
//			if (!mappingSet.getMapping().contains(mapping))
//				mappingSet.getMapping().add(mapping);
//			QuantitativeCharacter.MeasurementUnit measurementUnit = new QuantitativeCharacter.MeasurementUnit();
//			LabelText unitLabelText = sddFactory.createLabelText();
//			unitLabelText.setValue(state.getFromUnit());
//			if(state.getFromUnit() == null || state.getFromUnit().isEmpty())
//				unitLabelText.setValue("counted occurences");
//			unitLabelText.setRole(new QName("http://rs.tdwg.org/UBIF/2006/", "Abbrev"));
//			measurementUnit.getLabel().add(unitLabelText);
//			((sdd.QuantitativeCharacter) character).setMappings(mappingSet);
//			((sdd.QuantitativeCharacter) character)
//					.setMeasurementUnit(measurementUnit);
//		}
//		else if (state instanceof RangeState) {
//			QuantitativeCharMapping mapping = sddFactory.createQuantitativeCharMapping();
//			ValueRangeWithClass range = sddFactory.createValueRangeWithClass();
//			range.setLower((Double) state.getMap().get("from value"));
//			range.setUpper((Double) state.getMap().get("to value"));
//			mapping.setFrom(range);
//			if (!mappingSet.getMapping().contains(mapping))
//				mappingSet.getMapping().add(mapping);
//			QuantitativeCharacter.MeasurementUnit measurementUnit = new QuantitativeCharacter.MeasurementUnit();
//			LabelText unitLabelText = sddFactory.createLabelText();
//			unitLabelText.setValue(state.getFromUnit());
//			if(state.getFromUnit() == null || state.getFromUnit().isEmpty())
//				unitLabelText.setValue("counted occurences");
//			unitLabelText.setRole(new QName("http://rs.tdwg.org/UBIF/2006/", "Abbrev"));
//			measurementUnit.getLabel().add(unitLabelText);
//			((sdd.QuantitativeCharacter) character).setMappings(mappingSet);
//			((sdd.QuantitativeCharacter) character)
//					.setMeasurementUnit(measurementUnit);
//		}
//	}
//
//	/**
//	 * Add or merge character into existing set (really a map) of characters in the dataset.
//	 * @param characterMap
//	 * @param character
//	 */
//	protected void mergeNewCharacter(
//			Map<String, AbstractCharacterDefinition> characterMap,
//			AbstractCharacterDefinition character, String taxonName) {
//		String charName = character.getId();
//		if(!characterMap.containsKey(charName))
//			characterMap.put(charName, character);
//		else {
//			AbstractCharacterDefinition presentChar = characterMap.get(charName);
////			System.out.println("<Debug>Merging characters for taxon="+taxonName+"\nnew character="+character.toString() +"\npresent character="+presentChar.toString());
//			if(presentChar instanceof sdd.CategoricalCharacter) {
//				if(character instanceof sdd.CategoricalCharacter) {
//					//then just merge states of the two
//					((sdd.CategoricalCharacter)presentChar).getStates().getStateDefinitionOrStateReference().
//						addAll(
//								((sdd.CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference());
//					System.out.println("<Debug-merge>Merged new into present:"+presentChar.toString());
//					Set stateSet = new HashSet();
//					stateSet.addAll(((CategoricalCharacter) presentChar).getStates().getStateDefinitionOrStateReference());
//					((CategoricalCharacter) presentChar).getStates().getStateDefinitionOrStateReference().clear();
//					((CategoricalCharacter) presentChar).getStates().getStateDefinitionOrStateReference().addAll(stateSet);
//				}
//				else if(character instanceof sdd.QuantitativeCharacter) {
//					String q = "q_".concat(character.getId());
//					character.setId(q);
//					characterMap.put(q, character);
//				}
//			}
//			else if(presentChar instanceof sdd.QuantitativeCharacter) {
//				if(character instanceof sdd.CategoricalCharacter) {
//					String q = "q_".concat(presentChar.getId());
//					presentChar.setId(q);
//					characterMap.put(q, presentChar);
//					characterMap.put(charName, character);
//				}
//				else if(character instanceof sdd.QuantitativeCharacter) {
//					//we continuously merge the existing character to contain a minimum and maximum for it's range
//					//the individual taxon characters will maintain their specific ranges (which should fall in between the
//					//min and max for the Character Definition.
//					List<QuantitativeCharMapping> newMappings = ((QuantitativeCharacter)character).getMappings().getMapping();
//					List<QuantitativeCharMapping> presentMappings = ((QuantitativeCharacter) presentChar).getMappings().getMapping();
//					Double min = Double.POSITIVE_INFINITY;
//					Double max = Double.NEGATIVE_INFINITY;
//					for(QuantitativeCharMapping mapping : newMappings) {
//						if(mapping.getFrom().getLower() < min)
//							min = mapping.getFrom().getLower();
//						if(mapping.getFrom().getUpper() > max)
//							max = mapping.getFrom().getUpper();
//					}
//					for(QuantitativeCharMapping mapping : presentMappings) {
//						if(mapping.getFrom().getLower() < min)
//							min = mapping.getFrom().getLower();
//						if(mapping.getFrom().getUpper() > max)
//							max = mapping.getFrom().getUpper();
//					}
//					((QuantitativeCharMapping) ((QuantitativeCharacter) presentChar).getMappings().getMapping().get(0)).getFrom().setLower(min);
//					((QuantitativeCharMapping) ((QuantitativeCharacter) presentChar).getMappings().getMapping().get(0)).getFrom().setUpper(max);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Adds a Coded Description to the set of Coded Descriptions for a Dataset.
//	 * @param dataset
//	 * @param taxon
//	 */
//	protected void addCodedDescriptionToDataset(Dataset dataset, ITaxon taxon) {
//		if(dataset.getCodedDescriptions() == null)
//			dataset.setCodedDescriptions(sddFactory.createCodedDescriptionSet());
//		CodedDescription description = sddFactory.createCodedDescription();
//		Representation descRep = sddFactory.createRepresentation();
//		LabelText descLabel = sddFactory.createLabelText();
//		descLabel.setValue("Coded description for " + taxon.getName());
//		descRep.getRepresentationGroup().add(descLabel);
//		description.setRepresentation(descRep);
//		//indicate the taxon scope
//		TaxonNameRef taxonNameRef = sddFactory.createTaxonNameRef();
//		taxonNameRef.setRef(taxon.getName());
//		description.setScope(sddFactory.createDescriptionScopeSet());
//		description.getScope().getTaxonName().add(taxonNameRef);
//		//now add in the summary data
//		description.setSummaryData(sddFactory.createSummaryDataSet());
//		addSummaryDataToCodedDescription(description, taxon);
//		
//		//and finally, add this description to the Coded Description Set
//		dataset.getCodedDescriptions().getCodedDescription().add(description);
//	}
//
//	/**
//	 * Use references to previously-defined DCs and characters to build a coded description for this taxon.
//	 * @param description
//	 * @param taxon
//	 */
//	@SuppressWarnings("unchecked")
//	protected void addSummaryDataToCodedDescription(CodedDescription description,
//			ITaxon taxon) {
//		Set<JAXBElement> dataElementSet = new TreeSet<JAXBElement>(
//				new SummaryDataComparator<JAXBElement>());
//		Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>> map = this.taxonNameToCharState.get(taxon.getName());
//		for(AbstractCharacterDefinition character : map.keySet()) {
//			if(character instanceof sdd.CategoricalCharacter) {
//				CatSummaryData summaryData = sddFactory.createCatSummaryData();
//				summaryData.setRef(character.getId());
//				for(CharacterLocalStateDef stateDef : map.get(character)) {
//					if (stateDef != null) {
//						StateData stateData = sddFactory.createStateData();
//						stateData.setRef(stateDef.getId());
//						//add in modifier, if present
//						ModifierDef modDef = this.stateToModifier.get(taxon.getName()).get(stateDef);
//						if(modDef != null) {
//							ModifierRefWithData modRef = sddFactory.createModifierRefWithData();
//							modRef.setRef(modDef.getId());
//							stateData.getModifier().add(modRef);
//						}
//						summaryData.getState().add(stateData);
//					}
//				}
//				JAXBElement<CatSummaryData> dataElement =
//					new JAXBElement<CatSummaryData>(new QName("http://rs.tdwg.org/UBIF/2006/", "Categorical"), CatSummaryData.class, CatSummaryData.class, (CatSummaryData) summaryData);
//				dataElementSet.add(dataElement);
//			}
//			if(character instanceof sdd.QuantitativeCharacter) {
//				QuantSummaryData summaryData = sddFactory.createQuantSummaryData();
//				summaryData.setRef(character.getId());
//				UnivarSimpleStatMeasureData low = sddFactory.createUnivarSimpleStatMeasureData();
//				low.setType(new QName("http://rs.tdwg.org/UBIF/2006/", "ObserverEstLower"));
//				low.setValue(((sdd.QuantitativeCharacter)character).getMappings().getMapping().get(0).getFrom().getLower());
//				summaryData.getMeasureOrPMeasure().add(low);
//				UnivarSimpleStatMeasureData high = sddFactory.createUnivarSimpleStatMeasureData();
//				high.setType(new QName("http://rs.tdwg.org/UBIF/2006/", "ObserverEstUpper"));
//				high.setValue(((sdd.QuantitativeCharacter)character).getMappings().getMapping().get(0).getFrom().getUpper());
//				summaryData.getMeasureOrPMeasure().add(high);
//				for(CharacterLocalStateDef stateDef : map.get(character)) {
//					if (stateDef != null) {
//						//add in modifier, if present
//						ModifierDef modDef = this.stateToModifier.get(taxon.getName()).get(stateDef);
//						if(modDef != null) {
//							ModifierRefWithData modRef = sddFactory.createModifierRefWithData();
//							modRef.setRef(modDef.getId());
//							summaryData.getModifier().add(modRef);
//						}
//					}
//				}
//				JAXBElement<QuantSummaryData> dataElement =
//					new JAXBElement<QuantSummaryData>(new QName("http://rs.tdwg.org/UBIF/2006/", "Quantitative"), QuantSummaryData.class, QuantSummaryData.class, (QuantSummaryData) summaryData);
//				dataElementSet.add(dataElement);
//			}
//		}
//		for(JAXBElement element : dataElementSet)
//			description.getSummaryData().getCategoricalOrQuantitativeOrSequence().add(element);
//	}
//
//	/**
//	 * Add a represenation element to a Dataset.
//	 * @param dataset
//	 * @param taxon
//	 */
//	protected void addRepresentationToDataset(Dataset dataset, ITaxon taxon) {
//		Representation rep = sddFactory.createRepresentation();
//		LabelText labelText = sddFactory.createLabelText();
//		String taxonRank = taxon.getTaxonRank().toString();
//		String label = "The " + taxonRank + " " + taxon.getName();
//		labelText.setValue(label);
//		DetailText detailText = sddFactory.createDetailText();
//		detailText.setValue("Generated from Hong's mark-up of FNA document");
//		rep.getRepresentationGroup().add(labelText);
//		rep.getRepresentationGroup().add(detailText);
//		dataset.setRepresentation(rep);
//	}
//
//	/**
//	 * Add a set of natural language descriptions to a dataset.
//	 * @param dataset
//	 * @param taxon
//	 * @deprecated 
//	 */
//	private void addNaturalLanguageDescriptions(Dataset dataset, ITaxon taxon) {
//		NaturalLanguageDescriptionSet descriptionSet = sddFactory.createNaturalLanguageDescriptionSet();		
//		Map<String, NaturalLanguageDescription> statementIdToDescription = 
//			new HashMap<String, NaturalLanguageDescription>();
//		for(String id : taxon.getStatementTextMap().keySet()) {
//			NaturalLanguageDescription description = sddFactory.createNaturalLanguageDescription();
//			NaturalLanguageMarkup data = sddFactory.createNaturalLanguageMarkup();
//			MarkupText markupText = sddFactory.createMarkupText();
//			markupText.setValue(taxon.getStatementTextMap().get(id));
//			data.getMarkupGroup().add(markupText);
//			description.setNaturalLanguageData(data);
//			addRepresentationToNaturalLanguageDescription(description, taxon);
//			statementIdToDescription.put(id, description);
//		}
//		Iterator<TreeNode<Structure>> iter = taxon.getStructureTree().iterator();
//		while(iter.hasNext()) {
//			TreeNode<Structure> node = iter.next();
//			Structure structure = node.getElement();
//			NaturalLanguageDescription description = 
//				statementIdToDescription.get(structure.getStatementId());
//			addConceptToNaturalLanguageDescription(
//					description.getNaturalLanguageData(), node);
//		}
//		descriptionSet.getNaturalLanguageDescription().addAll(statementIdToDescription.values());
//		dataset.setNaturalLanguageDescriptions(descriptionSet);
//	}
//
//	/**
//	 * Adds a ConceptMarkup element to a piece of NaturalLanguageMarkup/data.
//	 * @param data
//	 * @param node
//	 * @deprecated
//	 */
//	private void addConceptToNaturalLanguageDescription(
//			NaturalLanguageMarkup data, TreeNode<Structure> node) {
//		Structure structure = node.getElement();
//		ConceptMarkup concept = sddFactory.createConceptMarkup();
//		concept.setRef(structure.getName());
//		for(String charName : structure.getCharStateMap().keySet()) {
//			addCharacterToConcept(concept, 
//					charName, structure.getCharStateMap().get(charName));
//		}
//		data.getMarkupGroup().add(concept);
//	}
//
//	/**
//	 * 
//	 * @param concept
//	 * @param charName
//	 * @param state
//	 * @deprecated
//	 */
//	private void addCharacterToConcept(ConceptMarkup concept, String charName,
//			IState state) {
//		if(state instanceof RangeState) {
//			if(TypeUtil.isNumeric(state.getMap().get("from value"))) {
//				QuantitativeMarkup qFrom = sddFactory.createQuantitativeMarkup();
//				qFrom.setLabel(charName.concat("_from"));
//				ValueMarkup vFrom = sddFactory.createValueMarkup();
//				vFrom.setValue((Double)state.getMap().get("from value"));
//				JAXBElement<ValueMarkup> eleFrom =
//					new JAXBElement<ValueMarkup>(new QName("ValueMarkup"), ValueMarkup.class, vFrom);
//				qFrom.getStatusOrModifierOrMeasure().add(eleFrom);
//				QuantitativeMarkup qTo = sddFactory.createQuantitativeMarkup();
//				qTo.setLabel(charName.concat("_to"));
//				ValueMarkup vTo = sddFactory.createValueMarkup();
//				vTo.setValue((Double)state.getMap().get("to value"));
//				JAXBElement<ValueMarkup> eleTo =
//					new JAXBElement<ValueMarkup>(new QName("ValueMarkup"), ValueMarkup.class, vTo);
//				qTo.getStatusOrModifierOrMeasure().add(eleTo);
//				if(state.getModifier() != null) {
//					addModifierToCharacterMarkup(qFrom, state);
//					addModifierToCharacterMarkup(qTo, state);
//				}
//				concept.getMarkupGroup().add(qFrom);
//				concept.getMarkupGroup().add(qTo);
//			}
//			else {
//				CategoricalMarkup cFrom = sddFactory.createCategoricalMarkup();
//				cFrom.setLabel(charName.concat("_from"));
//				StateMarkup stateFrom = sddFactory.createStateMarkup();
//				stateFrom.setLabel(state.getMap().get("from value").toString());
//				JAXBElement<StateMarkup> eleFrom =
//					new JAXBElement<StateMarkup>(new QName("StateMarkup"), StateMarkup.class, stateFrom);
//				cFrom.getStatusOrModifierOrState().add(eleFrom);
//				CategoricalMarkup cTo = sddFactory.createCategoricalMarkup();
//				cTo.setLabel(charName.concat("_to"));
//				StateMarkup stateTo = sddFactory.createStateMarkup();
//				stateTo.setLabel(state.getMap().get("to value").toString());
//				JAXBElement<StateMarkup> eleTo =
//					new JAXBElement<StateMarkup>(new QName("StateMarkup"), StateMarkup.class, stateTo);
//				cTo.getStatusOrModifierOrState().add(eleTo);
//				if (state.getModifier() != null) {
//					addModifierToCharacterMarkup(cFrom, state);
//					addModifierToCharacterMarkup(cTo, state);
//				}
//				concept.getMarkupGroup().add(cFrom);
//				concept.getMarkupGroup().add(cTo);
//			}
//		} else {
//			if(TypeUtil.isNumeric(state.getMap().get("value"))) {
//				QuantitativeMarkup qm = sddFactory.createQuantitativeMarkup();
//				qm.setLabel(charName);
//				ValueMarkup value = sddFactory.createValueMarkup();
//				value.setValue((Double)state.getMap().get("value"));
//				JAXBElement<ValueMarkup> ele = 
//					new JAXBElement<ValueMarkup>(new QName("ValueMarkup"), ValueMarkup.class, value);
//				qm.getStatusOrModifierOrMeasure().add(ele);
//				if(state.getModifier() != null) {
//					addModifierToCharacterMarkup(qm, state);
//				}
//				concept.getMarkupGroup().add(qm);
//			}
//			else {
//				CategoricalMarkup cm = sddFactory.createCategoricalMarkup();
//				cm.setLabel(charName);
//				StateMarkup stateVal = sddFactory.createStateMarkup();
//				stateVal.setLabel(state.getMap().get(("value")).toString());
//				JAXBElement<StateMarkup> ele =
//					new JAXBElement<StateMarkup>(new QName("StateMarkup"), StateMarkup.class, stateVal);
//				cm.getStatusOrModifierOrState().add(ele);
//				if(state.getModifier() != null) {
//					addModifierToCharacterMarkup(cm, state);
//				}
//				concept.getMarkupGroup().add(cm);
//			}
//		}
//		
//	}
//
//	private void addModifierToCharacterMarkup(AbstractCharacterMarkup cFrom,
//			IState state) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/**
//	 * Add a representation element to a natural language description.
//	 * @param description
//	 * @param taxon
//	 * @deprecated
//	 */
//	private void addRepresentationToNaturalLanguageDescription(
//			NaturalLanguageDescription description, ITaxon taxon) {
//		Representation rep = sddFactory.createRepresentation();
//		LabelText labelText = sddFactory.createLabelText();
//		labelText.setValue("placeholder");
//		DetailText detailText = sddFactory.createDetailText();
//		detailText.setValue("placeholder");
//		rep.getRepresentationGroup().add(labelText);
//		rep.getRepresentationGroup().add(detailText);
//		description.setRepresentation(rep);
//	}
//
//	/**
//	 * Adds metadata to the root Datasets object.  Throws on the current time as the "created" field
//	 * and a Generator named "RDF to SDD conversion tool."
//	 * @param root
//	 */
//	protected void addMetadata(Datasets root) {
//		TechnicalMetadata metadata = sddFactory.createTechnicalMetadata();
//		XMLGregorianCalendar xgcNow = XMLGregorianCalendarConverter.asXMLGregorianCalendar(new Date(System.currentTimeMillis()));
//		metadata.setCreated(xgcNow);
//		DocumentGenerator gen = sddFactory.createDocumentGenerator();
//		gen.setName("RDF to SDD conversion tool.");
//		gen.setVersion("0.1");
//		metadata.setGenerator(gen);
//		root.setTechnicalMetadata(metadata);
//	}
//	
//	/**
//	 * Do post-processing stuff for CharacterSet before adding to Dataset.
//	 * Namely, make sure StateDefinition ids come before refs.
//	 * @param characterSet
//	 */
//	protected void postProcessCharacterSet(CharacterSet characterSet) {
//		Map<String, CharacterLocalStateDef> seenIds = new HashMap<String, CharacterLocalStateDef>();
//		Map<String, ConceptStateRef> seenRefs = new HashMap<String, ConceptStateRef>();
//		List<AbstractCharacterDefinition> characterList = characterSet.getCategoricalCharacterOrQuantitativeCharacterOrTextCharacter();
//		for(AbstractCharacterDefinition character : characterList) {
//			if(character instanceof CategoricalCharacter) {
//				List<Object> states = ((CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference();
//				List<Object> reconciledStates = new ArrayList<Object>();
//				for(Object state : states) {
//					if(state instanceof CharacterLocalStateDef) {
//						String stateId = ((CharacterLocalStateDef) state).getId();
//						//if we're looking at an id, and there's already been an id, replace it with a ref
//						if(seenIds.containsKey(stateId)) {
//							//get it from seenRefs if it's already in there
//							if(seenRefs.containsKey(stateId)) {
//								reconciledStates.add(seenRefs.get(stateId));
//							}
//							else {
//								//Make a new ref and add it as a state
//								ConceptStateRef stateRef = sddFactory.createConceptStateRef();
//								stateRef.setRef(((CharacterLocalStateDef) state).getId());
//								reconciledStates.add(stateRef);
//								seenRefs.put(stateId, stateRef);
//							}
//						}
//						//else this is the first time we've seen an id for this state, mark it as seen, and include it 
//						//in the reconciled states
//						else {
//							seenIds.put(stateId, (CharacterLocalStateDef) state);
//							reconciledStates.add(state);
//						}
//					}
//					else if(state instanceof ConceptStateRef) {
//						String stateId = ((ConceptStateRef)state).getRef();
//						//if we're looking at a ref which we haven't seen an id for yet...
//						if(!seenIds.containsKey(stateId)) {
//							//Then make a StateDef out of this state, change the ref to id, and mark id as seen
//							CharacterLocalStateDef localStateDef = sddFactory.createCharacterLocalStateDef();
//							localStateDef.setId(stateId);
//							Representation stateRep = sddFactory.createRepresentation();
//							LabelText stateLabelText = sddFactory.createLabelText();
//							stateLabelText.setValue(stateId.replace("state_", ""));
//							stateRep.getRepresentationGroup().add(stateLabelText);
//							localStateDef.setRepresentation(stateRep);
//							reconciledStates.add(localStateDef);
//							seenIds.put(stateId, localStateDef);
//						}
//						else {
//							seenRefs.put(stateId, (ConceptStateRef) state);
//							reconciledStates.add(state);
//						}
//					}
//				}
//				((CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().clear();
//				((CategoricalCharacter)character).getStates().getStateDefinitionOrStateReference().addAll(reconciledStates);
//				postProcessCharacterHelper((CategoricalCharacter) character);
//			}
//		}
//	}
//
//	/**
//	 * This eliminates refs from characters that already have an id for the same state as the ref is pointing to.
//	 * @param character
//	 */
//	private void postProcessCharacterHelper(CategoricalCharacter character) {
//		List<Object> states = ((CategoricalCharacter) character)
//				.getStates().getStateDefinitionOrStateReference();
//		Map<String, CharacterLocalStateDef> ids = new HashMap<String, CharacterLocalStateDef>();
//		Map<String, ConceptStateRef> refs = new HashMap<String, ConceptStateRef>();
//		for (Object state : states) {
//			if (state instanceof CharacterLocalStateDef) {
//				String stateId = ((CharacterLocalStateDef) state).getId();
//				if(this.mustBeGlobal.containsKey(stateId)) {
//					refs.put(stateId, this.mustBeGlobal.get(stateId));
//				}
//				else {
//					ids.put(stateId, (CharacterLocalStateDef) state);
//					removeFromGlobalStates((CharacterLocalStateDef)state);
//				}
//			} else {
//				refs.put(((ConceptStateRef) state).getRef(), (ConceptStateRef) state);
//			}
//		}
//		for (String id : ids.keySet()) {
//			if (refs.containsKey(id))
//				refs.remove(id);
//		}
//		((CategoricalCharacter) character).getStates().getStateDefinitionOrStateReference().clear();
//		((CategoricalCharacter) character).getStates().getStateDefinitionOrStateReference().addAll(ids.values());
//		((CategoricalCharacter) character).getStates().getStateDefinitionOrStateReference().addAll(refs.values());
//	}
//	
//	/**
//	 * Remove all occurences from globalStates that match the given local state.
//	 * @param state
//	 */
//	private void removeFromGlobalStates(CharacterLocalStateDef state) {	
//		List<ConceptStateDef> markForRemoval = new ArrayList<ConceptStateDef>();
////		System.out.println("Here are the states currently marked as global:");
//		for(ConceptStateDef conceptState : this.globalStates.getConceptStates().getStateDefinition()) {
////			System.out.println(conceptState.getId());
//			if(conceptState.getId().equals(state.getId()))
//				markForRemoval.add(conceptState);
//		}
////		System.out.println("Here are states marked for removal:");
////		for(ConceptStateDef def : markForRemoval)
////			System.out.println(def.getId());
//		this.globalStates.getConceptStates().getStateDefinition().removeAll(markForRemoval);
////		System.out.println("Here are the states left:");
////		for(ConceptStateDef def : this.globalStates.getConceptStates().getStateDefinition())
////			System.out.println(def.getId());
//	}
//
//	/**
//	 * @return the model
//	 */
//	public Model getModel() {
//		return model;
//	}
//
//	/**
//	 * @param model the model to set
//	 */
//	public void setModel(Model model) {
//		this.model = model;
//	}
//
//}
