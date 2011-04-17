package taxonomy;

/**
 * Represents a Species taxon.
 * @author Alex
 *
 */
public class Species extends TaxonBase implements ITaxon {

	/**
	 * Create a new Species taxon object.
	 */
	public Species() {
		super(TaxonRank.SPECIES);
	}

	/**
	 * Create a new Species taxon object with a name.
	 * @param name The scientific name of this species.
	 */
	public Species(String name) {
		super(TaxonRank.SPECIES, name);
	}

}
