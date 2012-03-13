package conversion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import states.EmptyState;
import states.IState;
import states.RangeState;
import states.SingletonState;
import taxonomy.ITaxon;
import taxonomy.TaxonComparator;
import taxonomy.TaxonHierarchy;
import tree.TreeNode;
import util.ConversionUtil;
import annotationSchema.jaxb.Structure;

/**
 * Class for converting a Taxon Hierarchy into a Taxon-by-Character matrix.
 * @author Alex
 *
 */
public class TaxonCharacterMatrix {

	public static final String FROM_SUFFIX = "_from";
	public static final String TO_SUFFIX = "_to";
	private Map<String, Map<ITaxon, List<IState>>> table;
	private TaxonHierarchy hierarchy;
	
	public TaxonCharacterMatrix(TaxonHierarchy th) {
		this.hierarchy = th;
		this.table = createTable();
		expandTable();
	}

	/**
	 * Fills in a table that represents a taxon-by-character matrix.
	 * @return
	 */
	private Map<String, Map<ITaxon, List<IState>>> createTable() {
		Map<String, Map<ITaxon, List<IState>>> map = new TreeMap<String, Map<ITaxon,List<IState>>>();
		Iterator<TreeNode<ITaxon>> iter = this.hierarchy.getHierarchy().iterator();
		List<ITaxon> allTaxa = new ArrayList<ITaxon>();
		while(iter.hasNext()) {
			TreeNode<ITaxon> taxonNode = iter.next();
			ITaxon taxon = taxonNode.getElement();
			allTaxa.add(taxon);
			Iterator<TreeNode<Structure>> structIter = taxon.getStructureTree().iterator();
			while(structIter.hasNext()) {
				TreeNode<Structure> structNode = structIter.next();
				Structure structure = structNode.getElement();
				Map<String, List<IState>> charStateMap = structure.getCharStateMap();
				for(String charName : charStateMap.keySet()) {
					String fullName = resolveFullCharacterName(charName, structNode);
					List<IState> states = charStateMap.get(charName);
					if(map.containsKey(fullName)) {
						map.get(fullName).put(taxon, states);
					}
					else {
						Map<ITaxon, List<IState>> subMap = new HashMap<ITaxon, List<IState>>();
						subMap.put(taxon, states);
						map.put(fullName, subMap);
					}
				}
			}
		}
		postProcessTable(map, allTaxa);
		return map;
	}

	/**
	 * This is where state inheritance occurs, as well as assigning empty states to characters of taxa
	 * who have no state for some character in the set of defined characters.
	 * @param map
	 * @param allTaxa
	 */
	private void postProcessTable(Map<String, Map<ITaxon, List<IState>>> map,
			List<ITaxon> allTaxa) {
		for(String ch : map.keySet()) {
			Map<ITaxon, List<IState>> taxonToState = map.get(ch);
			if(taxonToState.size() != allTaxa.size()) {	//then begin "inheriting" character states from higher taxa
				for(ITaxon taxon : allTaxa) {
					TreeNode<ITaxon> taxonNode = this.hierarchy.findTaxonByName(taxon.getName(), taxon.getTaxonRank());
					if (!taxonToState.containsKey(taxon)) {
						if (taxonNode.getParent() != null) {
							ITaxon parentTaxon = taxonNode.getParent().getElement();
							if (taxonToState.containsKey(parentTaxon)) { //if a parent taxon is in the map, go grab states from it
								List<IState> parentStates = taxonToState.get(parentTaxon);
								taxonToState.put(taxon, parentStates);
							}
							else {	//if the parent is not in the map, add an empty state for the parent
								List<IState> parentList = new ArrayList<IState>();
								parentList.add(new EmptyState<String>());
								taxonToState.put(parentTaxon, parentList);
								List<IState> childList = new ArrayList<IState>();
								childList.add(new EmptyState<String>());
								taxonToState.put(taxon, childList);
							}
						}
						else {
							List<IState> childList = new ArrayList<IState>();
							childList.add(new EmptyState<String>());
							taxonToState.put(taxon, childList);
						}
							
					}	//end if taxon not mapped to some state.
				}
			}
			//now, take care of instances where singleton states are mixed with range states
			Set<Class> stateTypes = new HashSet<Class>();
			for(List<IState> stateList : taxonToState.values())
				for(IState state : stateList)
					stateTypes.add(state.getClass());
			if(stateTypes.size() > 1 && stateTypes.contains(SingletonState.class)) { 	
				//then promote the singleton states to range states
				for(ITaxon t : taxonToState.keySet()) {
					List<IState> states = taxonToState.get(t);
					//lists maintaining states for addition/removal
					List<IState> toAdd = new ArrayList<IState>();
					List<IState> toRemove = new ArrayList<IState>();
					for(IState state : states) {
						if(state instanceof SingletonState) {
							toAdd.add(state.promote());
							toRemove.add(state);
						}
					}
					states.addAll(toAdd);
					states.removeAll(toRemove);
				}
				
			}
//			else if(stateTypes.contains(SingletonState.class) 
//					&& stateTypes.contains(EmptyState.class) && 
//					!stateTypes.contains(RangeState.class)) {
//				for(ITaxon t : taxonToState.keySet()) {
//					IState state = taxonToState.get(t);
//					if(state instanceof EmptyState)
//						taxonToState.put(t, ((EmptyState) state).demote());
//				}
//			}
		}
		
	}

	/**
	 * Processes a "short" character name into a full name, i.e., the short name preceded by 
	 * the appropriate, full structure name (achieved by looking up the structure tree).
	 * @param shortCharName
	 * @param structNode
	 * @return
	 */
	private String resolveFullCharacterName(String shortCharName,
			TreeNode<Structure> structNode) {
		String structName = structNode.getElement().getName();
		String charName = structName + "_" + shortCharName;
		TreeNode<Structure> parent = structNode.getParent();
		while(parent != null) {
			structName = parent.getElement().getName();
			if(!structName.equals("whole_organism"))
				charName = structName + "_" + charName;
			parent = parent.getParent();
		}
		return charName;
	}
	
	/**
	 * Generates a comma-delimited file holding the taxon-character matrix.
	 * @param outputFile File name to send output to.
	 */
	@SuppressWarnings("rawtypes")
	public void generateMatrixFile(String outputFile) {
		FileWriter fstream = null;
		try {
			fstream = new FileWriter(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> header = new ArrayList<String>();
		header.add("");	//leave the first entry blank in the matrix.
		Map<ITaxon, List<String>> rows = new TreeMap<ITaxon, List<String>>(new TaxonComparator());
		for(String charName : table.keySet()) {
			header.add(charName);
			System.out.println(charName);
			Map<ITaxon, List<IState>> taxonToState = table.get(charName);
			for(ITaxon taxon : taxonToState.keySet()) {
				List<IState> stateList = taxonToState.get(taxon);	
				if(rows.containsKey(taxon)) {
					List<String> row = rows.get(taxon);
					if(row == null) {
						row = new ArrayList<String>();
						row.add(taxon.getName());
					}
					List<String> stateStrings = new ArrayList<String>();
					for(IState state : stateList) {
						if(state instanceof SingletonState) {
							String value = state.getMap().get(SingletonState.KEY).toString();
							stateStrings.add(value);
						}
						else {
							String value = "";
							if(charName.endsWith(FROM_SUFFIX))
								value = state.getMap().get(RangeState.KEY_FROM).toString();
							else
								value = state.getMap().get(RangeState.KEY_TO).toString();
							stateStrings.add(value);
						}
					}
					//peel off any empty strings from rows of length > 1
					if(stateStrings.size() > 1) {
						stateStrings.remove("");
						stateStrings.remove(null);
					}
					row.add(ConversionUtil.join(stateStrings, "|"));
				}
				else {
					List<String> row = new ArrayList<String>();
					row.add(taxon.getName());	//The first element of a row is the name of the taxon.
					List<String> stateStrings = new ArrayList<String>();
					for(IState state : stateList) {
						if(state instanceof SingletonState) {
							String value = state.getMap().get(SingletonState.KEY).toString();
							stateStrings.add(value);
						}
						else {
							String value = "";
							if(charName.endsWith(FROM_SUFFIX))
								value = state.getMap().get(RangeState.KEY_FROM).toString();
							else
								value = state.getMap().get(RangeState.KEY_TO).toString();
							stateStrings.add(value.trim());
						}
					}
					//peel off any empty strings from rows of length > 1
					if(stateStrings.size() > 1) {
						stateStrings.remove("");
						stateStrings.remove(null);
					}
					row.add(ConversionUtil.join(stateStrings, "|"));
					rows.put(taxon, row);
				}
			}
		}
		BufferedWriter out = new BufferedWriter(fstream);
		try {
			constructMatrix(out, header, rows, "\t");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the matrix to a file.
	 * @param out Writer to write to.
	 * @param header Header row of character names
	 * @param rows Taxon data
	 * @param delimiter Delimiter for the file (probably tab or comma).
	 * @throws IOException
	 */
	private void constructMatrix(BufferedWriter out, List<String> header,
			Map<ITaxon, List<String>> rows, String delimiter) throws IOException{
//		postProcessRows(header, rows);
		for(String h : header)
			out.write(h+delimiter);
		for(ITaxon t : rows.keySet()) {
			out.newLine();
			List<String> row = rows.get(t);
			for(String s : row)
				out.write(s+delimiter);
		}
		out.close();		
	}
	
	/**
	 * Expands the character names in the table to account for range states
	 * and units.
	 */
	@SuppressWarnings("rawtypes")
	private void expandTable() {
		//expand range states into new characters using this map
		Map<String, Map<ITaxon, List<IState>>> toAdd = 
				new HashMap<String, Map<ITaxon,List<IState>>>();
		//and remove the expanded range states from the original character
		Map<String, Map<ITaxon, List<IState>>> toRemove = 
				new HashMap<String, Map<ITaxon,List<IState>>>();
		for (String charName : this.table.keySet()) {
			//map of taxon -> states for this character
			Map<ITaxon, List<IState>> taxonToStates = this.table.get(charName);
			//flag to indicate if some taxon requires expanding the table for this character
			boolean wasExpanded = false;
			for (ITaxon taxon : taxonToStates.keySet()) {
				List<IState> stateList = taxonToStates.get(taxon);
				for(IState state : stateList) {
					String charNameFrom = charName + FROM_SUFFIX;
					String charNameTo = charName + TO_SUFFIX;
					if(!(state instanceof SingletonState)) {
						//then there's a range involved.
						if (!toAdd.containsKey(charNameFrom)) {
							//no entry for from character at all
							Map<ITaxon, List<IState>> tempMap = new HashMap<ITaxon, List<IState>>();
							List<IState> tempStates = new ArrayList<IState>();
							tempStates.add(state);
							tempMap.put(taxon, tempStates);
							toAdd.put(charNameFrom, tempMap);
						}
						else {
							//there is a from character entry...
							Map<ITaxon, List<IState>> tempMap = toAdd.get(charNameFrom);
							if(!tempMap.containsKey(taxon)) {
								//...but not for this taxon, or...
								List<IState> tempStates = new ArrayList<IState>();
								tempStates.add(state);
								tempMap.put(taxon, tempStates);
							}
							else {
								//...there is an entry for this taxon
								tempMap.get(taxon).add(state);
							}
						}
						if(!toAdd.containsKey(charNameTo)) {
							//no entry for to character at all
							Map<ITaxon, List<IState>> tempMap = new HashMap<ITaxon, List<IState>>();
							List<IState> tempStates = new ArrayList<IState>();
							tempStates.add(state);
							tempMap.put(taxon, tempStates);
							toAdd.put(charNameTo, tempMap);
						}
						else {
							//there is a to character entry...
							Map<ITaxon, List<IState>> tempMap = toAdd.get(charNameTo);
							if(!tempMap.containsKey(taxon)) {
								//...but not for this taxon, or...
								List<IState> tempStates = new ArrayList<IState>();
								tempStates.add(state);
								tempMap.put(taxon, tempStates);
							}
							else {
								//...there is an entry for this taxon
								tempMap.get(taxon).add(state);
							}
						}
						//now mark this state for removal from original character
						if(toRemove.containsKey(charName)) {
							Map<ITaxon, List<IState>> taxonMap = toRemove.get(charName);
							if(taxonMap.containsKey(taxon))
								taxonMap.get(taxon).add(state);
							else {
								List<IState> tempStates = new ArrayList<IState>();
								tempStates.add(state);
								taxonMap.put(taxon, tempStates);
							}
						}
						else {
							Map<ITaxon, List<IState>> taxonRemoval = new HashMap<ITaxon, List<IState>>();
							List<IState> tempStates = new ArrayList<IState>();
							tempStates.add(state);
							taxonRemoval.put(taxon, tempStates);
							toRemove.put(charName, taxonRemoval);
						}
						wasExpanded = true;
					}
					else if (wasExpanded) {
						//this is the case where we're looking at a single state
						//but expanded a range state in a previous taxon
						//for this character.
						List<IState> tempStates = new ArrayList<IState>();
						tempStates.add(new EmptyState());
						if(toAdd.containsKey(charNameFrom)) {
							Map<ITaxon, List<IState>> taxonMap = toAdd.get(charNameFrom);
							if(taxonMap.containsKey(taxon))
								taxonMap.get(taxon).add(new EmptyState());
							else
								taxonMap.put(taxon, tempStates);
						}
						else {
							Map<ITaxon, List<IState>> tempMap = new HashMap<ITaxon, List<IState>>();
							tempMap.put(taxon, tempStates);
							toAdd.put(charNameTo, tempMap);
						}
						if(toAdd.containsKey(charNameTo)) {
							Map<ITaxon, List<IState>> taxonMap = toAdd.get(charNameTo);
							if(taxonMap.containsKey(taxon))
								taxonMap.get(taxon).add(new EmptyState());
							else
								taxonMap.put(taxon, tempStates);
						}
					}
				}
			}
		} //end giant for loop
		//now add expanded char/states and remove those marked
		table.putAll(toAdd);
		for(String charName : toRemove.keySet()) {
			for(ITaxon taxon : toRemove.get(charName).keySet()) {
				List<IState> removableStates = toRemove.get(charName).get(taxon);
				List<IState> existingStates = table.get(charName).get(taxon);
				existingStates.removeAll(removableStates);
			}
		}
		//and lastly, clean out any characters that have empty
		//state lists for each taxon
		List<String> charsToRemove = new ArrayList<String>();
		for(String charName : table.keySet()) {
			int count = 0;
			for(ITaxon taxon : table.get(charName).keySet()) {
				List<IState> existingStates = table.get(charName).get(taxon);
				count += existingStates.size();
			}
			if (count == 0)
				charsToRemove.add(charName);
		}
		for(String ch : charsToRemove)
			table.remove(ch);
	}

	/**
	 * Internal representation of the taxon-char matrix is a mapping from character names
	 * to maps from taxa to states.
	 * @return the table
	 */
	public Map<String, Map<ITaxon, List<IState>>> getTable() {
		return table;
	}

	/**
	 * Returns the underlying taxon hierarchy for this matrix.
	 * @return the hierarchy
	 */
	public TaxonHierarchy getHierarchy() {
		return hierarchy;
	}
	
	/**
	 * "Pretty prints" the table.
	 */
	public void printSimple() {
		for(String s : this.table.keySet()) {
			Map<ITaxon, List<IState>> map = table.get(s);
			for(ITaxon taxon : map.keySet()) {
				System.out.println("Character:"+s+", Taxon:"+taxon.getName()+", "+map.get(taxon).toString());
			}
		}
	}
}
