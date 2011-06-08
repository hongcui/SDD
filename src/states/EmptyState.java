package states;

import java.util.HashMap;
import java.util.Map;

import annotationSchema.jaxb.Structure;

public class EmptyState<T> implements IState<T> {
	
	private Map<String, T> map;
	private String fromUnit, toUnit, modifier, constraint;
	private Structure constraintId;
	
	public EmptyState() {
		this.map = new HashMap<String, T>();
		this.map.put("from value", (T) "");
		this.map.put("to value", (T) "");
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
