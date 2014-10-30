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
@XmlType(name = "Property", propOrder = { "name", "type" })

public class Property {
  protected String name;
  protected String type;
 
  public Property() {}
  public Property(String name, String type) {
    this.name = name;
    this.type = type;
  }
  
  public String getName() { return this.name; }
  public void setName(String name) { this.name = name; }
  
  public String getType() { return this.type; }
  public void setType(String type) { this.type = type; }
	
}
