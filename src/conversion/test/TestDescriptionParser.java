/**
 * 
 */
package conversion.test;

import static org.junit.Assert.assertFalse;

import java.util.Map;

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

	private DescriptionParser parser = new DescriptionParser("cirsium", TaxonRank.GENUS);
	private ITaxon taxon = parser.parseTaxon();
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
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
		assertFalse(taxon.getStructureTree().isEmpty());
		System.out.println(taxon);
		System.out.println(taxon.getRelations());
		TaxonHierarchy h = new TaxonHierarchy(taxon);
	}
	
	@Test
	public void testGetTextMap() {
		Map<String, String> map = taxon.getStatementTextMap();
		assertFalse(map.isEmpty());
		System.out.println(map.toString());
	}

}
