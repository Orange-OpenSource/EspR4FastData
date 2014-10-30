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
 * <p>Classe Java pour Restriction complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Restriction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attributeExpression" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="scope" type="{}OperationScopeList" minOccurs="0"/>
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
@XmlType(name = "Restriction", propOrder = {
    "attributeExpression",
    "scope"
})
public class Restriction {

    @XmlElement(required = true)
    protected String attributeExpression;
    protected OperationScopeList scope;

    /**
     * Obtient la valeur de la propri�t� attributeExpression.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeExpression() {
        return attributeExpression;
    }

    /**
     * D�finit la valeur de la propri�t� attributeExpression.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeExpression(String value) {
        this.attributeExpression = value;
    }

    /**
     * Obtient la valeur de la propri�t� scope.
     * 
     * @return
     *     possible object is
     *     {@link OperationScopeList }
     *     
     */
    public OperationScopeList getScope() {
        return scope;
    }

    /**
     * D�finit la valeur de la propri�t� scope.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationScopeList }
     *     
     */
    public void setScope(OperationScopeList value) {
        this.scope = value;
    }

}
