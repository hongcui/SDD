package states;

import annotationSchema.jaxb.Structure;

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
				RangeState<Double> rangeState = new RangeState<Double>(from, to,
						c.getFromUnit(), c.getToUnit());
				rangeState.addModifier(c.getModifier());
				rangeState.addConstraint(c.getConstraint());
				if(!c.getConstraintid().isEmpty())
					rangeState.addConstraintId((Structure) c.getConstraintid().get(0));
				return rangeState;
			}
			else {
				//Let's first check if it's a count, by trying to parse it as an integer.
				Double from = null, to = null;
				IState rangeState = null;
				try {
					from = Double.parseDouble(c.getFrom());
					to = Double.parseDouble(c.getTo());
					rangeState = new RangeState<Double>(from, to);
					System.out.println("<Debug-StateFactory> Parsed as a quantitative count: "+c.toString());
				}
				catch (NumberFormatException e) {
					rangeState = new RangeState<String>(c.getFrom(), c.getTo());
				}
				rangeState.addModifier(c.getModifier());
				rangeState.addConstraint(c.getConstraint());
				if(!c.getConstraintid().isEmpty())
					rangeState.addConstraintId((Structure) c.getConstraintid().get(0));
				return rangeState;
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
				SingletonState<Double> singletonState = new SingletonState<Double>(value, c.getUnit());
				singletonState.addModifier(c.getModifier());
				singletonState.addConstraint(c.getConstraint());
				if(!c.getConstraintid().isEmpty())
					singletonState.addConstraintId((Structure) c.getConstraintid().get(0));
				return singletonState;
			} else {
				SingletonState<String> singletonState = new SingletonState<String>(
						c.getValue().replace("/","slash"));
				if(c.getModifier() != null)
					singletonState.addModifier(c.getModifier().replace("\u00B1","plus_minus"));
				else
					singletonState.addModifier(c.getModifier());
				singletonState.addConstraint(c.getConstraint());
				if(!c.getConstraintid().isEmpty())
					singletonState.addConstraintId((Structure) c.getConstraintid().get(0));
				return singletonState;
			}
		}
	}
}
