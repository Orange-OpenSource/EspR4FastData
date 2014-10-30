/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.6 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée é ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2013.01.04 é 05:59:51 PM CET 
//

package com.orange.espr4fastdata.oma.ngsidatastructures;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

/**
 * <p>Classe Java pour ContextRegistration complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ContextRegistration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entityIdList" type="{}EntityIdList" minOccurs="0"/>
 *         &lt;element name="contextRegistrationAttributeList" type="{}ContextRegistrationAttributeList" minOccurs="0"/>
 *         &lt;element name="registrationMetadata" type="{}registrationMetadata" minOccurs="0"/>
 *         &lt;element name="providingApplication" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
@XmlType(name = "ContextRegistration", propOrder = {
		"timeStamp",
		"entityIdType",
    "entityIdList",
    "contextRegistrationAttributeList",
    "registrationMetadata",
    "providingApplication"
})

public class ContextRegistration {

    @SuppressWarnings("unused")
		private static Logger logger = Logger.getLogger(ContextRegistration.class);    

    // The following declaration is not NGSI compliant: it is implemented to make data storage easier.
    protected String entityIdType;

    // The following declarations are NGSI compliant
    protected Date timeStamp;

    protected EntityIdList entityIdList;
    protected ContextRegistrationAttributeList contextRegistrationAttributeList;
    protected RegistrationMetadata registrationMetadata;

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String providingApplication;
    
//----------------------------------------------------------------------------------------------------------------------    
    
    public Date getTimeStamp() { return this.timeStamp; }
		public void setTimeStamp(Date timeStamp) { 
			this.timeStamp = timeStamp; 
	  }
		
//----------------------------------------------------------------------------------------------------------------------
		/**
     * Obtient la valeur de la propriété entityIdList.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdList }
     *     
     */
    public EntityIdList getEntityIdList() {
        return entityIdList;
    }

    /**
     * Définit la valeur de la propriété entityIdList.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdList }
     *     
     */
    public void setEntityIdList(EntityIdList value) {
        this.entityIdList = value;
      }


    /**
     * Obtient la valeur de la propriété contextRegistrationAttributeList.
     * 
     * @return
     *     possible object is
     *     {@link ContextRegistrationAttributeList }
     *     
     */
    public ContextRegistrationAttributeList getContextRegistrationAttributeList() {
        return contextRegistrationAttributeList;
    }

    /**
     * Définit la valeur de la propriété contextRegistrationAttributeList.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextRegistrationAttributeList }
     *     
     */
    public void setContextRegistrationAttributeList(ContextRegistrationAttributeList value) {
        this.contextRegistrationAttributeList = value;
      }

    /**
     * Obtient la valeur de la propriété registrationMetadata.
     * 
     * @return
     *     possible object is
     *     {@link registrationMetadata }
     *     
     */
    public RegistrationMetadata getRegistrationMetadata() {
        return registrationMetadata;
    }

    /**
     * Définit la valeur de la propriété registrationMetadata.
     * 
     * @param value
     *     allowed object is
     *     {@link registrationMetadata }
     *     
     */
    public void setRegistrationMetadata(RegistrationMetadata value) {
        this.registrationMetadata = value;
      }


    /**
     * Obtient la valeur de la propriété providingApplication.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvidingApplication() {
        return providingApplication;
    }

    /**
     * Définit la valeur de la propriété providingApplication.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvidingApplication(String value) {
        this.providingApplication = value;
    }
    
		public String getEntityIdType() {
			return entityIdType;
		}
		
		public void setEntityIdType(String entityIdType) {
			this.entityIdType = entityIdType;
		}    
}
