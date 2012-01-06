package conversion.datapassing;

import sdd.AbstractCharacterDefinition;
import sdd.CharacterLocalStateDef;

/**
 * Represents a taxon associated with some character-state.
 * @author Alex
 *
 */
public class TaxonCharacterStateTriple {
	
	private String taxonName;
	private AbstractCharacterDefinition character;
	private CharacterLocalStateDef state;
	
	/**
	 * Creates a new triple holding data on a taxon associated with
	 * a particular character-state.
	 * @param taxonName
	 * @param character
	 * @param state
	 */
	public TaxonCharacterStateTriple(String taxonName,
									AbstractCharacterDefinition character,
									CharacterLocalStateDef state) {
		this.taxonName = taxonName;
		this.character = character;
		this.state = state;
	}

	/**
	 * @return the taxonName
	 */
	public String getTaxonName() {
		return taxonName;
	}

	/**
	 * @return the character
	 */
	public AbstractCharacterDefinition getCharacter() {
		return character;
	}

	/**
	 * @return the state
	 */
	public CharacterLocalStateDef getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((character == null) ? 0 : character.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((taxonName == null) ? 0 : taxonName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TaxonCharacterStateTriple)) {
			return false;
		}
		TaxonCharacterStateTriple other = (TaxonCharacterStateTriple) obj;
		if (character == null) {
			if (other.character != null) {
				return false;
			}
		} else if (!character.equals(other.character)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		if (taxonName == null) {
			if (other.taxonName != null) {
				return false;
			}
		} else if (!taxonName.equals(other.taxonName)) {
			return false;
		}
		return true;
	}

}
