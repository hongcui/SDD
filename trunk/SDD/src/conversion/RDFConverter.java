/**
 * 
 */
package conversion;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import states.EmptyState;
import states.IState;
import states.RangeState;
import states.SingletonState;
import taxonomy.ITaxon;
import taxonomy.TaxonHierarchy;
import tree.TreeNode;
import util.ConversionUtil;
import annotationSchema.jaxb.Relation;
import annotationSchema.jaxb.Structure;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;

/**
 * Conversion class for marshalling descriptions of taxon hierarchies into an RDF document.
 * @author alex
 *
 */
public class RDFConverter {

	private TaxonHierarchy hierarchy;
	private Model taxonModel;
	private OntModel biolModel;
	private OntModel descModel;
	private String xmlBase;
	private final RDFProperties rdfProps = new RDFProperties();
	private final String propNS = rdfProps.getProperty("prefix.property");
	private final String biolNS = rdfProps.getProperty("biol");
	private final String structureNS = rdfProps.getProperty("prefix.structure");
	private final String characterNS = rdfProps.getProperty("prefix.character");
	private final String modifierNS = rdfProps.getProperty("prefix.modifier");
	private final String constraintNS = rdfProps.getProperty("prefix.constraint");
		
	private final Property hasSubstructure = new PropertyImpl(propNS+"#hasSubstructure");
	private final Property hasCharacter = new PropertyImpl(propNS+"#hasCharacter");
	private final Property hasState = new PropertyImpl(propNS+"#hasState");
	private final Property stateValue = new PropertyImpl(propNS+"#stateValue");
	private final Property stateValueFrom = new PropertyImpl(propNS+"#stateValueFrom");
	private final Property stateValueTo = new PropertyImpl(propNS+"#stateValueTo");
	
	
	public RDFConverter(TaxonHierarchy hierarchy, String filename) {
		this.hierarchy = hierarchy;
		taxonModel = ModelFactory.createDefaultModel();
		biolModel = ModelFactory.createOntologyModel();
		descModel = ModelFactory.createOntologyModel();
		biolModel.read(biolNS);
		xmlBase = "http://cs.umb.edu/biosemantics/"+filename;
		taxonModel.setNsPrefix("", xmlBase);
//		taxonModel.setNsPrefix("biosem", "http://cs.umb.edu/biosemantics/");
//		taxonModel.setNsPrefix("biol", biolNS);
//		taxonModel.setNsPrefix("bsprop", propNS);
	}
	
	/**
	 * Convert a Taxon object into an RDF model.
	 * @param taxon Taxon for which RDF model should be generated.
	 * @param filename Name of file to output RDF/XML to.
	 */
	public void taxonToRDF(ITaxon taxon, String filename) {
		Iterator<TreeNode<Structure>> structures = taxon.getStructureTree().iterator();
		addStructuresToModel(taxon, structures);
		addRelationsToModel(taxon.getRelations());
		writeRDF(taxonModel, filename);
	}
	
	/**
	 * Create an RDF model for a given Taxon object.
	 * @param taxon Taxon for which RDF model should be generated.
	 * @return The corresponding RDF model.
	 */
	public Model taxonToRDF(ITaxon taxon) {
		Iterator<TreeNode<Structure>> structures = taxon.getStructureTree().iterator();
		addStructuresToModel(taxon, structures);
		addRelationsToModel(taxon.getRelations());
		return taxonModel;
	}

	/**
	 * Add a set of structures to an RDF model.
	 * @param taxonModel
	 * @param structures
	 */
	private void addStructuresToModel(ITaxon taxon,	Iterator<TreeNode<Structure>> structures) {
		while(structures.hasNext()) {
			TreeNode<Structure> node = structures.next();
			Structure structure = node.getElement();
			Resource fromType = descModel.createResource(structureNS+"#"+structure.getName());
			Resource fromResource = taxonModel.createResource("#"+structure.getName(), fromType);
			if (structure.getName().equals("whole_organism")) {
				//the whole organism gets a taxonomy attached.
				Property hasTaxonomy = new PropertyImpl(biolNS+"#hasTaxonomy");
				//the taxonomy is of type purl.org/NET/biol/ns#Taxonomy
				Resource taxonomy = taxonModel.createResource("#taxonomy", biolModel.getResource(biolNS+"#Taxonomy")); 
				Property name = new PropertyImpl(biolNS+"#name");
				Property classification = new PropertyImpl(biolNS+"#"+taxon.getTaxonRank().toString().toLowerCase());
				taxonModel.add(fromResource, hasTaxonomy, taxonomy);
				taxonModel.add(taxonomy, name, taxon.getName());
				taxonModel.add(taxonomy, classification, taxon.getName());
			}
			Property property = new PropertyImpl(propNS+"#hasSubstructure");
			for(TreeNode<Structure> child : node.getChildren()) {
				Resource toType = descModel.createResource(structureNS+"#"+child.getElement().getName());
				Resource toResource = taxonModel.createResource("#"+child.getElement().getName(), toType);
				taxonModel.add(fromResource, property, toResource);
			}
			addCharactersToModel(taxonModel, fromResource, node, structure.getCharStateMap());
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
			TreeNode<Structure> node, Map<String, List<IState>> map) {
		for(String s : map.keySet()) {
			String fullCharName = ConversionUtil.resolveFullCharacterName(s, node);
			//first, define an RDF resource representing the character
			Resource characterType = descModel.createResource(characterNS+"#"+fullCharName);
			Resource characterDatum = taxonModel.createResource("#"+fullCharName, characterType);
			//now make a statement that the subject (structure) has this character
			subject.addProperty(hasCharacter, characterDatum);
			
			for(IState state : map.get(s)) {
				//we'll also need a blank node for the state datum
				Resource stateDatum = taxonModel.createResource();
				//and also a statement that this character has a state
				characterDatum.addProperty(hasState, stateDatum);
				
				if(state instanceof RangeState || state instanceof EmptyState) {	
					//place the state values as objects in a statement with the state datum
					Literal stateObjectFrom = taxonModel.createTypedLiteral(state.getMap().get("from value"));
					Statement stmtFrom = new StatementImpl(stateDatum, stateValueFrom, stateObjectFrom);
					taxonModel.add(stmtFrom);
					Literal stateObjectTo = taxonModel.createTypedLiteral(state.getMap().get("to value"));
					Statement stmtTo = new StatementImpl(stateDatum, stateValueTo, stateObjectTo);
					taxonModel.add(stmtTo);
					if(state.getModifier() != null) {
						addModifier(taxonModel, state, characterDatum, hasState, stateDatum);
						addModifier(taxonModel, state, characterDatum, hasState, stateDatum);
					}
					if(state.getConstraint() != null) {
						addConstraint(taxonModel, state, characterDatum, hasState, stateDatum);
						addConstraint(taxonModel, state, characterDatum, hasState, stateDatum);
					}
					
					//circa March 2012, all numeric states are normalized to same units
//					if(state.getFromUnit() != null) {
//						Property unitPredicate =
//							new PropertyImpl(rdfProps.getProperty("prefix.property")
//									.concat(s + "_from_unit"));
//						taxonModel.add(subject, unitPredicate,
//								taxonModel.createTypedLiteral(state.getFromUnit()));
//					}
//					if(state.getToUnit() != null) {
//						Property unitPredicate =
//							new PropertyImpl(rdfProps.getProperty("prefix.property")
//									.concat(s + "_to_unit"));
//						taxonModel.add(subject, unitPredicate,
//								taxonModel.createTypedLiteral(state.getToUnit()));
//					}
				}
				else {
					//place the state values as objects in a statement with the state datum
					Literal stateObject = taxonModel.createTypedLiteral(state.getMap().get("value"));
					Statement stmt = new StatementImpl(stateDatum, stateValue, stateObject);
					taxonModel.add(stmt);
					if(state.getModifier() != null)
						addModifier(taxonModel, state, characterDatum, hasState, stateDatum);
					if(state.getConstraint() != null)
						addConstraint(taxonModel, state, characterDatum, hasState, stateDatum);
					
					//circa March 2012, all numeric states are normalized to same units
//					if(state.getFromUnit() != null) {
//						Property unitPredicate =
//							new PropertyImpl(rdfProps.getProperty("prefix.property")
//									.concat(s + "_unit"));
//						taxonModel.add(subject, unitPredicate,
//								taxonModel.createTypedLiteral(state.getFromUnit()));
//					}
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
	private void addConstraint(Model taxonModel, IState state,
			Resource characterDatum, Property property, Resource stateDatum) {
		Property constraintPredicate = new PropertyImpl(constraintNS);
		Literal constraint = taxonModel.createTypedLiteral(state.getConstraint());
		Statement statement = new StatementImpl(characterDatum, property, stateDatum);
		ReifiedStatement reifStmt = taxonModel.createReifiedStatement(statement);
		Statement constraintStmt = taxonModel.createStatement(reifStmt, constraintPredicate, constraint);
		taxonModel.add(constraintStmt);
		
//		Statement stmt1 = new StatementImpl(reifStmt, constraintPredicate, constraint);
//		Structure constraintId = state.getConstraintId();
//
//		ReifiedStatement reifStmtAgain = taxonModel.createReifiedStatement(
//				rdfProps.getProperty("prefix.reified").
//				concat(reifStmt.getLocalName()).concat("_double_reified"), stmt1);
//		Property constrainedBy = new PropertyImpl(rdfProps.getProperty("prefix.constrained_by"));
//		Resource cId = taxonModel.createResource(rdfProps.getProperty("prefix.structure").concat(constraintId.getName()));
//		taxonModel.add(reifStmtAgain, constrainedBy, cId);
//		
//		if(constraintId.getConstraintType() != null) {
//			Property typeProp = new PropertyImpl(rdfProps.getProperty("prefix.constraint_type"));
//			Literal constraintType = taxonModel.createTypedLiteral(constraintId.getConstraintType());
//			taxonModel.add(cId, typeProp, constraintType);
//		}
//		taxonModel.remove(stmt1);
//		taxonModel.remove(statement);
//		
	}

	/**
	 * Attach a modifier to the RDF model.  This means making a reified statement, 
	 * for which 'modifier' is the predicate and the value of modifier is the object.
	 * @param taxonModel
	 * @param statement The statement to reify.
	 * @param state
	 */
	private void addModifier(Model taxonModel, IState state,
			Resource characterDatum, Property property, Resource stateDatum) {
		String key = "";
		if(state instanceof SingletonState)
			key = SingletonState.KEY;
		else
			key = RangeState.KEY_FROM;
		Property modifierPredicate = new PropertyImpl(modifierNS);
		Literal modifier = taxonModel.createTypedLiteral(state.getModifier());
//		String statementString = rdfProps.getProperty("prefix.reified").
//			concat(characterDatum.getLocalName()).
//			concat("_"+property.getLocalName()).
//			concat("_"+stateDatum.getLocalName());
		Statement statement = new StatementImpl(characterDatum, property, stateDatum); 
		Statement modifierStatement = new StatementImpl(
				taxonModel.createReifiedStatement(statement),
				modifierPredicate,
				modifier);
		taxonModel.add(modifierStatement);
//		taxonModel.remove(statement);
	}

	/**
	 * Adds all of the relations to the model.
	 * @param taxonModel
	 * @param relations
	 */
	private void addRelationsToModel(List<Relation> relations) {
		for(Relation r : relations) {
			Resource from = taxonModel.getResource("#"+((Structure)r.getFrom().get(0)).getName());
			Resource to = taxonModel.getResource("#"+((Structure)r.getTo().get(0)).getName());
			Property predicate = new PropertyImpl(propNS+"#"+r.getName());
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
		writer.setProperty("xmlbase", xmlBase);
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
