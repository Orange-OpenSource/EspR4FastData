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
 * <p>Classe Java pour OperationScope complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="OperationScope">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="scopeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="scopeValue" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
@XmlType(name = "OperationScope", propOrder = {
    "scopeType",
    "scopeValue"
})
public class OperationScope {

    @XmlElement(required = true)
    protected String scopeType;
    @XmlElement(required = true)
    protected Object scopeValue;

    /**
     * Obtient la valeur de la propri�t� scopeType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScopeType() {
        return scopeType;
    }

    /**
     * D�finit la valeur de la propri�t� scopeType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScopeType(String value) {
        this.scopeType = value;
    }

    /**
     * Obtient la valeur de la propri�t� scopeValue.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getScopeValue() {
        return scopeValue;
    }

    /**
     * D�finit la valeur de la propri�t� scopeValue.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setScopeValue(Object value) {
        this.scopeValue = value;
    }

}
