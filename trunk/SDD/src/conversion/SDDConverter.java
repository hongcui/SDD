package conversion;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import sdd.ConceptMarkup;
import sdd.Dataset;
import sdd.Datasets;
import sdd.DetailText;
import sdd.DocumentGenerator;
import sdd.LabelText;
import sdd.MarkupText;
import sdd.NaturalLanguageDescription;
import sdd.NaturalLanguageDescriptionSet;
import sdd.NaturalLanguageMarkup;
import sdd.ObjectFactory;
import sdd.Representation;
import sdd.TechnicalMetadata;
import states.IState;
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
		dataset.setLang("en-us");
		addRepresentationToDataset(dataset, taxon);
		addNaturalLanguageDescriptions(dataset, taxon);
		root.getDataset().add(dataset);
	}

	/**
	 * Add a represenation element to a Dataset.
	 * @param dataset
	 * @param taxon
	 */
	private void addRepresentationToDataset(Dataset dataset, ITaxon taxon) {
		Representation rep = sddFactory.createRepresentation();
		LabelText labelText = sddFactory.createLabelText();
		String taxonRank = taxon.getTaxonRank().toString();
		String label = "The " + taxonRank + " " + taxon.getName();
		labelText.setValue(label);
		DetailText detailText = sddFactory.createDetailText();
		detailText.setValue("Generated from Hong's mark-up of FNA document");
		rep.getRepresentationGroup().add(labelText);
		rep.getRepresentationGroup().add(detailText);
		dataset.setRepresentation(rep);
	}

	/**
	 * Add a set of natural language descriptions to a dataset.
	 * @param dataset
	 * @param taxon
	 */
	private void addNaturalLanguageDescriptions(Dataset dataset, ITaxon taxon) {
		NaturalLanguageDescriptionSet descriptionSet = sddFactory.createNaturalLanguageDescriptionSet();		
		Map<String, NaturalLanguageDescription> statementIdToDescription = 
			new HashMap<String, NaturalLanguageDescription>();
		for(String id : taxon.getStatementTextMap().keySet()) {
			NaturalLanguageDescription description = sddFactory.createNaturalLanguageDescription();
			NaturalLanguageMarkup data = sddFactory.createNaturalLanguageMarkup();
			MarkupText markupText = sddFactory.createMarkupText();
			markupText.setValue(taxon.getStatementTextMap().get(id));
			data.getMarkupGroup().add(markupText);
			description.setNaturalLanguageData(data);
			addRepresentationToDescription(description, taxon);
			statementIdToDescription.put(id, description);
		}
		Iterator<TreeNode<Structure>> iter = taxon.getStructureTree().iterator();
		while(iter.hasNext()) {
			TreeNode<Structure> node = iter.next();
			Structure structure = node.getElement();
			NaturalLanguageDescription description = 
				statementIdToDescription.get(structure.getStatementId());
			addConceptToNaturalLanguageDescription(
					description.getNaturalLanguageData(), node);
		}
		descriptionSet.getNaturalLanguageDescription().addAll(statementIdToDescription.values());
		dataset.setNaturalLanguageDescriptions(descriptionSet);
	}

	/**
	 * Adds a ConceptMarkup element to a piece of NaturalLanguageMarkup/data.
	 * @param data
	 * @param node
	 */
	private void addConceptToNaturalLanguageDescription(
			NaturalLanguageMarkup data, TreeNode<Structure> node) {
		Structure structure = node.getElement();
		ConceptMarkup concept = sddFactory.createConceptMarkup();
		concept.setRef(structure.getName());
		for(String charName : structure.getCharStateMap().keySet()) {
			addCharacterToConcept(concept, 
					charName, structure.getCharStateMap().get(charName));
		}
		data.getMarkupGroup().add(concept);
	}

	private void addCharacterToConcept(ConceptMarkup concept, String charName,
			IState state) {
		
		
	}

	/**
	 * Add a representation element to a natural language description.
	 * @param description
	 * @param taxon
	 */
	private void addRepresentationToDescription(
			NaturalLanguageDescription description, ITaxon taxon) {
		Representation rep = sddFactory.createRepresentation();
		LabelText labelText = sddFactory.createLabelText();
		labelText.setValue("placeholder");
		DetailText detailText = sddFactory.createDetailText();
		detailText.setValue("placeholder");
		rep.getRepresentationGroup().add(labelText);
		rep.getRepresentationGroup().add(detailText);
		description.setRepresentation(rep);
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
