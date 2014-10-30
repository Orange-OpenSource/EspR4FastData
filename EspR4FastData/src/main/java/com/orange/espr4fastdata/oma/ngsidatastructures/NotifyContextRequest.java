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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour NotifyContextRequest complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="NotifyContextRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriptionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="originator" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="contextResponseList" type="{}ContextElementResponseList" minOccurs="0"/>
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
@XmlType(name = "NotifyContextRequest", propOrder = {
    "subscriptionId",
    "originator",
    "contextResponseList"
})
public class NotifyContextRequest {

    @XmlElement(required = true)
    protected String subscriptionId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String originator;
    protected ContextElementResponseList contextResponseList;

    /**
     * Obtient la valeur de la propri�t� subscriptionId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * D�finit la valeur de la propri�t� subscriptionId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionId(String value) {
        this.subscriptionId = value;
    }

    /**
     * Obtient la valeur de la propri�t� originator.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginator() {
        return originator;
    }

    /**
     * D�finit la valeur de la propri�t� originator.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginator(String value) {
        this.originator = value;
    }

    /**
     * Obtient la valeur de la propri�t� contextResponseList.
     * 
     * @return
     *     possible object is
     *     {@link ContextElementResponseList }
     *     
     */
    public ContextElementResponseList getContextResponseList() {
        return contextResponseList;
    }

    /**
     * D�finit la valeur de la propri�t� contextResponseList.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextElementResponseList }
     *     
     */
    public void setContextResponseList(ContextElementResponseList value) {
        this.contextResponseList = value;
    }

}
