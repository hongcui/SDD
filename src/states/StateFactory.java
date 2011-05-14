package states;

/**
 * Factory that produces state objects, depending on data gotten 
 * from a Character.
 * @author Alex
 *
 */
public class StateFactory {

	@SuppressWarnings("rawtypes")
	public static IState getStateObject(annotationSchema.jaxb.Character c) {
		if(c.getValue() == null) {
			if(c.getFromUnit() != null && c.getToUnit() != null) {
				Double from = null, to = null;
				try {
					from = Double.parseDouble(c.getFrom());
					to = Double.parseDouble(c.getTo());
				}
				catch(NumberFormatException e) {
					System.out.println("Bad number:" + e.getMessage() + " in Character: " + c.toString());
					from = -1.0;
					to = -1.0;
				}
				return new RangeState<Double>(from, to,	c.getFromUnit(), c.getToUnit());
			}
			else {
				return new RangeState<String>(c.getFrom(), c.getTo());
			}
		}
		else {
			if(c.getUnit() != null) {
				Double value = null;
				try {
					value = Double.parseDouble(c.getValue());
				}
				catch(NumberFormatException e) {
					System.out.println("Bad number:" + e.getMessage() + " in Character: " + c.toString());
					value = -1.0;
				}
				return new SingletonState<Double>(value, c.getUnit());
			}
			else
				return new SingletonState<String>(c.getValue());
		}
	}
}
