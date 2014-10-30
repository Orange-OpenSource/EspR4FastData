/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/


// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.6 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2013.01.04 � 05:59:51 PM CET 
//


package com.orange.espr4fastdata.oma.ngsidatastructures;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour UpdateContextAttributeRequest complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="UpdateContextAttributeRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contextValue" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="metadata" type="{}ContextMetadataList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateContextAttributeRequest", propOrder = {
    "type",
    "contextValue",
    "metadata"
})
public class UpdateContextAttributeRequest {

    protected String type;
    @XmlElement(required = true)
    protected Object contextValue;
    protected ContextMetadataList metadata;

    /**
     * Obtient la valeur de la propri�t� type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * D�finit la valeur de la propri�t� type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propri�t� contextValue.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getContextValue() {
        return contextValue;
    }

    /**
     * D�finit la valeur de la propri�t� contextValue.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setContextValue(Object value) {
        this.contextValue = value;
    }

    /**
     * Obtient la valeur de la propri�t� metadata.
     * 
     * @return
     *     possible object is
     *     {@link ContextMetadataList }
     *     
     */
    public ContextMetadataList getMetadata() {
        return metadata;
    }

    /**
     * D�finit la valeur de la propri�t� metadata.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextMetadataList }
     *     
     */
    public void setMetadata(ContextMetadataList value) {
        this.metadata = value;
    }

}
