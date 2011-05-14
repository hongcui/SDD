/**
 * 
 */
package conversion;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import taxonomy.ITaxon;
import taxonomy.TaxonHierarchy;
import tree.TreeNode;
import annotationSchema.jaxb.Character;
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
	private RDFProperties rdfProps;
	
	public RDFConverter(TaxonHierarchy hierarchy) {
		this.hierarchy = hierarchy;
		rdfProps = new RDFProperties();
	}
	
	/**
	 * Convert a Taxon object into an RDF model.
	 * @param taxon
	 * @param filename
	 */
	public void taxonToRDF(ITaxon taxon, String filename) {
		Model taxonModel = ModelFactory.createDefaultModel();
		Iterator<TreeNode<Structure>> structures = taxon.getStructureTree().iterator();
		addStructuresToModel(taxonModel, structures);
		writeRDF(taxonModel, filename);
	}

	/**
	 * Add a set of structures to an RDF model.
	 * @param taxonModel
	 * @param structures
	 */
	private void addStructuresToModel(Model taxonModel,
			Iterator<TreeNode<Structure>> structures) {
		while(structures.hasNext()) {
			TreeNode<Structure> node = structures.next();
			Structure structure = node.getElement();
			List<annotationSchema.jaxb.Character> characters = structure.getCharacter();
			addCharactersToModel(taxonModel, characters);
			Resource fromResource = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
					.concat(structure.getName()));
			Property property = new PropertyImpl(rdfProps.getProperty("prefix.property").concat("has_substructure"));
			for(TreeNode<Structure> child : node.getChildren()) {
				Resource toResource = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
						.concat(child.getElement().getName()));
				taxonModel.add(fromResource, property, toResource);
			}
		}
	}

	/**
	 * Add all of the characters of a structure to an RDF model.
	 * @param taxonModel
	 * @param characters
	 */
	private void addCharactersToModel(Model taxonModel,
			List<Character> characters) {
		for(annotationSchema.jaxb.Character c : characters) {
			
		}
		
	}

	/**
	 * Write an RDF model to file with given filename.
	 * @param m
	 * @param filename
	 */
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
