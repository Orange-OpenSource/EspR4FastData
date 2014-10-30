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

import java.util.Collections;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mdps.MdpsList;
import com.mdps.ObservableList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContextElementList", propOrder = { "contextElement" })
public class ContextElementList implements MdpsList{

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ContextElementList.class);
	
  protected ObservableList<ContextElement> contextElement;
//----------------------------------------------------------------------------------------------------------------------
  @JsonProperty("contextElements")
  public ObservableList<ContextElement> getList() {
    return this.contextElement;
  }
  
  public void setList(ObservableList<ContextElement> contextElements) {
  	this.contextElement = contextElements; 
  }
//----------------------------------------------------------------------------------------------------------------------
  private class CustomComparator implements Comparator<ContextElement> {
		
		public int compare(ContextElement contextElement1, ContextElement contextElement2) {
			return contextElement1.getTimeStamp().compareTo(contextElement2.getTimeStamp());
		}
  }
//----------------------------------------------------------------------------------------------------------------------
  public void sortByTimeStamp() {
  	Collections.sort(contextElement, new CustomComparator());
  }  
}
