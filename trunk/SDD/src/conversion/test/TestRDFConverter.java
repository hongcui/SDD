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
		//parser = new DescriptionParser("cirsium", TaxonRank.GENUS);
		//parser = new DescriptionParser("Bellis perennis Linnaeus", TaxonRank.SPECIES);
	    parser = new DescriptionParser("ASTER Linnaeus", TaxonRank.GENUS);
		//parser = new DescriptionParser("Egletes viscosa (Linnaeus) Lessing", TaxonRank.SPECIES);
	}
	@After
	public void tearDown() throws Exception {
		parser = null;
	}
	@Test
	public void testTaxonToRDF() {
		//TaxonHierarchy h = new TaxonHierarchy(parser.parseTaxon());
		//RDFConverter rdfConverter = new RDFConverter(h);

		//String f = "2";
		//String outfile = "C:\\Documents and Settings\\Hong Updates\\Desktop\\Australia\\V20-fixed-630-project\\target\\RDF\\"+f+".rdf";
		//String outfile = "Z:\\COURSES\\ControlledVocabulary\\630Fall2011\\project\\RDF\\2.rdf";
		//rdfConverter.taxonToRDF(h.getHierarchy().getRoot().getElement(), outfile);
		//rdfConverter.taxonToRDF(h.getHierarchy().getRoot().getElement(), "output/cirsium.xml");
		//rdfConverter.taxonToRDF(h.getHierarchy().getRoot().getElement(), "output/cirsium-rdf.xml");

		for (String sp : species) {
			parser = new DescriptionParser(sp, TaxonRank.SPECIES);
			TaxonHierarchy h = new TaxonHierarchy(parser.parseTaxon());
			RDFConverter rdfConverter = new RDFConverter(h, parser.getFilename());
			rdfConverter.taxonToRDF(h.getHierarchy().getRoot().getElement(), "output/"+sp+"-rdf.xml");
		}
	}

}
