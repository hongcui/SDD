package states;

import java.util.HashMap;
import java.util.Map;

import annotationSchema.jaxb.Structure;

/**
 * @author Alex
 *
 */
public class RangeState<T> implements IState<T> {
	
	private Map<String, T> map;
	private String fromUnit, toUnit, modifier, constraint;
	private Structure constraintId;
	
	/**
	 * Create a new ranged state.
	 * @param from
	 * @param to
	 */
	public RangeState(T from, T to) {
		this.map = new HashMap<String, T>();
		this.map.put("from value", from);
		this.map.put("to value", to);
	}
	
	/**
	 * Create a new ranged state with a given unit.
	 * @param from
	 * @param to
	 * @param unit
	 */
	public RangeState(T from, T to, String fromUnit, String toUnit) {
		this.map = new HashMap<String, T>();
		this.map.put("from value", from);
		this.map.put("to value", to);
		this.fromUnit = fromUnit;
		this.toUnit = toUnit;
	}

	/*
	 * (non-Javadoc)
	 * @see states.IState#getMap()
	 */
	@Override
	public Map<String, T> getMap() {
		return this.map;
	}

	/*
	 * (non-Javadoc)
	 * @see states.IState#getFromUnit()
	 */
	@Override
	public String getFromUnit() {
		return this.fromUnit;
	}

	/*
	 * (non-Javadoc)
	 * @see states.IState#toUnit()
	 */
	@Override
	public String getToUnit() {
		return this.toUnit;
	}

	@Override
	public void addModifier(String modifier) {
		this.modifier = modifier;
	}

	@Override
	public String getModifier() {
		return this.modifier;
	}

	@Override
	public void addConstraint(String constraint) {
		this.constraint = constraint;
	}
	
	@Override
	public void addConstraintId(Structure constraintId) {
		this.constraintId = constraintId;
	}

	@Override
	public String getConstraint() {
		return this.constraint;
	}
	
	@Override
	public Structure getConstraintId() {
		return this.constraintId;
	}

}
