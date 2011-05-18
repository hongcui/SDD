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
import java.util.Map;

import states.IState;
import states.RangeState;
import taxonomy.ITaxon;
import taxonomy.TaxonHierarchy;
import tree.TreeNode;
import annotationSchema.jaxb.Relation;
import annotationSchema.jaxb.Structure;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
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
		addRelationsToModel(taxonModel, taxon.getRelations());
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
			Resource fromResource = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
					.concat(structure.getName()));
			Property property = new PropertyImpl(rdfProps.getProperty("prefix.property").concat("has_substructure"));
			for(TreeNode<Structure> child : node.getChildren()) {
				Resource toResource = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
						.concat(child.getElement().getName()));
				taxonModel.add(fromResource, property, toResource);
			}
			addCharactersToModel(taxonModel, fromResource, structure.getCharStateMap());
		}
	}

	/**
	 * Add all of the characters of a structure to an RDF model.
	 * @param taxonModel Model to add relations to.
	 * @param subject The structure to add characters of
	 * @param map Map from character name to state
	 */
	@SuppressWarnings({ "rawtypes" })
	private void addCharactersToModel(Model taxonModel, Resource subject,
			Map<String, IState> map) {
		for(String s : map.keySet()) {
			IState state = map.get(s);
			if(state instanceof RangeState) {	//break this into two char names
				Property predicateFrom = 
					new PropertyImpl(rdfProps.getProperty("prefix.character")
							.concat(s.concat("_from")));
				Property predicateTo = 
					new PropertyImpl(rdfProps.getProperty("prefix.character")
							.concat(s.concat("_to")));
				Literal stateObjectFrom = taxonModel.createTypedLiteral(state.getMap().get("from value"));
				taxonModel.add(subject, predicateFrom, stateObjectFrom);
				Literal stateObjectTo = taxonModel.createTypedLiteral(state.getMap().get("to value"));
				taxonModel.add(subject, predicateTo,stateObjectTo);
				if(state.getModifier() != null) {
					addModifier(taxonModel, subject, predicateFrom, state);
					addModifier(taxonModel, subject, predicateTo, state);
				}
				if(state.getConstraint() != null) {
					addConstraint(taxonModel, subject, predicateFrom, state);
					addConstraint(taxonModel, subject, predicateTo, state);
				}
				if(state.getFromUnit() != null) {
					Property unitPredicate =
						new PropertyImpl(rdfProps.getProperty("prefix.property")
								.concat(s + "_from_unit"));
					taxonModel.add(subject, unitPredicate,
							taxonModel.createTypedLiteral(state.getFromUnit()));
				}
				if(state.getToUnit() != null) {
					Property unitPredicate =
						new PropertyImpl(rdfProps.getProperty("prefix.property")
								.concat(s + "_to_unit"));
					taxonModel.add(subject, unitPredicate,
							taxonModel.createTypedLiteral(state.getToUnit()));
				}
			}
			else {
				Property predicate = 
					new PropertyImpl(rdfProps.getProperty("prefix.character").concat(s));
				taxonModel.add(subject, predicate,
						taxonModel.createTypedLiteral(state.getMap().get("value")));
				if(state.getModifier() != null)
					addModifier(taxonModel, subject, predicate, state);
				if(state.getConstraint() != null)
					addConstraint(taxonModel, subject, predicate, state);
				if(state.getFromUnit() != null) {
					Property unitPredicate =
						new PropertyImpl(rdfProps.getProperty("prefix.property")
								.concat(s + "_unit"));
					taxonModel.add(subject, unitPredicate,
							taxonModel.createTypedLiteral(state.getFromUnit()));
				}
			}
			
		}
	}

	/**
	 * Attach a constraint predicate (referring to a particular character property)
	 * to a structure/resource.
	 * @param taxonModel
	 * @param subject
	 * @param predicate
	 * @param state
	 */
	private void addConstraint(Model taxonModel, Resource subject, Property predicate,
			IState state) {
		String s = predicate.getURI();
		Property constraintPredicate = new PropertyImpl(s.concat("/constraint"));
		Literal constraint = taxonModel.createTypedLiteral(state.getConstraint());
		taxonModel.add(subject, constraintPredicate, constraint);
	}

	/**
	 * Attach a modifier property (referring to a particular character property)
	 * to a structure/resource.
	 * @param taxonModel
	 * @param subject
	 * @param predicate
	 * @param state
	 */
	private void addModifier(Model taxonModel, Resource subject, Property predicate,
			IState state) {
		String s = predicate.getURI();
		Property modifierPredicate = new PropertyImpl(s.concat("/modifier"));
		Literal modifier = taxonModel.createTypedLiteral(state.getModifier());
		taxonModel.add(subject, modifierPredicate, modifier);		
	}

	/**
	 * Adds all of the relations to the model.
	 * @param taxonModel
	 * @param relations
	 */
	private void addRelationsToModel(Model taxonModel, List<Relation> relations) {
		for(Relation r : relations) {
			Resource from = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
					.concat(((Structure)r.getFrom().get(0)).getName()));
			Resource to = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
					.concat(((Structure)r.getTo().get(0)).getName()));
			Property predicate = new PropertyImpl(rdfProps.getProperty("prefix.property")
					.concat(r.getName()));
			taxonModel.add(from, predicate, to);
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
