//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.17 at 07:34:43 PM EDT 
//


package sdd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RecommendedMeasureSeq complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RecommendedMeasureSeq">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rs.tdwg.org/UBIF/2006/}Seq">
 *       &lt;sequence>
 *         &lt;element name="StatisticalMeasure" type="{http://rs.tdwg.org/UBIF/2006/}UnivarStatMeasureElaboration" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecommendedMeasureSeq", propOrder = {
    "statisticalMeasure"
})
public class RecommendedMeasureSeq
    extends Seq
{

    @XmlElement(name = "StatisticalMeasure", required = true)
    protected List<UnivarStatMeasureElaboration> statisticalMeasure;

    /**
     * Gets the value of the statisticalMeasure property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statisticalMeasure property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatisticalMeasure().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnivarStatMeasureElaboration }
     * 
     * 
     */
    public List<UnivarStatMeasureElaboration> getStatisticalMeasure() {
        if (statisticalMeasure == null) {
            statisticalMeasure = new ArrayList<UnivarStatMeasureElaboration>();
        }
        return this.statisticalMeasure;
    }

}
