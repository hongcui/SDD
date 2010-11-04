<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Sep 14, 2010</xd:p>
            <xd:p><xd:b>Author:</xd:b> adusen</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:param name="count" select="1"/>
        <xml>
            <Datasets xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://rs.tdwg.org/UBIF/2006/" xsi:schemaLocation="http://rs.tdwg.org/UBIF/2006/ ../SDD.xsd">
                <TechnicalMetadata created="">
                    Generated from Hong Cui's 2-20-2010meeting instance data.
                </TechnicalMetadata>                 
                <Dataset xml:lang="en-us">
                    <Characters>
                        <xsl:for-each select="description/statement/structure">
                            
                            <xsl:choose>
                                <xsl:when test="child::character"> <!-- When there is a character child node of the structure -->
                                    <xsl:for-each select="character">
                                        
                                        <xsl:element name="CategoricalCharacter">
                                            
                                            <xsl:attribute name="id">
                                                <xsl:value-of select="parent::structure/@id"/>
                                            </xsl:attribute>
                                            
                                            <xsl:element name="Representation">
                                                <xsl:element name="Label">
                                                    <xsl:value-of select="concat(parent::structure/@name, ' ', @name)"/>
                                                </xsl:element>
                                            </xsl:element>
                                            
                                            <xsl:element name="States">
                                                <xsl:element name="StateDefinition">
                                                    <xsl:attribute name="s1"></xsl:attribute>
                                                    
                                                    <xsl:element name="Representation">
                                                        <xsl:element name="Label">
                                                            <xsl:value-of select="@value"/>
                                                        </xsl:element>
                                                    </xsl:element>
                                                    
                                                </xsl:element>
                                            </xsl:element>
                                            
                                        </xsl:element>
                                        
                                    </xsl:for-each>
                                </xsl:when>
                                <xsl:otherwise> <!-- Else, there is just the structure, build a character out of it alone -->
                                    <xsl:element name="CategoricalCharacter">
                                        <xsl:attribute name="id">
                                            <xsl:value-of select="@id"/>
                                        </xsl:attribute>
                                        
                                        <xsl:element name="Representation">
                                            <xsl:element name="Label">
                                                <xsl:value-of select="@name"/>
                                            </xsl:element>
                                        </xsl:element>
                                        
                                        <xsl:element name="StateDefinition">
                                            
                                        </xsl:element>
                                        
                                    </xsl:element>
                                </xsl:otherwise>
                            </xsl:choose>    
                        </xsl:for-each>    
                    </Characters>
                </Dataset>
            </Datasets>
        </xml>
        
    </xsl:template>
</xsl:stylesheet>
