package states;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex
 *
 */
public class RangeState<T> implements IState<T> {
	
	private Map<String, T> map;
	private String unit;
	
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
	public RangeState(T from, T to, String unit) {
		this.map = new HashMap<String, T>();
		this.map.put("from value", from);
		this.map.put("to value", to);
		this.unit = unit;
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
	 * @see states.IState#getUnit()
	 */
	@Override
	public String getUnit() {
		return this.unit;
	}

}
