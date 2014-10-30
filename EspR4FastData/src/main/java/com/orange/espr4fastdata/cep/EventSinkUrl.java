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
import javax.xml.datatype.Duration;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventSinkUrl", propOrder = { "name", 
																							"httpMethod", 
																							"target", 
																							"registrationURL", 
                                              "registered", 
                                              "httpRegistrationCode", 
                                              "afterRegistrationDelay",
                                              "registrationDuration",
                                              "registrationId",
                                              "xAuthToken"})

public class EventSinkUrl extends Observable {

  protected String name;
  protected String httpMethod;
  protected String target;
  protected String registrationURL = null;
  protected boolean registered = false;
  protected Integer httpRegistrationCode = null; 
  //Optional delay between possible registration and the actual sending of events: some event recipients might need some
  // time between a registration and the actual receiving of related events.
  protected Integer afterRegistrationDelay = null; 
  protected Duration registrationDuration = null;
  protected String registrationId = null; // The registration ID that is returned by the event sink.
  protected String xAuthToken = null; // When the event sink needs a x-auth authentication token.
  
  public String getxAuthToken() {
		return xAuthToken;
	}
	public void setxAuthToken(String xAuthToken) {
		this.xAuthToken = xAuthToken;
	}
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
		super.setChanged();
		super.notifyObservers();
	}
	
	public String getName() { return this.name; }
  public void setName(String name) { 
  	this.name = name;
		super.setChanged();
		super.notifyObservers();
  }

  public String getHttpMethod() { return this.httpMethod; }
  public void setHttpMethod(String method) { 
  	this.httpMethod = method; 
		super.setChanged();
		super.notifyObservers();
  }

  public String getTarget() { return this.target; }
  public void setTarget(String target) { 
  	this.target = target;
		super.setChanged();
		super.notifyObservers();
  }

  public String getRegistrationURL() { return this.registrationURL; }
  public void setRegistrationURL(String registrationURL) {
  	this.registrationURL = registrationURL;
		super.setChanged();
		super.notifyObservers();
  }

  public boolean isRegistered() { return registered; }
  public void setRegistered(boolean registered) {
  	this.registered = registered;
		super.setChanged();
		super.notifyObservers();
  }
  
  public Integer getHttpRegistrationCode() { return httpRegistrationCode; }
  public void setHttpRegistrationCode(Integer httpRegistrationCode) {
  	this.httpRegistrationCode = httpRegistrationCode;
		super.setChanged();
		super.notifyObservers();
  }
	
  public Integer getAfterRegistrationDelay() { return afterRegistrationDelay; }
	public void setAfterRegistrationDelay(Integer afterRegistrationDelay) {
		this.afterRegistrationDelay = afterRegistrationDelay; 
		super.setChanged();
		super.notifyObservers();
  }
	
	public Duration getRegistrationDuration() { return registrationDuration;	}
	public void setRegistrationDuration(Duration registrationDuration) {	
		this.registrationDuration = registrationDuration;
		super.setChanged();
		super.notifyObservers();
  }
}
