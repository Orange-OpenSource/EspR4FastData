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
 * <p>Classe Java pour StatusCode complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="StatusCode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="reasonPhrase" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="details" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
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
@XmlType(name = "StatusCode", propOrder = {
    "code",
    "reasonPhrase",
    "details"
})
public class StatusCode {

    protected int code;
    @XmlElement(required = true)
    protected String reasonPhrase;
    protected String details;

    public StatusCode(){}
    
    public StatusCode(int code, String reasonPhrase, String details) {
    	this.code = code;
    	this.reasonPhrase = new String(reasonPhrase);
    	this.details = details;
    }
    
    /**
     * Obtient la valeur de la propri�t� code.
     * 
     */
    public int getCode() {
        return code;
    }

    /**
     * D�finit la valeur de la propri�t� code.
     * 
     */
    public void setCode(int value) {
        this.code = value;
    }

    /**
     * Obtient la valeur de la propri�t� reasonPhrase.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    /**
     * D�finit la valeur de la propri�t� reasonPhrase.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonPhrase(String value) {
        this.reasonPhrase = value;
    }

    /**
     * Obtient la valeur de la propri�t� details.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDetails() {
        return details;
    }

    /**
     * D�finit la valeur de la propri�t� details.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDetails(String value) {
        this.details = value;
    }

}
