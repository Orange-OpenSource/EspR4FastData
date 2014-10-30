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
 * <p>Classe Java pour UpdateContextRequest complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="UpdateContextRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contextElement" type="{}ContextElementList"/>
 *         &lt;element name="updateAction" type="{}UpdateActionType"/>
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
@XmlType(name = "UpdateContextRequest", propOrder = {
    "contextElementList",
    "updateAction"
})
public class UpdateContextRequest {

    @XmlElement(required = true)
    protected ContextElementList contextElementList;
    @XmlElement(required = true)
    protected UpdateActionType updateAction;

    /**
     * Obtient la valeur de la propri�t� contextElement.
     * 
     * @return
     *     possible object is
     *     {@link ContextElementList }
     *     
     */
    public ContextElementList getContextElementList() {
        return contextElementList;
    }

    /**
     * D�finit la valeur de la propri�t� contextElement.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextElementList }
     *     
     */
    public void setContextElementList(ContextElementList value) {
        this.contextElementList = value;
    }

    /**
     * Obtient la valeur de la propri�t� updateAction.
     * 
     * @return
     *     possible object is
     *     {@link UpdateActionType }
     *     
     */
    public UpdateActionType getUpdateAction() {
        return updateAction;
    }

    /**
     * D�finit la valeur de la propri�t� updateAction.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateActionType }
     *     
     */
    public void setUpdateAction(UpdateActionType value) {
        this.updateAction = value;
    }

}
