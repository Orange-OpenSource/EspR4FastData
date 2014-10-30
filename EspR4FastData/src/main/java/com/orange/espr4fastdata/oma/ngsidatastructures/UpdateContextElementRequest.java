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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour UpdateContextElementRequest complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="UpdateContextElementRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attributeDomainName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contextAttributeList" type="{}ContextAttributeList" minOccurs="0"/>
 *         &lt;element name="domainMetadata" type="{}ContextMetadataList" minOccurs="0"/>
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
@XmlType(name = "UpdateContextElementRequest", propOrder = {
    "attributeDomainName",
    "contextAttributeList",
    "domainMetadata"
})
public class UpdateContextElementRequest {

    protected String attributeDomainName;
    protected ContextAttributeList contextAttributeList;
    protected ContextMetadataList domainMetadata;

    /**
     * Obtient la valeur de la propri�t� attributeDomainName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeDomainName() {
        return attributeDomainName;
    }

    /**
     * D�finit la valeur de la propri�t� attributeDomainName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeDomainName(String value) {
        this.attributeDomainName = value;
    }

    /**
     * Obtient la valeur de la propri�t� contextAttributeList.
     * 
     * @return
     *     possible object is
     *     {@link ContextAttributeList }
     *     
     */
    public ContextAttributeList getContextAttributeList() {
        return contextAttributeList;
    }

    /**
     * D�finit la valeur de la propri�t� contextAttributeList.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextAttributeList }
     *     
     */
    public void setContextAttributeList(ContextAttributeList value) {
        this.contextAttributeList = value;
    }

    /**
     * Obtient la valeur de la propri�t� domainMetadata.
     * 
     * @return
     *     possible object is
     *     {@link ContextMetadataList }
     *     
     */
    public ContextMetadataList getDomainMetadata() {
        return domainMetadata;
    }

    /**
     * D�finit la valeur de la propri�t� domainMetadata.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextMetadataList }
     *     
     */
    public void setDomainMetadata(ContextMetadataList value) {
        this.domainMetadata = value;
    }

}
