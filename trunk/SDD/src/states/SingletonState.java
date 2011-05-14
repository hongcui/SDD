package states;

import java.util.HashMap;
import java.util.Map;

/**
 * Single valued state of a given type.
 * @author Alex
 *
 * @param <T>
 */
public class SingletonState<T> implements IState<T> {
	
	private Map<String, T> map;
	private String unit;
	
	/**
	 * Create a new state with given value.
	 * @param value
	 */
	public SingletonState(T value) {
		this.map = new HashMap<String, T>();
		this.map.put("value", value);
		this.unit = null;
	}
	
	/**
	 * Create a new state with given value and unit.
	 * @param value
	 * @param unit
	 */
	public SingletonState(T value, String unit) {
		this.map = new HashMap<String, T>();
		this.map.put("value", value);
		this.unit = unit;
	}

	/*
	 * (non-Javadoc)
	 * @see states.IState#getMap()
	 */
	@Override
	public Map<String, T> getMap() {
		return map;
	}

	/*
	 * (non-Javadoc)
	 * @see states.IState#getFromUnit()
	 */
	@Override
	public String getFromUnit() {
		return this.unit;
	}

	/*
	 * (non-Javadoc)
	 * @see states.IState#toUnit()
	 */
	@Override
	public String getToUnit() {
		return this.unit;
	}

}
