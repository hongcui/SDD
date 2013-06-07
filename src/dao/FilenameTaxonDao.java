package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import taxonomy.TaxonRank;

/**
 * DAO for filename-taxon database.
 * @author Alex
 *
 */
public class FilenameTaxonDao extends BaseDao{
	//DatabaseProperties dp = this.getProperties(); 
	//String dbname = (String) dp.get("filename-database");
	private String database = this.getProperties().getProperty("filename-database");
	private String tablename = this.getProperties().getProperty("filename-table-name");
	private Connection conn;  //added by Jing Liu
	
	/**
	 * Ordered list of taxon ranks.
	 */
	private List<String> taxonRank;
	
	
	public void getDBConnection(){
		try {
			this.conn = getConnection(database);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void closeDBConnection(){
		try {
			if (conn!=null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a new FilenameTaxonDao and add ranking list.
	 */
	public FilenameTaxonDao() {
		taxonRank = new ArrayList<String>();
		String[] list = new String[] { "domain", "kingdom", "phylum",
				"subphylum", "superdivision", "division", "superclass",
				"class", "subclass", "superorder", "order", "suborder",
				"superfamily", "family", "subfamily", "tribe", "subtribe",
				"genus", "subgenus", "section", "subsection", "species",
				"subspecies", "variety" };
		for (String s : list)
			taxonRank.add(s);
	}
	
	/**
	 * Gets a map of taxon level values for a given file.
	 * @param filename
	 * @return
	 */
	public Map<String, String> getTaxonValues(String filename) {
		Map<String, String> result = new HashMap<String, String>();
		ResultSet rs = null;
	//	Connection conn = null;
		try {
	//		conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT * FROM " + this.database+"."+this.tablename +
					" WHERE filename = '" + filename +"';");
			while(rs.next()) {
				result.put("domain", rs.getString("domain"));
				result.put("kingdom", rs.getString("kingdom"));
				result.put("phylum", rs.getString("phylum"));
				result.put("subphylum", rs.getString("subphylum"));
				result.put("superdivision", rs.getString("superdivision"));
				result.put("division", rs.getString("division"));
				result.put("superclass", rs.getString("superclass"));
				result.put("class", rs.getString("class"));
				result.put("subclass", rs.getString("subclass"));
				result.put("superorder", rs.getString("superorder"));
				result.put("order", rs.getString("order"));
				result.put("suborder", rs.getString("suborder"));
				result.put("superfamily", rs.getString("superfamily"));
				result.put("family", rs.getString("family"));
				result.put("subfamily", rs.getString("subfamily"));
				result.put("tribe", rs.getString("tribe"));
				result.put("subtribe", rs.getString("subtribe"));
				result.put("genus", rs.getString("genus"));
				result.put("subgenus", rs.getString("subgenus"));
				result.put("section", rs.getString("section"));
				result.put("subsection", rs.getString("subsection"));
				result.put("species", rs.getString("species"));
				result.put("subspecies", rs.getString("subspecies"));
				result.put("variety", rs.getString("variety"));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	/*		try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	/**added by Jing Liu
	 * Gets a taxon level name list for a given file.
	 * @param filename
	 * @return
	 */
	public List<String> getTaxonRankNameList(String filename) {
		Map<String, String> result = getTaxonValues(filename);
		List<String> ranknamelist = new LinkedList<String>();
		String rank;
		rank = getTaxonValues(filename).get("domain");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("kingdom");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("phylum");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("subphylum");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("superdivision");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("division");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("superclass");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("class");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("subclass");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("superorder");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}		
		rank = getTaxonValues(filename).get("order");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}	
		rank = getTaxonValues(filename).get("suborder");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("superfamily");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("family");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("subfamily");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("tribe");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("subtribe");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("genus");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("subgenus");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("section");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("subsection");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("species");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("subspecies");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		rank = getTaxonValues(filename).get("variety");
		if (!rank.isEmpty()){
			ranknamelist.add(rank);
		}
		return ranknamelist;		
	}
	
	
	/**added by Jing Liu
	 * Gets a taxon level list for a given file.
	 * @param filename
	 * @return
	 */
	public List<String> getTaxonRankList(String filename) {
		Map<String, String> result = getTaxonValues(filename);
		List<String> ranklist = new LinkedList<String>();
		String rank;
		rank = getTaxonValues(filename).get("domain");
		if (!rank.isEmpty()){
			ranklist.add("domain");
		}		
		rank = getTaxonValues(filename).get("kingdom");
		if (!rank.isEmpty()){
			ranklist.add("kingdom");
		}		
		rank = getTaxonValues(filename).get("phylum");
		if (!rank.isEmpty()){
			ranklist.add("phylum");
		}		
		rank = getTaxonValues(filename).get("subphylum");
		if (!rank.isEmpty()){
			ranklist.add("subphylum");
		}		
		rank = getTaxonValues(filename).get("superdivision");
		if (!rank.isEmpty()){
			ranklist.add("superdivision");
		}		
		rank = getTaxonValues(filename).get("division");
		if (!rank.isEmpty()){
			ranklist.add("division");
		}		
		rank = getTaxonValues(filename).get("superclass");
		if (!rank.isEmpty()){
			ranklist.add("superclass");
		}		
		rank = getTaxonValues(filename).get("class");
		if (!rank.isEmpty()){
			ranklist.add("class");
		}		
		rank = getTaxonValues(filename).get("subclass");
		if (!rank.isEmpty()){
			ranklist.add("subclass");
		}		
		rank = getTaxonValues(filename).get("superorder");
		if (!rank.isEmpty()){
			ranklist.add("superorder");
		}		
		rank = getTaxonValues(filename).get("order");
		if (!rank.isEmpty()){
			ranklist.add("order");
		}	
		rank = getTaxonValues(filename).get("suborder");
		if (!rank.isEmpty()){
			ranklist.add("suborder");
		}
		rank = getTaxonValues(filename).get("superfamily");
		if (!rank.isEmpty()){
			ranklist.add("superfamily");
		}
		rank = getTaxonValues(filename).get("family");
		if (!rank.isEmpty()){
			ranklist.add("family");
		}
		rank = getTaxonValues(filename).get("subfamily");
		if (!rank.isEmpty()){
			ranklist.add("subfamily");
		}
		rank = getTaxonValues(filename).get("tribe");
		if (!rank.isEmpty()){
			ranklist.add("tribe");
		}
		rank = getTaxonValues(filename).get("subtribe");
		if (!rank.isEmpty()){
			ranklist.add("subtribe");
		}
		rank = getTaxonValues(filename).get("genus");
		if (!rank.isEmpty()){
			ranklist.add("genus");
		}
		rank = getTaxonValues(filename).get("subgenus");
		if (!rank.isEmpty()){
			ranklist.add("subgenus");
		}
		rank = getTaxonValues(filename).get("section");
		if (!rank.isEmpty()){
			ranklist.add("section");
		}
		rank = getTaxonValues(filename).get("subsection");
		if (!rank.isEmpty()){
			ranklist.add("subsection");
		}
		rank = getTaxonValues(filename).get("species");
		if (!rank.isEmpty()){
			ranklist.add("species");
		}
		rank = getTaxonValues(filename).get("subspecies");
		if (!rank.isEmpty()){
			ranklist.add("subspecies");
		}
		rank = getTaxonValues(filename).get("variety");
		if (!rank.isEmpty()){
			ranklist.add("variety");
		}
		return ranklist;		
	}
	
	/**added by Jing Liu
	 * Gets a map of taxon level value for a given file.
	 * @param filename
	 * @return
	 */
	public String getTaxonRank(String filename) {
		String result="";
		ResultSet rs = null;
	//	Connection conn = null;
		try {
		//	conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT * FROM " + this.database+"."+this.tablename +
					" WHERE filename = '" + filename +"';");
			while(rs.next()) {
				if (rs.getString("domain").length()!=0) result = "domain";
				if (rs.getString("kingdom").length()!=0) result = "kingdom";
				if (rs.getString("phylum").length()!=0) result = "phylum";
				if (rs.getString("subphylum").length()!=0) result = "subphylum";
				if (rs.getString("superdivision").length()!=0) result = "superdivision";
				if (rs.getString("division").length()!=0) result = "division";
				if (rs.getString("superclass").length()!=0) result = "superclass";				
				if (rs.getString("class").length()!=0) result = "class";
				if (rs.getString("subclass").length()!=0) result = "subclass";
				if (rs.getString("superorder").length()!=0) result = "superorder";
				if (rs.getString("order").length()!=0) result = "order";
				if (rs.getString("suborder").length()!=0) result = "suborder";
				if (rs.getString("superfamily").length()!=0) result = "superfamily";
				if (rs.getString("family").length()!=0) result = "family";
				if (rs.getString("subfamily").length()!=0) result = "subfamily";
				if (rs.getString("tribe").length()!=0) result = "tribe";
				if (rs.getString("subtribe").length()!=0) result = "subtribe";
				if (rs.getString("genus").length()!=0) result = "genus";
				if (rs.getString("subgenus").length()!=0) result = "subgenus";
				if (rs.getString("section").length()!=0) result = "section";
				if (rs.getString("subsection").length()!=0) result = "subsection";
				if (rs.getString("species").length()!=0) result = "species";
				if (rs.getString("subspecies").length()!=0) result = "subspecies";
				if (rs.getString("variety").length()!=0) result = "variety";
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	/**
	 * Get a list of filenames that match the name of a given taxon.
	 * @param taxon The taxon level at which to match.
	 * @param name The name for that taxon.
	 * @return A list of all file names matching that taxon name.
	 */
	public List<String> getFileListByTaxonName(String taxon, String name) {
		List<String> result = new ArrayList<String>();
		ResultSet rs = null;
	//	Connection conn = null;
		try {
		//	conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM "+this.database+"."+this.tablename+" WHERE "
					+ taxon + " = '" + name +"';");
			while(rs.next()) {
				result.add(rs.getString("filename"));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	/*		try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	/**
	 * Get the filename for the family description of a taxon.
	 * @param familyName
	 * @return
	 */
	public String getFilenameOfFamilyDescription(String familyName) {
		String result = "";
		ResultSet rs = null;
	//	Connection conn = null;
		try {
		//	conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM "+this.database+"."+this.tablename+" WHERE family = '" + familyName 
					+"' and subfamily = '' and tribe = '' and subtribe = '' and genus = '' "
					+"and subgenus = '' and section = '' and subsection = '' "
					+"and species = '' and subspecies = '' and variety = '' ;");
			while(rs.next()) {
				result = rs.getString("filename");
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	/**
	 * Gets the filename for a taxon at a given rank with a given name, such that the file is the lowest level description
	 * of the taxon at that level.
	 * @param rank
	 * @param name
	 * @return
	 */
	public String getFilenameForDescription(TaxonRank rank, String name) {
		String tRank = rank.toString().toLowerCase();
		int indexOfRank = taxonRank.indexOf(tRank);
		String whereClause = this.tablename +"."+ tRank + " = '"+name + "' ";
		for(int i = indexOfRank + 1; i < taxonRank.size(); i++)
			whereClause += "and " + this.tablename+"."+taxonRank.get(i) + " = '' ";
		String result = "";
		ResultSet rs = null;
	//	Connection conn = null;
		try {
	//		conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM "+this.database+"."+this.tablename+" WHERE " + whereClause + ";");
			while(rs.next()) {
				result = rs.getString("filename");
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	/**
	 * Get filenames for descriptions within a taxonomical range.
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getFilenamesForManyDescriptions(TaxonRank topLevel, String topName, TaxonRank bottomLevel) {
		List<String> filenames = new LinkedList<String>();
		String topRank = topLevel.toString().toLowerCase();
		String bottomRank = bottomLevel.toString().toLowerCase();
		int stopIndex = taxonRank.indexOf(bottomRank) + 1; 	//highest rank index at which value is empty
		String whereClause = topRank + " = \'" + topName + "\' ";
		for(int i = stopIndex; i < taxonRank.size(); i++)
			whereClause += "and " + taxonRank.get(i) + " = \'\' ";
		ResultSet rs = null;
	//	Connection conn = null;
		try {
		//	conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM "+this.database+"."+this.tablename+" WHERE " +  whereClause);
			while(rs.next()) {
				filenames.add(rs.getString("filename"));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		System.out.println(filenames);
		return filenames;
	}
	
	
	/**added by Jing Liu
	 * Get filenames for descriptions within a taxonomical range.
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getFilenamesForManyDescriptionsByOrder(TaxonRank topLevel, String topName, TaxonRank bottomLevel) {
		List<String> filenames = new LinkedList<String>();
		String topRank = topLevel.toString().toLowerCase();
		String bottomRank = bottomLevel.toString().toLowerCase();
		int stopIndex = taxonRank.indexOf(bottomRank) + 1; 	//highest rank index at which value is empty
		String whereClause = topRank + " = \'" + topName + "\' ";
		for(int i = stopIndex; i < taxonRank.size(); i++)
			whereClause += "and " + taxonRank.get(i) + " = \'\' ";
		
		int startIndex = taxonRank.indexOf(topRank) + 1; 
		String orderClause = "order by "+this.tablename+"." + topRank;
		for(int i = startIndex; i < stopIndex; i++)
			orderClause += ", " +this.tablename+"."+ taxonRank.get(i);
		
		ResultSet rs = null;
	//	Connection conn = null;
		try {
		//	conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM "+this.database+"."+this.tablename+" WHERE " +  whereClause + orderClause);
			while(rs.next()) {
				filenames.add(rs.getString("filename"));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		System.out.println(filenames);
		return filenames;
	}
	
	/**added by Jing Liu
	 * Get filenames for descriptions in the next rank.
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getFilenamesForManyDescriptionsNextOrder(List<TaxonRank> topLevel, List<String> topName) {
		List<String> filenames = new LinkedList<String>();
//		String topRank = topLevel.toString().toLowerCase();
		String bottomRank = getNextRank(topLevel, topName).toString()
				.toLowerCase();
		if (!bottomRank.isEmpty()) {
			int stopIndex = taxonRank.indexOf(bottomRank) + 1; // highest rank index at which value is empty
			String whereClause = "";
			int j = 0;
			for (String name : topName) {
				if (!whereClause.equals(""))
					whereClause += "and ";
				whereClause += this.tablename + "."
						+ topLevel.get(j).name().toLowerCase() + " = \'" + name
						+ "\' ";
				j++;
			}
			// if (!whereClause.equals("")) whereClause += "and ";
			whereClause += "and " + this.tablename + "." + bottomRank
					+ " != \'\' ";
			for (int i = stopIndex; i < taxonRank.size(); i++)
				whereClause += "and " + this.tablename + "." + taxonRank.get(i)
						+ " = \'\' ";

			// int startIndex = taxonRank.indexOf(topRank) + 1;
			// String orderClause = "order by "+this.tablename+"." + topRank;
			// for(int i = startIndex; i < stopIndex; i++)
			// orderClause += ", " +this.tablename+"."+ taxonRank.get(i);

			ResultSet rs = null;
			// Connection conn = null;
			try {
				// conn = getConnection(database);
				Statement s = conn.createStatement();
				rs = s.executeQuery("SELECT filename FROM " + this.database
						+ "." + this.tablename + " WHERE " + whereClause);// +
																			// orderClause
				while (rs.next()) {
					filenames.add(rs.getString("filename"));
				}
				rs.close();
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				/*
				 * try { if (conn!=null) conn.close(); } catch (SQLException e)
				 * { e.printStackTrace(); }
				 */
			}
		}
		System.out.println(filenames);
		return filenames;
	}
	
	
	/**added by Jing Liu
	 * Get filenames a given rank.
	 * @param rank
	 * @return
	 */
	public List<String> getFilenamesForGivenRank(String rank) {
		List<String> filenames = new LinkedList<String>();
		// String topRank = topLevel.toString().toLowerCase();
		int stopIndex = taxonRank.indexOf(rank) + 1; // highest rank index at
														// which value is empty
		String whereClause = this.tablename + "." + rank + " != \'\' ";
		for (int i = stopIndex; i < taxonRank.size(); i++) {
			whereClause += "and " + this.tablename + "." + taxonRank.get(i)
					+ " = \'\' ";
		}

		ResultSet rs = null;
		// Connection conn = null;
		try {
			// conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM " + this.database + "."
					+ this.tablename + " WHERE " + whereClause);// +
																// orderClause
			while (rs.next()) {
				filenames.add(rs.getString("filename"));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println(filenames);
		return filenames;
	}
	

	
	/**added by Jing Liu
	 * Get filenames for descriptions in the next rank.
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getRankNamesForNextOrder(List<TaxonRank> topLevel, List<String> topName) {
		List<String> ranknames = new LinkedList<String>();
	//	String curRank = topLevel.get(topLevel.size()-1).name().toLowerCase();
		String bottomRank = getNextRank(topLevel,topName).toString().toLowerCase();
		String whereClause = "";
		int j = 0;
		for(String name:topName){
			if (!whereClause.equals("")) whereClause += "and ";
			whereClause +=  this.tablename + "." + topLevel.get(j).name().toLowerCase() +" = \'"+name+"\' ";
			j++;
		}
	
		ResultSet rs = null;
	//	Connection conn = null;
		try {
		//	conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT distinct "+ bottomRank +" FROM "+this.database+"."+this.tablename+" WHERE " +  whereClause );//+ orderClause
			while(rs.next()) {
				if(!rs.getString(bottomRank).isEmpty())
					ranknames.add(rs.getString(bottomRank));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		System.out.println(ranknames);
		return ranknames;
	}

	
	//added byJing Liu 
	/**
	 * Gets the the highest rank that is not empty in the current table.
	 *
	 * @return
	 */
	public String getHigestRank() {		
	//	Connection conn = null;
		String result = "";
		try {
		//	conn = getConnection(database);
			for (int i = 0; i < taxonRank.size(); i++) {
				String tRank = taxonRank.get(i);
				String whereClause = this.tablename + "." + tRank + " != '' ";
				result = "";
				ResultSet rs = null;
				Statement s = conn.createStatement();
				rs = s.executeQuery("SELECT " + this.tablename + "." + tRank
						+ " FROM " + this.database + "." + this.tablename
						+ " WHERE " + whereClause + ";");
				if (rs.next()) {
					result = tRank;
					break;
				}
				rs.close();
				s.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	/**
	 * Gets the the lowest rank that is not empty in the current table.
	 * 
	 * @return
	 */
	public String getLowestRank() {		
	//	Connection conn = null;
		String result = "";
		try {
		//	conn = getConnection(database);
			for (int i = taxonRank.size()-1; i>=0; i--) {
				String tRank = taxonRank.get(i);
				String whereClause = this.tablename + "." + tRank + " != '' ";
				result = "";
				ResultSet rs = null;
				Statement s = conn.createStatement();
				rs = s.executeQuery("SELECT " + this.tablename + "." + tRank
						+ " FROM " + this.database + "." + this.tablename
						+ " WHERE " + whereClause + ";");
				if (rs.next()) {
					result = tRank;
					break;
				}
				rs.close();
				s.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	
	/**added by Jing Liu
	 * Gets the the rank list that is not empty in the current table.
	 * 
	 * @return
	 */
	public List<String> getNonEmptyRankList() {		
	//	Connection conn = null;
		String result = "";
		List<String> ranklist = new LinkedList<String>();
		try {
		//	conn = getConnection(database);
			for (int i = 0; i<taxonRank.size(); i++) {
				String tRank = taxonRank.get(i);
				String whereClause = this.tablename + "." + tRank + " != '' ";
				result = "";
				ResultSet rs = null;
				Statement s = conn.createStatement();
				rs = s.executeQuery("SELECT " + this.tablename + "." + tRank
						+ " FROM " + this.database + "." + this.tablename
						+ " WHERE " + whereClause + ";");
				if (rs.next()) {
					ranklist.add(tRank);
				}
				rs.close();
				s.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return ranklist;
	}
	
	/**By Jing Liu
	 * Whether we have reached the the lowest rank of currrent branch.
	 * 
	 * @return
	 */
	public boolean reachedLowestRank(List<TaxonRank> topLevel, List<String> topName) {		
		String bottomRank = getNextRank(topLevel, topName).toString()
				.toLowerCase();
		return bottomRank.isEmpty();
	}
	
	/**
	 * Gets the the next rank of the current rank that is not empty in the current table.
	 * 
	 * @return
	 */
	public String getNextRank(List<TaxonRank> levels, List<String> names) {		
		//	Connection conn = null;
		String result = "";
		try {
		//	conn = getConnection(database);
			String whereClause = "";
			int j = 0;
			for(String name:names){
				if (!whereClause.equals("")) whereClause += "and ";
				whereClause +=  this.tablename + "." + levels.get(j).name().toLowerCase() +" = \'"+name+"\' ";
				j++;
			}
			
			String currRank = levels.get(j-1).toString().toLowerCase();
			int currIndex = taxonRank.indexOf(currRank) + 1;
			for (int i = currIndex; i < taxonRank.size(); i++) {				
				String tRank = taxonRank.get(i);
				String whereClause2 = this.tablename + "." + tRank
						+ " != '' ";
				result = "";
				ResultSet rs = null;
				Statement s = conn.createStatement();
				rs = s.executeQuery("SELECT " + this.tablename + "."
						+ tRank + " FROM " + this.database + "."
						+ this.tablename + " WHERE " + whereClause + "and " + whereClause2 +";");
				if (rs.next()) {
					result = tRank;
					break;
				}
				rs.close();
				s.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	/**
	 * Gets the the lowest rank that is not empty in the current table.
	 * 
	 * @return true if rankA is higher than rankB; false if rank A is equal or lower than rankB.
	 * 
	 */
	public boolean compareRanks(String rankA, String rankB) {
		int a=0,b=0;
		for (int i = 0; i < taxonRank.size(); i++) {
			if (taxonRank.get(i).equals(rankA)) a=i;
			if (taxonRank.get(i).equals(rankB)) b=i;
		}
		return a<b;
	}	
	
	/**
	 * Gets the the lowest rank that is not empty in the current table.
	 * 
	 * @return true if rankA is equals as rankB; false otherwise.
	 * 
	 */
	public boolean equalRanks(String rankA, String rankB) {
		int a=0,b=0;
		for (int i = 0; i < taxonRank.size(); i++) {
			if (taxonRank.get(i).equals(rankA)) a=i;
			if (taxonRank.get(i).equals(rankB)) b=i;
		}
		return a==b;
	}	
	
	/**
	 * Get filenames for descriptions within a taxonomical range.
	 * @param topLevel
	 * @param topName
	 * @param bottomLevel
	 * @return
	 */
	public List<String> getRankNamesForGivenRank(TaxonRank topLevel) {
		List<String> ranknames = new LinkedList<String>();
		String topRank = topLevel.toString().toLowerCase();
		String whereClause = topRank + " != \'\' ";
		ResultSet rs = null;
	//	Connection conn = null;
		try {
	//		conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT DISTINCT("+topRank+") FROM "+this.database+"."+this.tablename+" WHERE " +  whereClause);
			while(rs.next()) {
				ranknames.add(rs.getString(topRank));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		System.out.println(ranknames);
		return ranknames;
	}
	
	
	//added by Jing Liu
	public List<String> getAllRankNames() {
		ResultSet rs = null;
	//	Connection conn = null;
		List<String> res= new LinkedList<String>();
		try {
	//		conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM "+this.database+"."+this.tablename);
			while(rs.next()) {		
				String filename = rs.getString("filename");
				List<String> ranknames= getTaxonRankNameList(filename);
		//		List<String> ranklists=getTaxonRankList(filename);
				String rankname = ranknames.get((ranknames.size()-1));
		//		String ranklist = ranklists.get((ranklists.size()-1));		
				res.add(rankname.toLowerCase());
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		/*	try {
				if (conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return res;
	}
}
