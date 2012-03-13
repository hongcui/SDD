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
		TaxonHierarchy th = makeHierarchyGenus("achillea");
//		TaxonHierarchy th = makeHierarchyTwoLevel("cynareae", TaxonRank.TRIBE, TaxonRank.GENUS);
		th.printSimple();
		TaxonCharacterMatrix matrix = new TaxonCharacterMatrix(th);
//		Map<String, Map<ITaxon, List<IState>>> map = matrix.getTable();
//		matrix.printSimple();
		matrix.generateMatrixFile("output/achillea.txt");
//		matrix.generateMatrixFile("output/cirsium.txt");
//		matrix.generateMatrixFile("output/cynareae-genus.txt");
	}
	
	/**
	 * 
	 */
	private TaxonHierarchy makeHierarchyGenus(String genusName) {
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
	
	private TaxonHierarchy makeHierarchyTwoLevel(String topName, TaxonRank topRank, TaxonRank bottomRank) {
		System.out.println("Making hierarchy for family: " + topName);
		DescriptionParser parser = new DescriptionParser(topName, topRank);
		ITaxon taxon = parser.parseTaxon();
		String rankTop = dao.getFilenameForDescription(topRank, topName);
		List<String> rankLower = dao.getFilenamesForManyDescriptions(topRank, topName, bottomRank);
		rankLower.remove(rankTop);
		TaxonHierarchy h = new TaxonHierarchy(taxon);
		DescriptionParser bottomParser;
		for(String s : rankLower) {
			String bottomName = dao.getTaxonValues(s).get(bottomRank.toString().toLowerCase());
			System.out.println("Bottom name: " + bottomName);
			bottomParser = new DescriptionParser(bottomName, bottomRank);
			ITaxon bottomTaxon = bottomParser.parseTaxon();
			try {
				h.addSubTaxon(bottomTaxon);
			}
			catch (SubTaxonException e) {
				System.out.println(e.getMessage());
			}
		}
		return h;
	}

}
