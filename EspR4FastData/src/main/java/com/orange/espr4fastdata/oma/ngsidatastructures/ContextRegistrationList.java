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

import java.util.ArrayList;
import java.util.Observable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Classe Java pour ContextRegistrationList complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ContextRegistrationList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contextRegistrationList" type="{}ContextRegistration" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ContextRegistrationList", propOrder = { "contextRegistration", "registrationId", "duration" })
public class ContextRegistrationList extends Observable {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ContextRegistrationList.class);
	
  protected ArrayList<ContextRegistration> contextRegistration;
  protected String registrationId;
  protected Duration duration;
  
//----------------------------------------------------------------------------------------------------------------------
  @JsonProperty("contextRegistrationLists")
  public ArrayList<ContextRegistration> getList() {
    return this.contextRegistration;
  }
  
  public void setList(ArrayList<ContextRegistration> contextRegistrations){ 
  	this.contextRegistration = contextRegistrations;
  	super.setChanged();
  	super.notifyObservers();
  }

	public String getRegistrationId() {
		return this.registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
		super.setChanged();
  	super.notifyObservers();
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
		super.setChanged();
  	super.notifyObservers();
	}	
}
