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
					if (c.getTo()!=null)
						to = Double.parseDouble(c.getTo());
					else 
						to = 9999.00;
					rangeState = new RangeState<Double>(from, to);
//					System.out.println("<Debug-StateFactory> Parsed as a quantitative count: "+c.toString());
				}
				catch (NumberFormatException e) {
					String sto;
					if (c.getTo()!=null)
						sto = c.getTo();
					else 
						sto = "9999.00";
					rangeState = new RangeState<String>(c.getFrom(), sto);
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
				//Need to check here for counts, too
				IState state = null;
				Double value = null;
				try {
					value = Double.parseDouble(c.getValue());
					state = new RangeState<Double>(value, value);
//					System.out.println("<DEBUG-StateFactory> Parse as quant. count: " + value.toString());
				}
				catch (NumberFormatException e) {
					if(c.getValue().equals("/"))
						state = new EmptyState<String>();
					else
						state = new SingletonState<String>(c.getValue().replace("/","slash"));
				}
				if(c.getModifier() != null)
					state.addModifier(c.getModifier().replace("\u00B1","plus_minus"));
				else
					state.addModifier(c.getModifier());
				state.addConstraint(c.getConstraint());
				if(!c.getConstraintid().isEmpty())
					state.addConstraintId((Structure) c.getConstraintid().get(0));
				return state;
			}
		}
	}
}
