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
 * <p>Classe Java pour NotifyCondition complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="NotifyCondition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{}NotifyConditionType"/>
 *         &lt;element name="condValueList" type="{}CondValueList" minOccurs="0"/>
 *         &lt;element name="restriction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "NotifyCondition", propOrder = {
    "type",
    "condValueList",
    "restriction"
})
public class NotifyCondition {

    @XmlElement(required = true)
    protected NotifyConditionType type;
    protected CondValueList condValueList;
    protected String restriction;

    /**
     * Obtient la valeur de la propri�t� type.
     * 
     * @return
     *     possible object is
     *     {@link NotifyConditionType }
     *     
     */
    public NotifyConditionType getType() {
        return type;
    }

    /**
     * D�finit la valeur de la propri�t� type.
     * 
     * @param value
     *     allowed object is
     *     {@link NotifyConditionType }
     *     
     */
    public void setType(NotifyConditionType value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propri�t� condValueList.
     * 
     * @return
     *     possible object is
     *     {@link CondValueList }
     *     
     */
    public CondValueList getCondValueList() {
        return condValueList;
    }

    /**
     * D�finit la valeur de la propri�t� condValueList.
     * 
     * @param value
     *     allowed object is
     *     {@link CondValueList }
     *     
     */
    public void setCondValueList(CondValueList value) {
        this.condValueList = value;
    }

    /**
     * Obtient la valeur de la propri�t� restriction.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestriction() {
        return restriction;
    }

    /**
     * D�finit la valeur de la propri�t� restriction.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestriction(String value) {
        this.restriction = value;
    }

}
