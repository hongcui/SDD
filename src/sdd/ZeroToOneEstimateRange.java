//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.17 at 07:34:43 PM EDT 
//


package sdd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Optional lower/upper estimate attributes in the range 0-1, with default values. Used, e. g., for certainty and frequency; the default values 0 and 1, resp., indicate that no estimate was possible.
 * 
 * <p>Java class for ZeroToOneEstimateRange complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ZeroToOneEstimateRange">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="lowerestimate" type="{http://rs.tdwg.org/UBIF/2006/}ZeroToOne" default="0" />
 *       &lt;attribute name="upperestimate" type="{http://rs.tdwg.org/UBIF/2006/}ZeroToOne" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZeroToOneEstimateRange")
public class ZeroToOneEstimateRange {

    @XmlAttribute
    protected Double lowerestimate;
    @XmlAttribute
    protected Double upperestimate;

    /**
     * Gets the value of the lowerestimate property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getLowerestimate() {
        if (lowerestimate == null) {
            return  0.0D;
        } else {
            return lowerestimate;
        }
    }

    /**
     * Sets the value of the lowerestimate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLowerestimate(Double value) {
        this.lowerestimate = value;
    }

    /**
     * Gets the value of the upperestimate property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getUpperestimate() {
        if (upperestimate == null) {
            return  1.0D;
        } else {
            return upperestimate;
        }
    }

    /**
     * Sets the value of the upperestimate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setUpperestimate(Double value) {
        this.upperestimate = value;
    }

}
