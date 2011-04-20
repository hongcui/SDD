package taxonomy;

/**
 * A Subgenus object.
 * @author alex
 *
 */
public class Subgenus extends TaxonBase {

	/**
	 * Creates a new, nameless subgenus object.
	 */
	public Subgenus() {
		super(TaxonRank.SUBGENUS);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a new subgenus with a given name.
	 * @param name
	 */
	public Subgenus(String name) {
		super(TaxonRank.SUBGENUS, name);
		// TODO Auto-generated constructor stub
	}

}
