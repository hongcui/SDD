package states;

import java.util.HashMap;
import java.util.Map;

import annotationSchema.jaxb.Structure;

public class EmptyState<T> extends BaseState implements IState<T> {
	
	public static final String KEY_FROM = "from value";
	public static final String KEY_TO = "to value";
	private Map<String, T> map;
	private String fromUnit, toUnit, modifier, constraint;
	private Structure constraintId;
	
	public EmptyState() {
		this.map = new HashMap<String, T>();
		this.map.put(KEY_FROM, (T) "");
		this.map.put(KEY_TO, (T) "");
		this.fromUnit = "";
		this.toUnit = "";
	}

	@Override
	public Map<String, T> getMap() {
		return this.map;
	}

	@Override
	public String getFromUnit() {
		return this.fromUnit;
	}

	@Override
	public String getToUnit() {
		return this.toUnit;
	}

	@Override
	public void addModifier(String modifier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getModifier() {
		return this.modifier;
	}

	@Override
	public void addConstraint(String constraint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addConstraintId(Structure struct) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConstraint() {
		return this.constraint;
	}

	@Override
	public Structure getConstraintId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IState promote() {
		return this;
	}
	
	public IState demote() {
		IState demoted = new SingletonState(map.get("from value"), fromUnit);
		return demoted;
	}
	
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
		builder.append("EmptyState [");
		if (map != null)
			builder.append("map=").append(map).append(", ");
		if (fromUnit != null)
			builder.append("fromUnit=").append(fromUnit).append(", ");
		if (toUnit != null)
			builder.append("toUnit=").append(toUnit).append(", ");
		if (modifier != null)
			builder.append("modifier=").append(modifier).append(", ");
		if (constraint != null)
			builder.append("constraint=").append(constraint).append(", ");
		if (constraintId != null)
			builder.append("constraintId=").append(constraintId);
		builder.append("]");
		return builder.toString();
	}

}
