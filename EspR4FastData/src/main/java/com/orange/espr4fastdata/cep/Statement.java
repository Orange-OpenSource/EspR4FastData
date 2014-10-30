/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.cep;

import java.util.Observable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Statement", propOrder = { "name", 
																					 "epl", 
																					 "targetEntityIdType",
                                           "propertyList", 
                                           "eventSinkUrlList" })
public class Statement extends Observable {

  protected String name =null;
  protected String epl = null;
  protected String targetEntityIdType = null;
  protected PropertyList propertyList = null;
  protected EventSinkUrlList eventSinkUrlList = null;
//----------------------------------------------------------------------------------------------------------------------
  public Statement() {}
  public Statement(String name, String epl) { 
    this.name = name;
    this.epl = epl;
  }
//----------------------------------------------------------------------------------------------------------------------
  public String getName() { return this.name; }
  public void setName(String name) { 
  	this.name = name; 
		super.setChanged();
		super.notifyObservers();
  }
//----------------------------------------------------------------------------------------------------------------------
  public String getEpl() { return this.epl; }
  public void setEpl(String epl) { 
  	this.epl = epl; 
		super.setChanged();
		super.notifyObservers();
  }
//----------------------------------------------------------------------------------------------------------------------
  public String getTargetEntityIdType() { return targetEntityIdType; }
  public void setTargetEntityIdType(String targetEntityIdType) { 
  	this.targetEntityIdType = targetEntityIdType; 
		super.setChanged();
		super.notifyObservers();
  }
//----------------------------------------------------------------------------------------------------------------------  
  public EventSinkUrlList getEventSinkUrlList() {
  	
  	if( this.eventSinkUrlList == null )
  		this.eventSinkUrlList = new EventSinkUrlList();
  	
  	return this.eventSinkUrlList; 
  }
//----------------------------------------------------------------------------------------------------------------------  
  public void setEventSinkUrlList(EventSinkUrlList eventSinkUrlList) { 
  	this.eventSinkUrlList = eventSinkUrlList; 
		super.setChanged();
		super.notifyObservers();
  }
//----------------------------------------------------------------------------------------------------------------------
  public PropertyList getPropertyList() {
  	
  	if(this.propertyList == null)
  		this.propertyList = new PropertyList();
  	
  	return propertyList; 
  }
//----------------------------------------------------------------------------------------------------------------------  
  public void setPropertyList(PropertyList propertyList) { 
    this.propertyList = propertyList; 
		super.setChanged();
		super.notifyObservers();
  }
}
