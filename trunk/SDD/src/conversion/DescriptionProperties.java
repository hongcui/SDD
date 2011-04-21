/**
 * 
 */
package conversion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Alex
 *
 */
public class DescriptionProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7731461614513630462L;

	/**
	 * 
	 */
	public DescriptionProperties() {
		super();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("src/conversion/description.properties");
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
	public DescriptionProperties(Properties arg0) {
		super(arg0);
	}

}
