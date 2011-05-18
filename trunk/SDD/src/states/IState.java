/**
 * 
 */
package states;

import java.util.Map;

/**
 * All State objects will implement this base interface.
 * @author Alex
 *
 */
public interface IState<T> {
	
	/**
	 * Get the value(s) of this state.
	 * @return
	 */
	public Map<String, T> getMap();
	
	/**
	 * Get the from unit of the state, if any.
	 * @return
	 */
	public String getFromUnit();
	
	/**
	 * Get the to unit of the state, if any.
	 * @return
	 */
	public String getToUnit();
	
	/**
	 * Adds a modifier to this state.
	 * @param modifier
	 */
	public void addModifier(String modifier);
	
	/**
	 * Get the modifier of this state, if any.
	 * @return
	 */
	public String getModifier();
	
	/**
	 * Add a constraint to this state.
	 * @param constraint
	 */
	public void addConstraint(String constraint);
	
	/**
	 * Return the constraint on this state, if any.
	 * @return
	 */
	public String getConstraint();
}
