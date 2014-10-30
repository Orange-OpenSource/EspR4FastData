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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProxyParams", propOrder = { "code", "reasonPhrase","details" })
public class ResponseMessage {

  protected int code;
  protected String reasonPhrase;
  protected String details;

  public ResponseMessage() {}

  public ResponseMessage(int code, String reasonPhrase, String details) {
    this.code = code;
    this.reasonPhrase = reasonPhrase;
    this.details = details;
  }
  
  public int getCode() { return this.code; }
  public void setCode(int code) { this.code = code; }
  
  public String getReasonPhrase() { return this.reasonPhrase; }
  public void setReasonPhrase(String reasonPhrase) { this.reasonPhrase = reasonPhrase; }

	public String getDetails() { return details; }
	public void setDetails(String details) { this.details = details; }  
}