/**
 * 
 */
package dao.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import taxonomy.TaxonRank;

import dao.FilenameTaxonDao;

/**
 * @author Alex
 *
 */
public class TestFilenameTaxonDao {

	private FilenameTaxonDao dao;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dao = new FilenameTaxonDao();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link dao.FilenameTaxonDao#getConnection()}.
	 */
	@Test
	public final void testGetConnection() {
		try {
			Connection conn = dao.getConnection();
			assertTrue(conn.isValid(30));
			conn.close();
			assertTrue(conn.isClosed());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link dao.FilenameTaxonDao#getTaxonValues(java.lang.String)}.
	 */
	@Test
	public final void testGetTaxonValues() {
		Map<String, String> map = dao.getTaxonValues("6.xml");
		assertFalse(map.isEmpty());
		assertTrue(map.get("family").equals("asteraceae"));
		assertTrue(map.get("tribe").equals("mutisieae"));
		assertTrue(map.get("subtribe").isEmpty());
		map.clear();
		map = dao.getTaxonValues("60.xml");
		assertTrue(map.get("family").equals("asteraceae"));
		assertTrue(map.get("tribe").equals("cynareae"));
	}
	
	/**
	 * Test method for {@link dao.FilenameTaxonDao#getFilenameOfFamilyDescription(String)}.
	 */
	@Test
	public final void testGetFilenameOfFamilyDescription () {
		String filename = dao.getFilenameOfFamilyDescription("asteraceae");
		assertFalse(filename.isEmpty());
		assertTrue(filename.equals("1.xml"));
	}
	
	/**
	 * Test method for {@link dao.FilenameTaxonDao#getFilenameForDescription(taxonomy.TaxonRank, String).
	 */
	@Test
	public final void testGetFilenameForDescription () {
		String filename = dao.getFilenameForDescription(TaxonRank.SPECIES, "arcticum");
		assertFalse(filename.isEmpty());
		assertTrue(filename.equals("932.xml"));
		filename = dao.getFilenameForDescription(TaxonRank.SPECIES, "ludoviciana");
		assertTrue(filename.equals("908.xml"));
	}

}
