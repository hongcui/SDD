package taxonomy;

public class SubTaxonException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5447266998978905618L;

	public SubTaxonException(ITaxon subTaxon, ITaxon superTaxon) {
		super("Cannot add taxon: " + subTaxon.toString() 
				+ " as a sub-taxa of: " + superTaxon.toString());
	}
}
