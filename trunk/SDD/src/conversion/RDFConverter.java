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
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ReifiedStatementImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;

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
	 * @param taxon Taxon for which RDF model should be generated.
	 * @param filename Name of file to output RDF/XML to.
	 */
	public void taxonToRDF(ITaxon taxon, String filename) {
		Model taxonModel = ModelFactory.createDefaultModel();
		addTaxonomyVocabularyStatements(taxonModel, taxon);
		Iterator<TreeNode<Structure>> structures = taxon.getStructureTree().iterator();
		addStructuresToModel(taxonModel, structures);
		addRelationsToModel(taxonModel, taxon.getRelations());
		writeRDF(taxonModel, filename);
	}
	
	/**
	 * Create an RDF model for a given Taxon object.
	 * @param taxon Taxon for which RDF model should be generated.
	 * @return The corresponding RDF model.
	 */
	public Model taxonToRDF(ITaxon taxon) {
		Model taxonModel = ModelFactory.createDefaultModel();
		addTaxonomyVocabularyStatements(taxonModel, taxon);
		Iterator<TreeNode<Structure>> structures = taxon.getStructureTree().iterator();
		addStructuresToModel(taxonModel, structures);
		addRelationsToModel(taxonModel, taxon.getRelations());
		return taxonModel;
	}

	/**
	 * Adds some taxonomy vocabulary triples to the taxon model.  Vocabulary defined by
	 * http://purl.org/NET/biol/ns#
	 * @param taxonModel
	 * @param taxon
	 */
	private void addTaxonomyVocabularyStatements(Model taxonModel,
			ITaxon taxon) {
		Structure wholeOrganism = taxon.getStructureTree().getRoot().getElement();
		Resource organismResource = taxonModel.getResource(rdfProps.getProperty("prefix.structure")
				.concat(wholeOrganism.getName()));
		Property hasTaxonomy = new PropertyImpl(rdfProps.getProperty("biol").concat("hasTaxonomy"));
		Resource taxonomy = taxonModel.getResource(rdfProps.getProperty("biol").concat("Taxonomy"));
		Property name = new PropertyImpl(rdfProps.getProperty("biol").concat("name"));
		Property classification = new PropertyImpl(rdfProps.getProperty("biol").concat(taxon.getTaxonRank().toString().toLowerCase()));
		taxonModel.add(organismResource, hasTaxonomy, taxonomy);
		taxonModel.add(taxonomy, name, taxon.getName());
		taxonModel.add(taxonomy, classification, taxon.getName());
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
				Statement stmtFrom = new StatementImpl(subject, predicateFrom, stateObjectFrom);
				taxonModel.add(stmtFrom);
				Literal stateObjectTo = taxonModel.createTypedLiteral(state.getMap().get("to value"));
				Statement stmtTo = new StatementImpl(subject, predicateTo, stateObjectTo);
				taxonModel.add(stmtTo);
				if(state.getModifier() != null) {
					addModifier(taxonModel, stmtFrom, state);
					addModifier(taxonModel, stmtTo, state);
				}
				if(state.getConstraint() != null) {
					addConstraint(taxonModel, stmtFrom, state);
					addConstraint(taxonModel, stmtTo, state);
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
				Statement stmt = new StatementImpl(subject, predicate,
						taxonModel.createTypedLiteral(state.getMap().get("value")));
				taxonModel.add(stmt);
				if(state.getModifier() != null)
					addModifier(taxonModel, stmt, state);
				if(state.getConstraint() != null)
					addConstraint(taxonModel, stmt, state);
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
	 * We're viewing constraints as reified statments.  If a constraint id referring to another structure is present,
	 * we reify the reified statment, as well
	 * @param taxonModel
	 * @param statement The statement to reify.
	 * @param state
	 */
	private void addConstraint(Model taxonModel, Statement statement, IState state) {
		Property constraintPredicate = new PropertyImpl(rdfProps.getProperty("prefix.constraint"));
		Literal constraint = taxonModel.createTypedLiteral(state.getConstraint());
		ReifiedStatement reifStmt = taxonModel.createReifiedStatement(
				rdfProps.getProperty("prefix.reified").
					concat(statement.getSubject().getLocalName()).
					concat("_"+statement.getPredicate().getLocalName()), statement);
		Statement stmt1 = new StatementImpl(reifStmt, constraintPredicate, constraint);
		Structure constraintId = state.getConstraintId();

		ReifiedStatement reifStmtAgain = taxonModel.createReifiedStatement(
				rdfProps.getProperty("prefix.reified").
				concat(reifStmt.getLocalName()).concat("_double_reified"), stmt1);
		Property constrainedBy = new PropertyImpl(rdfProps.getProperty("prefix.constrained_by"));
		Resource cId = taxonModel.createResource(rdfProps.getProperty("prefix.structure").concat(constraintId.getName()));
		taxonModel.add(reifStmtAgain, constrainedBy, cId);
		
		if(constraintId.getConstraintType() != null) {
			Property typeProp = new PropertyImpl(rdfProps.getProperty("prefix.constraint_type"));
			Literal constraintType = taxonModel.createTypedLiteral(constraintId.getConstraintType());
			taxonModel.add(cId, typeProp, constraintType);
		}
		taxonModel.remove(stmt1);
		taxonModel.remove(statement);
		
	}

	/**
	 * Attach a modifier to the RDF model.  This means making a reified statement, for which 'modifier' is the predicate
	 * and the value of modifier is the object.
	 * @param taxonModel
	 * @param statement The statement to reify.
	 * @param state
	 */
	private void addModifier(Model taxonModel, Statement statement,	IState state) {
		Property modifierPredicate = new PropertyImpl(rdfProps.getProperty("prefix.modifier"));
		Literal modifier = taxonModel.createTypedLiteral(state.getModifier());
		taxonModel.add(taxonModel.createReifiedStatement(
				rdfProps.getProperty("prefix.reified").
					concat(statement.getSubject().getLocalName()).
					concat("_"+statement.getPredicate().getLocalName()), statement),
				modifierPredicate,
				modifier);	
		taxonModel.remove(statement);
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
