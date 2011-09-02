/**
 * 
 */
package util;

import sdd.AbstractCharacterDefinition;
import sdd.CharacterLocalStateDef;

/**
 * This is used as a kind of "dummy" local state definition for QuantitativeCharacters.
 * Primary use is for help in maintaining map (in SDDConverter) 
 * from LocalStateDefs to ModifierDefs (which was originally created for use with 
 * Categorical Characters only).
 * @author Alex
 *
 */
public class QuantitativeStateDef extends CharacterLocalStateDef {

	private AbstractCharacterDefinition character;
	/**
	 * 
	 */
	public QuantitativeStateDef(String id, AbstractCharacterDefinition character) {
		this.setId(id);
		this.character = character;
	}
	/**
	 * @return the character
	 */
	public AbstractCharacterDefinition getCharacter() {
		return character;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuantitativeStateDef [");
		if (character != null)
			builder.append("character=").append(character).append(", ");
		if (id != null)
			builder.append("id=").append(id);
		builder.append("]");
		return builder.toString();
	}

}
