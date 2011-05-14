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
			if(c.getUnit() != null) {
				return new RangeState<Double>(Double.parseDouble(c.getFrom()),
											Double.parseDouble(c.getTo()),
											c.getUnit());
			}
			else {
				return new RangeState<String>(c.getFrom(), c.getTo());
			}
		}
		else {
			if(c.getUnit() != null) {
				return new SingletonState<Double>(Double.parseDouble(c.getValue()),
						c.getUnit());
			}
			else
				return new SingletonState<String>(c.getValue());
		}
	}
}
