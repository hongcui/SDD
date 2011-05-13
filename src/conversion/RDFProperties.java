package conversion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RDFProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6610612460826774124L;

	public RDFProperties() {
		super();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("src/conversion/rdf.properties");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			this.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param arg0
	 */
	public RDFProperties(Properties arg0) {
		super(arg0);
	}
}
