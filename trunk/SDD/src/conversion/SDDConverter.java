package conversion;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import sdd.Dataset;
import sdd.Datasets;
import sdd.DocumentGenerator;
import sdd.FreeFormTextData;
import sdd.LongStringL;
import sdd.NaturalLanguageDescription;
import sdd.NaturalLanguageDescriptionSet;
import sdd.NaturalLanguageMarkup;
import sdd.ObjectFactory;
import sdd.TechnicalMetadata;
import taxonomy.ITaxon;
import tree.TreeNode;
import util.XMLGregorianCalendarConverter;
import annotationSchema.jaxb.Structure;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Class for converting an RDF model into SDD.
 * @author Alex
 *
 */
public class SDDConverter {
	
	private Model model;
	private JAXBContext sddContext;
	private ObjectFactory sddFactory;
	
	/**
	 * Create a new converter object from a single taxon model.
	 * @param taxonModel
	 */
	public SDDConverter(Model taxonModel) {
		this.model = taxonModel;
		try {
			this.sddContext = JAXBContext.newInstance(sdd.ObjectFactory.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		this.sddFactory = new ObjectFactory();
	}
	
	/**
	 * Transform the model for a single taxon into an SDD document.
	 * @param filename Name of the xml document.
	 */
	public void taxonToSDD(ITaxon taxon, String filename) {
		try {
			Marshaller marshaller = sddContext.createMarshaller();
			Datasets root = sddFactory.createDatasets();
			addMetadata(root);
			addDataset(root, taxon);
			marshaller.marshal(root, new File(filename));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a Dataset to the root Datasets object, using data from taxon object.
	 * @param root The root object of the SDD document.
	 * @param taxon Object to retrieve data from.
	 */
	private void addDataset(Datasets root, ITaxon taxon) {
		Dataset dataset = sddFactory.createDataset();
		addNaturalLanguageDescriptions(dataset, taxon);
		root.getDataset().add(dataset);
	}

	/**
	 * Add a set of natural language descriptions to a dataset.
	 * @param dataset
	 * @param taxon
	 */
	private void addNaturalLanguageDescriptions(Dataset dataset, ITaxon taxon) {
		NaturalLanguageDescriptionSet descriptionSet = sddFactory.createNaturalLanguageDescriptionSet();
		NaturalLanguageDescription description = sddFactory.createNaturalLanguageDescription();
		//TODO: give the description some representation with a label and detail
		
		Iterator<TreeNode<Structure>> iter = taxon.getStructureTree().iterator();
		while(iter.hasNext()) {
			NaturalLanguageMarkup data = sddFactory.createNaturalLanguageMarkup();
			TreeNode<Structure> node = iter.next();
			Structure structure = node.getElement();
			FreeFormTextData textData = sddFactory.createFreeFormTextData();
			LongStringL string = sddFactory.createLongStringL();
			string.setValue(structure.getText());
			textData.getContent().add(string);
			data.getMarkupGroup().add(textData);
			description.setNaturalLanguageData(data);
		}
		descriptionSet.getNaturalLanguageDescription().add(description);
		dataset.setNaturalLanguageDescriptions(descriptionSet);
	}

	/**
	 * Adds metadata to the root Datasets object.  Throws on the current time as the "created" field
	 * and a Generator named "RDF to SDD conversion tool."
	 * @param root
	 */
	private void addMetadata(Datasets root) {
		TechnicalMetadata metadata = sddFactory.createTechnicalMetadata();
		XMLGregorianCalendar xgcNow = XMLGregorianCalendarConverter.asXMLGregorianCalendar(new Date(System.currentTimeMillis()));
		metadata.setCreated(xgcNow);
		DocumentGenerator gen = sddFactory.createDocumentGenerator();
		gen.setName("RDF to SDD conversion tool.");
		gen.setVersion("0.1");
		metadata.setGenerator(gen);
		root.setTechnicalMetadata(metadata);
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

}
