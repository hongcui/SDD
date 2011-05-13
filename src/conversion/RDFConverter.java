/**
 * 
 */
package conversion;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import taxonomy.ITaxon;
import taxonomy.TaxonHierarchy;
import tree.TreeNode;
import annotationSchema.jaxb.Structure;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;

/**
 * Conversion class for marshalling descriptions of taxon hierarchies into an RDF document.
 * @author alex
 *
 */
public class RDFConverter {

	private TaxonHierarchy hierarchy;
	private Model model;
	private RDFProperties rdfProps;
	
	public RDFConverter(TaxonHierarchy hierarchy) {
		this.hierarchy = hierarchy;
		model = ModelFactory.createDefaultModel();
		rdfProps = new RDFProperties();
	}
	
	public void taxonToRDF(ITaxon taxon, String filename) {
		Model taxonModel = ModelFactory.createDefaultModel();
		Iterator<TreeNode<Structure>> structures = taxon.getStructureTree().iterator();
		while(structures.hasNext()) {
			TreeNode<Structure> node = structures.next();
			Structure structure = node.getElement();
			Resource fromResource = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
					.concat(structure.getName()));
			Property property = new PropertyImpl(rdfProps.getProperty("prefix.property").concat("has_substructure"));
			for(TreeNode<Structure> child : node.getChildren()) {
				Resource toResource = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
						.concat(child.getElement().getName()));
				taxonModel.add(fromResource, property, toResource);
			}
		}
		writeRDF(taxonModel, filename);
	}

	private void writeRDF(Model m, String filename) {
		RDFWriter writer = m.getWriter("RDF/XML");
		writer.setProperty("allowBadURIs", "true");
		OutputStream out = null;
		try {
			out = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.write(m, out, "XML/RDF");
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
