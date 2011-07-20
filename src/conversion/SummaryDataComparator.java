/**
 * 
 */
package conversion;

import java.util.Comparator;

import javax.xml.bind.JAXBElement;

import sdd.AbstractCharSummaryData;

/**
 * @author Alex
 * @param <T>
 *
 */
public class SummaryDataComparator<T> implements Comparator<T> {

	@SuppressWarnings("unchecked")
	@Override
	public int compare(T arg0, T arg1) {
		JAXBElement<AbstractCharSummaryData> summary1 =
			(JAXBElement<AbstractCharSummaryData>)arg0;
		String ref1 = summary1.getValue().getRef();
		JAXBElement<AbstractCharSummaryData> summary2 = 
			(JAXBElement<AbstractCharSummaryData>) arg1;
		String ref2 = summary2.getValue().getRef();
		return ref1.compareTo(ref2);
	}

}
