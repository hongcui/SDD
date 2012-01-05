package conversion;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import sdd.AbstractCharacterDefinition;
import sdd.CharTreeAbstractNode;
import sdd.CharTreeCharacter;
import sdd.CharTreeNode;
import sdd.CharTreeNodeRef;
import sdd.CharTreeNodeSeq;
import sdd.CharacterRef;
import sdd.CharacterTree;
import sdd.CharacterTreeSet;
import sdd.DescriptiveConcept;
import sdd.DescriptiveConceptRef;
import sdd.ObjectFactory;
import sdd.Representation;
import sdd.TaxonNameCore;
import sdd.TaxonNameRef;
import sdd.TaxonomicScopeSet;
import taxonomy.ITaxon;
import tree.TreeNode;
import util.ConversionUtil;
import annotationSchema.jaxb.Structure;

/**
 * @author Alex
 *
 */
public class CharacterTreeHandler implements Observer, Handler {
	
	private static final String DC_NODE_PREFIX = "dc_node_";
	private static final String CT_REP_LABEL = "Morphological Character Tree";
	private static ObjectFactory sddFactory = new ObjectFactory();
	private CharacterTreeSet charTreeSet;
	/** This is used to keep track of which DCs are referred to by which CTNodes. */
	private Map<DescriptiveConcept, CharTreeNode> dcToCtNode;
	/** We might see descriptive concepts to add to a character tree before
	 * the tree has been added to the set.  Keep some temps around and merge
	 * on creation from TaxonNameCore.
	 */
	private Map<String, CharacterTree> tempTrees;
	
	public CharacterTreeHandler() {
		this.charTreeSet = sddFactory.createCharacterTreeSet();
		this.dcToCtNode = new HashMap<DescriptiveConcept, CharTreeNode>();
		this.tempTrees = new HashMap<String, CharacterTree>();
	}
	
	/** 
	 * Gets TaxonName data to create initial CharacterTree for each taxon
	 * from the TaxonNameHandler.
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof DatasetHandler && 
				arg instanceof TreeNode<?>){
			DatasetHandler handler = (DatasetHandler) o;
			TreeNode<ITaxon> taxonNode = (TreeNode<ITaxon>) arg;
			handler.getDataset().setCharacterTrees(charTreeSet);
		}
		else if(o instanceof TaxonNameHandler &&
				arg instanceof TaxonNameCore) {
			TaxonNameCore taxonName = (TaxonNameCore) arg;
			addCharacterTreeToSet(taxonName);
		}
		else if(o instanceof DescriptiveConceptHandler &&
				arg instanceof TaxonConceptStructureTriple) {
			TaxonConceptStructureTriple triple = (TaxonConceptStructureTriple) arg;
			TreeNode<ITaxon> taxonNode = triple.getTaxonNode();
			DescriptiveConcept dc = triple.getDescriptiveConcept();
			TreeNode<Structure> structureNode = triple.getStructureNode();
			addConceptNodesToCharacterTree(taxonNode, dc, structureNode);
		}
		else if(o instanceof CharacterSetHandler &&
				arg instanceof TaxonCharacterStructureTriple) {
			TaxonCharacterStructureTriple triple = (TaxonCharacterStructureTriple) arg;
			TreeNode<ITaxon> taxonNode = triple.getTaxonNode();
			AbstractCharacterDefinition character = triple.getCharacter();
			TreeNode<Structure> structureNode = triple.getStructureNode();
			addCharacterNodesToCharacterTree(taxonNode, character, structureNode);
		}
	}

	/**
	 * This method first adds a Node to a character tree that points to the descriptive concept object.  It then adds some character nodes 
	 * as references that point to the descriptive concept.
	 * @param taxonNode
	 * @param dc
	 * @param structureNode
	 */
	private void addConceptNodesToCharacterTree(TreeNode<ITaxon> taxonNode,
			DescriptiveConcept dc, TreeNode<Structure> structureNode) {
		CharacterTree characterTree = findCharacterTree(taxonNode);
		if(characterTree.getNodes() == null) {
			CharTreeNodeSeq ctNodeSeq = sddFactory.createCharTreeNodeSeq();
			characterTree.setNodes(ctNodeSeq);
		}
		//make a new DC node for the tree, set it's reference to the id of the actual Descriptive Concept, set
		//the ID to be the id of the DC object with 'dc_node' in front, and point to the parent of the DC object.
		Object dcNode = resolveDescriptiveConceptNode(dc);
		if(structureNode.getParent() != null) {
			Structure parentStructure = structureNode.getParent().getElement();
			CharTreeNodeRef ctNodeRef = sddFactory.createCharTreeNodeRef();
			ctNodeRef.setRef(DC_NODE_PREFIX+DescriptiveConceptHandler.DC_PREFIX+parentStructure.getName());
			if(dcNode instanceof CharTreeNode)
				((CharTreeNode) dcNode).setParent(ctNodeRef);
		}
		if(dcNode instanceof CharTreeAbstractNode)
			characterTree.getNodes().getNodeOrCharNode().add((CharTreeAbstractNode) dcNode);
	}
	
	/**
	 * Adds character nodes (as references pointing to the appropriate descriptive
	 * concept) to a character tree.
	 * @param taxonNode Contains taxon corresponding to a character tree.
	 * @param character Add nodes based on this character.
	 * @param structureNode "Guess" the descriptive concept based on the structure name.
	 */
	private void addCharacterNodesToCharacterTree(TreeNode<ITaxon> taxonNode, 
			AbstractCharacterDefinition character, TreeNode<Structure> structureNode) {
		CharacterTree characterTree = findCharacterTree(taxonNode);
		DescriptiveConcept dcTemp = sddFactory.createDescriptiveConcept();
		dcTemp.setId(DescriptiveConceptHandler.DC_PREFIX.concat(structureNode.getElement().getName()));
		Object dcNode = resolveDescriptiveConceptNodeForCharacter(dcTemp);
		String dcNodeId = resolveDescriptiveConceptNodeId(dcNode);
		//make a CharNode for the tree, and point to the descriptive
		//concept node as the parent.
		CharTreeCharacter ctCharNode = sddFactory.createCharTreeCharacter();
		CharacterRef charRef = sddFactory.createCharacterRef();
		charRef.setRef(character.getId());
		ctCharNode.setCharacter(charRef);
		CharTreeNodeRef ctNodeRef = sddFactory.createCharTreeNodeRef();
		ctNodeRef.setRef(dcNodeId);
		ctCharNode.setParent(ctNodeRef);
		if(characterTree.getNodes() == null) {
			CharTreeNodeSeq ctNodeSeq = sddFactory.createCharTreeNodeSeq();
			characterTree.setNodes(ctNodeSeq);
		}
		if(!characterTree.getNodes().getNodeOrCharNode().contains(ctCharNode))
			characterTree.getNodes().getNodeOrCharNode().add(ctCharNode);
	}
	
	/**
	 * Finds the right character tree to use when adding descriptive concept
	 * or character nodes.
	 * @param taxonNode
	 * @return
	 */
	private CharacterTree findCharacterTree(TreeNode<ITaxon> taxonNode) {
		CharacterTree characterTree = null;
		//first, check to see if we have a character tree for this taxon in the set.
		ITaxon taxon = taxonNode.getElement();
		for(CharacterTree tree : this.charTreeSet.getCharacterTree()) {
			if(tree.getScope().getTaxonName().get(0).getRef().equals(taxon.getName()))
				characterTree = tree;
		}
		if(characterTree == null && !tempTrees.containsKey(taxonNode.getElement().getName())) {
			//then we don't have a real tree yet, make a temp one.
			addTempCharacterTree(taxonNode.getElement().getName());
			characterTree = tempTrees.get(taxonNode.getElement().getName());
		}
		else
			characterTree = tempTrees.get(taxonNode.getElement().getName());
		if(characterTree.getNodes() == null) {
			CharTreeNodeSeq ctNodeSeq = sddFactory.createCharTreeNodeSeq();
			characterTree.setNodes(ctNodeSeq);
		}
		return characterTree;
	}
	
	/**
	 * Resolves a DescriptiveConcept to either a CharTreeNodeRef or a
	 * CharTreeNode.
	 * @param dc
	 * @return
	 */
	private Object resolveDescriptiveConceptNode(DescriptiveConcept dc) {
		Object dcNode = null;
		DescriptiveConceptRef dcRef = sddFactory.createDescriptiveConceptRef();
		dcRef.setRef(dc.getId());
		if(this.dcToCtNode.containsKey(dc)) {
			dcNode = sddFactory.createCharTreeNodeRef();
			((CharTreeNodeRef)dcNode).setRef(this.dcToCtNode.get(dc).getId());
		}
		else {
			dcNode = sddFactory.createCharTreeNode();
			((CharTreeNode) dcNode).setDescriptiveConcept(dcRef);
			((CharTreeNode) dcNode).setId(DC_NODE_PREFIX+dc.getId());
			this.dcToCtNode.put(dc, (CharTreeNode) dcNode);		
		}
		return dcNode;
	}
	
	/**
	 * Resolves a DescriptiveConcept to either a CharTreeNodeRef or a CharTreeNode
	 * without adding to dctToCtNode mapping.
	 * @param dc
	 * @return
	 */
	private Object resolveDescriptiveConceptNodeForCharacter(DescriptiveConcept dc) {
		Object dcNode = null;
		DescriptiveConceptRef dcRef = sddFactory.createDescriptiveConceptRef();
		dcRef.setRef(dc.getId());
		if(this.dcToCtNode.containsKey(dc)) {
			dcNode = sddFactory.createCharTreeNodeRef();
			((CharTreeNodeRef)dcNode).setRef(this.dcToCtNode.get(dc).getId());
		}
		else {
			dcNode = sddFactory.createCharTreeNode();
			((CharTreeNode) dcNode).setDescriptiveConcept(dcRef);
			((CharTreeNode) dcNode).setId(DC_NODE_PREFIX+dc.getId());
		}
		return dcNode;
	}
	
	
	
	/**
	 * Returns the id of a descriptive concept from either a CharTreeNodeRef
	 * of CharTreeNode.
	 * @param dcNode
	 * @return
	 */
	private String resolveDescriptiveConceptNodeId(Object dcNode) {
		if(dcNode instanceof CharTreeNodeRef) 
			return ((CharTreeNodeRef) dcNode).getRef();
		else
			return ((CharTreeNode) dcNode).getId();
	}

	/**
	 * This takes a TaxonName and adds a new character tree to the character
	 * tree set, referring to the taxon name.
	 * @param taxonName
	 */
	private void addCharacterTreeToSet(TaxonNameCore taxonName) {
		if(tempTrees.containsKey(taxonName.getId()))
			charTreeSet.getCharacterTree().add(tempTrees.get(taxonName.getId()));
		else {
			CharacterTree characterTree = sddFactory.createCharacterTree();
			Representation ctRep = ConversionUtil.makeRep(CT_REP_LABEL);
			characterTree.setRepresentation(ctRep);
			TaxonomicScopeSet taxonomicScopeSet = sddFactory.createTaxonomicScopeSet();
			TaxonNameRef taxonNameRef = sddFactory.createTaxonNameRef();
			taxonNameRef.setRef(taxonName.getId());
			taxonomicScopeSet.getTaxonName().add(taxonNameRef);
			characterTree.setScope(taxonomicScopeSet);
			charTreeSet.getCharacterTree().add(characterTree);
		}
	}
	
	/**
	 * Adds a temp character tree to the temp set.
	 * @param taxonName
	 */
	private void addTempCharacterTree(String taxonName) {
		CharacterTree characterTree = sddFactory.createCharacterTree();
		Representation ctRep = ConversionUtil.makeRep(CT_REP_LABEL);
		characterTree.setRepresentation(ctRep);
		TaxonomicScopeSet taxonomicScopeSet = sddFactory.createTaxonomicScopeSet();
		TaxonNameRef taxonNameRef = sddFactory.createTaxonNameRef();
		taxonNameRef.setRef(taxonName);
		taxonomicScopeSet.getTaxonName().add(taxonNameRef);
		characterTree.setScope(taxonomicScopeSet);
		tempTrees.put(taxonName, characterTree);
	}

	/* (non-Javadoc)
	 * @see conversion.Handler#handle()
	 */
	@Override
	public void handle() {
		// TODO Auto-generated method stub

	}

}
