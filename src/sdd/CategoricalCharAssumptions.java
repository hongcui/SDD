//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.17 at 07:34:43 PM EDT 
//


package sdd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * Inner class, used only within CategoricalCharacter
 * 
 * <p>Java class for CategoricalCharAssumptions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CategoricalCharAssumptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MeasurementScale" type="{http://rs.tdwg.org/UBIF/2006/}CategoricalMeasurementScaleEnum" minOccurs="0"/>
 *         &lt;element name="NaturallyContinuous" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;group ref="{http://rs.tdwg.org/UBIF/2006/}SpecificExtension" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategoricalCharAssumptions", propOrder = {
    "measurementScale",
    "naturallyContinuous",
    "nextVersion"
})
public class CategoricalCharAssumptions {

    @XmlElement(name = "MeasurementScale")
    protected QName measurementScale;
    @XmlElement(name = "NaturallyContinuous")
    protected Boolean naturallyContinuous;
    @XmlElement(name = "NextVersion")
    protected VersionExtension nextVersion;

    /**
     * Gets the value of the measurementScale property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getMeasurementScale() {
        return measurementScale;
    }

    /**
     * Sets the value of the measurementScale property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setMeasurementScale(QName value) {
        this.measurementScale = value;
    }

    /**
     * Gets the value of the naturallyContinuous property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNaturallyContinuous() {
        return naturallyContinuous;
    }

    /**
     * Sets the value of the naturallyContinuous property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNaturallyContinuous(Boolean value) {
        this.naturallyContinuous = value;
    }

    /**
     * Gets the value of the nextVersion property.
     * 
     * @return
     *     possible object is
     *     {@link VersionExtension }
     *     
     */
    public VersionExtension getNextVersion() {
        return nextVersion;
    }

    /**
     * Sets the value of the nextVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionExtension }
     *     
     */
    public void setNextVersion(VersionExtension value) {
        this.nextVersion = value;
    }

}
