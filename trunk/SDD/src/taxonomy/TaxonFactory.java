package taxonomy;

/**
 * Static factory for producing Taxon objects.
 * @author alex
 *
 */
public class TaxonFactory {
	
	public static TaxonBase getTaxonObject(TaxonRank rank, String name) {

		switch (rank) {
		case FAMILY:
			return new Family(name);
		case GENUS:
			return new Genus(name);
		case SPECIES:
			return new Species(name);
		case SUBGENUS:
			return new Subgenus(name);
		case SUBSPECIES:
			return new Subspecies(name);
		case SUBTRIBE:
			return new Subtribe(name);
		case TRIBE:
			return new Tribe(name);
		default:
			return new TaxonBase(rank, name);
		}
	}

}
