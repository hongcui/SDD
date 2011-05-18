//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.11 at 12:08:08 PM EDT 
//


package annotationSchema.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import states.IState;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}character" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}text" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="constraint_type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="constraint_parent_organ" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "character",
    "text"
})
@XmlRootElement(name = "structure")
public class Structure {

    protected List<Character> character;
    protected String text;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "constraint_type")
    protected String constraintType;
    @XmlAttribute(name = "constraint_parent_organ")
    protected String constraintParentOrgan;
    @XmlAttribute(required = true)
    protected String name;
    
    @SuppressWarnings("rawtypes")
    @XmlTransient
	protected Map<String, IState> stateMap;
    @XmlTransient
    protected Map<String, String> modifierMap;

    /**
     * Gets the value of the character property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the character property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCharacter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Character }
     * 
     * 
     */
    public List<Character> getCharacter() {
        if (character == null) {
            character = new ArrayList<Character>();
        }
        return this.character;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the constraintType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConstraintType() {
        return constraintType;
    }

    /**
     * Sets the value of the constraintType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConstraintType(String value) {
        this.constraintType = value;
    }

    /**
     * Gets the value of the constraintParentOrgan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConstraintParentOrgan() {
        return constraintParentOrgan;
    }

    /**
     * Sets the value of the constraintParentOrgan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConstraintParentOrgan(String value) {
        this.constraintParentOrgan = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

	/**
	 * @return the stateMap
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, IState> getCharStateMap() {
		if(stateMap == null)
			stateMap = new TreeMap<String, IState>();
		return stateMap;
	}
	
	/**
	 * Convenience method for adding a mapping to the stateMap.
	 * @param c Character name.
	 * @param s State.
	 */
	@SuppressWarnings("rawtypes")
	public void addMapping(String charName, IState state) {
		if(stateMap == null)
			stateMap = new TreeMap<String, IState>();
		stateMap.put(charName, state);
	}

	/**
	 * Return a map from character names to modifiers.
	 * @return the modifierMap
	 */
	public Map<String, String> getModifierMap() {
		if(modifierMap == null)
			modifierMap = new TreeMap<String, String>();
		return modifierMap;
	}
	
	/**
	 * Adds a new mapping from character name to modifier
	 * @param charName
	 * @param modifier
	 */
	public void addModifierToCharName(String charName, String modifier) {
		if(this.modifierMap == null)
			modifierMap = new TreeMap<String, String>();
		modifierMap.put(charName, modifier);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Structure [");
		if (name != null)
			builder.append("name=").append(name);
		builder.append("]");
		return builder.toString();
	}

}
