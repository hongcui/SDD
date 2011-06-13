package taxonomy;

import java.util.Comparator;

/**
 * Taxa are compared first by their taxonomical rank and then by their name.
 * @author Alex
 *
 */
public class TaxonComparator implements Comparator<ITaxon> {

	@Override
	public int compare(ITaxon arg0, ITaxon arg1) {
		 if(arg1.getTaxonRank().compareTo(arg0.getTaxonRank()) != 0)
			 return arg1.getTaxonRank().compareTo(arg0.getTaxonRank());
		 else
			 return arg0.getName().compareTo(arg1.getName());
	}

}
