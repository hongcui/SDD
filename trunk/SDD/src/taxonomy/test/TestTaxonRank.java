package taxonomy.test;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taxonomy.TaxonRank;

public class TestTaxonRank extends TestCase{

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public final void testTaxonRank() {
		assertTrue(TaxonRank.FAMILY.compareTo(TaxonRank.GENUS) > 0);
		assertTrue(TaxonRank.GENUS.compareTo(TaxonRank.SPECIES) > 0);
		assertTrue(TaxonRank.GENUS.compareTo(TaxonRank.GENUS) == 0);
	}

}
