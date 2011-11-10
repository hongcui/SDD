package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SingularPluralDao extends BaseDao {

	private String database = this.getProperties().getProperty("singular-plural-database");
	private String tablename = this.getProperties().getProperty("singular-plural-table-name");
	
	/**
	 * Return the singular form from a plural form (or an empty result 
	 * if none exists).
	 * @param plural
	 * @return
	 */
	public List<String> getSingularForPlural(String plural) {
		List<String> result = new ArrayList<String>();
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = getConnection(database);
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT singular FROM " + this.tablename +
					" WHERE plural = '" + plural + "';");
			while(rs.next()) {
				result.add(rs.getString("singular"));
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
