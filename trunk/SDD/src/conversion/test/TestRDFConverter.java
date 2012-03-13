package conversion.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;
import conversion.DescriptionParser;
import conversion.RDFConverter;

public class TestRDFConverter {

	DescriptionParser parser;
	@Before
	public void setUp() throws Exception {
		parser = new DescriptionParser("argentea", TaxonRank.SPECIES);
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}

	@Test
	public void testTaxonToRDF() {
		TaxonHierarchy h = new TaxonHierarchy(parser.parseTaxon());
		RDFConverter rdfConverter = new RDFConverter(h);
		rdfConverter.taxonToRDF(h.getHierarchy().getRoot().getElement(), "output/argentea-rdf.xml");
	}

}
