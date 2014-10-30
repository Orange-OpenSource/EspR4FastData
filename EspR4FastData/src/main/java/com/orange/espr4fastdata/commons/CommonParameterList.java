/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.commons;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mdps.ObservableList;
 
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommonParameterList", propOrder = { "commonParameters" })

public class CommonParameterList {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(CommonParameterList.class);
	
  protected ObservableList<CommonParameter> commonParameter;
//----------------------------------------------------------------------------------------------------------------------
	public CommonParameterList() {}		
//----------------------------------------------------------------------------------------------------------------------

	@JsonProperty("commonParameters")
  public ObservableList<CommonParameter> getList() { 
  	return this.commonParameter;
  }
  
  public void setList(ObservableList<CommonParameter> commonParameters) { 
  	this.commonParameter = commonParameters; 
  }  
}
