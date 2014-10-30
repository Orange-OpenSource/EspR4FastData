/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.ngsicontext;

import java.util.Observable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.orange.espr4fastdata.oma.ngsidatastructures.AttributeList;
import com.orange.espr4fastdata.oma.ngsidatastructures.EntityIdList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contextSubscription", propOrder ={"subscriptionId", "entityIdList", "attributeList", "reference" })

public class ContextSubscription extends Observable {

	protected String subscriptionId;
  protected EntityIdList entityIdList;
  protected AttributeList attributeList;
  protected String reference;

  public EntityIdList getEntityIdList() { return entityIdList; }
  public void setEntityIdList(EntityIdList value) { 
  	this.entityIdList = value; 
		super.setChanged();
		super.notifyObservers();
  }

  public AttributeList getAttributeList() { return attributeList; }
  public void setAttributeList(AttributeList value) { 
  	this.attributeList = value; 
		super.setChanged();
		super.notifyObservers();
  }

  public String getReference() { return reference; }
  public void setReference(String value) { 
  	this.reference = value; 
		super.setChanged();
		super.notifyObservers();
  }

  public String getSubscriptionId() { return subscriptionId; }
	public void setSubscriptionId(String subscriptionId) { 
		this.subscriptionId = subscriptionId; 
		super.setChanged();
		super.notifyObservers();
  }
  
}