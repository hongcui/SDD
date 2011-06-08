package conversion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import states.EmptyState;
import states.IState;
import states.SingletonState;
import taxonomy.ITaxon;
import taxonomy.TaxonHierarchy;
import tree.TreeNode;
import annotationSchema.jaxb.Structure;

/**
 * Class for converting a Taxon Hierarchy into a Taxon-by-Character matrix.
 * @author Alex
 *
 */
/**
 * @author Alex
 *
 */
/**
 * @author Alex
 *
 */
/**
 * @author Alex
 *
 */
public class TaxonCharacterMatrix {

	private Map<String, Map<ITaxon, IState>> table;
	private TaxonHierarchy hierarchy;
	
	public TaxonCharacterMatrix(TaxonHierarchy th) {
		this.hierarchy = th;
		this.table = createTable();
	}

	/**
	 * Fills in a table that represents a taxon-by-character matrix.
	 * @return
	 */
	private Map<String, Map<ITaxon, IState>> createTable() {
		Map<String, Map<ITaxon, IState>> map = new TreeMap<String, Map<ITaxon,IState>>();
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
				Map<String, IState> charStateMap = structure.getCharStateMap();
				for(String charName : charStateMap.keySet()) {
					String fullName = resolveFullCharacterName(charName, structNode);
					IState state = charStateMap.get(charName);
					if(map.containsKey(fullName)) {
						map.get(fullName).put(taxon, state);
					}
					else {
						Map<ITaxon, IState> subMap = new HashMap<ITaxon, IState>();
						subMap.put(taxon, state);
						map.put(fullName, subMap);
					}
				}
			}
		}
		postProcessTable(map, allTaxa);
		return map;
	}

	private void postProcessTable(Map<String, Map<ITaxon, IState>> map,
			List<ITaxon> allTaxa) {
		for(String ch : map.keySet()) {
			Map<ITaxon, IState> taxonToState = map.get(ch);
			if(taxonToState.size() != allTaxa.size()) {	//then begin "inheriting" character states from higher taxa
				for(ITaxon taxon : allTaxa) {
					TreeNode<ITaxon> taxonNode = this.hierarchy.findTaxonByName(taxon.getName(), taxon.getTaxonRank());
					if (!taxonToState.containsKey(taxon)) {
						if (taxonNode.getParent() != null) {
							ITaxon parentTaxon = taxonNode.getParent().getElement();
							if (taxonToState.containsKey(parentTaxon)) { //if a parent taxon is in the map, go grab states from it
								IState parentState = taxonToState.get(parentTaxon);
								taxonToState.put(taxon, parentState);
							}
							else {	//if the parent is not in the map, add an empty state for the parent
								taxonToState.put(parentTaxon, new EmptyState<String>());
								taxonToState.put(taxon, new EmptyState<String>());
							}
						}
						else
							taxonToState.put(taxon, new EmptyState<String>());
					}	//end if taxon not mapped to some state.
				}
			}
			//now, take care of instances where singleton states are mixed with range states
			Set<Class> stateTypes = new HashSet<Class>();
			for(IState state : taxonToState.values()) 
				stateTypes.add(state.getClass());
			if(stateTypes.size() > 1) { 	//then promote the singleton states to range states
				for(ITaxon t : taxonToState.keySet()) {
					IState state = taxonToState.get(t);
					if(state instanceof SingletonState)
						taxonToState.put(t, state.promote());
				}
			}
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
		Map<ITaxon, List<String>> rows = new HashMap<ITaxon, List<String>>();
		for(String charName : table.keySet()) {
			header.add(charName);
			Map<ITaxon, IState> taxonToState = table.get(charName);
			boolean needsHeaderExpansion = true;
			for(ITaxon taxon : taxonToState.keySet()) {
				IState state = taxonToState.get(taxon);	//here, we rely on the supposition that we'll never see two different state types for the same character across a range of taxa.
				if(rows.containsKey(taxon)) {
					List<String> row = rows.get(taxon);
					if(row == null) {
						row = new ArrayList<String>();
						row.add(taxon.getName());
					}
					needsHeaderExpansion = addStateToRow(state, row, header, charName, needsHeaderExpansion);
				}
				else {
					List<String> row = new ArrayList<String>();
					row.add(taxon.getName());	//The first element of a row is the name of the taxon.
					needsHeaderExpansion = addStateToRow(state, row, header, charName, needsHeaderExpansion);
					rows.put(taxon, row);
				}
			}
		}
		BufferedWriter out = new BufferedWriter(fstream);
		try {
			constructMatrix(out, header, rows);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void constructMatrix(BufferedWriter out, List<String> header,
			Map<ITaxon, List<String>> rows) throws IOException{
		postProcessRows(header, rows);
		for(String h : header)
			out.write(h+",");
		for(ITaxon t : rows.keySet()) {
			out.write("\n");
			List<String> row = rows.get(t);
			for(String s : row)
				out.write(s+",");
		}
		out.close();		
	}

	/**
	 * Cuts out any columns that have all empty/null entries.
	 * @param header
	 * @param rows
	 */
	private void postProcessRows(List<String> header,
			Map<ITaxon, List<String>> rows) {
		List<Integer> cutList = new ArrayList<Integer>();
		for(String s : header) {
			int i = header.indexOf(s);
			boolean cut = true;
			for(List<String> list : rows.values()) {
				if(!(list.get(i) == null || list.get(i).trim().isEmpty() || list.get(i).equals(" ")))
					cut = false;
			}
			if(cut)
				cutList.add(i);
		}
		int offset = 0;
		for(int i : cutList) {
			for(List<String> list : rows.values())
				list.remove(i-offset);
			header.remove(i-offset);
			offset++;
		}
		System.out.println(header.size());
		
	}

	/**
	 * This method resolves how to put a state object into a matrix.  If it's a range state, we need to list two characters
	 * and states (from and to) instead of one.  This also requires inserting additional characters into the header list.
	 * @param state State object to resolve to matrix entries.
	 * @param row The row (for a taxon) in question.
	 * @param header The header string list.
	 * @param charName The name of the original character, before resolution.
	 */
	@SuppressWarnings("rawtypes")
	private boolean addStateToRow(IState state, List<String> row, List<String> header, String charName, boolean needsHeaderExpansion) {
		if(needsHeaderExpansion) {
			int originalIndex = header.indexOf(charName);
			if (state instanceof SingletonState) {
				row.add(state.getMap().get("value").toString());
				if(state.getFromUnit() != null) {
					header.add(originalIndex+1, charName+"_unit");
					row.add(state.getFromUnit());
				}
			}
			else {	//handle EmptyState and RangeState identically
				header.set(originalIndex, charName+"_from");
				header.add(originalIndex+1, charName+"_to");
				header.add(originalIndex+2, charName+"_from_unit");
				header.add(originalIndex+3, charName+"_to_unit");
				row.add(state.getMap().get("from value").toString());
				row.add(state.getMap().get("to value").toString());
				row.add(state.getFromUnit());
				row.add(state.getToUnit());
			}
		}
		else {
			if (state instanceof SingletonState) {
				row.add(state.getMap().get("value").toString());
				if(state.getFromUnit() != null) {
					row.add(state.getFromUnit());
				}
			}
			else {
				row.add(state.getMap().get("from value").toString());
				row.add(state.getMap().get("to value").toString());
				row.add(state.getFromUnit());
				row.add(state.getToUnit());
			}
		}
		return false;
	}

	/**
	 * Internal representation of the taxon-char matrix is a mapping from character names
	 * to maps from taxa to states.
	 * @return the table
	 */
	public Map<String, Map<ITaxon, IState>> getTable() {
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
			Map<ITaxon, IState> map = table.get(s);
			for(ITaxon taxon : map.keySet()) {
				System.out.println("Character:"+s+", Taxon:"+taxon.getName()+", "+map.get(taxon).toString());
			}
		}
	}
}
