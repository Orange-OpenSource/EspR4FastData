/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/
 
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
 * <p>Classe Java pour ContextAttributeResponse complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ContextAttributeResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contextAttributeList" type="{}ContextAttributeList"/>
 *         &lt;element name="statusCode" type="{}StatusCode"/>
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
@XmlType(name = "ContextAttributeResponse", propOrder = {
    "contextAttributeList",
    "statusCode"
})
public class ContextAttributeResponse {

    @XmlElement(required = true)
    protected ContextAttributeList contextAttributeList;
    @XmlElement(required = true)
    protected StatusCode statusCode;

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
     * Obtient la valeur de la propri�t� statusCode.
     * 
     * @return
     *     possible object is
     *     {@link StatusCode }
     *     
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * D�finit la valeur de la propri�t� statusCode.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusCode }
     *     
     */
    public void setStatusCode(StatusCode value) {
        this.statusCode = value;
    }

}
