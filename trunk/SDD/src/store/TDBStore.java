package store;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * A TDBStore object creates (or connects to) a TDB dataset at a given location.
 * @author alex
 *
 */
public class TDBStore {
	
	private String storeLocation;
	private Dataset dataset;

	/**
	 * Create or connect to a dataset at a given location.
	 * @param location
	 */
	public TDBStore(String location) {
		this.storeLocation = location;
		this.dataset = TDBFactory.createDataset(location);
	}
	
	/**
	 * Create or connect to a TDB dataset at a given location, and add a model to the default model 
	 * of that dataset.
	 * @param location
	 * @param model
	 */
	public TDBStore(String location, Model model) {
		this.storeLocation = location;
		this.dataset = TDBFactory.createDataset(location);
		this.dataset.getDefaultModel().add(model);
	}
	
	/**
	 * Adds a given model to the default model of this dataset.
	 * @param model
	 */
	public void addModel(Model model) {
		this.dataset.getDefaultModel().add(model);
	}

	/**
	 * @return the storeLocation
	 */
	public String getStoreLocation() {
		return storeLocation;
	}

	/**
	 * @param storeLocation the storeLocation to set
	 */
	public void setStoreLocation(String storeLocation) {
		this.storeLocation = storeLocation;
	}

	/**
	 * @return the dataset
	 */
	public Dataset getDataset() {
		return dataset;
	}
}
