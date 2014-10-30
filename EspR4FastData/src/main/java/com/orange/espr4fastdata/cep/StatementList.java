/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.cep;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mdps.MdpsList;
import com.mdps.ObservableList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class StatementList implements MdpsList {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(StatementList.class);
	
  protected ObservableList<Statement> statement;
//----------------------------------------------------------------------------------------------------------------------
  @JsonProperty("statements")
  public ObservableList<Statement> getList() {
    return this.statement;
  }
  
  public void setList(ObservableList<Statement> statements) {
  	this.statement = statements; 
  }
}
