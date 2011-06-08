package conversion.test;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import states.IState;
import taxonomy.ITaxon;
import taxonomy.SubTaxonException;
import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;
import conversion.DescriptionParser;
import conversion.TaxonCharacterMatrix;
import dao.FilenameTaxonDao;

public class TaxonCharacterMatrixTest {

	private FilenameTaxonDao dao = new FilenameTaxonDao();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testTaxonCharacterMatrix() {
		TaxonHierarchy th = makeHierarchy("achillea");
		th.printSimple();
		TaxonCharacterMatrix matrix = new TaxonCharacterMatrix(th);
		Map<String, Map<ITaxon, IState>> map = matrix.getTable();
		matrix.printSimple();
		matrix.generateMatrixFile("output/matrix.csv");
	}
	
	/**
	 * 
	 */
	private TaxonHierarchy makeHierarchy(String genusName) {
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
		return h;
	}

}
