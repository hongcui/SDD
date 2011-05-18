package conversion.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;
import conversion.DescriptionParser;
import conversion.SDDConverter;

public class TestSDDConverter {

	DescriptionParser parser;
	@Before
	public void setUp() throws Exception {
		parser = new DescriptionParser("cirsium", TaxonRank.GENUS);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTaxonToSDD() {
		TaxonHierarchy h = new TaxonHierarchy(parser.parseTaxon());
		SDDConverter toSDD = new SDDConverter(null);
		toSDD.taxonToSDD(h.getHierarchy().getRoot().getElement(), "output/cirsium-sdd.xml");
	}

}
