package conversion;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import sdd.Datasets;
import taxonomy.TaxonHierarchy;

/**
 * This is the entry class for converting SDD documents.
 * @author alex
 *
 */
public class DatasetLoader {
	
	private JAXBContext sddContext;
	private TaxonHierarchy hierarchy;
	private DatasetsHandler datasetsHandler;
	private DatasetHandler datasetHandler;
	private TaxonNameHandler taxonNameHandler;
	private TaxonHierarchyObserver taxonHierarchyObserver;
	private DescriptiveConceptHandler dcHandler;
	private ModifierHandler modifierHandler;
	private CharacterSetHandler characterSetHandler;
	
	/**
	 * Constructs new entry-class object.
	 * This creates new DatasetsHandler, DatasetHandler, and all other
	 * Handler/Observer objects.  Also deals with adding observers to
	 * publishers as appropriate.
	 * @param hierarchy The Hierarchy to convert to SDD.
	 */
	public DatasetLoader(TaxonHierarchy hierarchy) {
		this.hierarchy = hierarchy;
		this.datasetsHandler = new DatasetsHandler();
		this.datasetHandler = new DatasetHandler(this.hierarchy);
		
		this.taxonNameHandler = new TaxonNameHandler(datasetHandler.getDataset());
		//Taxon name handler subscribes to the dataset handler
		datasetHandler.addObserver(taxonNameHandler);
		
		this.taxonHierarchyObserver = 
			new TaxonHierarchyObserver(datasetHandler.getDataset());
		//taxonHierarchy observer subscribes to the taxonNameHandlers
		taxonNameHandler.addObserver(taxonHierarchyObserver);
		
		this.dcHandler = new DescriptiveConceptHandler();
		//dc handler subscribes to the dataset handler
		datasetHandler.addObserver(dcHandler);
		
		this.modifierHandler = new ModifierHandler();
		//the dc and modifier handler pub/sub bi-directionally
		dcHandler.addObserver(modifierHandler);
		modifierHandler.addObserver(dcHandler);
		
		this.characterSetHandler = new CharacterSetHandler();
		//character set handler subscribes to the dataset handler
		datasetHandler.addObserver(characterSetHandler);
		
		try {
			this.sddContext = JAXBContext.newInstance(sdd.ObjectFactory.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts a TaxonHierarchy into an associated SDD XML file.  
	 * A Datasets SDD object is created and set as the root of the XML file. 
	 * The Dataset from the DatasetHandler is added to the Dataset list of the root.
	 * @param hierarchy
	 * @param filename
	 */
	public void taxonHierarchyToSDD(String filename) {
		this.datasetsHandler.handle();
		this.datasetHandler.handle();
		this.dcHandler.handle();
		this.characterSetHandler.handle();
		Datasets root = this.datasetsHandler.getDatasets();
		root.getDataset().add(this.datasetHandler.getDataset());
		
		try {
			Marshaller marshaller = sddContext.createMarshaller();
			marshaller.marshal(root, new File(filename));
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
