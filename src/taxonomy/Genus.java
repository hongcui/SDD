package taxonomy;

/**
 * Represents a Genus Taxon.
 * @author Alex
 *
 */
public class Genus extends TaxonBase implements ITaxon {

	/**
	 * Create a new Genus taxon object.
	 */
	public Genus() {
		super(TaxonRank.GENUS);
	}

	/**
	 * Create a new Genus taxon object with a name.
	 * @param name The scientific name of this genus.
	 */
	public Genus(String name) {
		super(TaxonRank.GENUS, name);
	}

}
