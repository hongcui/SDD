package conversion;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import dao.FilenameTaxonDao;

import annotationSchema.jaxb.Description;
import annotationSchema.jaxb.Statement;

public class DescriptionParser {

	private Properties props;
	private JAXBContext annotationContext;
	private String familyName;
	private FilenameTaxonDao filenameTaxonDao = new FilenameTaxonDao();
	
	/**
	 * Creates a new DescriptionParser object (loads a jaxb context
	 */
	public DescriptionParser(String familyName) {
		this.setFamilyName(familyName);
		this.props = new DescriptionProperties();
		try {
			annotationContext = JAXBContext.newInstance(props.getProperty("jaxb.annotation.directory"));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the filename of the family.
	 */
	public void parseFamily() {
		try {
			Unmarshaller unmarshaller = annotationContext.createUnmarshaller();
			String filename = filenameTaxonDao.getFilenameOfFamilyDescription(familyName);
			String path = props.getProperty("input.path") + filename;
			Description description = (Description) unmarshaller.unmarshal(new File(path));
			List<Statement> statementList = description.getStatement();
		} catch(JAXBException e) {
			
		}
	}

	/**
	 * @param familyName the familyName to set
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	/**
	 * @return the familyName
	 */
	public String getFamilyName() {
		return familyName;
	}
}
