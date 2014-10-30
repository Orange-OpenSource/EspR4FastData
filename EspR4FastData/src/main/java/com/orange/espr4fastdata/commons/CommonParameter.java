/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.commons;

import java.util.Observable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mdps.StorageNotAvailableException;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommonParameter", propOrder = { "appName", "appUrl", "proxyUrl", "proxyPort"} )

public class CommonParameter extends Observable implements Cloneable{

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(CommonParameter.class);
	
	@XmlElement
	public String appName = new String();

	@XmlElement
	public String appUrl = new String();

	@JsonIgnore
	@XmlTransient
	public String resourceDir = new String();
	
  @XmlElement
  public String proxyUrl = null;
  
  @XmlElement
  public Integer proxyPort = null;
//----------------------------------------------------------------------------------------------------------------------
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) { 
  	int nbObs = super.countObservers();
		this.appName = appName; 
		super.setChanged(); 
		super.notifyObservers(); 
	}

	public String getAppUrl() {	return appUrl; }

	public void setAppUrl(String appUrl) { 
		this.appUrl = appUrl; 
		super.setChanged(); 
		super.notifyObservers(); 
	}
	
	public String getResourceDir() { return resourceDir; }

	public void setResourceDir(String resourceDir) {
		int observers = this.countObservers();
		this.resourceDir = resourceDir; 
		super.setChanged(); 
		super.notifyObservers(); 
	}
	
	public String getProxyUrl() {	return proxyUrl; }

	public void setProxyUrl(String proxyUrl) { 
		this.proxyUrl = proxyUrl; 
		super.setChanged(); 
		super.notifyObservers(); }
	
	public Integer getProxyPort() { return proxyPort;	}

	public void setProxyPort(Integer proxyPort) { 
		this.proxyPort = proxyPort; 
		super.setChanged(); 
		super.notifyObservers(); }
	  
//----------------------------------------------------------------------------------------------------------------------

	public void reset() throws StorageNotAvailableException {
			// Delete proxy configuration. Do not delete appName and appUrl configuration.
			this.proxyUrl = null;
			this.proxyPort = null;
	}
//----------------------------------------------------------------------------------------------------------------------
	public CommonParameter clone() {
		
		CommonParameter commonParameter = new CommonParameter();
		
		if(this.appName != null) commonParameter.appName = new String(this.appName);
		if(this.appUrl != null) commonParameter.appUrl = new String(this.appUrl);
		if(this.proxyPort != null) commonParameter.proxyPort = this.proxyPort.intValue();
		if(this.proxyUrl != null) commonParameter.proxyUrl = new String(this.proxyUrl);
		if(this.resourceDir != null) commonParameter.resourceDir = new String(this.resourceDir);
		
		return commonParameter;		
	}
}
