package taxonomy;

/**
 * Represents a Subtribe.
 * @author alex
 *
 */
public class Subtribe extends TaxonBase {

	/**
	 * Creates a new (nameless) Subtribe object.
	 */
	public Subtribe() {
		super(TaxonRank.SUBTRIBE);
	}
	
	/**
	 * Creates a new Subtribe with a given name.
	 * @param name
	 */
	public Subtribe(String name) {
		super(TaxonRank.SUBTRIBE, name);
	}
}
