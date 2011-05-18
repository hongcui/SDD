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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines a stored identification key (dichotomous or multifurcating key) that has been digitized from printed publications or manually created to express expert knowledge that would not be available in dynamically created dichotomous keys (using Ratings from terminology and a 'find next best character' to minimize the average search tree).
 * 
 * <p>Java class for StoredKey complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StoredKey">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rs.tdwg.org/UBIF/2006/}VersionedAbstractObject">
 *       &lt;sequence>
 *         &lt;element name="Scope" type="{http://rs.tdwg.org/UBIF/2006/}DescriptionScopeSet" minOccurs="0"/>
 *         &lt;element name="Question" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice maxOccurs="unbounded">
 *                   &lt;element name="Text" type="{http://rs.tdwg.org/UBIF/2006/}LongStringL"/>
 *                   &lt;element name="MediaObject" type="{http://rs.tdwg.org/UBIF/2006/}MediaObjectRef"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Leads" type="{http://rs.tdwg.org/UBIF/2006/}StoredKey_LeadSeq"/>
 *         &lt;group ref="{http://rs.tdwg.org/UBIF/2006/}SpecificExtension" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoredKey", propOrder = {
    "scope",
    "question",
    "leads",
    "nextVersion"
})
public class StoredKey
    extends VersionedAbstractObject
{

    @XmlElement(name = "Scope")
    protected DescriptionScopeSet scope;
    @XmlElement(name = "Question")
    protected StoredKey.Question question;
    @XmlElement(name = "Leads", required = true)
    protected StoredKeyLeadSeq leads;
    @XmlElement(name = "NextVersion")
    protected VersionExtension nextVersion;

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link DescriptionScopeSet }
     *     
     */
    public DescriptionScopeSet getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptionScopeSet }
     *     
     */
    public void setScope(DescriptionScopeSet value) {
        this.scope = value;
    }

    /**
     * Gets the value of the question property.
     * 
     * @return
     *     possible object is
     *     {@link StoredKey.Question }
     *     
     */
    public StoredKey.Question getQuestion() {
        return question;
    }

    /**
     * Sets the value of the question property.
     * 
     * @param value
     *     allowed object is
     *     {@link StoredKey.Question }
     *     
     */
    public void setQuestion(StoredKey.Question value) {
        this.question = value;
    }

    /**
     * Gets the value of the leads property.
     * 
     * @return
     *     possible object is
     *     {@link StoredKeyLeadSeq }
     *     
     */
    public StoredKeyLeadSeq getLeads() {
        return leads;
    }

    /**
     * Sets the value of the leads property.
     * 
     * @param value
     *     allowed object is
     *     {@link StoredKeyLeadSeq }
     *     
     */
    public void setLeads(StoredKeyLeadSeq value) {
        this.leads = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice maxOccurs="unbounded">
     *         &lt;element name="Text" type="{http://rs.tdwg.org/UBIF/2006/}LongStringL"/>
     *         &lt;element name="MediaObject" type="{http://rs.tdwg.org/UBIF/2006/}MediaObjectRef"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "textOrMediaObject"
    })
    public static class Question {

        @XmlElements({
            @XmlElement(name = "MediaObject", type = MediaObjectRef.class),
            @XmlElement(name = "Text", type = LongStringL.class)
        })
        protected List<Object> textOrMediaObject;

        /**
         * Gets the value of the textOrMediaObject property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the textOrMediaObject property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTextOrMediaObject().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MediaObjectRef }
         * {@link LongStringL }
         * 
         * 
         */
        public List<Object> getTextOrMediaObject() {
            if (textOrMediaObject == null) {
                textOrMediaObject = new ArrayList<Object>();
            }
            return this.textOrMediaObject;
        }

    }

}
