/**
 * 
 */
package states;

import taxonomy.ITaxon;

/**
 * All State objects will implement this base interface.
 * @author Alex
 *
 */
public interface IState {
	
	public ITaxon getTaxon();
}
