package conversion;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import sdd.CharacterLocalStateDef;
import sdd.Datasets;
import sdd.ModifierDef;
import taxonomy.TaxonHierarchy;

/**
 * 
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
	
	/**
	 * 
	 */
	public DatasetLoader(TaxonHierarchy hierarchy) {
		this.hierarchy = hierarchy;
		this.datasetsHandler = new DatasetsHandler();
		this.datasetHandler = new DatasetHandler(this.hierarchy);
		
		this.taxonNameHandler = new TaxonNameHandler(datasetHandler);
		//Taxon name handler subscribes to the dataset handler
		datasetHandler.addObserver(taxonNameHandler);
		
		this.taxonHierarchyObserver = new TaxonHierarchyObserver(taxonNameHandler);
		//taxonHierarchy observer subscribes to the taxonNameHandlers
		taxonNameHandler.addObserver(taxonHierarchyObserver);
		
		try {
			this.sddContext = JAXBContext.newInstance(sdd.ObjectFactory.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts a TaxonHierarchy into an associated SDD XML file.  A Datasets SDD object is created
	 * and set as the root of the XML file. The Dataset from the DatasetHandler
	 * is added to the Dataset list of the root.
	 * @param hierarchy
	 * @param filename
	 */
	public void taxonHierarchyToSDD(String filename) {
		this.datasetsHandler.handle();
		this.datasetHandler.handle();
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
