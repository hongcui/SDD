package generateFileToTaxonMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import taxonomy.TaxonRank;

/**
 * DAO for filename-taxon database.
 * 
 * @author Jing Liu
 * 
 */
public class FilenameTaxon {
	/**
	 * Ordered list of taxon ranks.
	 */
	private List<String> taxonRank;
	
	protected HashMap valuesMap;

	/**
	 * Create a new FilenameTaxonDao and add ranking list.
	 */
	public FilenameTaxon(String filepath) {
		taxonRank = new ArrayList<String>();
		String[] list = new String[] { "domain", "kingdom", "phylum",
				"subphylum", "superdivision", "division", "superclass",
				"class", "subclass", "superorder", "order", "suborder",
				"superfamily", "family", "subfamily", "tribe", "subtribe",
				"genus", "subgenus", "section", "subsection", "species",
				"subspecies", "variety" };
		for (String s : list)
			taxonRank.add(s);
		
//		String filepath = ""C:\\Users\\jingliu5\\UFLwork\\Data\\Charaparser\\FoC\\FoCV10\\target\\final";
		FileName2TaxonFNA fntf = new FileName2TaxonFNA(filepath);
		fntf.populateFilename2TaxonTable();
		valuesMap = fntf.getValuesMap();
	}

	/**
	 * Gets a map of taxon level values for a given file.
	 * 
	 * @param filename
	 * @return
	 */
	public Map<String, String> getTaxonValues(String filename) {
		Map<String, String> result = new HashMap<String, String>();
		result = (Map<String, String>) valuesMap.get(filename);		
		return result;
	}

	/**
	 * added by Jing Liu Gets a taxon level name list for a given file.
	 * 
	 * @param filename
	 * @return
	 */
	public List<String> getTaxonRankNameList(String filename) {
		Map<String, String> result = getTaxonValues(filename);
		List<String> ranknamelist = new LinkedList<String>();
		String rank;
		Map<String, String> values = getTaxonValues(filename);
		rank = values.get("domain");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("kingdom");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("phylum");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("subphylum");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("superdivision");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("division");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("superclass");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("class");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("subclass");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("superorder");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("order");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("suborder");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("superfamily");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("family");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("subfamily");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("tribe");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("subtribe");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("genus");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("subgenus");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("section");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("subsection");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("species");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("subspecies");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		rank = values.get("variety");
		if (!rank.isEmpty()) {
			ranknamelist.add(rank);
		}
		return ranknamelist;
	}

	/**
	 * added by Jing Liu Gets a taxon level list for a given file.
	 * 
	 * @param filename
	 * @return
	 */
	public List<String> getTaxonRankList(String filename) {
		Map<String, String> result = getTaxonValues(filename);
		List<String> ranklist = new LinkedList<String>();
		String rank;
		rank = result.get("domain");
		if (!rank.isEmpty()) {
			ranklist.add("domain");
		}
		rank = result.get("kingdom");
		if (!rank.isEmpty()) {
			ranklist.add("kingdom");
		}
		rank = result.get("phylum");
		if (!rank.isEmpty()) {
			ranklist.add("phylum");
		}
		rank = result.get("subphylum");
		if (!rank.isEmpty()) {
			ranklist.add("subphylum");
		}
		rank = result.get("superdivision");
		if (!rank.isEmpty()) {
			ranklist.add("superdivision");
		}
		rank = result.get("division");
		if (!rank.isEmpty()) {
			ranklist.add("division");
		}
		rank = result.get("superclass");
		if (!rank.isEmpty()) {
			ranklist.add("superclass");
		}
		rank = result.get("class");
		if (!rank.isEmpty()) {
			ranklist.add("class");
		}
		rank = result.get("subclass");
		if (!rank.isEmpty()) {
			ranklist.add("subclass");
		}
		rank = result.get("superorder");
		if (!rank.isEmpty()) {
			ranklist.add("superorder");
		}
		rank = result.get("order");
		if (!rank.isEmpty()) {
			ranklist.add("order");
		}
		rank = result.get("suborder");
		if (!rank.isEmpty()) {
			ranklist.add("suborder");
		}
		rank = result.get("superfamily");
		if (!rank.isEmpty()) {
			ranklist.add("superfamily");
		}
		rank = result.get("family");
		if (!rank.isEmpty()) {
			ranklist.add("family");
		}
		rank = result.get("subfamily");
		if (!rank.isEmpty()) {
			ranklist.add("subfamily");
		}
		rank = result.get("tribe");
		if (!rank.isEmpty()) {
			ranklist.add("tribe");
		}
		rank = result.get("subtribe");
		if (!rank.isEmpty()) {
			ranklist.add("subtribe");
		}
		rank = result.get("genus");
		if (!rank.isEmpty()) {
			ranklist.add("genus");
		}
		rank = result.get("subgenus");
		if (!rank.isEmpty()) {
			ranklist.add("subgenus");
		}
		rank = result.get("section");
		if (!rank.isEmpty()) {
			ranklist.add("section");
		}
		rank = result.get("subsection");
		if (!rank.isEmpty()) {
			ranklist.add("subsection");
		}
		rank = result.get("species");
		if (!rank.isEmpty()) {
			ranklist.add("species");
		}
		rank = result.get("subspecies");
		if (!rank.isEmpty()) {
			ranklist.add("subspecies");
		}
		rank = result.get("variety");
		if (!rank.isEmpty()) {
			ranklist.add("variety");
		}
		return ranklist;
	}

	/**
	 * added by Jing Liu Gets a map of taxon level value for a given file.
	 * 
	 * @param filename
	 * @return
	 */
	public String getTaxonRank(String filename) {
		String result = "";
		
		Map<String, String> values = getTaxonValues(filename);
		if (values.get("domain").length() != 0)
			result = "domain";
		if (values.get("kingdom").length() != 0)
			result = "kingdom";
		if (values.get("phylum").length() != 0)
			result = "phylum";
		if (values.get("subphylum").length() != 0)
			result = "subphylum";
		if (values.get("superdivision").length() != 0)
			result = "superdivision";
		if (values.get("division").length() != 0)
			result = "division";
		if (values.get("superclass").length() != 0)
			result = "superclass";
		if (values.get("class").length() != 0)
			result = "class";
		if (values.get("subclass").length() != 0)
			result = "subclass";
		if (values.get("superorder").length() != 0)
			result = "superorder";
		if (values.get("order").length() != 0)
			result = "order";
		if (values.get("suborder").length() != 0)
			result = "suborder";
		if (values.get("superfamily").length() != 0)
			result = "superfamily";
		if (values.get("family").length() != 0)
			result = "family";
		if (values.get("subfamily").length() != 0)
			result = "subfamily";
		if (values.get("tribe").length() != 0)
			result = "tribe";
		if (values.get("subtribe").length() != 0)
			result = "subtribe";
		if (values.get("genus").length() != 0)
			result = "genus";
		if (values.get("subgenus").length() != 0)
			result = "subgenus";
		if (values.get("section").length() != 0)
			result = "section";
		if (values.get("subsection").length() != 0)
			result = "subsection";
		if (values.get("species").length() != 0)
			result = "species";
		if (values.get("subspecies").length() != 0)
			result = "subspecies";
		if (values.get("variety").length() != 0)
			result = "variety";		
		return result;
	}

	/**
	 * Get a list of filenames that match the name of a given taxon.
	 * 
	 * @param taxon
	 *            The taxon level at which to match.
	 * @param name
	 *            The name for that taxon.
	 * @return A list of all file names matching that taxon name.
	 */
	public List<String> getFileListByTaxonName(String taxon, String name) {
		List<String> result = new ArrayList<String>();
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		while (it.hasNext()){
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			if (values.get(taxon).equals(name))
				result.add(filename);
		}
		return result;
	}

	/**
	 * Get the filename for the family description of a taxon.
	 * 
	 * @param familyName
	 * @return
	 */
	public String getFilenameOfFamilyDescription(String familyName) {
		String result = "";
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		while (it.hasNext()){
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			if (values.get("family").equals(familyName)
					&& values.get("subfamily").equals("")
					&& values.get("tribe").equals("")
					&& values.get("subtribe").equals("")
					&& values.get("genus").equals("")
					&& values.get("subgenus").equals("")
					&& values.get("section").equals("")
					&& values.get("subsection").equals("")
					&& values.get("species").equals("")
					&& values.get("subspecies").equals("")
					&& values.get("variety").equals(""))
				result = filename;
		}
		return result;
	}

	/**
	 * Gets the filename for a taxon at a given rank with a given name, such
	 * that the file is the lowest level description of the taxon at that level.
	 * 
	 * @param rank
	 * @param name
	 * @return
	 */
	public String getFilenameForDescription(TaxonRank rank, String name) {		
		String tRank = rank.toString().toLowerCase();
		int indexOfRank = taxonRank.indexOf(tRank);
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		while (it.hasNext()){
			filename = (String) it.next();			
			Map<String, String> values = getTaxonValues(filename);
			int i = 0;
			if (values.get(tRank).equals(name)){
				for(i = indexOfRank + 1; i < taxonRank.size(); i++)
					if (!values.get(taxonRank.get(i)).equals(""))
						break;
			}
			if (i==taxonRank.size())
				return filename;			
		}
		return filename;
	}

	/**
	 * Get filenames for descriptions within a taxonomical range.
	 * 
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getFilenamesForManyDescriptions(TaxonRank topLevel,
			String topName, TaxonRank bottomLevel) {
		List<String> result = new ArrayList<String>();
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		
		String topRank = topLevel.toString().toLowerCase();
		String bottomRank = bottomLevel.toString().toLowerCase();
		int stopIndex = taxonRank.indexOf(bottomRank) + 1; // highest rank index
															// at which value is
															// empty
		String whereClause = topRank + " = \'" + topName + "\' ";
		while (it.hasNext()){
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			if (values.get(topRank).equals(topName)){
				int i;
				for (i = stopIndex; i < taxonRank.size(); i++)
					if (!taxonRank.get(i).equals(""))
						break;
				if (i==taxonRank.size())
					result.add(filename);
			}
		}
		System.out.println(result);
		return result;
	}

	/**
	 * added by Jing Liu Get filenames for descriptions within a taxonomical
	 * range.
	 * 
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getFilenamesForManyDescriptionsByOrder(
			TaxonRank topLevel, String topName, TaxonRank bottomLevel) {
		List<String> filenames = new LinkedList<String>();
		String topRank = topLevel.toString().toLowerCase();
		String bottomRank = bottomLevel.toString().toLowerCase();
		int stopIndex = taxonRank.indexOf(bottomRank) + 1; // highest rank index
		// at which value is
		// empty
		int startIndex = taxonRank.indexOf(topRank) + 1;
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		int index = taxonRank.size();
		while (it.hasNext()) {
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			if (values.get(topRank).equals(topName)) {
				int j;
				for (j = stopIndex; j < taxonRank.size(); j++) {
					String tRank = taxonRank.get(j);
					if (!values.get(tRank).equals("")) {
						break;
					}
				}
				if (j == taxonRank.size()) {
					filenames.add(filename);
				}
			}
		}
		
		int[] ranks = new int[filenames.size()];
		for (int i = 0; i < filenames.size(); i++) {
			Map<String, String> values = getTaxonValues(filenames.get(i));
			for (int j = stopIndex; j >= startIndex; j--) {
				String tRank = taxonRank.get(j);
				if(!values.get(tRank).equals(""))
					ranks[i] = j;
			}
		}
		
		
		List<String> orderedfilenames = new LinkedList<String>();
		for (int j = stopIndex; j >= startIndex; j--) {
			for (int i = 0; i < filenames.size(); i++) {
				if (ranks[i] == j)
					orderedfilenames.add(filenames.get(i));
			}
		}
		System.out.println(orderedfilenames);
		return orderedfilenames;
	}

	/**
	 * added by Jing Liu Get filenames for descriptions in the next rank.
	 * 
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getFilenamesForManyDescriptionsNextOrder(
			List<TaxonRank> topLevel, List<String> topName) {
		List<String> filenames = new LinkedList<String>();		
		String bottomRank = getNextRank(topLevel, topName).toString()
				.toLowerCase();
		if (!bottomRank.isEmpty()) {
			Set<String> keyset = valuesMap.keySet();
			Iterator it = keyset.iterator();
			String filename = null;
			int index = taxonRank.size();
			while (it.hasNext()) {
				filename = (String) it.next();
				Map<String, String> values = getTaxonValues(filename);
				int i = 0;

				for (String name : topName) {
					if (!values.get(topLevel.get(i).name().toLowerCase())
							.equals(name))
						break;
					else
						i++;
				}
				
				if (i == topName.size() && !values.get(bottomRank).equals("")) {	
					int stopIndex = taxonRank.indexOf(bottomRank) + 1; // highest rank
					// index at
					// which value
					// is empty
					int j;
					for (j = stopIndex; j < taxonRank.size(); j++) {
						String tRank = taxonRank.get(j);
						if (!values.get(tRank).equals("")) {
							break;
						}
					}
					if (j==taxonRank.size()){
						filenames.add(filename);
					}
				}

			}
		}	
		System.out.println(filenames);
		return filenames;
	}

	/**
	 * added by Jing Liu Get filenames a given rank.
	 * 
	 * @param rank
	 * @return
	 */
	public List<String> getFilenamesForGivenRank(String rank) {		
		List<String> filenames = new LinkedList<String>();
		int stopIndex = taxonRank.indexOf(rank) + 1; // highest rank index at
		// which value is empty
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		int index = taxonRank.size();
		while (it.hasNext()){
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			if (!values.get(rank).equals("")){
				int j;
				for (j = stopIndex; j < taxonRank.size(); j++) {
					String tRank = taxonRank.get(j);
					if (!values.get(tRank).equals(""))
						break;
				}		
				if (j==taxonRank.size())
					filenames.add(filename);
			}
		}
		System.out.println(filenames);
		return filenames;
	}

	/**
	 * added by Jing Liu Get filenames for descriptions in the next rank.
	 * 
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getRankNamesForNextOrder(List<TaxonRank> topLevel,
			List<String> topName) {
		List<String> ranknames = new LinkedList<String>();
		// String curRank =
		// topLevel.get(topLevel.size()-1).name().toLowerCase();
		String bottomRank = getNextRank(topLevel, topName).toString()
				.toLowerCase();
		
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		int index = taxonRank.size();
		while (it.hasNext()){
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			int i=0;
			for (String name : topName) {
				if (!values.get(topLevel.get(i).name().toLowerCase()).equals(name))
					break;
				else
					i++;						
			}
			if (i == topName.size()) {
				if (!values.get(bottomRank).equals("")){
					if (!ranknames.contains(values.get(bottomRank)))
						ranknames.add(values.get(bottomRank));
				}				
			}
		}
		System.out.println(ranknames);
		return ranknames;
	}

	// added byJing Liu
	/**
	 * Gets the the highest rank that is not empty in the current table.
	 * 
	 * @return
	 */
	public String getHigestRank() {
		List<String> ranklist = new LinkedList<String>();
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		int index = taxonRank.size();
		while (it.hasNext()) {
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			for (int i = 0; i < taxonRank.size(); i++) {
				String tRank = taxonRank.get(i);
				if (!values.get(tRank).equals(""))
					if (index > i) {
						index = i;
						break;
					}

			}
		}
		String rank = taxonRank.get(index);
		return rank;
	}

	/**
	 * Gets the the lowest rank that is not empty in the current table.
	 * 
	 * @return
	 */
	public String getLowestRank() {
		List<String> ranklist = new LinkedList<String>();
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		int index = 0;
		while (it.hasNext()) {
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			for (int i = taxonRank.size() - 1; i >= 0; i--) {
				String tRank = taxonRank.get(i);
				if (!values.get(tRank).equals(""))
					if (index < i) {
						index = i;
						break;
					}

			}
		}
		String rank = taxonRank.get(index);
		return rank;
	}

	/**
	 * added by Jing Liu Gets the the rank list that is not empty in the current
	 * table.
	 * 
	 * @return
	 */
	public List<String> getNonEmptyRankList() {
		List<String> ranklist = new LinkedList<String>();
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		while (it.hasNext()){
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			for (int i = 0; i < taxonRank.size(); i++) {
				String tRank = taxonRank.get(i);
				if (!values.get(tRank).equals(""))
					if (!ranklist.contains(tRank))
						ranklist.add(tRank);
			}
		}
		return ranklist;		
	
	}

	/**
	 * By Jing Liu Whether we have reached the the lowest rank of currrent
	 * branch.
	 * 
	 * @return
	 */
	public boolean reachedLowestRank(List<TaxonRank> topLevel,
			List<String> topName) {
		String bottomRank = getNextRank(topLevel, topName).toString()
				.toLowerCase();
		return bottomRank.isEmpty();
	}

	/**
	 * Gets the the next rank of the current rank that is not empty in the
	 * current table.
	 * 
	 * @return
	 */
	public String getNextRank(List<TaxonRank> levels, List<String> names) {
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		int index = taxonRank.size();
		while (it.hasNext()){
			filename = (String) it.next();
			Map<String, String> values = getTaxonValues(filename);
			int i=0;
			for (String name : names) {
				if (!values.get(levels.get(i).name().toLowerCase()).equals(name))
					break;
				else
					i++;						
			}
			if (i == names.size()) {
				String currRank = levels.get(i - 1).toString().toLowerCase();
				int currIndex = taxonRank.indexOf(currRank) + 1;
				for (int j = currIndex; j < taxonRank.size(); j++) {
					String tRank = taxonRank.get(j);
					if (!values.get(tRank).equals("")) {
						if (j < index) {
							index = j;
							break;
						} else {
							break;
						}

					}
				}
			}
		}
		
		it = keyset.iterator();
		String nextRank = taxonRank.get(index);
		return nextRank;
	}

	/**
	 * Gets the the lowest rank that is not empty in the current table.
	 * 
	 * @return true if rankA is higher than rankB; false if rank A is equal or
	 *         lower than rankB.
	 * 
	 */
	public boolean compareRanks(String rankA, String rankB) {
		int a = 0, b = 0;
		for (int i = 0; i < taxonRank.size(); i++) {
			if (taxonRank.get(i).equals(rankA))
				a = i;
			if (taxonRank.get(i).equals(rankB))
				b = i;
		}
		return a < b;
	}

	/**
	 * Gets the the lowest rank that is not empty in the current table.
	 * 
	 * @return true if rankA is equals as rankB; false otherwise.
	 * 
	 */
	public boolean equalRanks(String rankA, String rankB) {
		int a = 0, b = 0;
		for (int i = 0; i < taxonRank.size(); i++) {
			if (taxonRank.get(i).equals(rankA))
				a = i;
			if (taxonRank.get(i).equals(rankB))
				b = i;
		}
		return a == b;
	}

	/**
	 * Get filenames for descriptions within a taxonomical range.
	 * 
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getRankNamesForGivenRank(TaxonRank topLevel) {
		List<String> ranknames = new LinkedList<String>();
		String topRank = topLevel.toString().toLowerCase();		
		
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		while (it.hasNext()){
			filename = (String) it.next();			
			Map<String, String> values = getTaxonValues(filename);
			int i = 0;
			if (!values.get(topRank).equals("")){
				if (!ranknames.contains(values.get(topRank))){
					ranknames.add(values.get(topRank));
				}
			}	
		}
		System.out.println(ranknames);
		return ranknames;
	}

	// added by Jing Liu
	public List<String> getAllRankNames() {
		List<String> res = new LinkedList<String>();
		Set<String> keyset = valuesMap.keySet();
		Iterator it = keyset.iterator();
		String filename = null;
		while (it.hasNext()){
			filename = (String) it.next();			
			List<String> ranknames = getTaxonRankNameList(filename);
			// List<String> ranklists=getTaxonRankList(filename);
			String rankname = ranknames.get((ranknames.size() - 1));
			// String ranklist = ranklists.get((ranklists.size()-1));
			res.add(rankname.toLowerCase());		
		}
		return res;
	}
}
