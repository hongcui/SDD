package dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DatabaseProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1548553521773765476L;

	public DatabaseProperties() {
		super();
		FileInputStream fis = null;
		try {
    		//String dbp = System.getProperty("user.dir").replaceFirst("parsing-gui$", "")+"SDD\\src\\dao\\database.properties";   		
			//fis = new FileInputStream(dbp);
    		fis = new FileInputStream("src/dao/database.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
