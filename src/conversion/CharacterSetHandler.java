package conversion;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import sdd.AbstractCharacterDefinition;
import sdd.CharacterLocalStateDef;
import sdd.ObjectFactory;

/**
 * This class handles the addition of characters from a char name->state mapping
 * to an SDD CharacterSet.
 * 
 * @author Alex
 * 
 */
public class CharacterSetHandler extends Observable implements Handler,
		Observer {

	private static final String STATE_ID_PREFIX = "state_";
	private static ObjectFactory sddFactory = new ObjectFactory();
	
	/** Taxon-by-character matrix. */
	private Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> matrix;

	/**
	 * Creates a new CharacterSetHandler.  Adds characters from 
	 * ITaxon objects to SDD CharacterSet.
	 */
	public CharacterSetHandler() {
		this.matrix = new HashMap<String, 
				Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>>();
	}

	/**
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @see conversion.Handler#handle()
	 */
	@Override
	public void handle() {
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the taxon-by-char matrix used for building coded description.
	 * @return the matrix
	 */
	public Map<String, Map<AbstractCharacterDefinition, Set<CharacterLocalStateDef>>> getMatrix() {
		return matrix;
	}

}
