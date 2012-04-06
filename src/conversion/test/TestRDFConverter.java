package conversion.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;
import conversion.DescriptionParser;
import conversion.RDFConverter;

public class TestRDFConverter {

	String[] species = {"potentilloides", "cana", "diversifolia", "ruthiae", 
			"argentea", "capitata", "compacta", "simplex"};
	
	DescriptionParser parser;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}

	@Test
	public void testTaxonToRDF() {
		for (String sp : species) {
			parser = new DescriptionParser(sp, TaxonRank.SPECIES);
			TaxonHierarchy h = new TaxonHierarchy(parser.parseTaxon());
			RDFConverter rdfConverter = new RDFConverter(h, parser.getFilename());
			rdfConverter.taxonToRDF(h.getHierarchy().getRoot().getElement(), "output/"+sp+"-rdf.xml");
		}
	}

}
