//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.17 at 07:34:43 PM EDT 
//


package sdd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Expressing sex as code (enumerated vocabulary) or free-form literal or verbatim text. At least one attribute should be present; this can not be validated by the schema (external validation).
 * 
 * <p>Java class for Sex complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Sex">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://rs.tdwg.org/UBIF/2006/}AbstractStringOrCode">
 *       &lt;attribute name="code" type="{http://rs.tdwg.org/UBIF/2006/}SexStatusEnum" default="UnknownSex" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sex")
public class Sex
    extends AbstractStringOrCode
{


}
