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
	
	private String database = "toboston";
	
	/**
	 * Ordered list of taxon ranks.
	 */
	private List<String> taxonRank;
	
	/**
	 * Create a new FilenameTaxonDao and add ranking list.
	 */
	public FilenameTaxonDao() {
		taxonRank = new ArrayList<String>();
		String[] list = new String[]{"family", "subfamily", "tribe", "subtribe", "genus", "subgenus", "section", "subsection", "species", "subspecies", "variety"};
		for(String s : list)
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
		Connection conn = null;
		try {
			conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT * FROM fnav19_filename2taxon WHERE filename = '" + filename +"';");
			while(rs.next()) {
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
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		Connection conn = null;
		try {
			conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM fnav19_filename2taxon WHERE "
					+ taxon + " = '" + name +"';");
			while(rs.next()) {
				result.add(rs.getString("filename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		Connection conn = null;
		try {
			conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM fnav19_filename2taxon WHERE family = '" + familyName 
					+"' and subfamily = '' and tribe = '' and subtribe = '' and genus = '' "
					+"and subgenus = '' and section = '' and subsection = '' "
					+"and species = '' and subspecies = '' and variety = '' ;");
			while(rs.next()) {
				result = rs.getString("filename");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		String whereClause = tRank + " = '" + name + "' ";
		for(int i = indexOfRank + 1; i < taxonRank.size(); i++)
			whereClause += "and " + taxonRank.get(i) + " = '' ";
		String result = "";
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM fnav19_filename2taxon WHERE " + whereClause + ";");
			while(rs.next()) {
				result = rs.getString("filename");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		Connection conn = null;
		try {
			conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT filename FROM fnav19_filename2taxon WHERE " +  whereClause);
			while(rs.next()) {
				filenames.add(rs.getString("filename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return filenames;
	}
}
