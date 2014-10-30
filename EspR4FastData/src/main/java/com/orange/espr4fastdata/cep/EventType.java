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
@XmlType(name = "EventType", propOrder = { "name", "propertyList", "virtual", "xsltTransformer" })
public class EventType extends Observable {
  protected String name;
  protected PropertyList propertyList;
  protected boolean virtual;
  protected String xsltTransformer;  
  
  public String getXsltTransformer() { return this.xsltTransformer; }
	public void setXsltTransformer(String xsltTransformer) { 
		this.xsltTransformer = xsltTransformer;
		super.setChanged();
		super.notifyObservers();
  }

	public String getName() { return this.name; }
  public void setName(String name) { 
  	this.name = name; 
		super.setChanged();
		super.notifyObservers();
  }
  
  public PropertyList getPropertyList() { return propertyList; }
  public void setPropertyList(PropertyList propertyList) { 
  	this.propertyList = propertyList; 
		super.setChanged();
		super.notifyObservers();
  }
  
	public boolean isVirtual() { return virtual; }
	public void setVirtual(boolean virtual) {	
		this.virtual = virtual;	
		super.setChanged();
		super.notifyObservers();
  }
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Convert this class instance to an EPL create scheme statement, which is understood by the Esper library.
	 * @return an EPL statement as a String object.
	 */
	public String toEplCreateSchemaStatement() {
		
		String eplDeclareSchema = "create schema "+this.name+" \n(\n";
		 
		for(Property property : this.propertyList.getProperty()) {
			
			String type = property.getType();
			if(type.equals("dateTime"))
				type = "string";
			
		  eplDeclareSchema += "    "+property.getName()+" "+type+", \n";
		}	 

		eplDeclareSchema = eplDeclareSchema.substring(0, eplDeclareSchema.length()-3); // Remove the last trailing comma
    eplDeclareSchema += "\n)";	    

		return eplDeclareSchema;
	}
}