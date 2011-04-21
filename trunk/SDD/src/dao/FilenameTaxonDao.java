package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for filename-taxon database.
 * @author Alex
 *
 */
public class FilenameTaxonDao {
	
	private static DatabaseProperties props = new DatabaseProperties();
	static final String URL = (String) props.get("url") + "toboston";
	static final String USER = (String) props.get("user");
	static final String PASSWORD = (String) props.get("password");
	static final String DRIVER = (String) props.get("driver");

	/**
	 * Gets a new connection to the "toboston" MySQL database.
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection con = null;
		try {
			Class.forName(DRIVER);
			con = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		return con;
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
			conn = getConnection();
			Statement s = getConnection().createStatement();
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
			conn = getConnection();
			Statement s = getConnection().createStatement();
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
			conn = getConnection();
			Statement s = getConnection().createStatement();
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
}
