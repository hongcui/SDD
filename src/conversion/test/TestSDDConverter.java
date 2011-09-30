package conversion.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taxonomy.ITaxon;
import taxonomy.SubTaxonException;
import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;
import conversion.DescriptionParser;
import conversion.SDDConverter;
import dao.FilenameTaxonDao;

public class TestSDDConverter {

	DescriptionParser parser;
	private FilenameTaxonDao dao = new FilenameTaxonDao();
	@Before
	public void setUp() throws Exception {
//		parser = new DescriptionParser("centaurea", TaxonRank.GENUS);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTaxonToSDD() {
		TaxonHierarchy h = makeHierarchyGenus("achillea");
		SDDConverter toSDD = new SDDConverter();
//		toSDD.taxonToSDD(h.getHierarchy().getRoot().getElement(), "output/centaurea-sdd.xml");
//		toSDD.taxonHierarchyToSDD(h, "output/achillea-species-sdd.xml");
		toSDD.taxonHierarchyToSDD(h, "SPECIES", "output/achillea-species-sdd.xml");
	}
	
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

}
