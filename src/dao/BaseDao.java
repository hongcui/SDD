package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDao {

	private static DatabaseProperties props = new DatabaseProperties();
	private static final String URL = (String) props.get("url");
	private static final String USER = (String) props.get("user");
	private static final String PASSWORD = (String) props.get("password");
	private static final String DRIVER = (String) props.get("driver");
	//private static final String database = (String) props.get("filename-database");
	
	/**
	 * Gets a new connection to a MySQL database.
	 * @param database Database to connect to.
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String database) throws SQLException {
		Connection con = null;
		try {
			Class.forName(DRIVER);
			while (con==null) 
				con = DriverManager.getConnection(URL + database, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return con;
	}
	
	/**
	 * 
	 * @return The DatabaseProperties object.
	 */
	public DatabaseProperties getProperties() {
		return this.props;
	}
}
