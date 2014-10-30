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
 * <p>Classe Java pour SubscribeContextResponse complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="SubscribeContextResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscribeResponse" type="{}SubscribeResponse" minOccurs="0"/>
 *         &lt;element name="subscribeError" type="{}SubscribeError" minOccurs="0"/>
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
@XmlType(name = "SubscribeContextResponse", propOrder = {
    "subscribeResponse",
    "subscribeError"
})
public class SubscribeContextResponse {

    protected SubscribeResponse subscribeResponse;
    protected SubscribeError subscribeError;

    /**
     * Obtient la valeur de la propri�t� subscribeResponse.
     * 
     * @return
     *     possible object is
     *     {@link SubscribeResponse }
     *     
     */
    public SubscribeResponse getSubscribeResponse() {
        return subscribeResponse;
    }

    /**
     * D�finit la valeur de la propri�t� subscribeResponse.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscribeResponse }
     *     
     */
    public void setSubscribeResponse(SubscribeResponse value) {
        this.subscribeResponse = value;
    }

    /**
     * Obtient la valeur de la propri�t� subscribeError.
     * 
     * @return
     *     possible object is
     *     {@link SubscribeError }
     *     
     */
    public SubscribeError getSubscribeError() {
        return subscribeError;
    }

    /**
     * D�finit la valeur de la propri�t� subscribeError.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscribeError }
     *     
     */
    public void setSubscribeError(SubscribeError value) {
        this.subscribeError = value;
    }

}
