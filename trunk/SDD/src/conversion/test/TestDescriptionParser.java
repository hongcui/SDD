/**
 * 
 */
package conversion.test;

import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taxonomy.ITaxon;
import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;
import conversion.DescriptionParser;

/**
 * @author alex
 *
 */
public class TestDescriptionParser {

	DescriptionParser parser;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		parser = new DescriptionParser("cirsium", TaxonRank.GENUS);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link conversion.DescriptionParser#parseTaxon()}.
	 */
	@Test
	public void testParseTaxon() {
		ITaxon taxon = parser.parseTaxon();
		assertFalse(taxon.getStructureTree().isEmpty());
		System.out.println(taxon);
		System.out.println(taxon.getRelations());
		TaxonHierarchy h = new TaxonHierarchy(taxon);
	}

}
