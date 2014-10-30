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
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XSLTTransformer", propOrder = { "name", "relatedEventType", "xslt" })

public class XSLTTransformer {
	
  protected String name;
  protected String relatedEventType;
  protected String xslt;
  
	public String getName() {	return name; }
	public void setName(String name) { this.name = name; }
	
	public String getRelatedEventType() {	return relatedEventType; }
	public void setRelatedEventType(String relatedEventType) { this.relatedEventType = relatedEventType; }
	
	public String getXslt() {	return xslt; }
	public void setXslt(String xslt) { this.xslt = xslt; }  
}
