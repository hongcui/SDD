package util;

import sdd.LabelText;
import sdd.ObjectFactory;
import sdd.Representation;
import tree.TreeNode;
import annotationSchema.jaxb.Structure;

/**
 * Provides some utility functions for conversion chores.
 * @author alex
 *
 */
public class ConversionUtil {

	/**
	 * Processes a "short" character name into a full name, i.e., the short name preceded by 
	 * the appropriate, full structure name (achieved by looking up the structure tree).
	 * @param shortCharName
	 * @param structNode
	 * @return
	 */
	public static String resolveFullCharacterName(String shortCharName,
			TreeNode<Structure> structNode) {
		String structName = structNode.getElement().getName();
		String charName = structName + "_" + shortCharName;
		TreeNode<Structure> parent = structNode.getParent();
		while(parent != null) {
			structName = parent.getElement().getName();
			if(!structName.equals("whole_organism"))
				charName = structName + "_" + charName;
			parent = parent.getParent();
		}
		return charName;
	}
	
	/**
	 * Creates a new Representation object, with a LabelText
	 * piece that has value as it's string value.
	 * @param value String to place inside LabelText.
	 * @return New representation object.
	 */
	public static Representation makeRep(String value) {
		ObjectFactory sddFactory = new ObjectFactory();
		Representation rep = sddFactory.createRepresentation();
		LabelText labelText = sddFactory.createLabelText();
		labelText.setValue(value);
		rep.getRepresentationGroup().add(labelText);
		return rep;
	}
}
