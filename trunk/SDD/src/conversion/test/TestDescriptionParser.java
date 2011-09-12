/**
 * 
 */
package conversion.test;

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taxonomy.ITaxon;
import taxonomy.SubTaxonException;
import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;
import conversion.DescriptionParser;
import dao.FilenameTaxonDao;

/**
 * @author alex
 *
 */
public class TestDescriptionParser {

	private FilenameTaxonDao dao = new FilenameTaxonDao();
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
	
	@Test
	public final void testBuildHierarchy() {
		makeHierarchy("achillea");
	}

	/**
	 * 
	 */
	private void makeHierarchy(String genusName) {
		DescriptionParser parser = new DescriptionParser(genusName, TaxonRank.GENUS);
		ITaxon taxon = parser.parseTaxon();
		String cirsiumGenusFilename = dao.getFilenameForDescription(TaxonRank.GENUS, genusName);
		List<String> cirsiumSpecies = dao.getFilenamesForManyDescriptions(TaxonRank.GENUS, genusName, TaxonRank.SPECIES);
		cirsiumSpecies.remove(cirsiumGenusFilename);
		TaxonHierarchy h = new TaxonHierarchy(taxon);
		DescriptionParser speciesParser;
		for(String s : cirsiumSpecies) {
			String speciesName = dao.getTaxonValues(s).get("species");
			System.out.println("Species name: " + speciesName);
			speciesParser = new DescriptionParser(speciesName, TaxonRank.SPECIES);
			ITaxon speciesTaxon = speciesParser.parseTaxon();
			try {
				h.addSubTaxon(speciesTaxon);
			} catch (SubTaxonException e) {
				e.printStackTrace();
			}
		}
		h.printSimple();
	}

}
