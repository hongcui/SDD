/**
 * 
 */
package dao.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.SingularPluralDao;

/**
 * @author Alex
 *
 */
public class TestSingularPluralDao {

	private SingularPluralDao dao;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dao = new SingularPluralDao();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		dao = null;
	}

	/**
	 * Test method for {@link dao.SingularPluralDao#getSingularForPlural(java.lang.String)}.
	 */
	@Test
	public final void testGetSingularForPlural() {
		List<String> result = dao.getSingularForPlural("units");
		assertTrue(result.size()==1);
		assertTrue(result.get(0).equals("unit"));
		result = dao.getSingularForPlural("banana");
		assertTrue(result.isEmpty());
		result = dao.getSingularForPlural("unit");
		assertTrue(result.isEmpty());
	}

}
