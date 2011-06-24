package store.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import store.TDBStore;
import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.tdb.TDB;

import conversion.DescriptionParser;
import conversion.DescriptionProperties;
import conversion.RDFConverter;

public class TestTDBStore {

	DescriptionParser parser;
	private DescriptionProperties props = new DescriptionProperties();
	@Before
	public void setUp() throws Exception {
		parser = new DescriptionParser("cirsium", TaxonRank.GENUS);
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}

	@Test
	public void testTDBStore() {
		TaxonHierarchy h = new TaxonHierarchy(parser.parseTaxon());
		RDFConverter rdfConverter = new RDFConverter(h);
		Model cirsiumModel = rdfConverter.taxonToRDF(h.getHierarchy().getRoot().getElement());
		TDBStore store = new TDBStore(props.getProperty("tdb.store.path"), cirsiumModel);
		String queryStr = "PREFIX biosem: <http://cs.umb.edu/biosemantics/> SELECT ?x ?y ?z WHERE { ?x ?y ?z . }";
		QueryExecution qExec = QueryExecutionFactory.create(queryStr, store.getDataset().getDefaultModel());
		qExec.getContext().set(TDB.symUnionDefaultGraph, true);
		ResultSet rs = qExec.execSelect();
		PrefixMapping prefixMap = PrefixMapping.Factory.create();
		prefixMap.setNsPrefixes(store.getDataset().getDefaultModel().getNsPrefixMap());
		prefixMap.setNsPrefix("biosem", "http://cs.umb.edu/biosemantics/");
		ResultSetFormatter.out(System.out, rs, prefixMap);
	}

}
