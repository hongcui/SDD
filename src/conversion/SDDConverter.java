package conversion;

import com.hp.hpl.jena.rdf.model.Model;
import sdd.*;

/**
 * Class for converting an RDF model into SDD.
 * @author Alex
 *
 */
public class SDDConverter {
	
	private Model model;
	
	/**
	 * Create a new converter object from a single taxon model.
	 * @param taxonModel
	 */
	public SDDConverter(Model taxonModel) {
		this.model = taxonModel;
	}
	
	/**
	 * Transform the model for a single taxon into an SDD document.
	 * @param filename Name of the xml document.
	 */
	public void taxonToSDD(String filename) {
		
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
