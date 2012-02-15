package states;

import java.util.HashMap;
import java.util.Map;

import annotationSchema.jaxb.Structure;

/**
 * @author Alex
 *
 */
public class RangeState<T> extends BaseState implements IState<T> {
	
	public static final String KEY_FROM = "from value";
	public static final String KEY_TO = "to value";
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
		this.map.put(KEY_FROM, from);
		this.map.put(KEY_TO, to);
	}
	
	/**
	 * Create a new ranged state with a given unit.
	 * @param from
	 * @param to
	 * @param unit
	 */
	public RangeState(T from, T to, String fromUnit, String toUnit) {
		this.map = new HashMap<String, T>();
		this.map.put(KEY_FROM, from);
		this.map.put(KEY_TO, to);
		this.fromUnit = fromUnit;
		this.toUnit = toUnit;
		try {
			convertUnitToStandard();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
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
	
	@SuppressWarnings("rawtypes")
	@Override
	public IState promote() {
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private void convertUnitToStandard() throws Exception {
		if(this.fromUnit != null) {
			try {
				Double value = Double.parseDouble(this.map.get(KEY_FROM).toString());
				if (fromUnit.toLowerCase().equals("mm"))
					value *= this.millimeter;
				else if (fromUnit.toLowerCase().equals("cm"))
					value *= this.centimeter;
				else if (fromUnit.toLowerCase().equals("km"))
					value *= this.kilometer;
				else if (fromUnit.toLowerCase().equals("kg"))
					value *= this.kilogram;
				else if (fromUnit.toLowerCase().equals("mg"))
					value *= this.milligram;
				else if (fromUnit.toLowerCase().equals("cg"))
					value *= this.centigram;
				else
					throw new Exception("Could not find unit for conversion: " + fromUnit);
				map.put(KEY_FROM, (T) value);
			}
			catch (NumberFormatException e) {
				System.out.println("Could not parse as double: " 
						+ map.get(KEY_FROM).toString() + ", even though from_unit exists!");
			}
		}
		if(this.toUnit != null) {
			try {
				Double value = Double.parseDouble(this.map.get(KEY_TO).toString());
				if (toUnit.toLowerCase().equals("mm"))
						value *= this.millimeter;
				else if (toUnit.toLowerCase().equals("cm"))
					value *= this.centimeter;
				else if (toUnit.toLowerCase().equals("km"))
					value *= this.kilometer;
				else if (toUnit.toLowerCase().equals("kg"))
					value *= this.kilogram;
				else if (toUnit.toLowerCase().equals("mg"))
					value *= this.milligram;
				else if (toUnit.toLowerCase().equals("cg"))
					value *= this.centigram;
				else
					throw new Exception("Could not find to unit for conversion: " + toUnit);
				map.put(KEY_TO, (T) value);
			}
			catch (NumberFormatException e) {
				System.out.println("Could not parse as double: " 
						+ map.get(KEY_FROM).toString() + ", even though to_unit exists!");
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RangeState [");
		if (map != null)
			builder.append("map=").append(map.toString()).append(", ");
		if (fromUnit != null)
			builder.append("fromUnit=").append(fromUnit).append(", ");
		if (toUnit != null)
			builder.append("toUnit=").append(toUnit).append(", ");
		if (modifier != null)
			builder.append("modifier=").append(modifier).append(", ");
		if (constraint != null)
			builder.append("constraint=").append(constraint).append(", ");
		if (constraintId != null)
			builder.append("constraintId=").append(constraintId.getName());
		builder.append("]");
		return builder.toString();
	}
	

}
