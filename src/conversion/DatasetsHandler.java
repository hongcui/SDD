package conversion;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import sdd.Datasets;
import sdd.DocumentGenerator;
import sdd.TechnicalMetadata;
import util.XMLGregorianCalendarConverter;


public class DatasetsHandler implements Handler {

	private sdd.Datasets datasets;
	private sdd.ObjectFactory sddFactory;
	
	/**
	 * All this needs to do is generate the technical metadata.
	 */
	public DatasetsHandler() {
		this.sddFactory = new sdd.ObjectFactory();
		this.datasets = sddFactory.createDatasets();
	}

	/**
	 * Adds the metadata section to the SDD Datasets element.
	 */
	@Override
	public void handle() {
		addMetadata();		
	}
	
	/**
	 * Adds metadata to the root Datasets object.  Throws on the current time 
	 * as the "created" field and a Generator named "SDD conversion tool."
	 * @param datasets
	 */
	protected void addMetadata() {
		TechnicalMetadata metadata = sddFactory.createTechnicalMetadata();
		XMLGregorianCalendar xgcNow = XMLGregorianCalendarConverter.
				asXMLGregorianCalendar(new Date(System.currentTimeMillis()));
		metadata.setCreated(xgcNow);
		DocumentGenerator gen = sddFactory.createDocumentGenerator();
		gen.setName("SDD conversion tool.");
		gen.setVersion("0.2");
		metadata.setGenerator(gen);
		this.datasets.setTechnicalMetadata(metadata);
	}
	
	public sdd.Datasets getDatasets() {
		return this.datasets;
	}
}
