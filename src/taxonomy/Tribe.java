package taxonomy;

/**
 * @author alex
 *
 */
public class Tribe extends TaxonBase {

	/**
	 * Create a new Tribe taxon object.
	 */
	public Tribe() {
		super(TaxonRank.TRIBE);
	}

	/**
	 * Creates a tribe with a given name.
	 * @param name
	 */
	public Tribe(String name) {
		super(TaxonRank.TRIBE, name);
	}

}
